package org.laolittle.plugin.molly

import kotlinx.serialization.ExperimentalSerializationApi
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.subscribeGroupMessages
import net.mamoe.mirai.message.data.content
import org.laolittle.plugin.molly.MollyConfig.name
import org.laolittle.plugin.molly.model.MollyApiService.containsMember
import org.laolittle.plugin.molly.model.Reply.groupLoopReply

@ExperimentalSerializationApi
object GroupMessageListener : Service() {
    override suspend fun main() {
        GlobalEventChannel.parentScope(Molly).context(Molly.coroutineContext).filterIsInstance<GroupMessageEvent>()
            .filter { !containsMember(it.sender.id) }.subscribeGroupMessages {
                finding(Regex(name)) {
                    groupLoopReply(this@GroupMessageListener, message.content.replace(" ", ""))
                }
                atBot {
                    val msg = it
                        .replace("@${bot.id}", "")
                        .replace(" ", "")
                    groupLoopReply(this@GroupMessageListener, msg)
                }
                /*"mollydebug" {
                    subject.sendMessage("start")
                    whileSelectMessages {
                        "stop" {
                            subject.sendMessage("stopped")
                            false
                        }
                        default {
                            request(
                                message = it,
                                userId = sender.id,
                                userName = senderName,
                                groupName = group.name,
                                groupId = group.id,
                                true
                            )
                            val mollyReplyTempo = mollyReply
                            subject.sendMessage(mollyReplyTempo.toString())
                            for (i in mollyReplyTempo.keys)
                                if (mollyReplyTempo[i]?.typed == 1) {
                                    val random = (100..3000).random().toLong()
                                    delay(random)
                                    subject.sendMessage(mollyReplyTempo[i]?.content.toString())
                                } else {
                                    val url = "https://files.molicloud.com/" + mollyReplyTempo[i]?.content
                                    subject.sendImage(mollyFile(url))
                                }
                            mollyReply = linkedMapOf()
                            true
                        }
                    }
                }

                 */
            }
    }
}
