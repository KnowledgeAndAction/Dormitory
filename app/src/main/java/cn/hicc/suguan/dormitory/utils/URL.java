package cn.hicc.suguan.dormitory.utils;

/**
 * 接口地址
 */
public class URL {

    // 主机地址
    private static final String HOST = "http://10.187.1.185/";


    // 登录
    public static final String LOGIN_URL = HOST + "DormitoryAPI/User/Login";
    // 获取宿舍信息
    public static final String Get_User_Build = HOST + "DormitoryAPI/Query/Assistant/DormNum";
    // 上传分数
    public static final String UP_SCORE = HOST + "DormitoryAPI/Insert/WeekScore";
    // 获取宿舍成员
    public static final String Get_User_Name = HOST + "DormitoryAPI/Query/Student/Info";
    // 上传照片
    public static final String UP_IMAGE = "http://suguan.hicc.cn/UploadHandleServlet";
    // 检测更新
    public static final String CHECK_UPDATA = HOST + "DormitoryAPI/Query/AppInfo/Version";
    // 灵活查分
    public static final String CHECK_SCORE = HOST + "DormitoryAPI/Query/Custom";
    // 导员查询
    public static final String TEACHER_CHECK_SCORE = HOST + "DormitoryAPI/Query/WeekScore/Instructor";
    // 学部查询
    public static final String DIVISION_CHECK_SCORE = HOST + "DormitoryAPI/Query/WeekScore/Division";
    // 领导查询
    public static final String LEADER_CHECK_SCORE = HOST + "DormitoryAPI/Query/WeekScore/Dean";
}
