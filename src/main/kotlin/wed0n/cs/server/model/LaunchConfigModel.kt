package wed0n.cs.server.model

import java.io.Serializable

data class LaunchConfigModel(
    var address: String = "",
    var csgo: Boolean = false
) : Serializable