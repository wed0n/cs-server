package wed0n.csgo.server.config

import jakarta.annotation.Resource
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.WebSocketMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry
import org.springframework.web.util.UriComponentsBuilder
import java.util.concurrent.ConcurrentHashMap

@Configuration
@EnableWebSocket
class WebSocketConfig : WebSocketConfigurer {
    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(csWebSocketHandler(), "/").setAllowedOrigins("*")
    }

    @Bean
    fun csWebSocketHandler(): CSWebSocketHandler = CSWebSocketHandler()

    @Bean
    fun userMap(): ConcurrentHashMap<String, String> = ConcurrentHashMap()
}

class CSWebSocketHandler : WebSocketHandler {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    @Resource
    lateinit var userMap: ConcurrentHashMap<String, String>

    override fun afterConnectionEstablished(session: WebSocketSession) {
        val uri = session.uri!!
        val map = UriComponentsBuilder.fromUri(uri).build().queryParams
        val steamid = map["steamid"]
        if (steamid == null || steamid.size != 1 || steamid[0] == null || steamid[0] == "") {
            session.close()
            logger.error("非法访问 {}", session)
            return
        }
        logger.info("连接已建立 {}", steamid[0])
    }

    override fun handleMessage(session: WebSocketSession, message: WebSocketMessage<*>) {
        logger.info("{}收到消息{}", session, message.payload)
    }

    override fun handleTransportError(session: WebSocketSession, exception: Throwable) {
        logger.error("转换出错")
    }

    override fun afterConnectionClosed(session: WebSocketSession, closeStatus: CloseStatus) {
        userMap.remove(session.id)
        logger.info("{}连接关闭{}", session, closeStatus)
    }

    override fun supportsPartialMessages(): Boolean = false
}