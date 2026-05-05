# Troubleshooting

## Structurally Equal Objects Are Both Present

That is the point of this library. Identity collections use `===`, not `equals`.

```kotlin
data class Id(val value: Int)

check(identitySetOf(Id(1), Id(1)).size == 2)
```

If you want structural uniqueness, use Kotlin's standard `mutableSetOf`, `distinct`, or
`distinctBy`.

## A Mutated Object Is Still Found

Identity collections are insensitive to changes in an object's structural hash code. Lookup is based
on the reference, so mutating properties that participate in `equals` or `hashCode` does not move
the entry.

## Null Keys And Values

`Ref(null)` is supported and identity collections can store null when their Kotlin type allows it.
All null references are the same null identity.

```kotlin
check(identitySetOf(null, null).size == 1)
```
