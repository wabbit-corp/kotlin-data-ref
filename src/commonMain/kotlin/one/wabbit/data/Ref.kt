package one.wabbit.data

/**
 * Represents a reference to a value of type A. This class holds the reference and provides
 * identity-based equality and hash code generation using the underlying value's identity.
 *
 * @param A The type of the value referenced.
 * @property value The value held by the reference.
 */
class Ref<out A>(val value: A) {
    override fun hashCode(): Int = platformIdentityHashCode(value)

    override fun equals(other: Any?): Boolean =
        this === other || (other is Ref<*> && value === other.value)

    override fun toString(): String {
        val v = value ?: return "Ref[null]"
        val cls = platformClassName(v as Any)
        val hex = platformIdentityHashCode(v).toUInt().toString(16)
        return "Ref[$cls@$hex]"
    }
}
