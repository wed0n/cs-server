package wed0n.cs.server.handler

import com.fasterxml.jackson.databind.JsonNode
import jakarta.annotation.Resource
import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketSession
import wed0n.cs.server.dto.ServerMessage
import wed0n.cs.server.service.UserService
import wed0n.cs.server.util.sendOne

@Component
class RefreshUsersHandler : MessageHandler {
    @Resource
    lateinit var userService: UserService

    companion object {
        val type = "REFRESH_USERS"
    }

    override fun getType(): String = type

    override fun handle(session: WebSocketSession, data: JsonNode) {
        val loginUsers = userService.getAllLoinUsers()
        val result = ServerMessage(type, loginUsers)
        sendOne(session, result)
    }
}