# User Guide

`kotlin-data-ref` is intentionally small. It exists for the cases where Kotlin's default structural
collection semantics are wrong for the job.

## Identity Versus Equality

Ordinary Kotlin collections use `equals` and `hashCode`.

```kotlin
data class Node(val id: Int)

val a = Node(1)
val b = Node(1)

check(a == b)
check(setOf(a, b).size == 1)
```

An identity set keeps both objects because they are distinct references:

```kotlin
import one.wabbit.data.identitySetOf

val identity = identitySetOf(a, b)
check(identity.size == 2)
```

## Identity Maps

Use `identityMap` when the key object itself is the identity:

```kotlin
import one.wabbit.data.identityMap

val values = identityMap<Node, String>()
values[a] = "first"
values[b] = "second"

check(values[a] == "first")
check(values[b] == "second")
```

This is useful in object graphs, AST traversals, memoization over mutable objects, and cycle
detection where structural equality can merge nodes that must stay separate.

The returned value is still a `MutableMap`. Key lookup, insertion, and removal use identity, while
the `entries` view follows the map-entry contract with identity matching for keys and structural
matching for values.

## Distinct By Identity

`distinctByIdentity` removes repeated references while preserving the first occurrence:

```kotlin
import one.wabbit.data.distinctByIdentity

val node = Node(1)
val result = listOf(node, Node(1), node).distinctByIdentity()

check(result.size == 2)
check(result.first() === node)
```

Use the sequence overload when the input is already a sequence and you want lazy filtering.
