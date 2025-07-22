package de.animegamingcafe.entbannungssystem.config

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import io.github.freya022.botcommands.api.core.service.annotations.BService
import io.github.oshai.kotlinlogging.KotlinLogging
import java.nio.file.Path
import kotlin.io.path.*
import kotlin.system.exitProcess

private val logger = KotlinLogging.logger {}

fun String?.orEnvCount(key: String, stats: SourceCounter): String? {
    val env = System.getenv(key)
    return when {
        env != null -> {
            stats.env++
            env
        }
        this != null -> {
            stats.cfg++
            this
        }
        else -> null
    }
}

fun List<Long>?.orEnvLongList(key: String, stats: SourceCounter): List<Long>? {
    val env = System.getenv(key)
    return when {
        env != null -> {
            stats.env++
            try {
                env.split(",").map { it.trim().toLong() }
            } catch (e: NumberFormatException) {
                logger.error { "Invalid number in env var $key: $env" }
                exitProcess(272)
            }
        }
        this != null -> {
            stats.cfg++
            this
        }
        else -> null
    }
}


data class SourceCounter(var env: Int = 0, var cfg: Int = 0)

data class MainConfig(
    @SerializedName("Discord_Token") val token: String? = null,
    @SerializedName("BotOwnerId") val botOwnerIds: List<Long>? = null,
    @SerializedName("UnbanServerId") val unbanServerId: String? = null,
    @SerializedName("MainServerId") val mainServerId: String? = null,
    @SerializedName("MainGuildTeamRoleId") val mainGuildTeamRoleId: String? = null,
    @SerializedName("UnbanGuildTeamRoleId") val unbanGuildTeamRoleId: String? = null,
    @SerializedName("LogChannelId") val logChannelId: String? = null,
    @SerializedName("AppealRoleId") val appealRoleId: String? = null,
    @SerializedName("PingRoleId") val pingRoleId: String? = null,
    @SerializedName("HistoryChannelId") val historyChannelId: String? = null,
    @SerializedName("SyncIgnoredRoleList") val syncIgnoredRoleList: String? = null,
    @SerializedName("AbstimmungsChannelId") val abstimmungsChannelId: String? = null,
    @SerializedName("VoteCategoryChannelId") val voteCategoryChannelId: String? = null,
    @SerializedName("SperreRoleId") val sperreRoleId: String? = null,
    @SerializedName("SperreInfoChannelId") val sperreInfoChannelId: String? = null,
    @SerializedName("BearbeitetCategoryId") val bearbeitetCategoryId: String? = null,
    @SerializedName("BackupTranscriptPath") val backupTranscriptPath: String? = null,
    @SerializedName("BackupTranscriptUrl") val backupTranscriptUrl: String? = null,
)

data class EmbedConfig(
    @SerializedName("DefaultEmbedColor") val defaultEmbedColor: String? = "FFFFFF"
)

data class ModHQConfig(
    @SerializedName("BannSystemEnabled") val bannSystemEnabled: String = "false",
    @SerializedName("API_Key") val apiKey: String = "",
    @SerializedName("API_URL") val apiUrl: String = ""
)

data class DatabaseConfig(
    @SerializedName("DatabaseName") val name: String? = null,
    @SerializedName("DatabaseUser") val user: String? = null,
    @SerializedName("DatabasePassword") val password: String? = null,
    @SerializedName("DatabaseHost") val serverName: String? = null,
    @SerializedName("DatabasePort") val port: String? = null
) {
    val url: String
        get() = "jdbc:postgresql://${serverName.orEmpty()}:${port.orEmpty()}/${name.orEmpty()}"
}

data class RawConfig(
    @SerializedName("MainConfig") val main: MainConfig = MainConfig(),
    @SerializedName("EmbedConfig") val embed: EmbedConfig = EmbedConfig(),
    @SerializedName("ModHQConfig") val modHQ: ModHQConfig = ModHQConfig(),
    @SerializedName("Database") val database: DatabaseConfig = DatabaseConfig()
)

object Config {
    private val configFilePath: Path = Environment.folder.resolve("config.json")

    @get:BService
    val instance: RawConfig by lazy {
        val fileExists = configFilePath.exists()
        val counter = SourceCounter()

        val fromFile = if (fileExists) {
            logger.info { "Loading configuration file at ${configFilePath.absolutePathString()}" }
            Gson().fromJson(configFilePath.readText(), RawConfig::class.java)
        } else {
            logger.warn { "No config file found at ${configFilePath.absolutePathString()}, falling back to environment variables only." }
            RawConfig()
        }

        val final = RawConfig(
            main = MainConfig(
                token = fromFile.main.token.orEnvCount("DISCORD_TOKEN", counter),
                botOwnerIds = fromFile.main.botOwnerIds.orEnvLongList("BOT_OWNER_ID", counter),
                unbanServerId = fromFile.main.unbanServerId.orEnvCount("UNBAN_SERVER_ID", counter),
                mainServerId = fromFile.main.mainServerId.orEnvCount("MAIN_SERVER_ID", counter),
                mainGuildTeamRoleId = fromFile.main.mainGuildTeamRoleId.orEnvCount("MAIN_GUILD_TEAM_ROLE_ID", counter),
                unbanGuildTeamRoleId = fromFile.main.unbanGuildTeamRoleId.orEnvCount("UNBAN_GUILD_TEAM_ROLE_ID", counter),
                logChannelId = fromFile.main.logChannelId.orEnvCount("LOG_CHANNEL_ID", counter),
                appealRoleId = fromFile.main.appealRoleId.orEnvCount("APPEAL_ROLE_ID", counter),
                pingRoleId = fromFile.main.pingRoleId.orEnvCount("PING_ROLE_ID", counter),
                historyChannelId = fromFile.main.historyChannelId.orEnvCount("HISTORY_CHANNEL_ID", counter),
                syncIgnoredRoleList = fromFile.main.syncIgnoredRoleList.orEnvCount("SYNC_IGNORED_ROLE_LIST", counter),
                abstimmungsChannelId = fromFile.main.abstimmungsChannelId.orEnvCount("ABSTIMMUNGS_CHANNEL_ID", counter),
                voteCategoryChannelId = fromFile.main.voteCategoryChannelId.orEnvCount("VOTE_CATEGORY_CHANNEL_ID", counter),
                sperreRoleId = fromFile.main.sperreRoleId.orEnvCount("SPERRE_ROLE_ID", counter),
                sperreInfoChannelId = fromFile.main.sperreInfoChannelId.orEnvCount("SPERRE_INFO_CHANNEL_ID", counter),
                bearbeitetCategoryId = fromFile.main.bearbeitetCategoryId.orEnvCount("BEARBEITET_CATEGORY_ID", counter),
                backupTranscriptPath = fromFile.main.backupTranscriptPath.orEnvCount("BACKUP_TRANSCRIPT_PATH", counter),
                backupTranscriptUrl = fromFile.main.backupTranscriptUrl.orEnvCount("BACKUP_TRANSCRIPT_URL", counter)
            ),
            embed = EmbedConfig(
                defaultEmbedColor = fromFile.embed.defaultEmbedColor.orEnvCount("DEFAULT_EMBED_COLOR", counter)
            ),
            modHQ = ModHQConfig(
                bannSystemEnabled = System.getenv("BANN_SYSTEM_ENABLED")?.also { counter.env++ }
                    ?: fromFile.modHQ.bannSystemEnabled.also { counter.cfg++ },
                apiKey = System.getenv("MODHQ_API_KEY")?.also { counter.env++ }
                    ?: fromFile.modHQ.apiKey.also { counter.cfg++ },
                apiUrl = System.getenv("MODHQ_API_URL")?.also { counter.env++ }
                    ?: fromFile.modHQ.apiUrl.also { counter.cfg++ }
            ),
            database = DatabaseConfig(
                name = fromFile.database.name.orEnvCount("DATABASE_NAME", counter),
                user = fromFile.database.user.orEnvCount("DATABASE_USER", counter),
                password = fromFile.database.password.orEnvCount("DATABASE_PASSWORD", counter),
                serverName = fromFile.database.serverName.orEnvCount("DATABASE_HOST", counter),
                port = fromFile.database.port.orEnvCount("DATABASE_PORT", counter)
            )
        )

        logger.info { "Configuration loaded and merged." }
        logger.info { "→ Loaded settings: ${counter.env + counter.cfg} total" }
        logger.info { "→ From config.json: ${counter.cfg}" }
        logger.info { "→ From ENV:        ${counter.env}" }

        final
    }
}

fun listToCollection(list: List<Long>?): Collection<Long> {
    return list?.takeIf { it.isNotEmpty() } ?: emptyList()
}
