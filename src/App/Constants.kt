package App

/**
 * adb所在位置
 */
//val ADB_PATH = "/Users/iosdevlog/Library/Android/sdk/platform-tools/adb"
//val ADB_PATH = "D:\\soft\\adb\\adb.exe"
val ADB_PATH = "F:\\SOFT\\android\\sdk\\platform-tools\\adb.exe"

/**
 * 截屏文件所在位置
 */
val SCREENSHOT_LOCATION = "F:\\SOFT\\android\\sdk\\platform-tools\\s.png"
val SCREENSHOT_LOCATION_OUT = "F:\\SOFT\\android\\sdk\\platform-tools\\s_out.png"
val SCREENSHOT_LOCATION_OUT_DIR = "F:\\SOFT\\android\\sdk\\platform-tools\\out\\"

/**
 * 起始图查找位置
 */
val START_LOCATION = "F:\\SOFT\\android\\sdk\\platform-tools\\start.png"

/**
 * 是否使用opencv识别
 */
val USE_OPENCV = true

/**
 * 定时器时间,可以根据手机响应速度适当调整，单位毫秒
 */
val TIMER: Long = 5000

/**
 * 移动系数，如果跳的位置不准确，可以适当修改系数，
 * 我的手机是 1+3T 1080p，目前测试 2.21 合适.
 */
val MAGIC_NUMBER = 2.21

/**
 * 保存每次处理的截图,用于获取失败情况样本,目前版本识别没有出现失败,错误均出现在移动(点找对了,但是跳的位置不对).
 */
val IS_SAVE_HISTORY = true


