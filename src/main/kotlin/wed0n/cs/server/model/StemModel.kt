package wed0n.cs.server.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.io.Serializable

data class SteamResponse<T>(
    var response: T
) : Serializable

data class PlayerSummary(
    var players: ArrayList<SteamUser>
) : Serializable

@JsonIgnoreProperties(ignoreUnknown = true)
data class SteamUser(
    var steamid: Long
) : Comparable<SteamUser>, Serializable {
    var personaname: String = ""
    var avatarhash: String = ""
    override fun compareTo(other: SteamUser): Int = if (other.steamid < this.steamid) 1 else -1
}

fun stemIdToSteamId64(steamId: String): Long {
    val tmp = steamId.split("STEAM_")[1]
    val nums = tmp.split(":").map { it.toLong() }
    return 0x110000100000000 or (nums[2] shl 1) or (nums[1]) //SteamId编码规则: https://developer.valvesoftware.com/wiki/SteamID
}
