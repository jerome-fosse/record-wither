# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Test Commands

```bash
# Compile the project
mvn clean compile

# Run tests
mvn test

# Run a single test class
mvn test -Dtest=recordwitherTests

# Run the demo application
mvn exec:java -Dexec.mainClass="org.jf.recordwither.Test"
```

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

## Architecture

### Code generation — source injection via the `/wither` skill

The `/wither` Claude Code skill (`.claude/skills/wither/SKILL.md`) is the sole code generator. It:
1. Finds all `@Wither`-annotated records (including nested and local records inside methods).
2. Parses the record component list from the source.
3. Writes or replaces the generated block directly in the `.java` source file.

There is no annotation processor running at compile time. The `pom.xml` is a standard Maven setup with no special compiler flags.

### Skill invocation modes

| Command | Scope |
|---|---|
| `/wither` | Uncommitted files only (`git diff`); falls back to `--all` if no git repo |
| `/wither --all` | All `@Wither` files in the project |
| `/wither Foo.java` | The specified file only |

### `@Wither` annotation

`src/main/java/org/jf/recordwither/Wither.java` — a simple marker annotation with `CLASS` retention. Only `record` declarations are processed; `class` and `interface` types are ignored even if annotated.

### Generated block structure

```java
// region @Wither — generated, do not edit manually
public static class Wither {
    // one private field per component
    private Wither(MyRecord record) { ... }
    // one fluent setter per component (returns this)
    private MyRecord apply() { return new MyRecord(...); }
}
public MyRecord with(Consumer<Wither> consumer) { ... }
// endregion @Wither
```
