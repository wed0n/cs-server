package wed0n.cs.server.model

import org.springframework.web.socket.WebSocketSession

data class SessionModel(
    var session: WebSocketSession,
    var steamUser: SteamUser
)