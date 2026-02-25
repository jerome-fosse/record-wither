---
name: wither
description: Generate or regenerate the @Wither boilerplate (inner Wither class + with() method) directly in the source files of Java records annotated with @Wither.
allowed-tools:
  - Grep
  - Read
  - Edit
  - Write
  - Bash
---

Generate or regenerate the `@Wither` boilerplate for Java records in this project.

## Determining the file scope

The skill accepts an optional argument `$ARGUMENTS`.

- **`/wither <filename>`** (e.g. `/wither Publisher.java`) — process only the file(s) under `src/` whose name matches the argument.
- **`/wither --all`** — process all `.java` files under `src/` that contain `@Wither`.
- **`/wither` (no argument)** — process only the uncommitted `.java` files under `src/` that contain `@Wither`. Run `git diff --name-only HEAD` and `git ls-files --others --exclude-standard` to get the list of modified/untracked files, then filter to those that contain `@Wither`. If git is not available or the directory is not a git repository, fall back to `--all` behaviour.

## Steps

1. Determine the list of candidate files according to the scope above.

2. For each file found:
   a. Read the file.
   b. Find every occurrence of `@Wither` in the file. For each one, look at the next type declaration that follows it (skipping blank lines and other annotations). If that declaration is a `class` or `interface`, skip it. If it is a `record`, process it — this includes top-level records, nested records, and local records declared inside methods.
   c. Extract the record name and its component list from the record declaration line, e.g.:
      `record Foo(TypeA fieldA, TypeB fieldB)` → components: `[(TypeA, fieldA), (TypeB, fieldB)]`
   c. Check whether `java.util.function.Consumer` is already imported; if not, add the import.
   d. Build the generated block using the following template (replace `<Record>`, `<fields>`, `<setters>`, `<constructor-args>` with the actual values):

```java
    // region @Wither — generated, do not edit manually
    public static class Wither {
        private <Type1> <field1>;
        // one field per component

        private Wither(<Record> record) {
            <field1> = record.<field1>;
            // one line per component
        }

        public <Record>.Wither <field1>(<Type1> <field1>) {
            this.<field1> = <field1>;
            return this;
        }
        // one fluent setter per component

        private <Record> apply() {
            return new <Record>(<field1>, <field2>, ...);
        }
    }

    public <Record> with(Consumer<Wither> consumer) {
        var wither = new <Record>.Wither(this);
        consumer.accept(wither);
        return wither.apply();
    }
    // endregion @Wither
```

   e. If the file already contains a `// region @Wither` … `// endregion @Wither` block, **replace** it entirely with the freshly generated one. Otherwise **insert** the generated block just before the closing `}` of the record body.

3. After editing all files, report which files were updated and what changed (added/removed/updated components).

## Rules
- The `Wither` inner class must be `public static`.
- The constructor `Wither(<Record> record)` must be `private`.
- The `apply()` method must be `private`.
- The `with(Consumer<Wither> consumer)` method must be `public` and non-static, on the record itself.
- Do not change anything outside the `// region @Wither` … `// endregion @Wither` markers.
- If the record has no components, generate an empty `Wither` class and a `with()` that is a no-op.
