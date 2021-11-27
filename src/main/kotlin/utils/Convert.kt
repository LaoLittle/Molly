@file:Suppress("unused", "unchecked")

package org.laolittle.plugin.molly.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.event.events.MessageEvent

class ConversationBuilder(
    val eventContext: MessageEvent,
    val conversationBlock: suspend ConversationBuilder.() -> Unit
) {
    private val target: Contact = eventContext.subject

    suspend operator fun invoke() = conversationBlock()
}

suspend fun <T : MessageEvent> T.conversation(
    scope: CoroutineScope,
    block: suspend ConversationBuilder.() -> Unit
): ConversationBuilder {
    suspend fun executeICB() = ConversationBuilder(
            eventContext = this@conversation,
            conversationBlock = block
        ).also { it() }

    return withContext(scope.coroutineContext + scope.coroutineContext) { executeICB() }
}