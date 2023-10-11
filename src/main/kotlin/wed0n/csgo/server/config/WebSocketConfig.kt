package wed0n.csgo.server.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import jakarta.annotation.Resource
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.WebSocketMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry
import org.springframework.web.util.UriComponentsBuilder
import wed0n.csgo.server.model.SessionModel
import wed0n.csgo.server.model.SteamUser
import wed0n.csgo.server.model.stemIdToSteamId64
import wed0n.csgo.server.service.LoginService
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
    fun sessionMap(): ConcurrentHashMap<Long, SessionModel> = ConcurrentHashMap() //Steam ID

    @Bean
    fun sessionIdToSteamIdMap(): ConcurrentHashMap<String, Long> = ConcurrentHashMap() //Session ID转为Steam ID的Map

    @Bean
    fun objectMapper() = jacksonObjectMapper()

    @Bean
    fun restTemplate(): RestTemplate = RestTemplate()
}

class CSWebSocketHandler : WebSocketHandler {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    @Resource
    lateinit var sessionMap: ConcurrentHashMap<Long, SessionModel>

    @Resource
    lateinit var sessionIdToSteamIdMap: ConcurrentHashMap<String, Long>

    @Resource
    lateinit var objectMapper: ObjectMapper

    @Resource
    lateinit var loginService: LoginService

    override fun afterConnectionEstablished(session: WebSocketSession) {
        val uri = session.uri!!
        val map = UriComponentsBuilder.fromUri(uri).build().queryParams
        val steamidList = map["steamid"]
        if (steamidList == null || steamidList.size != 1 || steamidList[0] == null || steamidList[0] == "") {
            session.close()
            logger.error("非法访问 {}", session)
            return
        }
        val steamId = steamidList[0]
        val steamID64 = stemIdToSteamId64(steamId)
        sessionMap[steamID64] = SessionModel(session, SteamUser(steamID64))
        sessionIdToSteamIdMap[session.id] = steamID64
        logger.info("连接已建立 {}", steamID64)
        loginService.register()
    }

    override fun handleMessage(session: WebSocketSession, message: WebSocketMessage<*>) {
        logger.info("{}收到消息{}", session, message.payload)
    }

    override fun handleTransportError(session: WebSocketSession, exception: Throwable) {
        logger.error("转换出错")
    }

    override fun afterConnectionClosed(session: WebSocketSession, closeStatus: CloseStatus) {
        val sessionId = session.id
        val steamId64 = sessionIdToSteamIdMap[sessionId]
        sessionIdToSteamIdMap.remove(sessionId)
        sessionMap.remove(steamId64)
        logger.info("{} {} 退出登录 {}", session, steamId64, closeStatus)
    }

    override fun supportsPartialMessages(): Boolean = false
}