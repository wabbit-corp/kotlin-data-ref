package one.wabbit.data

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
