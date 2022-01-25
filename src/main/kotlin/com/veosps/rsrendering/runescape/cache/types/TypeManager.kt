package com.veosps.rsrendering.runescape.cache.types

import com.displee.cache.index.archive.file.File
import com.github.michaelbull.logging.InlineLogger
import com.veosps.rsrendering.runescape.cache.CacheInitializer
import com.veosps.rsrendering.runescape.cache.itemConfigs
import com.veosps.rsrendering.runescape.cache.modelArchive
import com.veosps.rsrendering.runescape.cache.typereaders.impl.ItemConfigTypeReader
import com.veosps.rsrendering.runescape.cache.typereaders.impl.read
import com.veosps.rsrendering.runescape.cache.types.impl.ItemConfigType
import io.netty.buffer.Unpooled
import org.springframework.stereotype.Component

private val logger = InlineLogger()

@Component
class TypeManager(
    private val itemConfigList: MutableList<ItemConfigType> = mutableListOf()
) {

    fun load() {
        itemConfigs()?.files?.forEach { (id, file) ->
            itemConfigList.add(file.wrapAndRead(id))
        }

        logger.info { "Loaded ${itemConfigList.size} item configs." }
    }
}

fun File.wrapAndRead(id: Int) = Unpooled.wrappedBuffer(this.data).read(id)
