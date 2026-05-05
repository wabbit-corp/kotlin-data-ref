# Development

`kotlin-data-ref` is a Kotlin Multiplatform library. In the monorepo workspace, build and test it
through the `dev` tooling:

```bash
./dev build kotlin-data-ref
```

From the project directory, or when working from a standalone checkout, run Gradle directly:

```bash
./gradlew build
```

## Documentation Standards

Public API should have KDoc that states:

- whether identity or structural equality is used
- allocation or laziness behavior where it matters
- platform caveats for diagnostic output such as class names

Hand-written docs should link to generated Dokka output for exact signatures instead of duplicating
the full API surface.
