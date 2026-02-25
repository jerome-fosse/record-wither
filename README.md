# record-wither

Adds the **wither pattern** to Java records — the equivalent of Kotlin's `copy()` or Scala's `copy`, without any JVM hacking.

## The problem

Java records are immutable. Creating a modified version requires repeating every component:

```java
var updated = new Author(author.name(), "French", author.dateOfBirth()); // verbose, fragile
```

`record-wither` replaces that with a fluent syntax:

```java
var updated = author.with(w -> w.nationality("French"));
```

## Usage

All `public` records get a wither automatically — no annotation required. Run the `/wither` skill in Claude Code and the boilerplate is generated directly in the source file:

```java
public record Author(String name, String nationality, LocalDate dateOfBirth) {

    // region @Wither — generated, do not edit manually
    public static class Wither {
        private String name;
        private String nationality;
        private LocalDate dateOfBirth;

        private Wither(Author author) { ... }

        public Author.Wither name(String name) { this.name = name; return this; }
        public Author.Wither nationality(String nationality) { ... }
        public Author.Wither dateOfBirth(LocalDate dateOfBirth) { ... }

        private Author apply() { return new Author(name, nationality, dateOfBirth); }
    }

    public Author with(Consumer<Wither> consumer) {
        var wither = new Author.Wither(this);
        consumer.accept(wither);
        return wither.apply();
    }
    // endregion @Wither
}
```

The generated code is plain Java — the IDE sees it, understands it, and can navigate it.

### Opting out — `@WitherIgnore`

Annotate a `public` record with `@WitherIgnore` to exclude it from generation entirely:

```java
@WitherIgnore
public record Internal(String data) {}
```

Annotate a **component** with `@WitherIgnore` to preserve its value across copies — no setter is generated for it. Typical use case: an immutable `id` that must never change:

```java
public record Author(@WitherIgnore UUID id, String name, String nationality) {}

// id is preserved, only name and nationality can be changed via with()
var updated = author.with(w -> w.name("Jane Doe"));
```

### Opting in — `@Wither`

Use `@Wither` to explicitly request generation on non-public records (protected, package-private, or local):

```java
public void doSomething() {
    @Wither
    record Point(int x, int y) {}

    var p = new Point(1, 2);
    var moved = p.with(w -> w.x(10));
}
```

### Chained calls

```java
var updated = author.with(w -> w.name("Jane Doe").nationality("French"));
```

## The `/wither` skill

The Claude Code skill generates and regenerates the boilerplate automatically. Just update the record signature and re-run `/wither`.

| Command | Behaviour |
|---|---|
| `/wither` | Processes only modified files (uncommitted, via `git diff`) |
| `/wither --all` | Processes all records in the project |
| `/wither Publisher.java` | Processes the specified file only |

> If the project is not a git repository, `/wither` behaves like `/wither --all`.

### Generation rules

| Record type | Annotation | Result |
|---|---|---|
| `public record` | none | ✅ generated |
| `public record` | `@Wither` | ✅ generated |
| `public record` | `@WitherIgnore` | ⏭ skipped |
| non-public record | `@Wither` | ✅ generated |
| non-public record | none | ⏭ skipped |
| `class` or `interface` | `@Wither` | ⏭ skipped |

- The generated block is delimited by `// region @Wither` and `// endregion @Wither` — do not edit its contents manually.
- If `java.util.function.Consumer` is not yet imported, it is added automatically.
- Re-running `/wither` after adding, removing, or renaming a component is enough to resync the block.

## Ideas for improvement

✅ **Wither auto-generated for all public records** — no annotation required.<br>
✅ **`@WitherIgnore` on a record** — opts the record out of wither generation entirely.<br>
✅ **`@Wither` on a non-public record** — explicit opt-in for local, protected or package-private records.<br>
✅ **`@WitherIgnore` on a field** — the field is preserved across copies but no setter is generated. Useful for immutable ids.<br>
🔳 **IntelliJ file watcher** — use IntelliJ's built-in File Watchers plugin to run `claude --print "/wither $FileName$"` on save, keeping the boilerplate in sync without any manual step.<br>
🔳 **Git pre-commit hook** — run `/wither` automatically before each commit to ensure the generated blocks are always up to date in the committed code.<br>
🔳 **Validation** — detect drift between the record signature and the existing generated block at `/wither` run time and warn explicitly rather than silently regenerating.

