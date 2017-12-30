package Opencv

import App.SCREENSHOT_LOCATION_OUT_DIR
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import org.opencv.highgui.Highgui
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.objdetect.CascadeClassifier
import org.opencv.features2d.DescriptorExtractor
import org.opencv.features2d.FeatureDetector
import org.opencv.features2d.Features2d
import org.opencv.features2d.DescriptorMatcher.BRUTEFORCE
import java.util.ArrayList
import jdk.nashorn.internal.objects.NativeRegExp.source
import org.opencv.core.Scalar
import org.opencv.core.Core
import org.opencv.core.Core.MinMaxLocResult
import org.opencv.core.Mat










/**
 * des
 * @Date: Created by uglyer in 2017/12/31.
 */

fun main(args: Array<String>) {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
    val filePath = SCREENSHOT_LOCATION_OUT_DIR
    val source = Imgcodecs.imread("${filePath}_50.png")//读图像
    val template = Imgcodecs.imread("${filePath}start.png")

    val outputImage = Mat()
    val machMethod = Imgproc.TM_CCOEFF
    //Template matching method
    Imgproc.matchTemplate(source, template, outputImage, machMethod)
    val mmr = Core.minMaxLoc(outputImage)
    val matchLoc = mmr.maxLoc
    //Draw rectangle on result image
    Imgproc.rectangle(source, matchLoc, Point(matchLoc.x + template.cols(),
            matchLoc.y + template.rows()), Scalar(255.0, 0.0, 0.0))
    Imgcodecs.imwrite(filePath + "sonuc.jpg", source)
    println("Complated.")
//    getFace(img)
//    val new_img = doCanny(img)
//    Imgcodecs.imwrite("F:\\SOFT\\android\\sdk\\platform-tools\\out\\_50_cv.png", new_img)//写图像

//    val new_img_bg = doBackgroundRemoval(img)
//    Imgcodecs.imwrite("F:\\SOFT\\android\\sdk\\platform-tools\\out\\_50_cv_bg.png", new_img_bg)//写图像
}

private fun doBackgroundRemoval(frame: Mat): Mat {
    // init
    val hsvImg = Mat()
    val hsvPlanes = ArrayList<Mat>()
    val thresholdImg = Mat()
    val thresh_type = Imgproc.THRESH_BINARY_INV
    // threshold the image with the average hue value
    hsvImg.create(frame.size(), CvType.CV_8U)
    Imgproc.cvtColor(frame, hsvImg,
            Imgproc.COLOR_BGR2HSV)
    Core.split(hsvImg, hsvPlanes)

    // get the average hue value of the image
    val average = Core.mean(hsvPlanes[0])
    val threshValue = average.`val`[0]
    Imgproc.threshold(hsvPlanes[0], thresholdImg, threshValue, 179.0, thresh_type)
    Imgproc.blur(thresholdImg, thresholdImg, Size(5.0, 5.0))
    // dilate to fill gaps, erode to smooth edges
    Imgproc.dilate(thresholdImg, thresholdImg, Mat(), Point(-1.0, -1.0), 1)
    Imgproc.erode(thresholdImg, thresholdImg, Mat(), Point(-1.0, -1.0), 3)
    Imgproc.threshold(thresholdImg, thresholdImg, threshValue, 179.0, Imgproc.THRESH_BINARY)
    // create the new image
    val foreground = Mat(frame.size(), CvType.CV_8UC3, Scalar(255.0, 255.0, 255.0))
    thresholdImg.convertTo(thresholdImg, CvType.CV_8U)
    frame.copyTo(foreground, thresholdImg)//掩膜图像复制
    return foreground
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

fun getFace(src: Mat): MatOfRect {
    val result = src.clone()
    if (src.cols() > 1000 || src.rows() > 1000) {
        Imgproc.resize(src, result, Size((src.cols() / 3).toDouble(), (src.rows() / 3).toDouble()))
    }

    val faceDetector = CascadeClassifier("${SCREENSHOT_LOCATION_OUT_DIR}/resource/haarcascade_frontalface_alt2.xml")
    val objDetections = MatOfRect()
    faceDetector.detectMultiScale(result, objDetections)

    return objDetections
}