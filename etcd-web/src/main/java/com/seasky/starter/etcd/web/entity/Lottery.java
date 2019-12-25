package com.seasky.starter.etcd.web.entity;

import java.util.Date;
import java.util.Objects;

public class Lottery {
    private Long lId;
    private String strWinNumber;
    private String strWinTime;
    private Integer nRed1;
    private Integer nRed2;
    private Integer nRed3;
    private Integer nRed4;
    private Integer nRed5;
    private Integer nRed6;
    private String strReds;
    private Integer nBlue;

    private Date dtCreateTime;

    private Integer score;


    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "Lottery{" +
                "lId=" + lId +
                ", strWinNumber='" + strWinNumber + '\'' +
                ", strWinTime=" + strWinTime +
                ", nRed1=" + nRed1 +
                ", nRed2=" + nRed2 +
                ", nRed3=" + nRed3 +
                ", nRed4=" + nRed4 +
                ", nRed5=" + nRed5 +
                ", nRed6=" + nRed6 +
                ", strReds='" + strReds + '\'' +
                ", nBlue=" + nBlue +
                ", dtCreateTime=" + dtCreateTime +
                ", score=" + score +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lottery lottery = (Lottery) o;
        return Objects.equals(lId, lottery.lId) &&
                Objects.equals(strWinNumber, lottery.strWinNumber) &&
                Objects.equals(strWinTime, lottery.strWinTime) &&
                Objects.equals(nRed1, lottery.nRed1) &&
                Objects.equals(nRed2, lottery.nRed2) &&
                Objects.equals(nRed3, lottery.nRed3) &&
                Objects.equals(nRed4, lottery.nRed4) &&
                Objects.equals(nRed5, lottery.nRed5) &&
                Objects.equals(nRed6, lottery.nRed6) &&
                Objects.equals(strReds, lottery.strReds) &&
                Objects.equals(nBlue, lottery.nBlue) &&
                Objects.equals(dtCreateTime, lottery.dtCreateTime) &&
                Objects.equals(score, lottery.score);
    }

    @Override
    public int hashCode() {

        return Objects.hash(lId, strWinNumber, strWinTime, nRed1, nRed2, nRed3, nRed4, nRed5, nRed6, strReds, nBlue, dtCreateTime, score);
    }

    public Long getlId() {

        return lId;
    }

    public void setlId(Long lId) {
        this.lId = lId;
    }

    public String getStrWinNumber() {
        return strWinNumber;
    }

    public void setStrWinNumber(String strWinNumber) {
        this.strWinNumber = strWinNumber;
    }

    public String getStrWinTime() {
        return strWinTime;
    }

    public void setStrWinTime(String strWinTime) {
        this.strWinTime = strWinTime;
    }

    public Integer getnRed1() {
        return nRed1;
    }

    public void setnRed1(Integer nRed1) {
        this.nRed1 = nRed1;
    }

    public Integer getnRed2() {
        return nRed2;
    }

    public void setnRed2(Integer nRed2) {
        this.nRed2 = nRed2;
    }

    public Integer getnRed3() {
        return nRed3;
    }

    public void setnRed3(Integer nRed3) {
        this.nRed3 = nRed3;
    }

    public Integer getnRed4() {
        return nRed4;
    }

    public void setnRed4(Integer nRed4) {
        this.nRed4 = nRed4;
    }

    public Integer getnRed5() {
        return nRed5;
    }

    public void setnRed5(Integer nRed5) {
        this.nRed5 = nRed5;
    }

    public Integer getnRed6() {
        return nRed6;
    }

    public void setnRed6(Integer nRed6) {
        this.nRed6 = nRed6;
    }

    public String getStrReds() {
        return strReds;
    }

    public void setStrReds(String strReds) {
        this.strReds = strReds;
    }

    public Integer getnBlue() {
        return nBlue;
    }

    public void setnBlue(Integer nBlue) {
        this.nBlue = nBlue;
    }

    public Date getDtCreateTime() {
        return dtCreateTime;
    }

    public void setDtCreateTime(Date dtCreateTime) {
        this.dtCreateTime = dtCreateTime;
    }
}
