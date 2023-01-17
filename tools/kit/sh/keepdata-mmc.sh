#!/sbin/sh 
 
 # MMC Disk
./parted /dev/block/mmcblk0 --script unit s print quit > /tmp/HuaweiFileGPT.txt
ls -la /dev/block/bootdevice/by-name  | grep "\->" | cut -c46- > /tmp/HuaweiFilePart.txt
chown media_rw:media_rw  /tmp/HuaweiFilePart.txt
chown media_rw:media_rw /tmp/HuaweiFileGPT.txt

