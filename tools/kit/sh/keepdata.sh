#!/sbin/sh 
 
 # MMC Disk
./parted /dev/block/mmcblk0 --script unit s print quit > /tmp/HuaweiFileGPT-MMC.txt
 
# UFS disk
./parted /dev/block/sdd --script unit s print quit > /tmp/HuaweiFileGPT-UFS.txt


ls -la /dev/block/bootdevice/by-name  | grep "\->" > /tmp/HuaweiFilePart.txt


chown media_rw:media_rw  /tmp/HuaweiFilePart.txt
chown media_rw:media_rw /tmp/HuaweiFileGPT-MMC.txt
chown media_rw:media_rw /tmp/HuaweiFileGPT-UFS.txt
