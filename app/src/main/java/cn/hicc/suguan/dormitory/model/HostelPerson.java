package cn.hicc.suguan.dormitory.model;

public class HostelPerson {
    String StudentName;
    int BedNumber;

    public String getStudentName() {
        return StudentName;
    }

    public void setStudentName(String studentName) {
        StudentName = studentName;
    }

    public int getBedNumber() {
        return BedNumber;
    }

    public void setBedNumber(int bedNumber) {
        BedNumber = bedNumber;
    }

    public HostelPerson(String studentName, int bedNumber) {

        StudentName = studentName;
        BedNumber = bedNumber;
    }
}
