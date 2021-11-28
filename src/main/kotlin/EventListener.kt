package org.laolittle.plugin.molly

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.serialization.ExperimentalSerializationApi
import net.mamoe.mirai.contact.Contact.Companion.sendImage
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.event.subscribeFriendMessages
import net.mamoe.mirai.event.subscribeGroupMessages
import net.mamoe.mirai.event.whileSelectMessages
import net.mamoe.mirai.message.data.content
import org.laolittle.plugin.molly.MollyConfig.Name
import org.laolittle.plugin.molly.model.*
import org.laolittle.plugin.molly.utils.conversation
import kotlin.contracts.ExperimentalContracts

@ExperimentalSerializationApi
object EventListener : Service() {
    override suspend fun main() {

        GlobalEventChannel.subscribeGroupMessages {
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
            finding(Regex(Name)){
                if (inActMember.contains(sender.id)) return@finding
                reply(this@EventListener, message.content)
            }
            atBot {
                if (inActMember.contains(sender.id)) return@atBot
                val msg = it
                    .replace("@${bot.id}", "")
                    .replace(" ", "")
                groupReply(this@EventListener, msg)
            }
        }

        GlobalEventChannel.subscribeFriendMessages {
            finding(Regex("聊天")) {
                subject.sendMessage("在呢")
                var times = 0
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
                        reply(this@EventListener, mollyReply)
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
            }
        }
    }
}

@ExperimentalSerializationApi
@OptIn(ExperimentalContracts::class)
suspend fun GroupMessageEvent.reply(ctx: CoroutineScope, msg: String) {
    conversation(ctx) {
        request(
            message = msg,
            userId = sender.id,
            userName = senderName,
            groupName = group.name,
            groupId = group.id,
            true
        )
        reply(EventListener, mollyReply)
    }
}

suspend fun MessageEvent.reply(ctx: CoroutineScope, mollyReply: Map<Int, MollyReply>){
    conversation(ctx){
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
    org.laolittle.plugin.molly.model.mollyReply = linkedMapOf()
}

@ExperimentalSerializationApi
suspend fun GroupMessageEvent.groupReply(ctx: CoroutineScope, msg: String){
    conversation(ctx){
        inActMember.add(sender.id)
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
                default { msgPost ->
                    reply(EventListener, msgPost)
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
            reply(EventListener, msg)
        }
        inActMember.remove(sender.id)
    }
}