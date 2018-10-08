package cn.hicc.suguan.dormitory.utils;

/**
 * 接口地址
 */
public class URL {

    // 主机地址
    private static final String HOST = "http://10.187.1.185/";


    // 登录
    public static final String LOGIN_URL = HOST + "newDormitoryAPI/User/Login";
    // 获取宿舍信息
    public static final String GET_USER_BUILD = HOST + "newDormitoryAPI/Query/Assistant/DormNum";
    // 上传分数
    public static final String UP_SCORE = HOST + "newDormitoryAPI/Insert/WeekScore";
    // 获取宿舍成员
    public static final String GET_USER_NAME = HOST + "newDormitoryAPI/Query/Student/Info";
    // 上传照片
    public static final String UP_IMAGE = "http://suguan.hicc.cn/UploadHandleServlet";
    // 检测更新
    public static final String CHECK_UPDATA = HOST + "newDormitoryAPI/Query/AppInfo/Version";
    // 灵活查分 暂时去掉
    public static final String CHECK_SCORE = HOST + "DormitoryAPI/Query/Custom";
    // 导员查询
    public static final String TEACHER_CHECK_SCORE = HOST + "newDormitoryAPI/Query/WeekScore/Instructor";
    // 学部查询
    public static final String DIVISION_CHECK_SCORE = HOST + "newDormitoryAPI/Query/WeekScore/Division";
    // 领导查询
    public static final String LEADER_CHECK_SCORE = HOST + "newDormitoryAPI/Query/WeekScore/Dean";
    // 获取评分详情
    public static final String GET_DETAILS_SCORE = HOST + "newDormitoryAPI/Query/WeekScore/Dormitory";
    // 修改分数  用上传分数的接口   （原来的：DormitoryAPI/Update/WeekScore)
    public static final String CHANGE_SCORE = HOST + "newDormitoryAPI/Insert/WeekScore";
    // 获取宿舍成员详细信息
    public static final String GET_DOR_MEMBERS = HOST + "newDormitoryAPI/Query/StudentInfo/DormStudentInfo";
    // TODO 本周查宿情况 可能需要修改地址
    public static final String DOR_CHECK_STATE = HOST + "DormitoryAPI/Query/CheckState";
}
