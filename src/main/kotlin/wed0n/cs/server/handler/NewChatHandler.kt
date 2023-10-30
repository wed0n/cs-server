package wed0n.cs.server.handler

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketSession
import wed0n.cs.server.service.ChatService
import wed0n.cs.server.util.objectMapper

@Component
class NewChatHandler : MessageHandler {
    @Autowired
    private lateinit var chatService: ChatService

    companion object {
        const val type = "NEW_CHAT"
    }

    override fun getType(): String {
        return type
    }

    override fun handle(session: WebSocketSession, data: JsonNode) {
        val steamId = sessionIdToSteamIdMap[session.id]!!
        try {
            val content = objectMapper.convertValue(data, String::class.java)
            chatService.addChatMessage(steamId, content)
        } catch (e: Throwable) {
            e.printStackTrace()
            session.close()
        }
    }
}

@Component
class GetAllChatsHandler : MessageHandler {
    @Autowired
    private lateinit var chatService: ChatService

    companion object {
        const val type = "REFRESH_CHATS"
    }

    override fun getType(): String {
        return type
    }

    override fun handle(session: WebSocketSession, data: JsonNode) {
        try {
            chatService.getAllChats(session)
        } catch (e: Throwable) {
            e.printStackTrace()
            session.close()
        }
    }
}
