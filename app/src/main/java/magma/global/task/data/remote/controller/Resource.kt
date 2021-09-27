package magma.global.task.data.remote.controller

import magma.global.task.data.remote.controller.ErrorManager


// A generic class that contains data and status about loading this data.
sealed class Resource<T>(
    val response: Any?,
) {
    class Success<T>(response: T) : Resource<T>(response)
    class Loading<T>(data: T? = null) : Resource<T>(data)
    class DataError<T>(errorResponse: ErrorManager) : Resource<T>(errorResponse)
    class Exception<T>(errorMessage: String) : Resource<T>(errorMessage)
}