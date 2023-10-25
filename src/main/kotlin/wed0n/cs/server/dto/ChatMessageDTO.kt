package wed0n.cs.server.dto

import java.io.Serializable

data class ChatMessageDTO(
    val time: Long,
    val name: String,
    val content: String
) : Serializable