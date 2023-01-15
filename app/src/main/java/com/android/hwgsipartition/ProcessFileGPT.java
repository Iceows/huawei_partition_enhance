package com.android.hwgsipartition;

import android.util.Log;

import java.io.*;
import java.util.Arrays;

public class ProcessFileGPT
{
    Partition[] objFullPart = new Partition[100];
    int iNbFullPart=0;
    Partition[] objModPart = new Partition[50];
    int iNbModPart=0;
    Partition[] objProcPart = new Partition[50];
    int iNbProcPart=0;

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
    public boolean StartProcess(String sMyInitialGPT, String sMyPartition) {
        iNbFullPart=KeepFullPart(sMyInitialGPT,sMyPartition);

        if (iNbFullPart>0) {
            Log.println(Log.INFO, "ReadGPT", "iNbFullPart > 0 : " + String.valueOf(iNbFullPart));
            iNbModPart=KeepModPart();
            if (iNbModPart>0) {
                Log.println(Log.INFO, "ReadGPT", "iNbModPart > 0 :" + String.valueOf(iNbModPart));
                if (iNbModPart<3)
                    return false;
                iNbProcPart=KeepProcPart();
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
        szClearCmd = szClearCmd +"./parted /dev/block/mmcblk0 --script \\\n";
        if (iNbProcPart>1) {
            for (int i = 0; i < iNbProcPart; i++) {
                szClearCmd = szClearCmd + String.format("rm %d \\\n", objProcPart[i].getId());
            }
            Log.println(Log.INFO, "ReadGPT", "  " + szClearCmd);
        }
        szClearCmd = szClearCmd +"p free \\\nquit\n";
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
                    szClearCmd = szClearCmd + String.format("./mke2fs -t %s %s\n", objProcPart[i].getTypeFs(),objProcPart[i].getPname());
                if (objProcPart[i].getTypeFs().equals("f2fs"))
                    szClearCmd = szClearCmd + String.format("./mkfs.f2fs %s\n", objProcPart[i].getPname());
                if (objProcPart[i].getTypeFs().equals(""))
                    szClearCmd = szClearCmd + String.format("./mkfs.erofs %s\n", objProcPart[i].getPname());
            }
        }
        Log.println(Log.INFO, "ReadGPT", "  " + szClearCmd);

        return szClearCmd;
    }

    //mkpart system ext2 7153MB 11153MB
    //set 59 msftdata on
    //name 59 system-b
    public String GeneratedScriptMake() {
        String szClearCmd="";

        szClearCmd = szClearCmd +"#!/sbin/sh \n";
        szClearCmd = szClearCmd +" \n";
        szClearCmd = szClearCmd +"./parted -a optimal /dev/block/mmcblk0 --script \\\n";
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

    public int KeepProcPart() {
        int iCurrentItem=0;
        int iNewStartSector=0;
        int iNewEndSector=0;
        int iNewNbSector=0;
        int iOldNbSector=0;
        int iIncreaseSize=0; // Nd de secteur a augmenter
        int iNewSystemSize=7168000;// Pour optimiser la vitesse multiple de 2048

        // Process system - taille d'un secteur = 512 bytes
        // 6 160 384 sector = 3 008 Mo = 2,93 Go
        // 7 000 000 sector = 3 500 Mo
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

            // On a trouvé l'entete du début de table des partitions
            // PRA-LX1
            if (szLine.equals("Number  Start      End        Size       File system  Name               Flags")) {
                    bStartTable = true;
            }
            // ANE-LX1
            if (szLine.equals("Number  Start      End         Size        File system  Name               Flags")) {
                bStartTable = true;
            }
            // POT-LX1 (86)
            if (szLine.equals("Number  Start      End         Size        File system  Name                 Flags")) {
                bStartTable = true;
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
                if ((szLine.length()!=81)
                        && (szLine.length()!=83)
                        && (szLine.length()!=85)) {
                    Log.println(Log.WARN, "ReadGPT"," Incorrect line len : " + szLine.length() + " != 81 or 83 or 85");
                    return iCurrentItem;
                }

                objFullPart[iCurrentItem] = new Partition();

                // partition number
                szTmp=szLine.substring(0, 7).trim();
                if (!isEmpty(szTmp)) {
                    objFullPart[iCurrentItem].setId(Integer.parseInt(szTmp));
                }

                // Start sector
                szTmp=szLine.substring(8, 18).trim();
                if (!isEmpty(szTmp)) {
                    String szTmp1=szTmp.replace('s', ' ').trim();
                    objFullPart[iCurrentItem].setStartSectorPos(Integer.parseInt(szTmp1));
                }

                // End sector
                szTmp=szLine.substring(19, 28).trim();
                if (!isEmpty(szTmp)) {
                    String szTmp1=szTmp.replace('s', ' ').trim();
                    objFullPart[iCurrentItem].setEndSectorPos(Integer.parseInt(szTmp1));
                }

                // Nb de sector
                if (iLineLength==81)
                    szTmp=szLine.substring(29, 40).trim();
                if (iLineLength==83)
                    szTmp=szLine.substring(31, 42).trim();
                if (iLineLength==85)
                    szTmp=szLine.substring(31, 42).trim();

                if (!isEmpty(szTmp)) {
                    String szTmp1=szTmp.replace('s', ' ').trim();
                    objFullPart[iCurrentItem].setNbSector(Integer.parseInt(szTmp1));
                }

                // Type
                if (iLineLength==81)
                    szTmp=szLine.substring(41, 53).trim();
                if (iLineLength==83)
                    szTmp=szLine.substring(43, 55).trim();
                if (iLineLength==85)
                    szTmp=szLine.substring(43, 55).trim();
                if (!isEmpty(szTmp)) {
                    objFullPart[iCurrentItem].setTypeFs(szTmp);
                }

                // Name
                if (iLineLength==81)
                    szTmp=szLine.substring(54, 72).trim();
                if (iLineLength==83)
                    szTmp=szLine.substring(56, 74).trim();
                if (iLineLength==85)
                    szTmp=szLine.substring(56, 74).trim();
                if (!isEmpty(szTmp)) {
                    objFullPart[iCurrentItem].setName(szTmp);
                }

                // Flags
                if (iLineLength==81)
                    szTmp=szLine.substring(73, 81).trim();
                if (iLineLength==83)
                    szTmp=szLine.substring(75, 83).trim();
                if (iLineLength==85)
                    szTmp=szLine.substring(77, 85).trim();
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

                for (String szLine2 : strFullPartitionMnt) {
                    i=szLine2.indexOf(szName +  " -> ");
                    if (i>-1) {
                        // Found
                        int istart=szLine2.indexOf(" -> ");
                        if (istart>-1) {
                            String szPointName = szLine2.substring(istart + 4);
                            objFullPart[iCurrentItem].setPname(szPointName);
                        }
                    }
                }

                iCurrentItem++;
            }
        }

        iNbFullPart=iCurrentItem;
        return iCurrentItem;
    }

	public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }
}