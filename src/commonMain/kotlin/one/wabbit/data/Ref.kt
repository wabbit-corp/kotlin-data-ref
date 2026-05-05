package one.wabbit.data

/**
 * Identity wrapper for a value.
 *
 * [Ref] compares the wrapped [value] by reference identity (`===`) instead of structural equality.
 * Its hash code is the platform identity hash of [value], so it can be used as a key when ordinary
 * `equals`/`hashCode` semantics are not appropriate.
 *
 * Null values are supported. Two `Ref(null)` instances compare equal because their wrapped values
 * are the same null reference.
 *
 * @param A The type of the wrapped value.
 */
class Ref<out A>(
    /** The value whose reference identity is used for equality and hashing. */
    val value: A,
) {
    /**
     * Returns the platform identity hash code of [value].
     *
     * This deliberately ignores [value]'s own `hashCode` implementation.
     */
    override fun hashCode(): Int = platformIdentityHashCode(value)

    /**
     * Compares this wrapper with [other] by wrapped-value identity.
     *
     * Two [Ref] instances are equal when their [value] references are identical.
     */
    override fun equals(other: Any?): Boolean =
        this === other || (other is Ref<*> && value === other.value)

    /**
     * Returns a diagnostic representation of the wrapped value.
     *
     * For non-null values, the output includes the platform class name and unsigned hexadecimal
     * identity hash. For null, it returns `Ref[null]`. The exact class-name spelling is platform
     * dependent and should not be parsed.
     */
    override fun toString(): String {
        val v = value ?: return "Ref[null]"
        val cls = platformClassName(v as Any)
        val hex = platformIdentityHashCode(v).toUInt().toString(16)
        return "Ref[$cls@$hex]"
    }
}
