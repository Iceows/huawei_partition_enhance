############################################################
#
#   Huawei GSI Partition kit - Version 0.6
#   Par Iceows
#
############################################################

0) ATTENTION ----- Cette opération supprime la partition data (vos données), vous devez faire une sauvegarde avant -----------

1) Démarrer sous TWRP
2) Executer le script suivant sous cmd en mode administrateur
	1-GetData.cmd
3) Démarrer le telephone sous Android : adb reboot
4) Activer le partage de fichier via USB et le debuggage via adb (dans le mode Developer)
   Executer le script suivant, il va installer le programme HWGSIPartition et les fichiers de parametrage qui correspondent a votre appareil
	2-InstallProg.cmd
5) Executer le programme HWGSIPartition, vous devez donner les autorisations d'accés a tous les fichiers (privacy/permission manager/Files and media)
	Cliquer sur Read Phone GPT
	Cliquer sur Refactor GPT
	Vous dever avoir apparaitre a l'ecran les infos de partitions 
	Fermer HWGSIPartition
6) Recuperer les fichiers générés par le programme HWGSIPartition et vérifier que vous avez bien un repertoire HW désormais avec 7 fichiers.
	3-GetIMG.cmd
7) Démarrer sous TWRP. Vérifier que votre partition data est bien montée
8) Executer le script qui va retailler les partitions
	4-ProcessData.cmd
9) Executer le script qui va reflasher les images des partitions modifiés
	5-FlashIMG.cmd
10) Démarrer sous TWRP et verifier que vous pouvez monter la partition data et la partition vendor
11) Démarrer en mode bootloader

------------------

Vous pouvez desormais installer votre twrp et votre image system LeaOS



