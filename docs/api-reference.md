# API Reference

`kotlin-data-ref` exposes a small public surface for reference identity wrappers, identity
collections, and identity-based distinct filtering.

Generate exact signatures locally with:

```bash
./gradlew dokkaGeneratePublicationHtml
```

## Public Surface

- `Ref<A>`: identity wrapper for a value. The public `value` property exposes the wrapped value.
- `identitySet<T>()`: creates an empty mutable identity set. Lookup and removal use reference
  identity.
- `identitySetOf(vararg elements)`: creates a mutable identity set with initial contents. Lookup and
  removal use reference identity.
- `identityMap<K, V>()`: creates an empty mutable identity map. Key operations use identity; the
  `entries` view matches entry keys by identity and entry values structurally.
- `Iterable<T>.distinctByIdentity()`: returns the first occurrence of each reference as a list.
- `Sequence<T>.distinctByIdentity()`: lazily yields the first occurrence of each reference.

See the generated reference for exact signatures and platform availability.
