package wed0n.cs.server.service

import org.springframework.stereotype.Service
import org.springframework.web.socket.WebSocketSession
import wed0n.cs.server.dto.ServerMessage
import wed0n.cs.server.model.LaunchConfigModel
import wed0n.cs.server.util.sendAll
import wed0n.cs.server.util.sendOne

interface LaunchService {
    fun setConfig(launchConfig: LaunchConfigModel)
    fun sendConfig(session: WebSocketSession)
    fun launch()
}

@Service
class LaunchServiceImpl : LaunchService {
    private var launchConfig = LaunchConfigModel()
    override fun setConfig(launchConfig: LaunchConfigModel) {
        this.launchConfig = launchConfig
        val serverMessage = ServerMessage("LAUNCH_CONFIG", launchConfig)
        sendAll(serverMessage)
    }

    override fun sendConfig(session: WebSocketSession) {
        val serverMessage = ServerMessage("LAUNCH_CONFIG", launchConfig)
        sendOne(session, serverMessage)
    }

    override fun launch() {
        val serverMessage = ServerMessage("LAUNCH", "")
        sendAll(serverMessage)
    }
}