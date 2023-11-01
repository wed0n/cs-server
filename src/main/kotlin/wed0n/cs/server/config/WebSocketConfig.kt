package wed0n.cs.server.config

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry
import wed0n.cs.server.handler.CSWebSocketHandler
import java.time.Duration


@Configuration
@EnableWebSocket
class WebSocketConfig : WebSocketConfigurer {
    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(csWebSocketHandler(), "/").setAllowedOrigins("*")
    }

    @Bean
    fun csWebSocketHandler(): CSWebSocketHandler = CSWebSocketHandler()

    @Bean
    fun objectMapper() = jacksonObjectMapper()

    @Bean
    fun restTemplate(): RestTemplate {
        val duration = Duration.ofSeconds(5)
        return RestTemplateBuilder()
            .setConnectTimeout(duration)
            .setReadTimeout(duration)
            .build()
    }
}
