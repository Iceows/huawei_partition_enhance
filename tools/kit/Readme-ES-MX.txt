###########################################################
#
# Huawei GSI Partition kit - Version 0.62
# By Iceows
#
###########################################################

0) Hacer una copia de seguridad /data (De la particion entera, porque se formateara)

1) Bootea en el TWRP, conecta tu telefono al pc por cable
2) En tu pc, ejecuta el siguiente archivo
	1-GetData.cmd
3) Enciende el telefono normalmente (puedes escribir adb reboot)
4) En el telefono habilita el modo de transferencia USB y tambien la depuracion por usb (puedes habilitarlo en el modo Desarollador)
   Ejecuta el siguiente comando, El cual instalara el programa HWGSIPartition y las configuracion correspondientes para tu dispositivo
	2-InstallProg.cmd
5) En tu Android ejeecuta el programa HWGSIPartition , Antes que nada, debes darle acceso/permisos a todos los archivos (Ve a informacion de la app > permisos y dale los permisos.)
	Toca en Read Phone GPT
	Segundo paso toca en  GPT Refactor
	Ahora deberia aparecerte la informacion de la particion en la pantalla
	Cierra el programa HWGSIPartition
6) Ahora recuperaremos los archivos que generamos con el programa HWGSIPartition,  y veremos que tienes en el directorio HW  now deberia tener 10 archivos. Ejecutaremos el:
	3-GetIMG.cmd
7) Bootea TWRP. Verifica que la particion /data esta montada correctamente
8) Ejecuremos el siguiente script, el cual redimensionara las particiones
	4-ProcessData.cmd
9) Ejecuremos el siguiente script, el cual reflasheara las particiones modificadas
	5-FlashIMG.cmd
10) Bootea TWRP y verifica nuevamente que puedas montar la particion /data y la particion  /vendor
11) Bootea en el modo bootloader

------------------

Ahora podras instalar tu particion LeaOs como imagen del sistema, o cualquier otra GSI
