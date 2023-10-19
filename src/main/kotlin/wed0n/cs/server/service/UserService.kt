package wed0n.cs.server.service

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.annotation.Resource
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import wed0n.cs.server.dto.ServerMessage
import wed0n.cs.server.handler.RefreshUsersHandler
import wed0n.cs.server.handler.sessionMap
import wed0n.cs.server.model.PlayerSummary
import wed0n.cs.server.model.SteamResponse
import wed0n.cs.server.model.SteamUser
import wed0n.cs.server.util.sendAll

interface UserService {
    fun broadcastLoginUsers()
    fun refreshLoginUsers()
    fun getAllLoinUsers(): ArrayList<SteamUser>
}

data class BlockingStatus(
    var isBlocking: Boolean = false,
    var isNew: Boolean = false
)

@Service
class UserServiceImpl : UserService {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    @Resource
    lateinit var restTemplate: RestTemplate

    @Resource
    lateinit var objectMapper: ObjectMapper

    @Value("\${steam.webAPIKey}")
    private lateinit var webAPIKey: String

    @Volatile
    var refreshingStatus = BlockingStatus()

    @Volatile
    var broadcastingStatus = BlockingStatus()

    override fun refreshLoginUsers() {
        synchronized(refreshingStatus) {
            if (refreshingStatus.isBlocking) {
                //如果阻塞，则标记有新的请求
                refreshingStatus.isNew = true
                logger.info("新登录")
                return
            }
            refreshingStatus.isBlocking = true
        }
        do {
            logger.info("获取头像中")

            synchronized(refreshingStatus) {
                refreshingStatus.isNew = false
            }

            val uriComponentsBuilder =
                UriComponentsBuilder.fromUriString("https://api.steampowered.com/ISteamUser/GetPlayerSummaries/v2/?key=${webAPIKey}")
            //拼接steamIds
            val stringBuilder = StringBuilder()
            for (item in sessionMap) {
                stringBuilder.append("${item.value.steamUser.steamid},")
            }
            stringBuilder.setLength(stringBuilder.length - 1)

            try {
                //获取用户信息请求
                uriComponentsBuilder.queryParam("steamids", stringBuilder.toString())
                val uri = uriComponentsBuilder.build().toUri()
                val response = restTemplate.getForObject(
                    uri,
                    String::class.java
                )
                val result = objectMapper.readValue(
                    response,
                    object : TypeReference<SteamResponse<PlayerSummary>>() {})

                val players = result.response.players
                for (item in players) {
                    val steamUser = sessionMap[item.steamid]!!.steamUser
                    steamUser.personaname = item.personaname
                    steamUser.avatarhash = item.avatarhash
                }
            } catch (e: Throwable) {
                //获取异常后，重试
                synchronized(refreshingStatus) {
                    refreshingStatus.isNew = true
                }
            }
            broadcastLoginUsers()
            synchronized(refreshingStatus) {
                if (!refreshingStatus.isNew) {
                    refreshingStatus.isBlocking = false
                    logger.info("完成刷新用户")
                    return
                }
            }
        } while (true)
    }

    override fun broadcastLoginUsers() {
        synchronized(broadcastingStatus) {
            if (broadcastingStatus.isBlocking) {
                broadcastingStatus.isNew = true
                logger.info("新广播登录用户请求")
                return
            }
            broadcastingStatus.isBlocking = true
        }
        do {
            synchronized(broadcastingStatus) {
                broadcastingStatus.isNew = false
            }
            logger.info("开始广播登录用户")
            try {
                val loginUsers = getAllLoinUsers()
                val message = ServerMessage(RefreshUsersHandler.type, loginUsers)
                sendAll(message)
            } catch (e: Throwable) {
                synchronized(broadcastingStatus) {
                    broadcastingStatus.isNew = true
                }
            }
            synchronized(broadcastingStatus) {
                if (!broadcastingStatus.isNew) {
                    broadcastingStatus.isBlocking = false
                    logger.info("完成广播登录用户")
                    return
                }
            }
        } while (true)
    }

    override fun getAllLoinUsers(): ArrayList<SteamUser> {
        val result: ArrayList<SteamUser> = ArrayList()
        for (item in sessionMap) {
            val steamUser = item.value.steamUser
            if (steamUser.personaname != "")
                result.add(item.value.steamUser)
        }
        result.sort()
        return result
    }

}