package wed0n.csgo.server

data class SteamUser(
    var steamId64: Long
) : Comparable<SteamUser> {
    var name: String = ""
    var avatar: String = ""

    override fun compareTo(other: SteamUser): Int = if (other.steamId64 < this.steamId64) 1 else -1
}

fun stemIdToSteamId64(steamId: String): Long {
    val tmp = steamId.split("STEAM_")[1]
    val nums = tmp.split(":").map { it.toLong() }
    return 0x110000100000000 or (nums[2] shl 1) or (nums[1]) //SteamId编码规则: https://developer.valvesoftware.com/wiki/SteamID
}