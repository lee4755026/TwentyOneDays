package com.famo.twentyonedays.model;

public class VersionEntity implements Comparable<VersionEntity>{
    public Integer versionCode;
    public String versionName;
    public String updateUrl;
    @Override
    public String toString() {
        return "VersionEntity [versionCode=" + versionCode + ", versionName=" + versionName
            + ", updateUrl=" + updateUrl + "]";
    }
    @Override
    public int compareTo(VersionEntity another) {
        return this.versionCode.compareTo(another.versionCode);
    }
    
    
}
