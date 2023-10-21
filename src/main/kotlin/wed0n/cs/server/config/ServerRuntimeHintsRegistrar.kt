package wed0n.cs.server.config

import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar
import wed0n.cs.server.dto.ServerMessage

class ServerRuntimeHintsRegistrar : RuntimeHintsRegistrar {
    override fun registerHints(hints: RuntimeHints, classLoader: ClassLoader?) {
        hints.resources().registerPattern("private.crt")
        hints.resources().registerPattern("private.key")

        hints.serialization().registerType(ServerMessage::class.java)
    }
}