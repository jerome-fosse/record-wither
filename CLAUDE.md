# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

`record-wither` adds a **wither pattern** to Java records — the equivalent of Kotlin's `copy()`. Annotating a record with `@Wither` and running the `/wither` Claude Code skill injects the boilerplate directly into the source file:

1. An inner static `Wither` class with a field, fluent setter, and getter for each record component, plus a private `apply()` method.
2. A public `with(Consumer<Wither> consumer)` method on the record itself.

**Usage example:**
```java
@Wither
record Author(String name, String nationality, LocalDate birthDate) {}

var updated = author.with(w -> w.name("Jane Doe").nationality("French"));
```

The generated code is plain Java inside `// region @Wither` / `// endregion @Wither` markers — visible to the IDE, no compiler hacks required.

### `@Wither` annotation

`src/main/java/org/jf/recordwither/Wither.java` — a simple marker annotation with `CLASS` retention. Only `record` declarations are processed; `class` and `interface` types are ignored even if annotated.
