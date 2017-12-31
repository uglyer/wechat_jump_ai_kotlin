
# wechat_jump_ai_kotlin
---

微信小游戏 跳一跳 kotlin AI 并用 OpenCV 自动找起始点和目标点，目标坐标，使用 ADB 命令完成自动跳转.

# 成果
---

![](https://raw.githubusercontent.com/uglyer/wechat_jump_ai_kotlin/master/pic/show.jpg?)

# 使用方法
---

1. 在电脑上下载好adb
1. 打开安卓手机的usb调试模式并授权连接的电脑
1. 打开微信跳一跳，并点击开始
1. 在`Constans.kt`中配置好adb路径与截图路径还有需要设置起跳点的图像路径，定时器时间、移动系数 可以根据实际情况适当调整.
1. 运行 Main.kt 中的主函数(启用 OpenCv 需要添加VM参数 -Djava.library.path=F:\code\gua\wechat_jump_ai_kotlin\lib\x64)
1. 会自动识别当前坐标和目标点，完成跳跃

# 跳一跳
---

微信小程序可以玩游戏了，我们来破解一下《跳一跳》这个官方出品的小游戏吧。



# 思路
---

用usb调试安卓手机，用adb截图并用 OpenCV 自动找起始点和目标点，测量距离，然后计算按压时间后模拟按压。

```bash
$ adb shell input swipe <x1> <y1> <x2> <y2> [duration(ms)] (Default: touchscreen) # 模拟长按
$ adb shell screencap <filename> # 保存截屏到手机
$ adb pull /sdcard/screen.png # 下载截屏文件到本地
```

1. 得到手指按的时间 t
1. 时间 = 距离 / 速度(常量) t = L / k
1. L = p2 - p1
1. 获取到起始点和结束点的坐标

# 源码
---

开发环境： Kotlin, IetelliJ IDEA

<https://github.com/uglyer/wechat_jump_ai_kotlin>


# 参考
---

<https://github.com/easyworld/PlayJumpJumpWithMouse>
<https://github.com/iOSDevLog/JumpJump>

# License
---

wechat_jump_ai_kotlin is released under the GPL V3 license. See LICENSE for details.