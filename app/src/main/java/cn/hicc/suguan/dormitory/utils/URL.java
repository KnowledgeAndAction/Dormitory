package cn.hicc.suguan.dormitory.utils;


public class URL {
    /**
     * 接口地址
     */
    private static final String HOST = "http://10.187.1.185/";
    public static final String LOGIN_URL = HOST + "com.wlzx.dormitory.servlet/PhoneLoginServlet";
    public static final String Get_User_Build = HOST + "com.wlzx.dormitory.servlet/PhoneSearchUserDorNum";
    public static final String UP_SCORE = HOST + "com.wlzx.dormitory.servlet/PhoneUploadScore";
    public static final String Get_User_Name = HOST + "com.wlzx.dormitory.servlet/PhoneSearchStudentBedNumberServlet";
    public static final String UP_IMAGE = "http://suguan.hicc.cn/UploadHandleServlet";
    public static final String CHECK_UPDATA = HOST + "com.wlzx.dormitory.servlet/PhoneVersion";
}
