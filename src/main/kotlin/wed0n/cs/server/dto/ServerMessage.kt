package wed0n.cs.server.dto

data class ServerMessage<T>(
    var type: String,
    var data: T,
    var code: Long = 0
)