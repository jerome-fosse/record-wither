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

1. Determine the list of candidate files according to the scope above. A candidate file is any `.java` file that contains `\brecord\s+\w+\s*\(` (any record declaration) or `@Wither\b` (word boundary — excludes `@WitherIgnore`).

2. For each file found:
   a. Read the file. Always read the current content — never rely on a previously seen state.
   b. Find every `record` declaration in the file (top-level, nested, or local). For each one, apply the following rules to decide whether to generate a wither:
      - `public record` **without** `@WitherIgnore` → **generate** (regardless of whether `@Wither` is present)
      - `public record` **with** `@WitherIgnore` → **skip**
      - non-public record (protected, package-private, local) **with** `@Wither` → **generate**
      - non-public record **without** `@Wither` → **skip**
      - `class` or `interface` annotated with `@Wither` → **skip**
   c. Extract the record name and its component list from the record declaration line, e.g.:
      `record Foo(TypeA fieldA, TypeB fieldB)` → components: `[(TypeA, fieldA), (TypeB, fieldB)]`
   d. Check whether `java.util.function.Consumer` is already imported; if not, add the import.
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

   f. If the file already contains a `// region @Wither` … `// endregion @Wither` block, **replace** it entirely with the freshly generated one. Otherwise **insert** the generated block just before the closing `}` of the record body.

3. After editing all files, report:
   - records where a wither was generated or is already up to date
   - records annotated with `@WitherIgnore` (opted out explicitly)
   - non-public records without `@Wither` (not eligible)
   Do not mention files that were excluded from the scan entirely (e.g. not a record).

## Rules
- The `Wither` inner class must be `public static`.
- The constructor `Wither(<Record> record)` must be `private`.
- The `apply()` method must be `private`.
- The `with(Consumer<Wither> consumer)` method must be `public` and non-static, on the record itself.
- Do not change anything outside the `// region @Wither` … `// endregion @Wither` markers.
- If the record has no components, generate an empty `Wither` class and a `with()` that is a no-op.
