package App

import java.io.IOException
import java.util.*

val r = Random()

fun call(timeMilli: Double) {
    try {
        val locationX = r.nextInt(720)
        val locationY = r.nextInt(720)
        val addX = r.nextInt(10)
        val addY = r.nextInt(10)
        Runtime.getRuntime().exec(ADB_PATH + " shell input touchscreen swipe ${locationX} ${locationY} ${locationX + addX} ${locationY + addY} ${timeMilli.toInt()}")
    } catch (e: IOException) {
        e.printStackTrace()
    }

}

fun printScreen() {
    try {
        val p1 = Runtime.getRuntime().exec(ADB_PATH + " shell screencap -p /sdcard/screenshot.png")
        p1.waitFor()
        val p2 = Runtime.getRuntime().exec(ADB_PATH + " pull /sdcard/screenshot.png " + SCREENSHOT_LOCATION)
        p2.waitFor()
    } catch (e: IOException) {
        e.printStackTrace()
    } catch (e: InterruptedException) {
        e.printStackTrace()
    }

}