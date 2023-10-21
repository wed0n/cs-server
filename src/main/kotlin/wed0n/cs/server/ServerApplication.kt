package wed0n.cs.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ImportRuntimeHints
import wed0n.cs.server.config.ServerRuntimeHintsRegistrar

@SpringBootApplication
@ImportRuntimeHints(ServerRuntimeHintsRegistrar::class)
class ServerApplication

fun main(args: Array<String>) {
    runApplication<ServerApplication>(*args)
}
