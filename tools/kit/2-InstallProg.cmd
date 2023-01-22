echo "HWGSIPartition - Part 2/5"

adb root

adb install HWGSIPartition.apk
adb shell "mkdir /sdcard/Documents"
adb shell "mkdir /sdcard/Documents/HW"
adb push ./phone/HuaweiFileGPT-MMC.txt /sdcard/Documents/HW
adb push ./phone/HuaweiFileGPT-UFS.txt /sdcard/Documents/HW
adb push ./phone/HuaweiFilePart.txt /sdcard/Documents/HW

