echo "HWGSIPartition - Part 2/2"

adb root
adb pull "/data/media/0/Documents/"

adb shell "umount /system;umount /vendor;umount /cust;umount /product;umount /version;umount /data"

adb push ".\Documents\clearpart.sh" /tmp/clearpart.sh
adb push ".\Documents\formatpart.sh" /tmp/formatpart.sh
adb push ".\Documents\makepart.sh" /tmp/makepart.sh

adb shell "cd /tmp;chmod 777 clearpart.sh;chmod 777 formatpart.sh;chmod 777 makepart.sh"

adb shell "./tmp/clearpart.sh"
adb shell "./tmp/makepart.sh"
adb shell "./tmp/formatpart.sh"

adb reboot bootloader

ping -n 5 127.0.0.1 > null

fastboot flash version .\img\VERSION.img
fastboot flash cust .\img\CUST.img
fastboot flash product .\img\PRODUCT.img
fastboot flash vendor .\img\VENDOR.img

fastboot flash system LeaOS-20221213-iceows-pra.img
