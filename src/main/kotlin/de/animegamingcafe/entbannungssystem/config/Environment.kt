package de.animegamingcafe.entbannungssystem.config

import kotlin.io.path.Path
import kotlin.io.path.exists
import java.nio.file.Path

object Environment {
    val folder: Path = Path("data")
    val logbackConfigPath: Path = folder.resolve("logback.xml")

    val isDev: Boolean = Path(".dev").exists()
}