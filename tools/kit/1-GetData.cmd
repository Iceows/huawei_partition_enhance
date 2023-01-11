echo "HWGSIPartition - Part 1/2"

adb root
adb push ./arm64/parted /cache
adb push ./sh/keepdata.sh /cache

adb shell "cd /cache;chmod 777 parted;chmod 777 keepdata.sh"
adb shell "cd /cache; bash /cache/keepdata.sh"

adb install HWGSIPartition.apk

