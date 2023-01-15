echo "HWGSIPartition - Part 2/2"

adb root


adb shell "umount /cache;umount /system;umount /vendor;umount /cust;umount /product;umount /version;umount /data;umount /sdcard"
ping -n 5 127.0.0.1 > null

adb push ".\Documents\clearpart.sh" /tmp/clearpart.sh
adb push ".\Documents\formatpart.sh" /tmp/formatpart.sh
adb push ".\Documents\makepart.sh" /tmp/makepart.sh
adb push .\arm64\parted /tmp
adb push .\arm64\mke2fs /tmp
adb push .\arm64\mkfs.f2fs /tmp

adb shell "cd /tmp;chmod 777 parted;chmod 777 mke2fs;chmod 777 mkfs.f2fs"
adb shell "cd /tmp;chmod 777 clearpart.sh;chmod 777 formatpart.sh;chmod 777 makepart.sh"

pause "Start process ?"

adb shell "cd /tmp;./clearpart.sh"
ping -n 5 127.0.0.1 > null

adb shell "cd /tmp;./makepart.sh"
ping -n 5 127.0.0.1 > null

adb shell "cd /tmp;./formatpart.sh"
pause "Reboot to bootloader ?"

adb reboot bootloader

ping -n 5 127.0.0.1 > null

fastboot flash recovery_ramdisk .\img\RECOVERY_RAMDIS.img

ping -n 5 127.0.0.1 > null

fastboot erase userdata

ping -n 5 127.0.0.1 > null

fastboot flash system LeaOS-20221213-iceows-ane.img

ping -n 5 127.0.0.1 > null

