#!/sbin/sh

echo "HWGSIPartition - Part 4/5"

#enable root
adb root

#push all scripts and binaries
adb push ".\HW\clearpart.sh" /tmp/clearpart.sh
adb push ".\HW\formatpart.sh" /tmp/formatpart.sh
adb push ".\HW\makepart.sh" /tmp/makepart.sh
adb push ".\HW\backup.sh" /tmp/backup.sh
adb push .\arm64\parted /tmp
adb push .\arm64\mkfs.f2fs /tmp
adb push .\arm64\fsck.f2fs /tmp

adb shell "cd /tmp;chmod 777 parted;chmod 777 mkfs.f2fs fsck.f2fs"
adb shell "cd /tmp;chmod 777 clearpart.sh;chmod 777 formatpart.sh;chmod 777 makepart.sh;chmod 777 backup.sh"


#Remove all data in /data and generate image backup
pause "Start backup img (this operation is long) ?"
read a
adb shell "mkdir /data/HW-IMG/"
adb shell "cd /tmp;./backup.sh"
adb pull "/data/HW-IMG/" 
adb shell "umount /sdcard;umount /data"

#Time to resize partition
pause "Start resize partition ?"
read b

adb shell "cd /tmp;./clearpart.sh"
adb shell "cd /tmp;./makepart.sh"
adb shell "cd /tmp;./formatpart.sh"

#Copy flash cmd
copy .\HW\5-FlashIMG.sh

#Flash userdata with fastboot
pause "Reboot to bootloader ?" 
read c

adb reboot bootloader
fastboot flash userdata ./userdata/userdata-huawei.img

pause "Please reboot to twrp and lunch 5-FlashIMG.sh" 
read d


