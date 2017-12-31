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
    var endH2 = 0
    val bgColor = Color(bufferedImage.getRGB(0, 0))
    val bgColorEnd = Color(bufferedImage.getRGB(0, bufferedImage.width - 100))
    println("bgColor R:${bgColor.red} G:${bgColor.green} B:${bgColor.blue}")

    if (!USE_OPENCV) {
        throw Exception("最后一版支持不用 OpenCV 的是 d556d1d")
    }
    ImageIO.write(bufferedImage, "png", File(SCREENSHOT_LOCATION))
    //去杂色
    bufferedImage.findByCondition(isBreak = false, startH = 200,
            condition = { w, h, color -> !color.notBg(bgColor, bgColorEnd) }) { w, h, color ->
        bufferedImage.setRGB(w, h, Color(0, 0, 255).rgb)
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
    val new_img = toEnd.doCanny()
    Imgcodecs.imwrite(SCREENSHOT_LOCATION + "_end.jpg", new_img)
    //Draw rectangle on result image
    Imgproc.rectangle(source, matchLoc, org.opencv.core.Point(matchLoc.x + template.cols(),
            matchLoc.y + template.rows()), Scalar(255.0, 0.0, 0.0))
    Imgcodecs.imwrite(SCREENSHOT_LOCATION + "_start.jpg", source)
    val startLeftTop = Point(matchLoc.x.toInt(), matchLoc.y.toInt())
    val startRightBottom = Point((matchLoc.x + template.cols()).toInt(), (matchLoc.y + template.rows()).toInt())
    val bufferedImageEnd = ImageIO.read(File(SCREENSHOT_LOCATION + "_end.jpg"))

//        bufferedImageEnd.findByCondition(startH = 200,
//                condition = { w, h, color ->
//                    color.notBlack() && (w > startRightBottom.x
////                        && h > startRightBottom.y
//                            || w < startLeftTop.x)
////                        && h < startLeftTop.y
//                            && endH == 0
//                }) { w, h, color ->
//            endW = w
//            endH = h
//            println("endW:${w},endH:${h}")
//            println("color.red:${color.red} G:${color.green} B:${color.blue} a:${color.alpha}")
//        }

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
                endH = h
                println("color.red:${color.red} G:${color.green} B:${color.blue} a:${color.alpha}")
            }
        }
    }

    val buffedImageResult = ImageIO.read(File(SCREENSHOT_LOCATION + "_start.jpg"))
    buffedImageResult.paintPoint(endW, endH, Color(0, 255, 0))

    bufferedImageEnd.findByCondition(inW = endW, startH = endH + 5,
            condition = { w, h, color -> color.notBlack() }) { w, h, color ->
        endH2 = h
        println("endH2:${endH2}")
        println("color.red:${color.red} G:${color.green} B:${color.blue} a:${color.alpha}")
    }

    buffedImageResult.paintPoint(endW, endH2, Color(0, 255, 0))

    println("endH = (endH2 - endH) / 2:${endH} = (${endH2} - ${endH}) / 2")

    endH += (endH2 - endH) / 2
    startW = startLeftTop.x + 32
    startH = startRightBottom.y - 20

    buffedImageResult.paintPoint(endW, endH, Color(255, 0, 0))
    buffedImageResult.paintPoint(startW, startH, Color(255, 255, 255))

    println("Complated.")
    ImageIO.write(buffedImageResult, "png", File(SCREENSHOT_LOCATION_OUT))
    if (IS_SAVE_HISTORY)
        ImageIO.write(buffedImageResult, "png", File("$SCREENSHOT_LOCATION_OUT_DIR\\_${index}_2.png"))
    val distance = distance(Point(startW, startH), Point(endW, endH))
    println("distance:" + distance)
    call(distance * MAGIC_NUMBER)//magic number
    index++
    return buffedImageResult
}

//test
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