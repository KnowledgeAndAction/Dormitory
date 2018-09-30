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
    public static final String GET_USER_BUILD = HOST + "DormitoryAPI/Query/Assistant/DormNum";
    // 上传分数
    public static final String UP_SCORE = HOST + "DormitoryAPI/Insert/WeekScore";
    // 获取宿舍成员
    public static final String GET_USER_NAME = HOST + "DormitoryAPI/Query/Student/Info";
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
    // 获取评分详情
    public static final String GET_DETAILS_SCORE = HOST + "DormitoryAPI/Query/WeekScore/Dormitory";
    // 修改分数
    public static final String CHANGE_SCORE = HOST + "DormitoryAPI/Update/WeekScore";
    // 获取宿舍成员详细信息
    public static final String GET_DOR_MEMBERS = HOST + "DormitoryAPI/Query/StudentInfo/DormStudentInfo";
    // 本周查宿情况
    public static final String DOR_CHECK_STATE = HOST + "DormitoryAPI/Query/CheckState";
}
