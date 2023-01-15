echo "HWGSIPartition - Part 1/2"

adb root
adb push ./arm64/parted /tmp
adb push ./sh/keepdata.sh /tmp

adb shell "cd /tmp;chmod 777 parted;chmod 777 keepdata.sh"
adb shell "cd /tmp; bash /tmp/keepdata.sh"

adb pull /tmp/HuaweiFileGPT.txt ./phone/HuaweiFileGPT.txt
adb pull /tmp/HuaweiFilePart.txt ./phone/HuaweiFilePart.txt




