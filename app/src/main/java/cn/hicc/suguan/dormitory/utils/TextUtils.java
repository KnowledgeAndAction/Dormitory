package cn.hicc.suguan.dormitory.utils;

public class TextUtils {

    // 24项查分项
    public static String[] detailScoreNames = {"无睡懒觉现象", "无玩电脑游戏", "配合检查", "着装得体，举止文明", "学生关系融洽",
    "张贴宿舍规定", "地面洁净", "床下洁净", "床铺整齐", "被子整洁", "门窗洁净", "墙壁洁净", "桌椅电脑整齐", "桌面书架干净", "无乱搭乱挂杂物",
    "暖气片无杂物", "阳台物品整齐", "阳台地面干净", "装饰美观健康向上", "乱扔烟头吸烟", "宿舍无人插座未关", "私拉电源线", "饲养宠物",
    "违章电器，管制刀具或其他"};

    // 宿舍楼名字
    public static String[] buildName = {"馨源楼","馨逸楼","馨宁楼","馨雅楼","馨清楼","厚望楼","厚朴楼","厚泽楼","信士楼","信香楼","信然楼","信佳楼","馨致楼"};
    public static String[] ChoiceDialogBuildName = {"全部","馨源楼","馨逸楼","馨宁楼","馨雅楼","馨清楼","厚望楼","厚朴楼","厚泽楼","信士楼","信香楼","信然楼","信佳楼","馨致楼"};

    // 书院名字
    public static String[] academyName = {"明德书院","笃学书院","诚行书院","治平书院","致用书院"};
    public static String[] ChoiceDialogAcademyName = {"全部","明德书院","笃学书院","诚行书院","治平书院","致用书院"};

    public static String GetBuildName(int code) {
        String string = "";
        switch (code) {
            case 10:
                string = "馨源楼";
                break;
            case 11:
                string = "馨逸楼";
                break;
            case 12:
                string = "馨宁楼";
                break;
            case 13:
                string = "馨雅楼";
                break;
            case 14:
                string = "馨清楼";
                break;
            case 15:
                string = "厚望楼";
                break;
            case 16:
                string = "厚朴楼";
                break;
            case 17:
                string = "厚泽楼";
                break;
            case 18:
                string = "信士楼";
                break;
            case 19:
                string = "信香楼";
                break;
            case 20:
                string = "信然楼";
                break;
            case 21:
                string = "信佳楼";
                break;
            case 22:
                string = "馨致楼";
                break;
        }
        return string;
    }

    public static int GetBuildCode(String BuildName) {
        int i = 0;
        if (BuildName.equals("馨源楼")) {
            i = 10;
        }
        if (BuildName.equals("馨逸楼")) {
            i = 11;
        }
        if (BuildName.equals("馨宁楼")) {
            i = 12;
        }
        if (BuildName.equals("馨雅楼")) {
            i = 13;
        }
        if (BuildName.equals("馨清楼")) {
            i = 14;
        }
        if (BuildName.equals("厚望楼")) {
            i = 15;
        }
        if (BuildName.equals("厚朴楼")) {
            i = 16;
        }
        if (BuildName.equals("厚泽楼")) {
            i = 17;
        }
        if (BuildName.equals("信士楼")) {
            i = 18;
        }
        if (BuildName.equals("信香楼")) {
            i = 19;
        }
        if (BuildName.equals("信然楼")) {
            i = 20;
        }
        if (BuildName.equals("信佳楼")) {
            i = 21;
        }
        if (BuildName.equals("馨致楼")) {
            i = 22;
        }
        return i;
    }

    // 获取检查类型名字
    public static String getCheckTypeName(int checkType) {
        String type = "";
        switch (checkType) {
            case 1:
                type = "普查";
                break;
            case 2:
                type = "抽查";
                break;
        }
        return type;
    }
}
