@file:OptIn(kotlin.experimental.ExperimentalNativeApi::class)

package one.wabbit.data

import kotlin.native.identityHashCode

internal actual fun platformIdentityHashCode(value: Any?): Int = value?.identityHashCode() ?: 0

internal actual fun platformClassName(value: Any): String =
    value::class.simpleName ?: value::class.qualifiedName ?: value::class.toString()
