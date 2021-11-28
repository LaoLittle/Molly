package org.laolittle.plugin.molly

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

object MollyConfig : AutoSavePluginConfig("MollyConfig") {
    @ValueDescription("在此填入你的Api-Key")
    val api_key: String by value("")

    @ValueDescription("在此填入你的Api-Secret")
    val api_secret: String by value("")

    @ValueDescription("你的机器人昵称")
    val Name: String by value("茉莉")

    @ValueDescription("群聊呼叫机器人后持续回复的次数，默认为0")
    val replyTimes: Int by value(0)
}