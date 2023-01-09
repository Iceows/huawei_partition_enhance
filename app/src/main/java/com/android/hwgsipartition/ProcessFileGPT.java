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
                if ((iNbModPart!=6) && (iNbModPart!=8))
                    return false;
                iNbProcPart=KeepProcPart();
                Log.println(Log.INFO, "ReadGPT", "iNbProcPart :" + String.valueOf(iNbProcPart));
                if ((iNbProcPart==6) || (iNbProcPart==8)) {
                    return true;
                }
            }
        }
        return false;
    }

    public String GeneratedScriptRM() {
        String szClearCmd="";

        szClearCmd = szClearCmd +"#!/sbin/sh \n";
        szClearCmd = szClearCmd +" \n";
        szClearCmd = szClearCmd +"./parted /dev/block/mmcblk0 --script \n";
        if ((iNbProcPart==6) || (iNbProcPart==8)) {
            for (int i = 0; i < iNbProcPart; i++) {
                szClearCmd = szClearCmd + String.format("rm %d\n", objProcPart[i].getId());
            }
            Log.println(Log.INFO, "ReadGPT", "  " + szClearCmd);
        }
        return szClearCmd;
    }

    //mkfs.ext4 /dev/block/mmcblk0p59
    //mkfs.f2fs /dev/block/mmcblk0p60
    public String GeneratedScriptFormat() {
        String szClearCmd="";

        return szClearCmd;
    }

    //mkpart system ext2 7153MB 11153MB
    //set 59 msftdata on
    //name 59 system-b
    public String GeneratedScriptMake() {
        String szClearCmd="";

        szClearCmd = szClearCmd +"#!/sbin/sh \n";
        szClearCmd = szClearCmd +" \n";
        szClearCmd = szClearCmd +"./parted -a optimal /dev/block/mmcblk0 --script \n";
        szClearCmd = szClearCmd +"unit s\n";
        if ((iNbProcPart==6) || (iNbProcPart==8)) {
            for (int i = 0; i < iNbProcPart; i++) {
                szClearCmd = szClearCmd + String.format("mkpart %s %s %ds %ds\n", objProcPart[i].getName(),objProcPart[i].getTypeFs(),objProcPart[i].getStartSectorPos(),objProcPart[i].getEndSectorPos());
                szClearCmd = szClearCmd + String.format("set %d %s on\n", objProcPart[i].getId(),objProcPart[i].getFlagFs());
                szClearCmd = szClearCmd + String.format("name %d %s\n", objProcPart[i].getId(),objProcPart[i].getName());
            }
            Log.println(Log.INFO, "ReadGPT", "  " + szClearCmd);
        }
        szClearCmd = szClearCmd +"p free\nquit\n";
        return szClearCmd;
    }

    public int KeepProcPart() {
        int iCurrentItem=0;
        int iNewStartSector=0;
        int iNewEndSector=0;
        int iNewNbSector=0;
        int iOldNbSector=0;
        int iIncreaseSize=0; // Nd de secteur a augmenter

        // Process system - taille d'un secteur = 512 bytes
        // 6 160 384 sector = 3 008 Mo = 2,93 Go
        // 7 000 000 sector = 3 417 Mo
        objProcPart[iCurrentItem] = new Partition(objModPart[iCurrentItem]);
        iOldNbSector=objProcPart[iCurrentItem].getNbSector();
        iIncreaseSize=7000000-iOldNbSector;
        iNewEndSector=objProcPart[iCurrentItem].getEndSectorPos()+iIncreaseSize;
        iNewNbSector=7000000;
        objProcPart[iCurrentItem].setEndSectorPos(iNewEndSector);
        objProcPart[iCurrentItem].setNbSector(iNewNbSector);
        objProcPart[iCurrentItem].LogInfo();

        // Preavs et preas partition
        if (iNbModPart==8) {
            //01-08 13:07:20.625 10806 10806 I ReadGPT :   52      2064384s   9699327s    7634944s    ext2         system             msftdata
            //01-08 13:07:20.625 10806 10806 I ReadGPT :   53      9699328s   12009471s   2310144s    ext4         preas              msftdata
            //01-08 13:07:20.625 10806 10806 I ReadGPT :   54      12009472s  12075007s   65536s      ext4         preavs             msftdata
            // preas
            iCurrentItem++;
            objProcPart[iCurrentItem] = new Partition(objModPart[iCurrentItem]);
            iNewStartSector=objProcPart[iCurrentItem].getStartSectorPos()+iIncreaseSize;
            iNewEndSector=objProcPart[iCurrentItem].getEndSectorPos()+iIncreaseSize;
            objProcPart[iCurrentItem].setStartSectorPos(iNewStartSector);
            objProcPart[iCurrentItem].setEndSectorPos(iNewEndSector);
            objProcPart[iCurrentItem].LogInfo();

            // Preavs
            iCurrentItem++;
            objProcPart[iCurrentItem] = new Partition(objModPart[iCurrentItem]);
            iNewStartSector=objProcPart[iCurrentItem].getStartSectorPos()+iIncreaseSize;
            iNewEndSector=objProcPart[iCurrentItem].getEndSectorPos()+iIncreaseSize;
            objProcPart[iCurrentItem].setStartSectorPos(iNewStartSector);
            objProcPart[iCurrentItem].setEndSectorPos(iNewEndSector);
            objProcPart[iCurrentItem].LogInfo();
        }

        // Cust
        iCurrentItem++;
        objProcPart[iCurrentItem] = new Partition(objModPart[iCurrentItem]);
        iNewStartSector=objProcPart[iCurrentItem].getStartSectorPos()+iIncreaseSize;
        iNewEndSector=objProcPart[iCurrentItem].getEndSectorPos()+iIncreaseSize;
        objProcPart[iCurrentItem].setStartSectorPos(iNewStartSector);
        objProcPart[iCurrentItem].setEndSectorPos(iNewEndSector);
        objProcPart[iCurrentItem].LogInfo();

        // version
        iCurrentItem++;
        objProcPart[iCurrentItem] = new Partition(objModPart[iCurrentItem]);
        iNewStartSector=objProcPart[iCurrentItem].getStartSectorPos()+iIncreaseSize;
        iNewEndSector=objProcPart[iCurrentItem].getEndSectorPos()+iIncreaseSize;
        objProcPart[iCurrentItem].setStartSectorPos(iNewStartSector);
        objProcPart[iCurrentItem].setEndSectorPos(iNewEndSector);
        objProcPart[iCurrentItem].LogInfo();

        // vendor
        iCurrentItem++;
        objProcPart[iCurrentItem] = new Partition(objModPart[iCurrentItem]);
        iNewStartSector=objProcPart[iCurrentItem].getStartSectorPos()+iIncreaseSize;
        iNewEndSector=objProcPart[iCurrentItem].getEndSectorPos()+iIncreaseSize;
        objProcPart[iCurrentItem].setStartSectorPos(iNewStartSector);
        objProcPart[iCurrentItem].setEndSectorPos(iNewEndSector);
        objProcPart[iCurrentItem].LogInfo();

        // product
        iCurrentItem++;
        objProcPart[iCurrentItem] = new Partition(objModPart[iCurrentItem]);
        iNewStartSector=objProcPart[iCurrentItem].getStartSectorPos()+iIncreaseSize;
        iNewEndSector=objProcPart[iCurrentItem].getEndSectorPos()+iIncreaseSize;
        objProcPart[iCurrentItem].setStartSectorPos(iNewStartSector);
        objProcPart[iCurrentItem].setEndSectorPos(iNewEndSector);
        objProcPart[iCurrentItem].LogInfo();

        // userdata
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

        for (int i = 0; i < iNbFullPart; i++) {
            String szPartName=objFullPart[i].getName();

            if ((szPartName != null) &&  (!szPartName.isEmpty())) {
                if (szPartName.equals("system")) {
                    bStart = true;
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
            // EMUI 8
            if (szLine.equals("Number  Start      End        Size       File system  Name               Flags")) {
                    bStartTable = true;
            }
            // EMUI 9.1
            if (szLine.equals("Number  Start      End         Size        File system  Name               Flags")) {
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
                if ((szLine.length()!=81) && (szLine.length()!=83)) {
                    Log.println(Log.WARN, "ReadGPT"," Incorrect line len : " + szLine.length() + " != 81");
                    return iCurrentItem;
                }

                objFullPart[iCurrentItem] = new Partition();

                // partition number
                szTmp=szLine.substring(0, 7).trim();
                if ((szTmp != null) &&  (!szTmp.isEmpty())) {
                    objFullPart[iCurrentItem].setId(Integer.parseInt(szTmp));
                }

                // Start sector
                szTmp=szLine.substring(8, 18).trim();
                if ((szTmp != null) &&  (!szTmp.isEmpty())) {
                    String szTmp1=szTmp.replace('s', ' ').trim();
                    objFullPart[iCurrentItem].setStartSectorPos(Integer.parseInt(szTmp1));
                }

                // End sector
                szTmp=szLine.substring(19, 28).trim();
                if ((szTmp != null) &&  (!szTmp.isEmpty())) {
                    String szTmp1=szTmp.replace('s', ' ').trim();
                    objFullPart[iCurrentItem].setEndSectorPos(Integer.parseInt(szTmp1));
                }

                // Nb de sector
                if (iLineLength==81)
                    szTmp=szLine.substring(29, 40).trim();
                if (iLineLength==83)
                    szTmp=szLine.substring(31, 42).trim();

                if ((szTmp != null) &&  (!szTmp.isEmpty())) {
                    String szTmp1=szTmp.replace('s', ' ').trim();
                    objFullPart[iCurrentItem].setNbSector(Integer.parseInt(szTmp1));
                }

                // Type
                if (iLineLength==81)
                    szTmp=szLine.substring(41, 53).trim();
                if (iLineLength==83)
                    szTmp=szLine.substring(43, 55).trim();
                if ((szTmp != null) &&  (!szTmp.isEmpty())) {
                    objFullPart[iCurrentItem].setTypeFs(szTmp);
                }

                // Name
                if (iLineLength==81)
                    szTmp=szLine.substring(54, 72).trim();
                if (iLineLength==83)
                    szTmp=szLine.substring(56, 74).trim();
                if ((szTmp != null) &&  (!szTmp.isEmpty())) {
                    objFullPart[iCurrentItem].setName(szTmp);
                }

                // Flags
                if (iLineLength==81)
                    szTmp=szLine.substring(73, 81).trim();
                if (iLineLength==83)
                    szTmp=szLine.substring(75, 83).trim();
                if ((szTmp != null) &&  (!szTmp.isEmpty())) {
                    objFullPart[iCurrentItem].setFlagFs(szTmp);
                }

                // Mount point for format
                String szName = objFullPart[iCurrentItem].getName();
                for (String szLine2 : strFullPartitionMnt) {

                }

                iCurrentItem++;
            }
        }

        iNbFullPart=iCurrentItem;
        return iCurrentItem;
    }

}