// SPDX-License-Identifier: AGPL-3.0-or-later

package one.wabbit.data

internal actual fun platformIdentityHashCode(value: Any?): Int = System.identityHashCode(value)

internal actual fun platformClassName(value: Any): String =
    value::class.simpleName ?: value.javaClass.name
