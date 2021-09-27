package magma.global.task.data.remote.controller

@Suppress("unused")
data class ResponseWrapper<T>(val code : Int, val message:String, val data: T)