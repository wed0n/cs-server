@file:JvmName("LaunchController")

package wed0n.cs.server.controller

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import wed0n.cs.server.service.LaunchService

@RestController
class LaunchController {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    @Value("\${steam.webAPIKey}")
    private lateinit var key: String

    @Autowired
    private lateinit var launchService: LaunchService

    @GetMapping("/setLaunchConfig")
    fun setLaunchConfig(key: String, address: String, csgo: Boolean): String {
        if (this.key != key) {
            logger.error("wrong key $key")
            return "key error"
        }
        launchService.setConfig(address, csgo)
        logger.info("成功设置服务器地址为: $address")
        return "Succeed set server address to $address"
    }

    @GetMapping("/launch")
    fun launchCSGO(key: String): String {
        if (this.key != key) {
            logger.error("wrong key $key")
            return "key error"
        }
        launchService.launch()
        return "Succeed launch"
    }
}