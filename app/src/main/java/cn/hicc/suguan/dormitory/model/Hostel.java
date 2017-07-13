package cn.hicc.suguan.dormitory.model;

public class Hostel {
    private int Building;//XX楼
    private int Hostel;//宿舍号
    private int Value;//分数
    private boolean IsCheck;//是否查询
    private int Code;//编号
    private int checkType;//查询类型（1普查，2抽查）
    private int weekCode;//第几周

    public Hostel(int building, int hostel, int value, boolean isCheck, int code, int checkType, int weekCode) {
        Building = building;
        Hostel = hostel;
        Value = value;
        IsCheck = isCheck;
        Code = code;
        this.checkType = checkType;
        this.weekCode = weekCode;
    }

    public int getWeekCode() {
        return weekCode;
    }

    public void setWeekCode(int weekCode) {
        this.weekCode = weekCode;
    }

    public int getCheckType() {
        return checkType;
    }

    public void setCheckType(int checkType) {
        this.checkType = checkType;
    }


    public int getHostel() {
        return Hostel;
    }

    public void setHostel(int hostel) {
        Hostel = hostel;
    }


    public int getValue() {
        return Value;
    }

    public void setValue(int value) {
        Value = value;
    }


    public boolean isCheck() {
        return IsCheck;
    }

    public void setIsCheck(boolean isCheck) {
        IsCheck = isCheck;
    }


    public int getCode() {
        return Code;
    }

    public void setCode(int code) {
        Code = code;
    }

    public int getBuilding() {
        return Building;
    }

    public void setBuilding(int building) {
        Building = building;
    }
}
