package wed0n.csgo.server.service

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.annotation.Resource
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import wed0n.csgo.server.model.PlayerSummary
import wed0n.csgo.server.model.SessionModel
import wed0n.csgo.server.model.SteamResponse
import java.util.concurrent.ConcurrentHashMap

interface LoginService {
    fun register()
}

@Service
class LoginServiceImpl : LoginService {
    @Resource
    lateinit var restTemplate: RestTemplate

    @Resource
    lateinit var objectMapper: ObjectMapper

    @Resource
    lateinit var sessionMap: ConcurrentHashMap<Long, SessionModel>

    @Value("\${steam.webAPIKey}")
    private lateinit var webAPIKey: String

    override fun register() {
        val uriComponentsBuilder =
            UriComponentsBuilder.fromUriString("https://api.steampowered.com/ISteamUser/GetPlayerSummaries/v2/?key=${webAPIKey}")
        //拼接steamIds
        val stringBuilder = StringBuilder()
        for (item in sessionMap) {
            stringBuilder.append("${item.value.steamUser.steamid},")
        }
        stringBuilder.setLength(stringBuilder.length - 1)

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
    }
}