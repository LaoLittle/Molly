package org.laolittle.plugin.molly.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext

class ConversationBuilder(
    val conversationBlock: suspend ConversationBuilder.() -> Unit
) {
    suspend operator fun invoke() = conversationBlock()
}

suspend fun conversation(
    scope: CoroutineScope,
    block: suspend ConversationBuilder.() -> Unit
): ConversationBuilder {
    suspend fun execute() = ConversationBuilder(
        conversationBlock = block
    ).also { it() }

    return withContext(scope.coroutineContext + scope.coroutineContext) { execute() }
}