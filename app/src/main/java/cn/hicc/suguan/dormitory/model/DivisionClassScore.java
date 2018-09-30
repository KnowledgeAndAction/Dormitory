package cn.hicc.suguan.dormitory.model;

/**
 * Created by 陈帅 on 2018/6/7/007.
 * 教师宿舍成绩对比
 */

public class DivisionClassScore {
    private String className;
    private String teacher;
    private double score;

    public DivisionClassScore() {
    }

    public DivisionClassScore(String className, String teacher, double score) {
        this.className = className;
        this.teacher = teacher;
        this.score = score;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
