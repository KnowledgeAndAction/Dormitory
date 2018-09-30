package cn.hicc.suguan.dormitory.model;

/**
 * Created by 陈帅 on 2018/6/9/009.
 */

public class DetailScore {
    private String scoreName;
    private int score;

    public DetailScore() {
    }

    public DetailScore(String scoreName, int score) {
        this.scoreName = scoreName;
        this.score = score;
    }

    public String getScoreName() {
        return scoreName;
    }

    public void setScoreName(String scoreName) {
        this.scoreName = scoreName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
