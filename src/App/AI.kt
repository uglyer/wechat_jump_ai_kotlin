package App

import com.iosdevlog.jumpjump.SCREENSHOT_LOCATION
import com.iosdevlog.jumpjump.SCREENSHOT_LOCATION_OUT
import com.iosdevlog.jumpjump.call
import com.iosdevlog.jumpjump.distance
import java.awt.Color
import java.awt.Point
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

/**
 * des
 * @Date: Created by uglyer in 2017/12/30.
 */

fun notBg(color: Color, bgColor: Color, bgColorEnd: Color): Boolean {
    if (color.red in 251..255 && color.green in 70..76 && color.blue in 82..85) return true //红包盒子
    if (color.red in 179..190 && color.green in 235..245 && color.blue in 65..70) return true //绿色盒子
    if (color.red in 245..255 && color.green in 245..255 && color.blue in 245..255) return true //白色盒子
    if (color.red in 254..255 && color.green in 170..175 && color.blue in 175..180) return true //粉色盒子
    if (color.red in 134..138 && color.green in 120..125 && color.blue in 225..230) return true //紫色盒子

    //是否为变化的背景
    if ((color.red in bgColor.red - 8..bgColor.red + 8
            && color.green in bgColor.green - 8..bgColor.green + 8
            && color.blue in bgColor.blue - 8..bgColor.blue + 8)
            || (color.red in bgColorEnd.red - 8..bgColorEnd.red + 8
            && color.green in bgColorEnd.green - 8..bgColorEnd.green + 8
            && color.blue in bgColorEnd.blue - 8..bgColorEnd.blue + 8)
            || color.blue in bgColorEnd.blue..bgColor.blue) return false
    if (color.red == 255 && color.green == 238 && color.blue == 97) return true

    return color.red < 253 && (color.green < 240 || color.green > 250) && (color.blue > 180 || color.blue < 140)
}

fun isStart(color: Color): Boolean {
    return (color.red in 52..60) && (color.green in 49..55) && (color.blue in 85..92)
}

fun runAI(bufferedImage: BufferedImage) {
    println("w:${bufferedImage.width} h:${bufferedImage.height}")
    var startW = 0
    var startH = 0
    var endW = 0
    var endH = 0
    var count = 0
    var findStart = true
    val bgColor = Color(bufferedImage.getRGB(0, 0))
    val bgColorEnd = Color(bufferedImage.getRGB(0, bufferedImage.width - 100))
    println("bgColor R:${bgColor.red} G:${bgColor.green} B:${bgColor.blue}")
    for (h in 200 until bufferedImage.height) {
        for (w in 0 until bufferedImage.width) {
            val rgbValue = bufferedImage.getRGB(w, h)
            val color = Color(rgbValue)
            if (notBg(color, bgColor, bgColorEnd)) {
                if (count == 1) {
                    endW = w
                    endH = h + 30
                    findStart = true
                }
                if (findStart) {
//                    print("color.red:${color.red} G:${color.green} B:${color.blue}")
                    if (isStart(color)) {
                        startW = w
                        startH = h
                        findStart = false
                    }
                }
                count++
//                bufferedImage.setRGB(w, h, 0)
            }

        }
    }
    for (i in 0 until 20) {
        for (j in 0 until 20) {
            bufferedImage.setRGB(endW + i, endH + j, 0)
        }
    }
    for (i in 0 until 20) {
        for (j in 0 until 20) {
            bufferedImage.setRGB(startW + i, startH + j, -555)
        }
    }
    ImageIO.write(bufferedImage, "png", File(SCREENSHOT_LOCATION_OUT))
    val distance = distance(Point(startW, startH), Point(endW, endH))
    println("distance:" + distance)
    call(distance * 2.2)//magic number
}

fun main(args: Array<String>) {
    val bufferedImage = ImageIO.read(File(SCREENSHOT_LOCATION))
    val newImage = BufferedImage(675, 1200, bufferedImage.getType())
    val gTemp = newImage.graphics
    gTemp.drawImage(bufferedImage, 0, 0, 675, 1200, null)
    gTemp.dispose()
//    bufferedImage = newImage
    runAI(newImage)
}