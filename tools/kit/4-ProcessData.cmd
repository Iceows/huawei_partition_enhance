echo "HWGSIPartition - Part 2/5"

REM enable root
adb root

REM push all scripts and binaries
adb push ".\HW\clearpart.sh" /tmp/clearpart.sh
adb push ".\HW\formatpart.sh" /tmp/formatpart.sh
adb push ".\HW\makepart.sh" /tmp/makepart.sh
adb push ".\HW\backup.sh" /tmp/backup.sh
adb push .\arm64\parted /tmp
adb push .\arm64\mke2fs /tmp
adb push .\arm64\mkfs.f2fs /tmp
adb shell "cd /tmp;chmod 777 parted;chmod 777 mke2fs;chmod 777 mkfs.f2fs"
adb shell "cd /tmp;chmod 777 clearpart.sh;chmod 777 formatpart.sh;chmod 777 makepart.sh;chmod 777 backup.sh"

REM unmount all partitions (but not userdata)
adb shell "umount /system;umount /vendor;umount /cust;umount /product;umount /version"

REM Remove all data in /data and generate image backup
pause "Start backup img (this operation is long) ?"
adb shell "cd /tmp;./backup.sh"
adb pull "/data/" 
adb shell "umount /data;umount /sdcard"
ping -n 5 127.0.0.1 > null

REM Time to resize partition
pause "Start resize partition ?"
adb shell "cd /tmp;./clearpart.sh"
adb shell "cd /tmp;./makepart.sh"
adb shell "cd /tmp;./formatpart.sh"

REM Copy flash cmd
copy .\HW\5-FlashIMG.cmd .

pause "Reboot to bootloader ?"
adb reboot bootloader
