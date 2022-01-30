package com.veosps.rsrendering.runescape.cache

import com.displee.cache.CacheLibrary
import com.veosps.rsrendering.opengl.game.DummyInitializer
import com.veosps.rsrendering.runescape.cache.types.TypeManager
import com.veosps.rsrendering.runescape.loaders.OSRSTextureLoader
import org.springframework.beans.factory.InitializingBean
import org.springframework.stereotype.Component

lateinit var cache: CacheLibrary
lateinit var texLoader: OSRSTextureLoader

@Component
class CacheInitializer(
    private val typeManager: TypeManager,
    private val textureLoader: OSRSTextureLoader,
    private val dummyInitializer: DummyInitializer,
) : InitializingBean {

    override fun afterPropertiesSet() {
        cache = CacheLibrary("./data/cache/")
        texLoader = textureLoader

        modelArchive().cache()
        //binariesArchive().cache()
        //componentArchive().cache()
        configArchive().cache()
        //cs2Archive().cache()
        //mapArchive().cache()
        //musicJingleArchive().cache()
        //musicTrackArchive().cache()
        //soundEffectArchive().cache()
        spriteIndex().cache()
        textureIndex().cache()

        typeManager.load()

        textureLoader.load()

        dummyInitializer.start()
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
fun spriteIndex() = cache.index(8)
fun textureIndex() = cache.index(9)