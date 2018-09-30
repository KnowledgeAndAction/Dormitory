package cn.hicc.suguan.dormitory.model;

/**
 * Created by 陈帅 on 2018/6/12/012.
 */

public class DorCheckState {
    private String shuyuan;
    private String lou;
    private String hao;
    private int pucha;
    private int choucha;

    public DorCheckState() {
    }

    public DorCheckState(String shuyuan, String lou, String hao, int pucha, int choucha) {
        this.shuyuan = shuyuan;
        this.lou = lou;
        this.hao = hao;
        this.pucha = pucha;
        this.choucha = choucha;
    }

    public String getShuyuan() {
        return shuyuan;
    }

    public void setShuyuan(String shuyuan) {
        this.shuyuan = shuyuan;
    }

    public String getLou() {
        return lou;
    }

    public void setLou(String lou) {
        this.lou = lou;
    }

    public String getHao() {
        return hao;
    }

    public void setHao(String hao) {
        this.hao = hao;
    }

    public int getPucha() {
        return pucha;
    }

    public void setPucha(int pucha) {
        this.pucha = pucha;
    }

    public int getChoucha() {
        return choucha;
    }

    public void setChoucha(int choucha) {
        this.choucha = choucha;
    }
}
