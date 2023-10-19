package wed0n.cs.server.handler

import jakarta.annotation.PostConstruct
import jakarta.annotation.Resource
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import org.springframework.web.util.UriComponentsBuilder
import wed0n.cs.server.model.SessionModel
import wed0n.cs.server.model.SteamUser
import wed0n.cs.server.model.stemIdToSteamId64
import wed0n.cs.server.service.ChatService
import wed0n.cs.server.service.UserService
import wed0n.cs.server.util.objectMapper
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Consumer


val sessionMap: ConcurrentHashMap<Long, SessionModel> = ConcurrentHashMap()
val sessionIdToSteamIdMap: ConcurrentHashMap<String, Long> = ConcurrentHashMap() //Session ID转为Steam ID的Map

class CSWebSocketHandler : TextWebSocketHandler() {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    @Resource
    private lateinit var applicationContext: ApplicationContext

    @Resource
    private lateinit var userService: UserService

    @Resource
    private lateinit var chatService: ChatService

    private val handlers = HashMap<String, MessageHandler>()

    @PostConstruct
    fun init() {
        applicationContext.getBeansOfType(MessageHandler::class.java).values // 获得所有 MessageHandler Bean
            .forEach(Consumer { messageHandler: MessageHandler ->
                handlers[messageHandler.getType()] = messageHandler
            })
        logger.info("注册的Handler数量为: {}", handlers.size)
    }

    override fun afterConnectionEstablished(session: WebSocketSession) {
        val uri = session.uri!!
        val map = UriComponentsBuilder.fromUri(uri).build().queryParams
        val steamidList = map["steamid"]
        if ((steamidList == null) || (steamidList.size != 1) || (steamidList[0] == null) || (steamidList[0] == "")) {
            session.close()
            logger.error("非法访问 {}", session)
            return
        }
        val steamId = steamidList[0]
        val steamID64 = stemIdToSteamId64(steamId)
        sessionMap[steamID64] = SessionModel(session, SteamUser(steamID64))
        sessionIdToSteamIdMap[session.id] = steamID64
        logger.info("连接已建立 {}", steamID64)
        userService.refreshLoginUsers()
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        logger.info("{}收到消息{}", session, message.payload)
        try {
            val jsonNode = objectMapper.readTree(message.payload)
            val type = objectMapper.convertValue(jsonNode.get("type"), String::class.java)
            val dataNode = jsonNode.get("data")
            val handler = handlers[type]!!
            handler.handle(session, dataNode)
        } catch (e: Throwable) {
            e.printStackTrace()
            session.close()
        }
    }

    override fun handleTransportError(session: WebSocketSession, exception: Throwable) {
        logger.error("{} 转换出错", session)
    }

    override fun afterConnectionClosed(session: WebSocketSession, closeStatus: CloseStatus) {
        val sessionId = session.id
        val steamId64 = sessionIdToSteamIdMap[sessionId]
        val sessionModel = sessionMap[steamId64]

        //断开连接后删除连接信息
        sessionIdToSteamIdMap.remove(sessionId)
        sessionMap.remove(steamId64)

        if (sessionModel != null) {
            chatService.addChatMessage(0, "${sessionModel.steamUser.personaname} 断开了连接")
        }

        userService.broadcastLoginUsers()
        logger.info("退出登录 {} {}", steamId64, closeStatus)
    }

    override fun supportsPartialMessages(): Boolean = false
}