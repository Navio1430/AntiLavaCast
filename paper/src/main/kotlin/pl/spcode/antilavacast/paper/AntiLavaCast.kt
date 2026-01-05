package pl.spcode.antilavacast.paper

import org.bukkit.plugin.java.JavaPlugin
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class AntiLavaCast: JavaPlugin() {

  val logger: Logger = LoggerFactory.getLogger(AntiLavaCast::class.java)

  override fun onEnable() {
    logger.info("AntiLavaCast enabled!")
  }

  override fun onDisable() {
    logger.info("Goodbye!")
  }
}