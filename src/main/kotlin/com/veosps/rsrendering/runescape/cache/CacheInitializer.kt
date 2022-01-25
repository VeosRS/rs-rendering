package com.veosps.rsrendering.runescape.cache

import com.displee.cache.CacheLibrary
import com.veosps.rsrendering.runescape.cache.types.TypeManager
import org.springframework.beans.factory.InitializingBean
import org.springframework.stereotype.Component
import java.nio.file.Path

lateinit var cache: CacheLibrary

@Component
class CacheInitializer(
    val typeManager: TypeManager
) : InitializingBean {

    override fun afterPropertiesSet() {
        cache = CacheLibrary("./data/cache/")

        modelArchive().cache()
        //binariesArchive().cache()
        //componentArchive().cache()
        configArchive().cache()
        //cs2Archive().cache()
        //mapArchive().cache()
        //musicJingleArchive().cache()
        //musicTrackArchive().cache()
        //soundEffectArchive().cache()
        //spriteArchive().cache()
        //textureArchive().cache()

        typeManager.load()
    }
}

fun modelArchive() = cache.index(7)
fun binariesArchive() = cache.index(10)
fun componentArchive() = cache.index(3)
fun configArchive() = cache.index(2)
fun cs2Archive() = cache.index(12)
fun mapArchive() = cache.index(5)
fun musicJingleArchive() = cache.index(11)
fun musicTrackArchive() = cache.index(6)
fun soundEffectArchive() = cache.index(4)
fun spriteArchive() = cache.index(8)
fun textureArchive() = cache.index(9)