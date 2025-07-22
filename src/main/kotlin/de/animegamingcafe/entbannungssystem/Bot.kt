package de.animegamingcafe.entbannungssystem


import de.animegamingcafe.entbannungssystem.config.Config
import io.github.freya022.botcommands.api.core.JDAService
import io.github.freya022.botcommands.api.core.events.BReadyEvent
import io.github.freya022.botcommands.api.core.service.annotations.BService
import io.github.oshai.kotlinlogging.KotlinLogging
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.hooks.IEventManager
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder
import net.dv8tion.jda.api.utils.ChunkingFilter
import net.dv8tion.jda.api.utils.MemberCachePolicy
import net.dv8tion.jda.api.utils.cache.CacheFlag

private val logger by lazy { KotlinLogging.logger {} }


@BService
class Bot(private val config: Config) : JDAService() {
    override val intents: Set<GatewayIntent> =
        defaultIntents + GatewayIntent.MESSAGE_CONTENT


    override val cacheFlags: Set<CacheFlag> = setOf(
    )

    override fun createJDA(event: BReadyEvent, eventManager: IEventManager) {
        val shardManager = DefaultShardManagerBuilder.createLight(config.instance.main.token, intents).apply {
            enableCache(cacheFlags)
            setMemberCachePolicy(MemberCachePolicy.ALL)
            setChunkingFilter(ChunkingFilter.ALL)
            setStatus(OnlineStatus.DO_NOT_DISTURB)
            setActivityProvider { Activity.playing("Booting up...") }
            setEventManagerProvider { eventManager }
        }.build()
        logger.info { "Booting up ${shardManager.shards.size} shards" }
    }

}
