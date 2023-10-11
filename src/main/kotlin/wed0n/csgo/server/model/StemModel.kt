package wed0n.csgo.server.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

data class SteamResponse<T>(
    var response: T
)

data class PlayerSummary(
    var players: ArrayList<SteamUser>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SteamUser(
    var steamid: Long,
    var personaname: String,
    var avatar: String
)
