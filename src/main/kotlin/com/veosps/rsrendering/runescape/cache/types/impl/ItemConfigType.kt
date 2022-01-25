@file:Suppress("ArrayInDataClass")

package com.veosps.rsrendering.runescape.cache.types.impl

import com.veosps.rsrendering.runescape.buffer.readParameters
import com.veosps.rsrendering.runescape.buffer.readString
import com.veosps.rsrendering.runescape.cache.types.CacheType
import io.netty.buffer.ByteBuf

data class ItemConfigType(
    var id: Int = DEFAULT_ID,
    var name: String = DEFAULT_NAME,
    var model: Int = DEFAULT_MODEL,
    var zoom2d: Int = DEFAULT_ZOOM_2D,
    var xan2d: Int = DEFAULT_XAN_2D,
    var yan2d: Int = DEFAULT_YAN_2D,
    var zan2d: Int = DEFAULT_ZAN_2D,
    var xOff2d: Int = DEFAULT_X_OFF_2D,
    var yOff2d: Int = DEFAULT_Y_OFF_2D,
    var stacks: Boolean = DEFAULT_STACKS,
    var cost: Int = DEFAULT_COST,
    var members: Boolean = DEFAULT_MEMBERS,
    var maleModelOffset: Int = DEFAULT_MODEL_OFFSET,
    var femaleModelOffset: Int = DEFAULT_MODEL_OFFSET,
    var maleModel0: Int = DEFAULT_MODEL,
    var maleModel1: Int = DEFAULT_MODEL,
    var maleModel2: Int = DEFAULT_MODEL,
    var femaleModel0: Int = DEFAULT_MODEL,
    var femaleModel1: Int = DEFAULT_MODEL,
    var femaleModel2: Int = DEFAULT_MODEL,
    var maleHeadModel0: Int = DEFAULT_MODEL,
    var maleHeadModel1: Int = DEFAULT_MODEL,
    var femaleHeadModel0: Int = DEFAULT_MODEL,
    var femaleHeadModel1: Int = DEFAULT_MODEL,
    var groundOptions: Array<String?> = DEFAULT_GROUND_OPTIONS,
    var inventoryOptions: Array<String?> = DEFAULT_INVENTORY_OPTIONS,
    var recolorSrc: IntArray = DEFAULT_INT_ARRAY,
    var recolorDest: IntArray = DEFAULT_INT_ARRAY,
    var reTextureSrc: IntArray = DEFAULT_INT_ARRAY,
    var reTextureDest: IntArray = DEFAULT_INT_ARRAY,
    var dropOptionIndex: Int = DEFAULT_DROP_OPTION_INDEX,
    var resizeX: Int = DEFAULT_RESIZE,
    var resizeY: Int = DEFAULT_RESIZE,
    var resizeZ: Int = DEFAULT_RESIZE,
    var ambient: Int = DEFAULT_AMBIENT,
    var contrast: Int = DEFAULT_CONTRAST,
    var exchangeable: Boolean = DEFAULT_EXCHANGEABLE,
    var teamCape: Int = DEFAULT_TEAM_CAPE,
    var noteLink: Int = DEFAULT_NOTE_LINK,
    var noteValue: Int = DEFAULT_NOTE_VALUE,
    var placeholderLink: Int = DEFAULT_PLACEHOLDER_LINK,
    var placeholderValue: Int = DEFAULT_PLACEHOLDER_VALUE,
    var boughtLink: Int = DEFAULT_BOUGHT_LINK,
    var boughtValue: Int = DEFAULT_BOUGHT_VALUE,
    var countItem: IntArray = DEFAULT_INT_ARRAY,
    var countCo: IntArray = DEFAULT_INT_ARRAY,
    var parameters: Map<Int, Any> = DEFAULT_PARAMETERS
) : CacheType {

    val defaultGroundOps: Boolean
        get() = groundOptions === DEFAULT_GROUND_OPTIONS

    val defaultInventoryOps: Boolean
        get() = inventoryOptions === DEFAULT_INVENTORY_OPTIONS

    fun readBuffer(instruction: Int, buf: ByteBuf): Any = when(instruction) {
        1 -> model = buf.readUnsignedShort()
        2 -> name = buf.readString()
        4 -> zoom2d = buf.readUnsignedShort()
        5 -> xan2d = buf.readUnsignedShort()
        6 -> yan2d = buf.readUnsignedShort()
        7 -> xOff2d = buf.readShort().toInt()
        8 -> yOff2d = buf.readShort().toInt()
        11 -> stacks = true
        12 -> cost = buf.readInt()
        16 -> members = true
        23 -> {
            maleModel0 = buf.readUnsignedShort()
            maleModelOffset = buf.readUnsignedByte().toInt()
        }
        24 -> maleModel1 = buf.readUnsignedShort()
        25 -> {
            femaleModel0 = buf.readUnsignedShort()
            femaleModelOffset = buf.readUnsignedByte().toInt()
        }
        26 -> femaleModel1 = buf.readUnsignedShort()
        in 30 until 35 -> {
            if (defaultGroundOps) groundOptions = arrayOfNulls(5)
            val index = instruction - 30
            val option = buf.readString()
            groundOptions[index] = if (option == "Hidden") null else option
        }
        in 35 until 40 -> {
            if (defaultInventoryOps) inventoryOptions = arrayOfNulls(5)
            val index = instruction - 35
            val option = buf.readString()
            inventoryOptions[index] = option
        }
        40, 41 -> {
            val count = buf.readUnsignedByte().toInt()
            val src = IntArray(count)
            val dest = IntArray(count)
            repeat(count) {
                src[it] = buf.readUnsignedShort()
                dest[it] = buf.readUnsignedShort()
            }
            if (instruction == 40) {
                recolorSrc = src
                recolorDest = dest
            } else {
                reTextureSrc = src
                reTextureDest = dest
            }
        }
        42 -> dropOptionIndex = buf.readByte().toInt()
        65 -> exchangeable = true
        78 -> maleModel2 = buf.readUnsignedShort()
        79 -> femaleModel2 = buf.readUnsignedShort()
        90 -> maleHeadModel0 = buf.readUnsignedShort()
        91 -> femaleHeadModel0 = buf.readUnsignedShort()
        92 -> maleHeadModel1 = buf.readUnsignedShort()
        93 -> femaleHeadModel1 = buf.readUnsignedShort()
        94 -> buf.readUnsignedShort()
        95 -> zan2d = buf.readUnsignedShort()
        97 -> noteLink = buf.readUnsignedShort()
        98 -> noteValue = buf.readUnsignedShort()
        in 100 until 110 -> {
            if (countItem.isEmpty()) {
                countItem = IntArray(10)
                countCo = IntArray(10)
            }
            val index = instruction - 100
            countItem[index] = buf.readUnsignedShort()
            countCo[index] = buf.readUnsignedShort()
        }
        110 -> resizeX = buf.readUnsignedShort()
        111 -> resizeY = buf.readUnsignedShort()
        112 -> resizeZ = buf.readUnsignedShort()
        113 -> ambient = buf.readByte().toInt()
        114 -> contrast = buf.readByte().toInt()
        115 -> teamCape = buf.readUnsignedByte().toInt()
        139 -> boughtLink = buf.readUnsignedShort()
        140 -> boughtValue = buf.readUnsignedShort()
        148 -> placeholderLink = buf.readUnsignedShort()
        149 -> placeholderValue = buf.readUnsignedShort()
        249 -> parameters = buf.readParameters()
        else -> error("Unknown instruction: $instruction.")
    }
}

private const val DEFAULT_ID = -1
private const val DEFAULT_MODEL = 0
private const val DEFAULT_MODEL_OFFSET = 0
private const val DEFAULT_NAME = "null"
private const val DEFAULT_ZOOM_2D = 2000
private const val DEFAULT_XAN_2D = 0
private const val DEFAULT_YAN_2D = 0
private const val DEFAULT_ZAN_2D = 0
private const val DEFAULT_X_OFF_2D = 0
private const val DEFAULT_Y_OFF_2D = 0
private const val DEFAULT_STACKS = false
private const val DEFAULT_COST = 1
private const val DEFAULT_MEMBERS = false
private const val DEFAULT_EXCHANGEABLE = false
private const val DEFAULT_TEAM_CAPE = 0
private const val DEFAULT_NOTE_LINK = 0
private const val DEFAULT_NOTE_VALUE = 0
private const val DEFAULT_PLACEHOLDER_LINK = 0
private const val DEFAULT_PLACEHOLDER_VALUE = 0
private const val DEFAULT_DROP_OPTION_INDEX = -2
private const val DEFAULT_RESIZE = 128
private const val DEFAULT_AMBIENT = 0
private const val DEFAULT_CONTRAST = 0
private const val DEFAULT_BOUGHT_LINK = 0
private const val DEFAULT_BOUGHT_VALUE = 0

private val DEFAULT_GROUND_OPTIONS = arrayOf(null, null, "Take", null, null)
private val DEFAULT_INVENTORY_OPTIONS = arrayOf(null, null, null, null, "Drop")
private val DEFAULT_INT_ARRAY = IntArray(0)
private val DEFAULT_PARAMETERS = emptyMap<Int, Any>()