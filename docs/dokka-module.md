# Module kotlin-data-ref

`kotlin-data-ref` provides identity-based wrappers and collection helpers.

The primary API is:

- `Ref`, a wrapper that compares the wrapped value by reference identity
- `identitySet`, `identitySetOf`, and `identityMap`, mutable collections backed by identity keys
- `distinctByIdentity`, iterable and sequence helpers that remove repeated references without using
  structural equality

Use these utilities when equal objects must remain distinct unless they are the exact same object.
