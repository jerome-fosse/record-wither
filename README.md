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

Annotate the record with `@Wither`:

```java
@Wither
public record Author(String name, String nationality, LocalDate dateOfBirth) {}
```

Then run the `/wither` skill in Claude Code (see below). The boilerplate is generated directly in the source file:

```java
@Wither
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

### Chained calls

```java
var updated = author.with(w -> w.name("Jane Doe").nationality("French"));
```

### Local records

`@Wither` also works on records declared inside a method:

```java
public void doSomething() {
    @Wither
    record Point(int x, int y) {}

    var p = new Point(1, 2);
    var moved = p.with(w -> w.x(10));
}
```

## The `/wither` skill

The Claude Code skill generates and regenerates the boilerplate automatically. Just update the record signature and re-run `/wither`.

| Command | Behaviour |
|---|---|
| `/wither` | Processes only modified files (uncommitted, via `git diff`) |
| `/wither --all` | Processes all `@Wither` files in the project |
| `/wither Publisher.java` | Processes the specified file only |

> If the project is not a git repository, `/wither` behaves like `/wither --all`.

### Generation rules

- The generated block is delimited by `// region @Wither` and `// endregion @Wither` — do not edit its contents manually.
- Only `record` types are processed; `class` and `interface` types annotated with `@Wither` are ignored.
- If `java.util.function.Consumer` is not yet imported, it is added automatically.
- Re-running `/wither` after adding, removing, or renaming a component is enough to resync the block.

## Ideas for improvement

- **`@Wither` optional for public records** — wither boilerplate is generated automatically for all `public` records; use `@WitherIgnore` to opt out. `@Wither` remains useful to explicitly request generation on `protected` or package-private records.
- **`@WitherIgnore`** — a companion annotation to exclude specific components from the generated `Wither` class (useful for computed or derived fields).
- **IntelliJ file watcher** — use IntelliJ's built-in File Watchers plugin to run `claude --print "/wither $FileName$"` on save, keeping the boilerplate in sync without any manual step.
- **Git pre-commit hook** — run `/wither` automatically before each commit to ensure the generated blocks are always up to date in the committed code.
- **Validation** — detect drift between the record signature and the existing generated block at `/wither` run time and warn explicitly rather than silently regenerating.

## Build

```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="org.jf.recordwither.Test"
```
