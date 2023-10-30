package wed0n.cs.server.service

import org.springframework.stereotype.Service
import org.springframework.web.socket.WebSocketSession
import wed0n.cs.server.dto.ServerMessage
import wed0n.cs.server.util.sendAll
import wed0n.cs.server.util.sendOne

interface LaunchService {
    fun setConfig(serverTarget: String, launchCSGO: Boolean)
    fun sendConfig(session: WebSocketSession)
    fun launch()
}

data class LaunchConfig(
    var address: String = "",
    var csgo: Boolean = false
)

@Service
class LaunchServiceImpl : LaunchService {
    private val launchConfig = LaunchConfig()
    override fun setConfig(serverTarget: String, launchCSGO: Boolean) {
        launchConfig.address = serverTarget
        launchConfig.csgo = launchCSGO
        val serverMessage = ServerMessage("LAUNCH_CONFIG", launchConfig)
        sendAll(serverMessage)
    }

    override fun sendConfig(session: WebSocketSession) {
        val serverMessage = ServerMessage("LAUNCH_CONFIG", launchConfig)
        sendOne(session, serverMessage)
    }

    override fun launch() {
        val serverMessage=ServerMessage("LAUNCH","")
        sendAll(serverMessage)
    }
}