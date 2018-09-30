package cn.hicc.suguan.dormitory.model;

public class SQLITE {
    public static String db = "DormitoryBuild.db";
    public static String TABLE_HELPER_CHECK = "HelperCheckTable";
    public static String TABLE_HELPER_CREATE = "create table if not exists " + TABLE_HELPER_CHECK + "("
            + "Hid integer primary key autoincrement,"
            + "Building TEXT not null,"
            + "Hostel TEXT not null,"
            + "DormitoryState integer not null"
            + ")";
}
