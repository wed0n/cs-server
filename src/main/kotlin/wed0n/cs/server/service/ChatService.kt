package wed0n.cs.server.service

import org.springframework.stereotype.Service
import org.springframework.web.socket.WebSocketSession
import wed0n.cs.server.dto.ChatMessageDTO
import wed0n.cs.server.dto.ServerMessage
import wed0n.cs.server.handler.NewChatHandler
import wed0n.cs.server.handler.GetAllChatsHandler
import wed0n.cs.server.handler.sessionMap
import wed0n.cs.server.util.sendAll
import wed0n.cs.server.util.sendOne
import java.util.concurrent.ConcurrentLinkedDeque

data class ChatMessage(
    val time: Long,
    val steamId: Long,
    val content: String
)

val chatMessageQueue = ConcurrentLinkedDeque<ChatMessage>()

interface ChatService {
    fun addChatMessage(steamId: Long, content: String)
    fun getAllChats(session: WebSocketSession)
}

@Service
class ChatServiceImpl : ChatService {
    override fun addChatMessage(steamId: Long, content: String) {
        while (chatMessageQueue.size >= 100) { //最多存100条消息
            chatMessageQueue.poll()
        }
        val message = ChatMessage(System.currentTimeMillis(), steamId, content)
        chatMessageQueue.add(message)
        val serverMessage = ServerMessage(NewChatHandler.type, toMessageDTO(message))
        sendAll(serverMessage)
    }

    override fun getAllChats(session: WebSocketSession) {
        val messages = ArrayList<ChatMessageDTO>()
        for (message in chatMessageQueue) {
            messages.add(toMessageDTO(message))
        }
        val serverMessage = ServerMessage(GetAllChatsHandler.type, messages)
        sendOne(session, serverMessage)
    }

    private fun toMessageDTO(message: ChatMessage): ChatMessageDTO {
        var name = "系统消息"
        if (message.steamId != 0L) {
            name = sessionMap[message.steamId]!!.steamUser.personaname
        }
        return ChatMessageDTO(message.time, name, message.content)
    }

}