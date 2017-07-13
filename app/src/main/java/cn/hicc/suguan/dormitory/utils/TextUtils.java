package cn.hicc.suguan.dormitory.utils;

public class TextUtils {
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
}
