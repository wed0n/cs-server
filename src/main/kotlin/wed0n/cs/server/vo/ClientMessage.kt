package wed0n.cs.server.vo

import java.io.Serializable

data class ClientMessage<T>(
    var type: String,
    var data: T
) : Serializable