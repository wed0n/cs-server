@file:JvmName("WebSocketUtil")

package wed0n.cs.server.util

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import wed0n.cs.server.dto.ServerMessage
import wed0n.cs.server.handler.sessionMap


val objectMapper = jacksonObjectMapper()

fun convertMessage(data: ServerMessage<*>): TextMessage {
    val json = objectMapper.writeValueAsString(data)
    return TextMessage(json)
}

fun sendOne(session: WebSocketSession, data: ServerMessage<*>) {
    session.sendMessage(convertMessage(data))
}

fun sendAll(data: ServerMessage<*>) {
    val message = convertMessage(data)
    for (item in sessionMap) {
        try {
            item.value.session.sendMessage(message)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }
}
