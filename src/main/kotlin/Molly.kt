package org.laolittle.plugin.molly

import kotlinx.coroutines.delay
import kotlinx.serialization.ExperimentalSerializationApi
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.contact.Contact.Companion.sendImage
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.subscribeGroupMessages
import net.mamoe.mirai.event.whileSelectMessages
import net.mamoe.mirai.utils.info
import org.laolittle.plugin.molly.MollyConfig.api_key
import org.laolittle.plugin.molly.MollyConfig.api_secret
import org.laolittle.plugin.molly.model.*

@ExperimentalSerializationApi
object Molly : KotlinPlugin(
    JvmPluginDescription(
        id = "org.laolittle.plugin.molly.Molly",
        version = "1.0",
        name = "Molly"
    )
) {
    override fun onEnable() {
        MollyConfig.reload()
        logger.info { "茉莉云机器人加载完毕" }
        if (api_key == "" || api_secret == "")
            logger.info { "请修改配置文件添加ApiKey和ApiSecret，配置文件位于./config/Molly/MollyConfig.yml" }
        else
        GlobalEventChannel.subscribeGroupMessages {
            atBot {
                if (inActMember.contains(sender.id)) return@atBot
                inActMember.add(sender.id)
                val msg = it
                    .replace("@${bot.id}", "")
                    .replace(" ", "")
                if (msg == "") {
                    subject.sendMessage(
                        when ((0..4).random()) {
                            0 -> "？"
                            1 -> "怎么"
                            2 -> "怎么了"
                            3 -> "什么？"
                            4 -> "在"
                            else -> "嗯？"
                        }
                    )
                    whileSelectMessages {
                        default { message ->
                            if (message == "") return@default true
                            request(
                                message = message,
                                userId = sender.id,
                                userName = senderName,
                                groupName = group.name,
                                groupId = group.id
                            )
                            for (i in mollyReply.keys)
                                if (mollyReply[i]?.typed == 1) {
                                    subject.sendMessage(mollyReply[i]?.content.toString())
                                    val random = (100..3000).random().toLong()
                                    delay(random)
                                } else {
                                    val url = "https://files.molicloud.com/" + mollyReply[i]?.content
                                    subject.sendImage(mollyFile(url))
                                }
                            false
                        }
                        timeout(10000) {
                            subject.sendMessage(
                                when ((0..4).random()) {
                                    0 -> "没事我就溜了"
                                    1 -> "emmmmm"
                                    2 -> "......"
                                    3 -> "溜了"
                                    else -> "？"
                                }
                            )
                            false
                        }
                    }
                } else {
                    request(
                        message = msg,
                        userId = sender.id,
                        userName = senderName,
                        groupName = group.name,
                        groupId = group.id
                    )
                    for (i in mollyReply.keys)
                        if (mollyReply[i]?.typed == 1) {
                            subject.sendMessage(mollyReply[i]?.content.toString())
                            val random = (100..3000).random().toLong()
                            delay(random)
                        } else {
                            val url = "https://files.molicloud.com/" + mollyReply[i]?.content
                            subject.sendImage(mollyFile(url))
                        }
                }
                inActMember.remove(sender.id)
                mollyReply = linkedMapOf()
            }
        }
    }
}