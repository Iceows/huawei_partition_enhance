package com.android.hwgsipartition;

import android.util.Log;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ProcessFileGPT
{
    // Partitions
    Partition[] objFullPart = new Partition[100];
    int iNbFullPart=0;
    Partition[] objModPart = new Partition[50];
    int iNbModPart=0;
    Partition[] objProcPart = new Partition[50];
    int iNbProcPart=0;

    // Point de montage
    List<MountPoint> lstMount = new ArrayList<>();


    // Information pour determiner chaque item dans le fichier GPT
    Integer[] iInfosPos = new Integer[20];


    // Size of a sector
    double iSizeSector=0;

    // Name of the disk
    // for example /dev/block/mmcblk0
    String szNameDisk="";

    public Partition[] getFullPart() {
        return objFullPart;
    }
    public int getNbFullPart() {
        return iNbFullPart;
    }
    public Partition[] getModPart() {
        return objModPart;
    }
    public int getNbModPart() {
        return iNbModPart;
    }
    public Partition[] getProcPart() {
        return objProcPart;
    }
    public int getNbProcPart() {
        return iNbProcPart;
    }


    // en parametre le resultat du print parted
    public boolean StartProcess(String sMyInitialGPT, String sMyPartition, Integer iSize) {
        double iNewSystemSize=7168000; // Pour optimiser la vitesse multiple de 2048

        // Charge la liste des points de montage
        InitListMount(sMyPartition);

        // Recupere le nombre de partition total et les infos pour chaque partition
        iNbFullPart=KeepFullPart(sMyInitialGPT,sMyPartition);

        if (iNbFullPart>0) {
            Log.println(Log.INFO, "ReadGPT", "iNbFullPart > 0 : " + String.valueOf(iNbFullPart));

            // System size - size of sector = 512 bytes or 4096 (UFS)
            if (iSize==0)
                iNewSystemSize=(1073741824/iSizeSector)*2; // 2 Go
            if (iSize==1)
                iNewSystemSize=(1073741824/iSizeSector)*2.5; // 2.5 Go
            if (iSize==2)
                iNewSystemSize=(1073741824/iSizeSector)*3;  // 6291456 =  3 Go
            if (iSize==3)
                iNewSystemSize=(1073741824/iSizeSector)*3.5; // 7340032 = 3.5 Go
            if (iSize==4)
                iNewSystemSize=(1073741824/iSizeSector)*4; // 8388608 = 4 Go

            iNbModPart=KeepModPart();
            if (iNbModPart>0) {
                Log.println(Log.INFO, "ReadGPT", "iNbModPart > 0 :" + String.valueOf(iNbModPart));
                if (iNbModPart<3)
                    return false;
                iNbProcPart=KeepProcPart((int)iNewSystemSize);
                Log.println(Log.INFO, "ReadGPT", "iNbProcPart :" + String.valueOf(iNbProcPart));
                if (iNbProcPart==iNbModPart) {
                    return true;
                }
            }
        }
        return false;
    }

    /*
    ## SAFETY CHECK ###########################################################
    # Check if ${BLOCK_DEVICE_PATH} is mounted, if yes, then exit
        if [[ $(/bin/mount | grep -q "${BLOCK_DEVICE_PATH}") ]]; then
            echo "BLOCK DEVICE ${BLOCK_DEVICE_PATH} ALREADY MOUNTED"
            exit 1;
        fi

     ## SAFETY CHECK ###########################################################
        if [[ $(/sbin/blkid ${BLOCK_DEVICE_PATH}) ]]; then
            echo "BLOCK DEVICE ALREADY INITIALIZED, WILL NOT PROCEED WITH SCRIPT";
            exit 1;
        fi
    */

    public String GeneratedScriptRM() {
        String szClearCmd="";

        szClearCmd = szClearCmd +"#!/sbin/sh \n";
        szClearCmd = szClearCmd +" \n";
        szClearCmd = szClearCmd + String.format("./parted %s --script \\\n", szNameDisk);
        if (iNbProcPart>1) {
            for (int i = 0; i < iNbProcPart; i++) {
                szClearCmd = szClearCmd + String.format("rm %d \\\n", objProcPart[i].getId());
            }
            Log.println(Log.INFO, "ReadGPT", "  " + szClearCmd);
        }
        szClearCmd = szClearCmd +"p free \\\nquit\n";
        return szClearCmd;
    }


    public String GeneratedScriptRestore() {
        String szClearCmd="";

        szClearCmd = szClearCmd +"echo \"HWGSIPartition - Part 5/5\n";
        szClearCmd = szClearCmd +" \n";
        if (iNbProcPart>1) {
            for (int i = 0; i < iNbProcPart; i++) {
                if (!objProcPart[i].getName().equals("userdata") &&
                        !objProcPart[i].getName().equals("system") &&
                        !objProcPart[i].getName().equals("system_a") )
                    szClearCmd = szClearCmd + String.format("fastboot flash %s .\\HW-IMG\\%s.img  \n", objProcPart[i].getName(),objProcPart[i].getName());
            }
        }
        Log.println(Log.INFO, "ReadGPT", "  " + szClearCmd);

        return szClearCmd;
    }

    //mkfs.ext4 /dev/block/mmcblk0p59
    //mkfs.f2fs /dev/block/mmcblk0p60
    public String GeneratedScriptFormat() {
        String szClearCmd="";

        szClearCmd = szClearCmd +"#!/sbin/sh \n";
        szClearCmd = szClearCmd +" \n";
        if (iNbProcPart>1) {
            for (int i = 0; i < iNbProcPart; i++) {
                if ((objProcPart[i].getTypeFs().equals("ext2")) || (objProcPart[i].getTypeFs().equals("ext4")))
                    szClearCmd = szClearCmd + String.format("/sbin/mke2fs -t %s %s\n", objProcPart[i].getTypeFs(),objProcPart[i].getPname());
                if (objProcPart[i].getTypeFs().equals("f2fs")) {
                    szClearCmd = szClearCmd + String.format("/tmp/mkfs.f2fs -l data %s\n", objProcPart[i].getPname());
                    szClearCmd = szClearCmd + String.format("/tmp/fsck.f2fs %s\n", objProcPart[i].getPname());
                }
                //if (objProcPart[i].getTypeFs().equals(""))
                //    szClearCmd = szClearCmd + String.format("/sbin/mkfs.erofs %s\n", objProcPart[i].getPname());
            }
        }

        Log.println(Log.INFO, "ReadGPT", "  " + szClearCmd);

        return szClearCmd;
    }

    //dd if=/dev/block/mmcblk0p51 of=/sdcard/preas.img
    //dd if=/dev/block/mmcblk0p52 of=/sdcard/preavs.img
    public String GeneratedScriptBackup() {
        String szClearCmd = "";

        szClearCmd = szClearCmd +"#!/sbin/sh\n";
        szClearCmd = szClearCmd +"\n";
        szClearCmd = szClearCmd +"# to list all partitions\n";
        szClearCmd = szClearCmd +"# adb root\n";
        szClearCmd = szClearCmd +"# adb shell\n";
        szClearCmd = szClearCmd +"# ls -la /dev/block/bootdevice/by-name  | grep \"\\->\" \n";
        szClearCmd = szClearCmd +"\n";
        szClearCmd = szClearCmd +"rm -rf /data/HW-IMG/*\n";
        szClearCmd = szClearCmd +"\n";


        if (iNbProcPart>1) {
            for (int i = 0; i < iNbProcPart; i++) {
                // don't backup system or userdata
                if (!objProcPart[i].getName().equals("userdata") &&
                        !objProcPart[i].getName().equals("system") &&
                        !objProcPart[i].getName().equals("system_a") ) {
                    szClearCmd = szClearCmd + String.format("umount /%s \n", objProcPart[i].getName());
                    szClearCmd = szClearCmd + String.format("dd if=%s of=/data/HW-IMG/%s.img \n", objProcPart[i].getPname(), objProcPart[i].getName());

                }
            }
        }
        return szClearCmd;
    }

    //mkpart system ext2 7153MB 11153MB
    //set 59 msftdata on
    //name 59 system-b
    public String GeneratedScriptMake() {
        String szClearCmd="";

        szClearCmd = szClearCmd +"#!/sbin/sh \n";
        szClearCmd = szClearCmd +" \n";
        szClearCmd = szClearCmd + String.format("./parted %s --script \\\n", szNameDisk);
        szClearCmd = szClearCmd +"unit s \\\n";
        if (iNbProcPart>1) {
            for (int i = 0; i < iNbProcPart; i++) {
                szClearCmd = szClearCmd + String.format("mkpart %s %s %ds %ds \\\n", objProcPart[i].getName(),objProcPart[i].getTypeFs(),objProcPart[i].getStartSectorPos(),objProcPart[i].getEndSectorPos());
                szClearCmd = szClearCmd + String.format("set %d %s on \\\n", objProcPart[i].getId(),objProcPart[i].getFlagFs());
                szClearCmd = szClearCmd + String.format("name %d %s \\\n", objProcPart[i].getId(),objProcPart[i].getName());
            }
            Log.println(Log.INFO, "ReadGPT", "  " + szClearCmd);
        }
        szClearCmd = szClearCmd +"p free \\\nquit\n";
        return szClearCmd;
    }

    public int KeepProcPart(int iNewSystemSize ) {
        int iCurrentItem=0;
        int iNewStartSector=0;
        int iNewEndSector=0;
        int iNewNbSector=0;
        int iOldNbSector=0;
        int iIncreaseSize=0; // Nd de secteur a augmenter

        objProcPart[iCurrentItem] = new Partition(objModPart[iCurrentItem]);
        iOldNbSector=objProcPart[iCurrentItem].getNbSector();
        iIncreaseSize=iNewSystemSize-iOldNbSector;
        iNewEndSector=objProcPart[iCurrentItem].getEndSectorPos()+iIncreaseSize;
        iNewNbSector=iNewSystemSize;
        objProcPart[iCurrentItem].setEndSectorPos(iNewEndSector);
        objProcPart[iCurrentItem].setNbSector(iNewNbSector);
        objProcPart[iCurrentItem].setTypeFs("ext2");
        objProcPart[iCurrentItem].LogInfo();


        for (int i=1;i<iNbModPart-1;i++) {
            iCurrentItem++;
            objProcPart[iCurrentItem] = new Partition(objModPart[iCurrentItem]);
            iNewStartSector=objProcPart[iCurrentItem].getStartSectorPos()+iIncreaseSize;
            iNewEndSector=objProcPart[iCurrentItem].getEndSectorPos()+iIncreaseSize;
            objProcPart[iCurrentItem].setStartSectorPos(iNewStartSector);
            objProcPart[iCurrentItem].setEndSectorPos(iNewEndSector);
            String szTypeFS=objProcPart[iCurrentItem].getTypeFs();
            // erofs or ext4
            if (szTypeFS.equals(""))
                objProcPart[iCurrentItem].setTypeFs("ext4");
            objProcPart[iCurrentItem].LogInfo();
        }


        // userdata - just check if the last part is userdata
        iCurrentItem++;
        if (!objModPart[iCurrentItem].getName().equals("userdata"))
            return 0;

        objProcPart[iCurrentItem] = new Partition(objModPart[iCurrentItem]);
        iNewStartSector=objProcPart[iCurrentItem].getStartSectorPos()+iIncreaseSize;
        iNewNbSector=objProcPart[iCurrentItem].getEndSectorPos()-iNewStartSector+1;
        objProcPart[iCurrentItem].setStartSectorPos(iNewStartSector);
        objProcPart[iCurrentItem].setNbSector(iNewNbSector);
        objProcPart[iCurrentItem].LogInfo();

        iCurrentItem++;
        iNbProcPart=iCurrentItem;
        return iNbProcPart;
    }

    public int KeepModPart() {
        boolean bStart=false;
        int iCurrentItem=0;
        boolean bAB=false;

        for (int i = 0; i < iNbFullPart; i++) {
            String szPartName=objFullPart[i].getName();

            bAB=false;
            if ((szPartName != null) &&  (!szPartName.isEmpty())) {
                if (szPartName.equals("system")) {
                    bStart = true;
                    bAB = false;
                }
                if (szPartName.equals("system_a")) {
                    bStart = true;
                    bAB = true;
                }
            }

            if (bStart) {
                objModPart[iCurrentItem] = new Partition(objFullPart[i]);
                iCurrentItem++;
            }
        }

        return iCurrentItem;
    }

    public int KeepFullPart(String sMyInitialGPT, String szMyPartitionMount) {
        Boolean bStartTable = false;
        String[] strNewPartition = new String[100];
        int numberOfItems = 0;
        int iCurrentItem=0;
        String[] strFullPartition = sMyInitialGPT.split("\n");
        String[] strFullPartitionMnt = szMyPartitionMount.split("\n");


        for (String szLine : strFullPartition) {

            // Lecture de chaque partition dans un tableau
            if (bStartTable) {
                Log.println(Log.INFO, "ReadGPT", "  " + szLine);
                strNewPartition[numberOfItems++] = szLine;
            }
            else
            {
                // Disk /dev/block/sdd: 15616000s
                // Sector size (logical/physical): 4096B/4096B
                if (szNameDisk.isEmpty())
                    if (szLine.startsWith("Disk")) {
                        szNameDisk=szLine.substring(5,szLine.indexOf(":"));
                    }
                if (szLine.startsWith("Sector size (logical/physical)")) {
                    String szTmp = szLine.substring(32);
                    String szSectorSize=szTmp.substring(0,szTmp.indexOf("/")-1);

                    iSizeSector=Integer.parseInt(szSectorSize);
                }
            }

            // Search l'entete
            if (szLine.startsWith("Number")) {
                int i1=0;

                if (szLine.endsWith("Flags"))
                    bStartTable = true;
                else
                    bStartTable = false;

                // On a trouvé l'entete, on recupere la position de chaque element
                if (bStartTable) {
                    iInfosPos[0]=0;

                    i1=szLine.indexOf("Start",i1);
                    if (i1==-1) return 0;
                    iInfosPos[1]=i1;

                    i1=szLine.indexOf("End",i1);
                    if (i1==-1) return 0;
                    iInfosPos[2]=i1;

                    i1=szLine.indexOf("Size",i1);
                    if (i1==-1) return 0;
                    iInfosPos[3]=i1;

                    i1=szLine.indexOf("File system",i1);
                    if (i1==-1) return 0;
                    iInfosPos[4]=i1;

                    i1=szLine.indexOf("Name",i1);
                    if (i1==-1) return 0;
                    iInfosPos[5]=i1;

                    i1=szLine.indexOf("Flags",i1);
                    if (i1==-1) return 0;
                    iInfosPos[6]=i1;

                    // End
                    iInfosPos[7]=szLine.length();
                }
            }

        }

        if (bStartTable!=true)
            return 0;

        Log.println(Log.INFO, "ReadGPT", "Start transform this partition :");

        if (numberOfItems>0) {
            for (String szLine : strNewPartition) {
                String szTmp;
                int iLineLength;

                // End scan
                if ((szLine == null) || (szLine.isEmpty())) {
                    return iCurrentItem;
                }

                iLineLength=szLine.length();
                // Error scan
                if (szLine.length()<80) {
                    Log.println(Log.WARN, "ReadGPT"," Incorrect line len : " + szLine.length() + " < 80");
                    return iCurrentItem;
                }

                objFullPart[iCurrentItem] = new Partition();

                // partition number
                szTmp=szLine.substring(iInfosPos[0], iInfosPos[1]-1).trim();
                if (!isEmpty(szTmp)) {
                    objFullPart[iCurrentItem].setId(Integer.parseInt(szTmp));
                }

                // Start sector
                szTmp=szLine.substring(iInfosPos[1], iInfosPos[2]-1).trim();
                if (!isEmpty(szTmp)) {
                    String szTmp1=szTmp.replace('s', ' ').trim();
                    objFullPart[iCurrentItem].setStartSectorPos(Integer.parseInt(szTmp1));
                }

                // End sector
                szTmp=szLine.substring(iInfosPos[2], iInfosPos[3]-1).trim();
                if (!isEmpty(szTmp)) {
                    String szTmp1=szTmp.replace('s', ' ').trim();
                    objFullPart[iCurrentItem].setEndSectorPos(Integer.parseInt(szTmp1));
                }

                // Nb de sector
                szTmp=szLine.substring(iInfosPos[3], iInfosPos[4]-1).trim();
                if (!isEmpty(szTmp)) {
                    String szTmp1=szTmp.replace('s', ' ').trim();
                    objFullPart[iCurrentItem].setNbSector(Integer.parseInt(szTmp1));
                }

                // Type
                szTmp=szLine.substring(iInfosPos[4], iInfosPos[5]-1).trim();
                if (!isEmpty(szTmp)) {
                    objFullPart[iCurrentItem].setTypeFs(szTmp);
                }

                // Name
                szTmp=szLine.substring(iInfosPos[5], iInfosPos[6]-1).trim();
                if (!isEmpty(szTmp)) {
                    objFullPart[iCurrentItem].setName(szTmp);
                }

                // Flags
                szTmp=szLine.substring(iInfosPos[6]).trim();
                if (!isEmpty(szTmp)) {
                    objFullPart[iCurrentItem].setFlagFs(szTmp);
                }

                // Mount point for format
                String szName = objFullPart[iCurrentItem].getName();
                boolean bAB=false;
                int i=-1;

                // Enleve _a
                if (szName.indexOf("_a")!=-1)
                    szName=szName.substring(0,szName.length()-2);

                MountPoint objMount = findMountPointbyName(szName);
                objFullPart[iCurrentItem].setPname(objMount.getPname());

                iCurrentItem++;
            }
        }

        iNbFullPart=iCurrentItem;
        return iCurrentItem;
    }

	public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public void InitListMount(String szMountString)
    {
        int iCurrentItem=0;
        String[] strFullPartitionMnt = szMountString.split("\n");


        for (String szLine : strFullPartitionMnt) {
            int istart = szLine.indexOf(" -> ");

            if (istart > -1) {
                String szPointName = szLine.substring(istart + 4);
                String szLine2 = szLine.substring(0,istart);

                int istart1 = szLine2.lastIndexOf(' ');
                String szName  = szLine.substring(istart1+1,istart);
                lstMount.add(new MountPoint(0,szName,szPointName));
            }
        }
    }

    public MountPoint findMountPointbyName(String name) {
        Iterator<MountPoint> iterator = lstMount.iterator();
        while (iterator.hasNext()) {
            MountPoint ptMount = iterator.next();
            if (ptMount.getName().equals(name)) {
                return ptMount;
            }
        }
        return null;
    }
}