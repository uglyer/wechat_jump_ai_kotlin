package App

import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import java.awt.Color
import java.awt.image.BufferedImage

/**
 * 扩展方法
 * @Date: Created by uglyer in 2017/12/31.
 */

fun BufferedImage.paintPoint(w: Int, h: Int, color: Color) {
    for (i in 0 until 10) {
        for (j in 0 until 10) {
            setRGB(w + i, h + j, color.rgb)
        }
    }
}

inline fun BufferedImage.findByCondition(isBreak: Boolean = true, inW: Int = -1, startW: Int = 0, startH: Int = 0, condition: (w: Int, h: Int, color: Color) -> Boolean, action: (w: Int, h: Int, color: Color) -> Unit) {
    for (h in startH until height) {
        if (inW > 0) {
            val rgbValue = getRGB(inW, h)
            val color = Color(rgbValue)
            if (condition(inW, h, color)) {
                action(inW, h, color)
                if (isBreak) return
            }
        } else {
            for (w in startW until width) {
                val rgbValue = getRGB(w, h)
                val color = Color(rgbValue)
                if (condition(w, h, color)) {
                    action(w, h, color)
                    if (isBreak) return
                }
            }
        }
    }
}

fun Color.notBg(bgColor: Color, bgColorEnd: Color): Boolean {
    val color = this
    if (color.red in 251..255 && color.green in 70..76 && color.blue in 82..85) return true //红包盒子
    if (color.red in 179..190 && color.green in 235..245 && color.blue in 65..70) return true //绿色盒子
    if (color.red in 97..101 && color.green in 145..150 && color.blue in 100..109) return true //深绿色盒子
    if (color.red in 105..119 && color.green in 105..119 && color.blue in 105..119) return true //灰色圆桌
    if (color.red in 240..244 && color.green in 240..244 && color.blue in 240..244) return true //灰色书本
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

fun Color.notBlack(): Boolean = red > 0 && green > 0 && blue > 0

fun Mat.doCanny(): Mat {
    // init
    val grayImage = Mat()
    val detectedEdges = Mat()
    val threshold = 10.0
    // convert to grayscale
    Imgproc.cvtColor(this, grayImage, Imgproc.COLOR_BGR2GRAY)
    // reduce noise with a 3x3 kernel
    Imgproc.blur(grayImage, detectedEdges, Size(3.0, 3.0))
    // canny detector, with ratio of lower:upper threshold of 3:1
    Imgproc.Canny(detectedEdges, detectedEdges, threshold, threshold * 3)
    // using Canny's output as a mask, display the result
    val dest = Mat()
    this.copyTo(dest, detectedEdges)
    return dest
}