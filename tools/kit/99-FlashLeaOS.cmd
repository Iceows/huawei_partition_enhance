

fastboot flash recovery_ramdisk .\twrp\RECOVERY_RAMDIS.img

ping -n 5 127.0.0.1 > null

fastboot flash system LeaOS-20221213-iceows-ane.img

ping -n 5 127.0.0.1 > null

