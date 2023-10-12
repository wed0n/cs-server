package wed0n.cs.server.handler

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.web.socket.WebSocketSession

interface MessageHandler {
    fun getType(): String
    fun handle(session: WebSocketSession, data: JsonNode)
}