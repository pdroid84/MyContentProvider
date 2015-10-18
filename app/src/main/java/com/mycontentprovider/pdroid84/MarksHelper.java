package com.mycontentprovider.pdroid84;

/**
 * Created by debashispaul on 06/10/2015.
 */
public class MarksHelper {

    private long key = 0;
    private String subj = null;
    private int mark = 0;
    private String grade = null;

    public MarksHelper (long key, String subj, int mark, String grade) {
        this.key = key;
        this.subj = subj;
        this.mark = mark;
        this.grade = grade;
    }

    public long getKey() {
        return key;
    }

    public void setKey(long key) {
        this.key = key;
    }

    public String getSubj() {
        return subj;
    }

    public void setSubj(String subj) {
        this.subj = subj;
    }

    public int getMark() {
        return mark;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }
}
