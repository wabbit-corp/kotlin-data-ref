package one.wabbit.data

/**
 * Represents a reference to a value of type A. This class holds the reference and provides
 * identity-based equality and hash code generation using the underlying value's identity.
 *
 * @param A The type of the value referenced.
 * @property value The value held by the reference.
 */
class Ref<out A>(val value: A) {
    override fun hashCode(): Int =
        System.identityHashCode(value)

    override fun equals(other: Any?): Boolean =
        other is Ref<*> && value === other.value

    override fun toString(): String {
        if (value === null) return "Ref[null]"
        val hash = Integer.toHexString(System.identityHashCode(value))
        return "Ref[${value.javaClass.simpleName}@$hash]"
    }
}