package org.laolittle.plugin.molly.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.serialization.ExperimentalSerializationApi
import net.mamoe.mirai.contact.AudioSupported
import net.mamoe.mirai.contact.Contact.Companion.sendImage
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import net.mamoe.mirai.message.data.buildMessageChain
import net.mamoe.mirai.message.data.content
import net.mamoe.mirai.message.data.sendTo
import net.mamoe.mirai.message.nextMessage
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import org.laolittle.plugin.molly.MollyConfig.defaultReply
import org.laolittle.plugin.molly.MollyConfig.doQuoteReply
import org.laolittle.plugin.molly.MollyConfig.name
import org.laolittle.plugin.molly.MollyConfig.replyTimes
import org.laolittle.plugin.molly.MollyConfig.timeoutReply
import org.laolittle.plugin.molly.model.MollyApiService.inActMember
import org.laolittle.plugin.molly.model.MollyApiService.request
import org.laolittle.plugin.molly.utils.KtorOkHttp.getFile
import org.laolittle.plugin.molly.utils.conversation

object Reply {
    /**
     * 接收消息并发送请求到Molly机器人
     * */
    @ExperimentalSerializationApi
    suspend fun GroupMessageEvent.reply(ctx: CoroutineScope, msg: String) {
        conversation(ctx) {
            val mollyReply = request(
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
        mollyReplyTempo: List<MollyReply>,
        quoteReply: Boolean = false
    ) {
        conversation(ctx) {
            mollyReplyTempo.forEach { receive ->
                when (receive.typed) {
                    1 -> {
                        val send = buildMessageChain {
                            add(receive.content)
                        }
                        val random = (100..3000).random().toLong()
                        delay(random)
                        if (quoteReply)
                            subject.sendMessage(send.plus(message.quote()))
                        else subject.sendMessage(send)
                    }

                    2 -> {
                        val url = "https://files.molicloud.com/" + receive.content
                        getFile(url).use { subject.sendImage(it) }
                    }

                    4 -> {
                        val receiver = subject as AudioSupported
                        val url = "https://files.molicloud.com/" + receive.content
                        getFile(url).use { input -> input.toExternalResource().use { receiver.uploadAudio(it).sendTo(subject) } }
                    }

                    5-> {
                        val send = buildMessageChain {
                            add(receive.content)
                        }
                        val random = (100..3000).random().toLong()
                        delay(random)
                        if (quoteReply)
                            subject.sendMessage(send.plus(message.quote()))
                        else subject.sendMessage(send)
                        throw IllegalAccessException("回复次数超限")
                    }

                    else -> {
                        subject.sendMessage("https://files.molicloud.com/${receive.content}")
                    }
                }
            }
        }
    }

    @ExperimentalSerializationApi
    suspend fun GroupMessageEvent.groupLoopReply(ctx: CoroutineScope, msg: String) {
        conversation(ctx) {
            inActMember.add(sender.id)
            val proMsg = msg.replace("@${bot.id}", name)
            runCatching {
                if (proMsg == "") {
                    subject.sendMessage(defaultReply.random())
                    if (replyTimes == 0) waitReply(ctx, 1) else waitReply(ctx, 0)
                    for (i in 1 until replyTimes) {
                        if (waitReply(ctx, i)) break
                    }
                } else {
                    reply(ctx, proMsg)
                    if (proMsg == name) waitReply(ctx, 0)
                    else waitReply(ctx, 1)
                    for (i in 1 until replyTimes)
                        if (waitReply(ctx, i)) break
                }
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
        var isTimeout = false
        conversation(ctx) {
            isTimeout = runCatching {
                reply(ctx, nextMessage(10_000).content.replace("@${bot.id}", name).replace(" ", ""))
                false
            }.onFailure {
                if ((i == 0) && (replyTimes > 0)) {
                    subject.sendMessage(timeoutReply.random())
                }
            }.getOrDefault(true)
            /*   whileSelectMessages {
                   default {
                       reply(ctx, it.replace("@${bot.id}", name))
                       false
                   }
                   timeout(10_000) {
                       if ((i == 1) && (replyTimes > 0)) {
                           subject.sendMessage(timeoutReply.random())
                       }
                       isTimeout = false
                       false
                   }
               } */
        }
        return isTimeout
    }
}