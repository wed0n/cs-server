package wed0n.cs.server.model

import java.io.Serializable

data class ChatMessageModel(
    val time: Long,
    val steamId: Long,
    val content: String
) : Serializable

data class LoginMessageModel(
    val time: Long,
    val steamId: Long
) : Serializable
