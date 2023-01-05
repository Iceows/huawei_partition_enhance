package com.android.hwgsipartition;

import android.util.Log;

public class Partition {

    private int id;
    private String name;
    private String typefs;
    private String flagfs;
    private int startSector;
    private int endSector;
    private int nbSector;

    //Employee class constructor
    Partition(){
        id=0;
        startSector=0;
        endSector=0;
        nbSector=0;
        flagfs=new String("");
        name=new String("");
        typefs=new String("");
    }

    Partition(int newId, int newStartSector,int newEndSector,int newNbSector,String newFlagfs,
              String newName, String newTypefs){
        id=newId;
        startSector=newStartSector;
        endSector=newEndSector;
        nbSector=newNbSector;
        flagfs=newFlagfs;
        name=newName;
        typefs=newTypefs;
    }

    Partition(Partition objPart){
        id=objPart.getId();
        startSector=objPart.getStartSectorPos();
        endSector=objPart.getEndSectorPos();
        nbSector=objPart.getNbSector();
        flagfs=objPart.getFlagFs();
        name=objPart.getName();
        typefs=objPart.getTypeFs();
    }

    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getTypeFs() {
        return typefs;
    }
    public String getFlagFs() {
        return flagfs;
    }
    public int getStartSectorPos() {
        return startSector;
    }
    public int getEndSectorPos() { return endSector; }
    public int getNbSector() { return nbSector; }


    public void setId(int newId) {
        this.id = newId;
    }
    public void setName(String newName) {
        this.name = newName.trim();
    }
    public void setTypeFs(String newTypefs) {
        this.typefs = newTypefs.trim();
    }
    public void setFlagFs(String newFlagfs) {
        this.flagfs = newFlagfs.trim();
    }
    public void setStartSectorPos(int newStartSector) {
        this.startSector = newStartSector;
    }
    public void setEndSectorPos(int newEndSector) {
        this.endSector = newEndSector;
    }
    public void setNbSectorPos(int newnbSector) {
        this.nbSector = newnbSector;
    }

    public String StringInfo()
    {
        String szFullLine = String.valueOf(id) + " - "
                + String.valueOf(startSector) + " - "
                + String.valueOf(endSector) + " - "
                + String.valueOf(nbSector) + " - "
                + typefs + " - "
                + name + " - "
                + flagfs;

        return szFullLine;

    }

    public void LogInfo()
    {
        String szFullLine = String.valueOf(id) + " - "
                + String.valueOf(startSector) + " - "
                + String.valueOf(endSector) + " - "
                + String.valueOf(nbSector) + " - "
                + typefs + " - "
                + name + " - "
                + flagfs;

        Log.println(Log.INFO, "ReadGPT", szFullLine);
    }
}

