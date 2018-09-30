package cn.hicc.suguan.dormitory.model;

/**
 * Created by 陈帅 on 2018/6/9/009.
 */

public class DorMember {
    private String division;    // 学部
    private String gender;  // 性别
    private String instructor;  // 导员
    private String grade;   // 性别
    private String name;    // 姓名
    private String dor;  // 宿舍
    private String id;  // 学号
    private String academy; // 书院
    private String classNu; // 班级

    public DorMember() {
    }

    public DorMember(String division, String gender, String instructor, String grade, String name, String dor, String id, String academy, String classNu) {
        this.division = division;
        this.gender = gender;
        this.instructor = instructor;
        this.grade = grade;
        this.name = name;
        this.dor = dor;
        this.id = id;
        this.academy = academy;
        this.classNu = classNu;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDor() {
        return dor;
    }

    public void setDor(String dor) {
        this.dor = dor;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAcademy() {
        return academy;
    }

    public void setAcademy(String academy) {
        this.academy = academy;
    }

    public String getClassNu() {
        return classNu;
    }

    public void setClassNu(String classNu) {
        this.classNu = classNu;
    }
}
