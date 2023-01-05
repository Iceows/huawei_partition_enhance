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
    public boolean StartProcess(String sMyInitialGPT) {
        iNbFullPart=KeepFullPart(sMyInitialGPT);

        if (iNbFullPart>0) {
            Log.println(Log.INFO, "ReadGPT", "iNbFullPart > 0 ");
            iNbModPart=KeepModPart();
            if (iNbModPart>0) {
                Log.println(Log.INFO, "ReadGPT", "iNbModPart > 0 ");
                iNbProcPart=KeepProcPart();
                Log.println(Log.INFO, "ReadGPT", "iNbProcPart" + String.valueOf(iNbProcPart));
                if (iNbProcPart==6) {
                    return true;
                }
            }
        }
        return false;
    }

    public String GeneratedScriptRM() {
        String szClearCmd="";

        if (iNbProcPart==6) {
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

    //mkpart system-b ext4 7153MB 11153MB
    //set 59 msftdata on
    //name 59 system-b
    public String GeneratedScriptMake() {
        String szClearCmd="";

        szClearCmd = szClearCmd +"unit s\n";
        if (iNbProcPart==6) {
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
        int iNewSartSector=0;
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
        objProcPart[iCurrentItem].setNbSectorPos(iNewNbSector);
        objProcPart[iCurrentItem].LogInfo();

        // Process cust - decale de la taille calculé avant
        iCurrentItem++;
        objProcPart[iCurrentItem] = new Partition(objModPart[iCurrentItem]);
        iNewSartSector=objProcPart[iCurrentItem].getStartSectorPos()+iIncreaseSize;
        iNewEndSector=objProcPart[iCurrentItem].getEndSectorPos()+iIncreaseSize;
        objProcPart[iCurrentItem].setStartSectorPos(iNewSartSector);
        objProcPart[iCurrentItem].setEndSectorPos(iNewEndSector);
        objProcPart[iCurrentItem].LogInfo();

        // version
        iCurrentItem++;
        objProcPart[iCurrentItem] = new Partition(objModPart[iCurrentItem]);
        iNewSartSector=objProcPart[iCurrentItem].getStartSectorPos()+iIncreaseSize;
        iNewEndSector=objProcPart[iCurrentItem].getEndSectorPos()+iIncreaseSize;
        objProcPart[iCurrentItem].setStartSectorPos(iNewSartSector);
        objProcPart[iCurrentItem].setEndSectorPos(iNewEndSector);
        objProcPart[iCurrentItem].LogInfo();

        // vendor
        iCurrentItem++;
        objProcPart[iCurrentItem] = new Partition(objModPart[iCurrentItem]);
        iNewSartSector=objProcPart[iCurrentItem].getStartSectorPos()+iIncreaseSize;
        iNewEndSector=objProcPart[iCurrentItem].getEndSectorPos()+iIncreaseSize;
        objProcPart[iCurrentItem].setStartSectorPos(iNewSartSector);
        objProcPart[iCurrentItem].setEndSectorPos(iNewEndSector);
        objProcPart[iCurrentItem].LogInfo();

        // product
        iCurrentItem++;
        objProcPart[iCurrentItem] = new Partition(objModPart[iCurrentItem]);
        iNewSartSector=objProcPart[iCurrentItem].getStartSectorPos()+iIncreaseSize;
        iNewEndSector=objProcPart[iCurrentItem].getEndSectorPos()+iIncreaseSize;
        objProcPart[iCurrentItem].setStartSectorPos(iNewSartSector);
        objProcPart[iCurrentItem].setEndSectorPos(iNewEndSector);
        objProcPart[iCurrentItem].LogInfo();

        // userdata
        iCurrentItem++;
        objProcPart[iCurrentItem] = new Partition(objModPart[iCurrentItem]);
        iNewSartSector=objProcPart[iCurrentItem].getStartSectorPos()+iIncreaseSize;
        iNewEndSector=objProcPart[iCurrentItem].getEndSectorPos()+iIncreaseSize;
        objProcPart[iCurrentItem].setStartSectorPos(iNewSartSector);
        objProcPart[iCurrentItem].setEndSectorPos(iNewEndSector);
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

    public int KeepFullPart(String sMyInitialGPT) {
        Boolean bStartTable = false;
        String[] strNewPartition = new String[100];
        int numberOfItems = 0;
        int iCurrentItem=0;
        String[] strFullPartition = sMyInitialGPT.split("\n");

        for (String szLine : strFullPartition) {

            // Lecture de chaque partition dans un tableau
            if (bStartTable) {
                Log.println(Log.INFO, "ReadGPT", "  " + szLine);
                strNewPartition[numberOfItems++] = szLine;
            }

            // On a trouvé l'entete du début de table des partitions
            if (szLine.equals("Number  Start      End        Size       File system  Name               Flags")) {
                bStartTable = true;
            }
        }

        if (bStartTable!=true)
            return 0;

        Log.println(Log.INFO, "ReadGPT", "Start transform this partition :");

        if (numberOfItems>0) {
            for (String szLine : strNewPartition) {
                String szTmp;

                // End scan
                if ((szLine == null) || (szLine.isEmpty())) {
                    return numberOfItems;
                }

                // Error scan
                if (szLine.length()!=81) {
                    Log.println(Log.WARN, "ReadGPT"," Incorrect line len : " + szLine.length() + " != 81");
                    return numberOfItems;
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
                szTmp=szLine.substring(29, 40).trim();
                if ((szTmp != null) &&  (!szTmp.isEmpty())) {
                    String szTmp1=szTmp.replace('s', ' ').trim();
                    objFullPart[iCurrentItem].setNbSectorPos(Integer.parseInt(szTmp1));
                }

                // Type
                szTmp=szLine.substring(41, 53).trim();
                if ((szTmp != null) &&  (!szTmp.isEmpty())) {
                    objFullPart[iCurrentItem].setTypeFs(szTmp);
                }

                // Name
                szTmp=szLine.substring(54, 72).trim();
                if ((szTmp != null) &&  (!szTmp.isEmpty())) {
                    objFullPart[iCurrentItem].setName(szTmp);
                }

                // Flags
                szTmp=szLine.substring(73, 81).trim();
                if ((szTmp != null) &&  (!szTmp.isEmpty())) {
                    objFullPart[iCurrentItem].setFlagFs(szTmp);
                }

                iCurrentItem++;
            }
            return numberOfItems;
        }

        return numberOfItems;
    }

}