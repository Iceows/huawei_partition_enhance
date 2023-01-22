package com.android.hwgsipartition;

public class MountPoint {

    private int id;
    private String name;
    private String pname;


    //Employee class constructor
    MountPoint(int nid, String newName, String newPname) {
        id = nid;
        name = newName;
        pname = newPname;
    }

    public String getName() {
        return name;
    }
    public String getPname() {
        return pname;
    }

}
