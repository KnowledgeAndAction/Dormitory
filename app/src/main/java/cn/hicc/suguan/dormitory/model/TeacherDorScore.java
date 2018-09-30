package cn.hicc.suguan.dormitory.model;

/**
 * Created by 陈帅 on 2018/6/7/007.
 * 教师宿舍成绩对比
 */

public class TeacherDorScore {
    private String className;
    private String dorNum;
    private double score;

    public TeacherDorScore() {
    }

    public TeacherDorScore(String className, String dorNum, double score) {
        this.className = className;
        this.dorNum = dorNum;
        this.score = score;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getDorNum() {
        return dorNum;
    }

    public void setDorNum(String dorNum) {
        this.dorNum = dorNum;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
