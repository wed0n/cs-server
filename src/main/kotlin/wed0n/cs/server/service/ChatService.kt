package wed0n.cs.server.service

import org.springframework.stereotype.Service
import org.springframework.web.socket.WebSocketSession
import wed0n.cs.server.dto.ChatMessageDTO
import wed0n.cs.server.dto.ServerMessage
import wed0n.cs.server.handler.GetAllChatsHandler
import wed0n.cs.server.handler.NewChatHandler
import wed0n.cs.server.handler.sessionMap
import wed0n.cs.server.model.ChatMessageModel
import wed0n.cs.server.model.LoginMessageModel
import wed0n.cs.server.util.sendAll
import wed0n.cs.server.util.sendOne
import java.util.concurrent.ConcurrentLinkedDeque


val chatMessageQueue = ConcurrentLinkedDeque<ChatMessageModel>()
val loginMessageQueue = ConcurrentLinkedDeque<LoginMessageModel>()

interface ChatService {
    fun addChatMessage(steamId: Long, content: String, time: Long = System.currentTimeMillis())
    fun getAllChats(session: WebSocketSession)
    fun newLoginMessage(loginMessage: LoginMessageModel)
    fun broadcastLoginMessage()
}

@Service
class ChatServiceImpl : ChatService {
    override fun addChatMessage(steamId: Long, content: String, time: Long) {
        while (chatMessageQueue.size >= 100) { //最多存100条消息
            chatMessageQueue.poll()
        }
        val message = ChatMessageModel(time, steamId, content)
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

    override fun newLoginMessage(loginMessage: LoginMessageModel) {
        loginMessageQueue.add(loginMessage)
    }

    override fun broadcastLoginMessage() {
        val iterator = loginMessageQueue.iterator()
        while (iterator.hasNext()) {
            val loginMessage = iterator.next()
            val sessionModel = sessionMap[loginMessage.steamId]
            if (sessionModel == null) {
                iterator.remove()
                return
            }
            val name = sessionModel.steamUser.personaname
            if (name != "") {
                addChatMessage(0, "$name 已连接", loginMessage.time)
                iterator.remove()
            }
        }
    }

    private fun toMessageDTO(message: ChatMessageModel): ChatMessageDTO {
        var name = "系统消息"
        if (message.steamId != 0L) {
            name = sessionMap[message.steamId]!!.steamUser.personaname
        }
        return ChatMessageDTO(message.time, name, message.content)
    }

}