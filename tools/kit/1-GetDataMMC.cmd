echo "HWGSIPartition - Part 1/5"

adb root
adb push ./arm64/parted /tmp
adb push ./sh/keepdata-mmc.sh /tmp

adb shell "cd /tmp;chmod 777 parted;chmod 777 keepdata-mmc.sh"
adb shell "cd /tmp;./keepdata-mmc.sh"

adb pull /tmp/HuaweiFileGPT.txt ./phone/HuaweiFileGPT.txt
adb pull /tmp/HuaweiFilePart.txt ./phone/HuaweiFilePart.txt




