package org.babyfish.jimmer.sql.kt.common

import kotlin.test.expect

fun assertContent(expected: String, actual: Any) {
    expect(expected.replace("--->", "").replace("\r", "").replace("\n", "")) {
        actual.toString()
    }
}