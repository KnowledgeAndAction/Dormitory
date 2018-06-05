package cn.hicc.suguan.dormitory.model;

/**
 * Created by 陈帅 on 2018/5/31/031.
 */

public class Score {
    private String typeName;
    private double score;

    public Score() {
    }

    public Score(String typeName, double score) {
        this.typeName = typeName;
        this.score = score;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
