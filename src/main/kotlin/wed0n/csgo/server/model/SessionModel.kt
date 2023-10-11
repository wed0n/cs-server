package wed0n.csgo.server.model

import org.springframework.web.socket.WebSocketSession

data class SessionModel(
    var session: WebSocketSession,
    var steamUser: SteamUser
)