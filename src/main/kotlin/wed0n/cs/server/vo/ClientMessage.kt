package wed0n.cs.server.vo

data class ClientMessage<T>(
    var type: String,
    var data: T
)