package cn.hicc.suguan.dormitory.model;

/**
 * Created by 陈帅 on 2018/3/16/016.
 * 灵活查分
 */

public class FlexibleScore {
    private String seleteCondition1;
    private String seleteCondition2;
    private String seleteCondition3;
    private double score;

    public String getSeleteCondition1() {
        return seleteCondition1;
    }

    public void setSeleteCondition1(String seleteCondition1) {
        this.seleteCondition1 = seleteCondition1;
    }

    public String getSeleteCondition2() {
        return seleteCondition2;
    }

    public void setSeleteCondition2(String seleteCondition2) {
        this.seleteCondition2 = seleteCondition2;
    }

    public String getSeleteCondition3() {
        return seleteCondition3;
    }

    public void setSeleteCondition3(String seleteCondition3) {
        this.seleteCondition3 = seleteCondition3;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
