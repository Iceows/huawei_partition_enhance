#!/sbin/sh 
 
# UFS disk
./parted /dev/block/sdd --script unit s print quit > /tmp/HuaweiFileGPT.txt
ls -la /dev/block/bootdevice/by-name  | grep "\->" | cut -c58- > /tmp/HuaweiFilePart.txt
chown media_rw:media_rw  /tmp/HuaweiFilePart.txt
chown media_rw:media_rw /tmp/HuaweiFileGPT.txt
