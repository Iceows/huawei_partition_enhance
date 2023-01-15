echo "HWGSIPartition - Part 2/4"

adb root

adb install HWGSIPartition.apk
adb shell "mkdir /sdcard/Documents"
adb shell "mkdir /sdcard/Documents/HW"
adb push ./phone/HuaweiFileGPT.txt /sdcard/Documents/HW
adb push ./phone/HuaweiFilePart.txt /sdcard/Documents/HW

