package wed0n.cs.server.config

import org.reflections.Reflections
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar
import java.io.Serializable

class ServerRuntimeHintsRegistrar : RuntimeHintsRegistrar {
    override fun registerHints(hints: RuntimeHints, classLoader: ClassLoader?) {
        hints.resources().registerPattern("private.crt")
        hints.resources().registerPattern("private.key")

        for (subPackage in arrayOf("model", "vo", "dto")) {
            val reflections = Reflections("wed0n.cs.server.$subPackage")
            val allClasses = reflections.getSubTypesOf(Serializable::class.java)
            for (item in allClasses) {
                println(item)
                hints.serialization().registerType(item)
            }
        }
    }
}