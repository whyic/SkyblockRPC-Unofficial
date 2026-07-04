package me.owdding.skyblockrpc

import me.owdding.skyblockrpc.rpc.DiscordButton
import tech.thatgravyboat.skyblockapi.helpers.McPlayer

enum class Buttons(val label: String, private val urlProvider: () -> String) {
    DISCORD("Discord", { "https://discord.gg/uY5J3RwmTX" }),
    SKY_CRYPT("SkyCrypt", { "https://sky.shiiyu.moe/stats/${McPlayer.name}" }),
    ELITE_BOT("EliteSkyBlock", { "https://eliteskyblock.com/@${McPlayer.name}" }),
    ;

    val url: String by lazy { "${urlProvider()}?utm_source=SkyBlockRPC" }

    fun toButton() = DiscordButton(label, url) // Now perfectly matches the new name!

    override fun toString() = label
}
