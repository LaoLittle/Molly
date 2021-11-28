package org.laolittle.plugin.molly.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.serialization.ExperimentalSerializationApi
import net.mamoe.mirai.contact.Contact.Companion.sendImage
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.event.whileSelectMessages
import org.laolittle.plugin.molly.MollyConfig.replyTimes
import org.laolittle.plugin.molly.utils.conversation
import kotlin.contracts.ExperimentalContracts


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
        reply(ctx, mollyReply)
    }
}

suspend fun MessageEvent.reply(ctx: CoroutineScope, mollyReply: Map<Int, MollyReply>) {
    conversation(ctx) {
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
suspend fun GroupMessageEvent.groupLoopReply(ctx: CoroutineScope, msg: String) {
    conversation(ctx) {
        inActMember.add(sender.id)
        var remainingTimes = replyTimes
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
                if (replyTimes == 0) return@whileSelectMessages
                default { msgPost ->
                    remainingTimes--
                    reply(ctx, msgPost)
                    if (remainingTimes <= 0) return@default false
                    true
                }
                timeout(10000) {
                    if (remainingTimes == replyTimes) {
                        subject.sendMessage(
                            when ((0..4).random()) {
                                0 -> "没事我就溜了"
                                1 -> "emmmmm"
                                2 -> "......"
                                3 -> "溜了"
                                else -> "？"
                            }
                        )
                        return@timeout false
                    }
                    true
                }
            }
        } else {
            remainingTimes--
            reply(ctx, msg)
            whileSelectMessages {
                if (replyTimes == 0) return@whileSelectMessages
                default {
                    remainingTimes--
                    reply(ctx, it)
                    if (remainingTimes <= 0) return@default false
                    true
                }
            }
        }
        inActMember.remove(sender.id)
    }
}