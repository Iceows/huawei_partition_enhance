#!/sbin/sh 
 
./parted /dev/block/mmcblk0 --script unit s print quit > /data/media/0/Documents/HuaweiFileGPT.txt
ls -la /dev/block/platform/hi_mci.0/by-name/  | grep "\->" | cut -c46- > /data/media/0/Documents/HuaweiFilePart.txt

chown media_rw:media_rw  /data/media/0/Documents/HuaweiFilePart.txt
chown media_rw:media_rw /data/media/0/Documents/HuaweiFileGPT.txt

