package org.laolittle.plugin.molly.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.serialization.ExperimentalSerializationApi
import net.mamoe.mirai.contact.Contact.Companion.sendImage
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.event.whileSelectMessages
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import net.mamoe.mirai.message.data.buildMessageChain
import org.laolittle.plugin.molly.MollyConfig.doQuoteReply
import org.laolittle.plugin.molly.MollyConfig.replyTimes
import org.laolittle.plugin.molly.utils.conversation

/**
 * 接收消息并发送请求到Molly机器人
 * */

@ExperimentalSerializationApi
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
        reply(ctx, mollyReply, doQuoteReply)
    }
}

suspend fun MessageEvent.reply(
    ctx: CoroutineScope,
    mollyReplyTempo: Map<Int, MollyReply>,
    quoteReply: Boolean = false
) {
    conversation(ctx) {
        for (i in mollyReplyTempo.keys)
            if (mollyReplyTempo[i]?.typed == 1) {
                val send = buildMessageChain {
                    add(mollyReplyTempo[i]?.content.toString())
                }
                val random = (100..3000).random().toLong()
                delay(random)
                if (quoteReply)
                    subject.sendMessage(send.plus(message.quote()))
                else subject.sendMessage(send)
            } else {
                val url = "https://files.molicloud.com/" + mollyReplyTempo[i]?.content
                subject.sendImage(mollyFile(url))
            }
    }
    mollyReply = linkedMapOf()
}

@ExperimentalSerializationApi
suspend fun GroupMessageEvent.groupLoopReply(ctx: CoroutineScope, msg: String) {
    conversation(ctx) {
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
            for (i in 1..replyTimes) {
                if (!waitReply(ctx, i)) break
            }
        } else {
            reply(ctx, msg)
            for (i in 1..replyTimes)
                if (!waitReply(ctx, i)) break
        }
        inActMember.remove(sender.id)
    }
}

/**
 * 挂起当前协程并监听消息
 *
 * @return 是否超时
 * */

@ExperimentalSerializationApi
suspend fun GroupMessageEvent.waitReply(ctx: CoroutineScope, i: Int): Boolean {
    var isTimeout = true
    var notReplied = true
    conversation(ctx) {
        whileSelectMessages {
            default {
                notReplied = false
                reply(ctx, it)
                false
            }
            timeout(10_000) {
                if ((i == 1) && (replyTimes > 0) && notReplied) {
                    subject.sendMessage(
                        when ((0..4).random()) {
                            0 -> "没事我就溜了"
                            1 -> "emmmmm"
                            2 -> "......"
                            3 -> "溜了"
                            else -> "？"
                        }
                    )
                }
                isTimeout = false
                false
            }
        }
    }
    return isTimeout
}