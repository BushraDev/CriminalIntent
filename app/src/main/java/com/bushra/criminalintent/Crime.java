package com.bushra.criminalintent;

import java.util.Date;
import java.util.UUID;

public class Crime {

    private UUID cId;
    private String cTitle;
    private String cPlace;
    private Date cDate;
    private Date cTime;
    private boolean cSolved;
    private String cSuspect;
    private String cSuspectNumber;

    public Crime() {
        this(UUID.randomUUID());

    }

    public Crime(UUID id) {
        cId = id;
        cDate = new Date();
        cTime = new Date();
    }


    public UUID getcId() {
        return cId;
    }

    public void setcId(UUID cId) {
        this.cId = cId;
    }

    public String getcTitle() {
        return cTitle;
    }

    public void setcTitle(String cTitle) {
        this.cTitle = cTitle;
    }

    public String getcPlace() {
        return cPlace;
    }

    public void setcPlace(String cPlace) {
        this.cPlace = cPlace;
    }

    public Date getcDate() {
        return cDate;
    }

    public void setcDate(Date cDate) {
        this.cDate = cDate;
    }

    public Date getcTime() {
        return cTime;
    }

    public void setcTime(Date cTime) {
        this.cTime = cTime;
    }

    public boolean iscSolved() {
        return cSolved;
    }

    public void setcSolved(boolean cSolved) {
        this.cSolved = cSolved;
    }

    public String getcSuspect() {
        return cSuspect;
    }

    public void setcSuspect(String cSuspect) {
        this.cSuspect = cSuspect;
    }

    public String getcSuspectNumber() {
        return cSuspectNumber;
    }

    public void setcSuspectNumber(String cSuspectNumber) {
        this.cSuspectNumber = cSuspectNumber;
    }

    public String getPhotoFilename() {
        return "IMG_" + getcId().toString() + ".jpg";
    }
}
