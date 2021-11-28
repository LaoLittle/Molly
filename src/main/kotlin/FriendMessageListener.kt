package org.laolittle.plugin.molly

import kotlinx.serialization.ExperimentalSerializationApi
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.subscribeFriendMessages
import net.mamoe.mirai.event.whileSelectMessages
import org.laolittle.plugin.molly.model.mollyReply
import org.laolittle.plugin.molly.model.reply
import org.laolittle.plugin.molly.model.request

@ExperimentalSerializationApi
object FriendMessageListener : Service(){
    override suspend fun main() {
        GlobalEventChannel.subscribeFriendMessages {
            finding(Regex("聊天")) {
                subject.sendMessage("在呢")
                whileSelectMessages {
                    default {
                        request(
                            message = it,
                            userId = sender.id,
                            userName = senderName,
                            null,
                            null,
                            false
                        )
                        reply(this@FriendMessageListener, mollyReply)
                        true
                    }
                    startsWith("不聊了") {
                        subject.sendMessage("好吧")
                        false
                    }
                }
            }
        }
    }
}