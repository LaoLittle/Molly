package org.laolittle.plugin.molly

import kotlinx.serialization.ExperimentalSerializationApi
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.subscribeFriendMessages
import net.mamoe.mirai.message.data.content
import org.laolittle.plugin.molly.MollyConfig.dontReply
import org.laolittle.plugin.molly.model.MollyApiService.mollyReply
import org.laolittle.plugin.molly.model.MollyApiService.request
import org.laolittle.plugin.molly.model.Reply.reply

@ExperimentalSerializationApi
object FriendMessageListener : Service() {
    override suspend fun main() {
        GlobalEventChannel.subscribeFriendMessages {
            always {
                if (subject.id == bot.id) return@always
                dontReply.forEach { dontNode -> if (message.content.contains(Regex(dontNode))) return@always }
                request(
                    message = it,
                    userId = sender.id,
                    userName = senderName,
                    groupName = null,
                    groupId = null,
                    false
                )
                reply(this@FriendMessageListener, mollyReply)
            }
        }
        /*     finding(Regex("聊天")) {
                 subject.sendMessage("在呢")
                 var loop = true
                 while (loop)
                     whileSelectMessages {
                         default {
                             request(
                                 message = it,
                                 userId = sender.id,
                                 userName = senderName,
                                 groupName = null,
                                 groupId = null,
                                 false
                             )
                             reply(this@FriendMessageListener, mollyReply)
                             false
                         }
                         startsWith("不聊了") {
                             subject.sendMessage("好吧")
                             loop = false
                             false
                         }
                         timeout(30_000) {
                             subject.sendMessage("不聊了么？那我走了")
                             loop = false
                             false
                         }
                     }
             }
         }
     } */
    }
}