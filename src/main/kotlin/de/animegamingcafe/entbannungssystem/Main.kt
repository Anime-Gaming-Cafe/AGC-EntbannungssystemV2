package de.animegamingcafe.entbannungssystem

import de.animegamingcafe.entbannungssystem.config.Config
import de.animegamingcafe.entbannungssystem.config.Environment
import de.animegamingcafe.entbannungssystem.config.listToCollection
import dev.reformator.stacktracedecoroutinator.jvm.DecoroutinatorJvmApi
import io.github.freya022.botcommands.api.core.BotCommands
import io.github.freya022.botcommands.api.core.config.DevConfig
import io.github.oshai.kotlinlogging.KotlinLogging
import java.lang.management.ManagementFactory
import kotlin.io.path.absolutePathString
import kotlin.system.exitProcess
import ch.qos.logback.classic.ClassicConstants as LogbackConstants

private val logger by lazy { KotlinLogging.logger {} }

private const val mainPackageName = "de.animegamingcafe.entbannungssystem"
const val botName = "AGC Utils v3"



object Main {
    @JvmStatic
    fun main(args: Array<out String>) {
        try {
            System.setProperty(LogbackConstants.CONFIG_FILE_PROPERTY, Environment.logbackConfigPath.absolutePathString())
            logger.info { "Loading logback configuration at ${Environment.logbackConfigPath.absolutePathString()}" }

            if ("-XX:+AllowEnhancedClassRedefinition" in ManagementFactory.getRuntimeMXBean().inputArguments) {
                logger.info { "Skipping stacktrace-decoroutinator as enhanced hotswap is active" }
            } else if ("--no-decoroutinator" in args) {
                logger.info { "Skipping stacktrace-decoroutinator as --no-decoroutinator is specified" }
            } else {
                DecoroutinatorJvmApi.install()
            }

            val config = Config.instance

            BotCommands.create {
                disableExceptionsInDMs = Environment.isDev



                addPredefinedOwners(listToCollection(config.main.botOwnerIds))

                addSearchPath(mainPackageName)

                textCommands {
                    usePingAsPrefix = true
                }

                applicationCommands {
                    @OptIn(DevConfig::class)
                    disableAutocompleteCache = Environment.isDev
                    fileCache {
                        @OptIn(DevConfig::class)
                        checkOnline = Environment.isDev
                    }

                }

                components {
                    enable = true
                }
            }

            logger.info { "Loaded bot" }
        } catch (e: Exception) {
            logger.error(e) { "Unable to start the bot" }
            exitProcess(1)
        }
    }
}
