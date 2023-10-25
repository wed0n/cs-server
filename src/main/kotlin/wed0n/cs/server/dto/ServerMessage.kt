package wed0n.cs.server.dto

import java.io.Serializable

data class ServerMessage<T>(
    var type: String,
    var data: T,
    var code: Long = 0
) : Serializable