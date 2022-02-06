package com.dontsu.composepokedex.util

sealed class Resource<T> (
    val data: T? = null,
    val message: String? = null
) {

    class Success<T> (data: T): Resource<T>(data) // 성공하면 data가 무조건 있으므로 T?가 아님
    class Error<T> (message: String, data: T? = null): Resource<T>(data, message)
//    class Loading<T> (data: T? = null): Resource<T>(data) part5 강좌에서 사용하지 않아도 된다고 하여 없앰. coil 라이브러리 내장함수를 사용하려는듯 함.

}
