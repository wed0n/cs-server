package wed0n.cs.server.model

import org.springframework.web.socket.WebSocketSession
import java.io.Serializable

data class SessionModel(
    var session: WebSocketSession,
    var steamUser: SteamUser
) : Serializable