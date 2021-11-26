package org.laolittle.plugin.molly

import kotlinx.coroutines.delay
import kotlinx.serialization.ExperimentalSerializationApi
import net.mamoe.mirai.contact.Contact.Companion.sendImage
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.subscribeFriendMessages
import net.mamoe.mirai.event.subscribeGroupMessages
import net.mamoe.mirai.event.whileSelectMessages
import org.laolittle.plugin.molly.model.inActMember
import org.laolittle.plugin.molly.model.mollyFile
import org.laolittle.plugin.molly.model.mollyReply
import org.laolittle.plugin.molly.model.request

@ExperimentalSerializationApi
object EventListener : Service() {
    override suspend fun main() {
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
                                request(
                                    message = message,
                                    userId = sender.id,
                                    userName = senderName,
                                    groupName = group.name,
                                    groupId = group.id
                                )
                                for (i in mollyReply.keys)
                                    if (mollyReply[i]?.typed == 1) {
                                        val random = (100..3000).random().toLong()
                                        delay(random)
                                        subject.sendMessage(mollyReply[i]?.content.toString())
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
                                val random = (100..3000).random().toLong()
                                delay(random)
                                subject.sendMessage(mollyReply[i]?.content.toString())
                            } else {
                                val url = "https://files.molicloud.com/" + mollyReply[i]?.content
                                subject.sendImage(mollyFile(url))
                            }
                    }
                    inActMember.remove(sender.id)
                    mollyReply = linkedMapOf()
                }
            }

            GlobalEventChannel.subscribeFriendMessages {
                startsWith("聊天") {
                    whileSelectMessages {
                        var times = 0
                        default {
                            request(
                                message = it,
                                userId = sender.id,
                                userName = senderName
                            )
                            for (i in mollyReply.keys)
                                if (mollyReply[i]?.typed == 1) {
                                    val random = (100..3000).random().toLong()
                                    delay(random)
                                    subject.sendMessage(mollyReply[i]?.content.toString())
                                } else {
                                    val url = "https://files.molicloud.com/" + mollyReply[i]?.content
                                    subject.sendImage(mollyFile(url))
                                }
                            times = 0
                            true
                        }
                        startsWith("不聊了") {
                            subject.sendMessage("好吧")
                            false
                        }
                        timeout(10000) {
                            times++
                            if (times >= 2) {
                                subject.sendMessage("我下咯")
                                return@timeout false
                            }
                            true
                        }
                    }
                    mollyReply = linkedMapOf()
                }
            }
        }
    }