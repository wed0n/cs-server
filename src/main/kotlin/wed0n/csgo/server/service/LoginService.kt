package wed0n.csgo.server.service

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.annotation.Resource
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import wed0n.csgo.server.SteamUser
import wed0n.csgo.server.model.PlayerSummary
import wed0n.csgo.server.model.SteamResponse

interface LoginService {
    fun register(steamUsers: ArrayList<SteamUser>)
}

@Service
class LoginServiceImpl : LoginService {
    @Resource
    lateinit var restTemplate: RestTemplate

    @Resource
    lateinit var objectMapper: ObjectMapper

    @Value("\${steam.webAPIKey}")
    private lateinit var webAPIKey: String

    override fun register(steamUsers: ArrayList<SteamUser>) {
        val uriComponentsBuilder =
            UriComponentsBuilder.fromUriString("https://api.steampowered.com/ISteamUser/GetPlayerSummaries/v2/?key=${webAPIKey}")
        //拼接steamIds
        val stringBuilder = StringBuilder()
        for (user in steamUsers) {
            stringBuilder.append("${user.steamId64},")
        }
        stringBuilder.setLength(stringBuilder.length - 1)

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
        steamUsers.clear()
        for (item in players) {
            val steamUser = SteamUser(item.steamid)
            steamUser.avatar = item.avatar.substring(32, 72)
            steamUser.name = item.personaname
            steamUsers.add(steamUser)
        }
        steamUsers.sort()
    }
}