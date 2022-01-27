package org.laolittle.plugin.molly

import kotlinx.serialization.ExperimentalSerializationApi
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.utils.info
import org.laolittle.plugin.molly.MollyConfig.api_key
import org.laolittle.plugin.molly.MollyConfig.api_secret

@ExperimentalSerializationApi
object Molly : KotlinPlugin(
    JvmPluginDescription(
        id = "org.laolittle.plugin.molly.Molly",
        version = "1.2.0",
        name = "Molly"
    )
) {
    override fun onEnable() {
        MollyConfig.reload()
        MollyData.reload()
        logger.info { "茉莉云机器人加载完毕" }
        if (api_key == "" || api_secret == "")
            logger.info { "请修改配置文件添加ApiKey和ApiSecret，配置文件位于./config/Molly/MollyConfig.yml" }
        else {
            GroupMessageListener.start()
            FriendMessageListener.start()
        }

    }

    override fun onDisable() {
        logger.info { "已卸载" }
    }
}