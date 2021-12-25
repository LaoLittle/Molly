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
    val name: String by value("茉莉")

    @ValueDescription("群聊呼叫机器人后持续回复的次数，默认为0")
    val replyTimes: Int by value(0)

    @ValueDescription("是否引用回复")
    val doQuoteReply: Boolean by value(false)

    @ValueDescription("控制台显示服务器返回的数据")
    val doPrintResultsOnConsole: Boolean by value(false)

    @ValueDescription(
        """
        私聊不触发聊天关键字
        主要是防止私聊进行功能调用的时候触发此聊天
        消息中包含就不回复
        支持正则
    """
    )
    val dontReply: Set<String> by value(setOf("/", "色图"))

    @ValueDescription("机器人被呼叫但是消息没有任何内容的回应")
    val defaultReply: Set<String> by value(setOf("？", "怎么", "怎么了", "什么？", "在", "嗯？"))

    @ValueDescription("机器人被呼叫，消息没有任何内容且发送人一直没有说话的回应")
    val timeoutReply: Set<String> by value(setOf("没事我就溜了", "emmmmm", "......", "溜了", "？"))

    @ValueDescription("是否开启私聊")
    val enablePrivateChatReply: Boolean by value(true)
}