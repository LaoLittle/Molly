package org.laolittle.plugin.molly

import kotlinx.coroutines.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlin.coroutines.CoroutineContext

@ExperimentalSerializationApi
abstract class Service(ctx: CoroutineContext? = null) : CoroutineScope {

    final override val coroutineContext: CoroutineContext
        get() = SupervisorJob(Molly.coroutineContext.job)

    init {
        if (ctx != null) {
            coroutineContext.plus(ctx)
        }
    }

    protected abstract suspend fun main()

    fun start(): Job = this.launch(context = this.coroutineContext) { main() }
}