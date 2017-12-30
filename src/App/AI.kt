package App

import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import java.awt.Color
import java.awt.Point
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

/**
 * 通过颜色找点,比较简单的方式.
 * @Date: Created by uglyer in 2017/12/30.
 */

fun notBg(color: Color, bgColor: Color, bgColorEnd: Color): Boolean {
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

fun isStart(color: Color): Boolean {
    return (color.red in 52..60) && (color.green in 49..55) && (color.blue in 85..92)
}

var index = 0
fun runAI(bufferedImage: BufferedImage): BufferedImage {
    if (IS_SAVE_HISTORY) {
        val outDir = File(SCREENSHOT_LOCATION_OUT_DIR)
        if (!outDir.exists()) outDir.mkdirs()
        ImageIO.write(bufferedImage, "png", File("$SCREENSHOT_LOCATION_OUT_DIR\\_$index.png"))
    }
//    println("w:${bufferedImage.width} h:${bufferedImage.height}")
    var startW = 0
    var startH = 0
    var endW = 0
    var endH = 0
    var count = 0
    var refind = 0
    var findStart = true
    val bgColor = Color(bufferedImage.getRGB(0, 0))
    val bgColorEnd = Color(bufferedImage.getRGB(0, bufferedImage.width - 100))
    println("bgColor R:${bgColor.red} G:${bgColor.green} B:${bgColor.blue}")

    if (USE_OPENCV) {
        ImageIO.write(bufferedImage, "png", File(SCREENSHOT_LOCATION))
        //去杂色
        for (h in 200 until bufferedImage.height) {
            for (w in 0 until bufferedImage.width) {
                val rgbValue = bufferedImage.getRGB(w, h)
                val color = Color(rgbValue)
                if (!notBg(color, bgColor, bgColorEnd)) {
                    bufferedImage.setRGB(w, h, Color(0,0,255).rgb)
                }
            }
        }
        ImageIO.write(bufferedImage, "png", File(SCREENSHOT_LOCATION + "_to_end.jpg"))
        val source = Imgcodecs.imread(SCREENSHOT_LOCATION)//读图像
        val template = Imgcodecs.imread(START_LOCATION)

        val outputImage = Mat()
        val machMethod = Imgproc.TM_CCOEFF
        //Template matching method
        Imgproc.matchTemplate(source, template, outputImage, machMethod)
        val mmr = Core.minMaxLoc(outputImage)
        val matchLoc = mmr.maxLoc
        val toEnd = Imgcodecs.imread(SCREENSHOT_LOCATION + "_to_end.jpg")
        val new_img = doCanny(toEnd)
        Imgcodecs.imwrite(SCREENSHOT_LOCATION + "_end.jpg", new_img)//写图像
        //Draw rectangle on result image
        Imgproc.rectangle(source, matchLoc, org.opencv.core.Point(matchLoc.x + template.cols(),
                matchLoc.y + template.rows()), Scalar(255.0, 0.0, 0.0))
        Imgcodecs.imwrite(SCREENSHOT_LOCATION + "_start.jpg", source)
        val startLeftTop = Point(matchLoc.x.toInt(), matchLoc.y.toInt())
        val startRightBottom = Point((matchLoc.x + template.cols()).toInt(), (matchLoc.y + template.rows()).toInt())
        val bufferedImageEnd = ImageIO.read(File(SCREENSHOT_LOCATION + "_end.jpg"))
        for (h in 200 until bufferedImageEnd.height) {
            for (w in 20 until bufferedImageEnd.width) {
                val rgbValue = bufferedImageEnd.getRGB(w, h)
                val color = Color(rgbValue)
                if ((color.red > 5 && color.green > 5 && color.blue > 5)
                        && (w > startRightBottom.x
//                        && h > startRightBottom.y
                        || w < startLeftTop.x)
//                        && h < startLeftTop.y
                        && endH == 0) {
                    endW = w
                    endH = h + 10
                    println("color.red:${color.red} G:${color.green} B:${color.blue} a:${color.alpha}")
                }
            }
        }
        startW = startLeftTop.x + 32
        startH = startRightBottom.y - 20
        val buffedImageResult = ImageIO.read(File(SCREENSHOT_LOCATION + "_start.jpg"))
        for (i in 0 until 10) {
            for (j in 0 until 10) {
                buffedImageResult.setRGB(endW + i, endH + j, 0)
            }
        }
        for (i in 0 until 10) {
            for (j in 0 until 10) {
                buffedImageResult.setRGB(startW + i, startH + j, -555)
            }
        }
        println("Complated.")
        ImageIO.write(buffedImageResult, "png", File(SCREENSHOT_LOCATION_OUT))
        if (IS_SAVE_HISTORY)
            ImageIO.write(buffedImageResult, "png", File("$SCREENSHOT_LOCATION_OUT_DIR\\_${index}_2.png"))
        val distance = distance(Point(startW, startH), Point(endW, endH))
        println("distance:" + distance)
        call(distance * MAGIC_NUMBER)//magic number
        index++
        return buffedImageResult
//    getFace(img)
    }

    for (h in 200 until bufferedImage.height) {
        for (w in 0 until bufferedImage.width) {
            val rgbValue = bufferedImage.getRGB(w, h)
            val color = Color(rgbValue)
            if (notBg(color, bgColor, bgColorEnd)) {
                if (count == 40) {
                    endW = w
                    endH = h + 10
                    findStart = true
                }
                if (findStart) {
//                    print("color.red:${color.red} G:${color.green} B:${color.blue}")
                    if (isStart(color)) {
                        startW = w
                        startH = h
                        findStart = false
                        //目标区域比起跳点y轴小的情况,需要重新查找
                        if (endW in startW - 50..startW + 60) {
                            refind = h + 20
                        }
                    }
                }
                count++
//                bufferedImage.setRGB(w, h, 0)
            }

        }
    }

    if (refind > 0 && !USE_OPENCV) {
        count = 0
        for (h in 200 until bufferedImage.height) {
            for (w in 0 until bufferedImage.width) {
                val rgbValue = bufferedImage.getRGB(w, h)
                val color = Color(rgbValue)
                if (notBg(color, bgColor, bgColorEnd)) {
                    if (count == 40) {
                        endW = w
                        endH = h + 10
                        if (endW in startW - 50..startW + 60) {
                            count = 0
                        }
                    }
                    count++
                }

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
    if (IS_SAVE_HISTORY)
        ImageIO.write(bufferedImage, "png", File("$SCREENSHOT_LOCATION_OUT_DIR\\_${index}_2.png"))
    val distance = distance(Point(startW, startH), Point(endW, endH))
    println("distance:" + distance)
    call(distance * MAGIC_NUMBER)//magic number
    index++
    return bufferedImage
}

fun doCanny(frame: Mat): Mat {
    // init
    val grayImage = Mat()
    val detectedEdges = Mat()
    val threshold = 10.0
    // convert to grayscale
    Imgproc.cvtColor(frame, grayImage, Imgproc.COLOR_BGR2GRAY)
    // reduce noise with a 3x3 kernel
    Imgproc.blur(grayImage, detectedEdges, Size(3.0, 3.0))
    // canny detector, with ratio of lower:upper threshold of 3:1
    Imgproc.Canny(detectedEdges, detectedEdges, threshold, threshold * 3)
    // using Canny's output as a mask, display the result
    val dest = Mat()
    frame.copyTo(dest, detectedEdges)
    return dest
}

fun main(args: Array<String>) {
    if (USE_OPENCV) System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
    val bufferedImage = ImageIO.read(File(SCREENSHOT_LOCATION))
    val newImage = BufferedImage(675, 1200, bufferedImage.getType())
    val gTemp = newImage.graphics
    gTemp.drawImage(bufferedImage, 0, 0, 675, 1200, null)
    gTemp.dispose()
//    bufferedImage = newImage
    runAI(newImage)
}