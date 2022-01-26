package org.laolittle.plugin.molly

import net.mamoe.mirai.console.data.ReadOnlyPluginData
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

object MollyData : ReadOnlyPluginData("MollyData") {
    @ValueDescription("自定义万金油回复，重复语句会被覆盖")
    val customUnknownReply by value(setOf("我不理解", "嗯？"))
}