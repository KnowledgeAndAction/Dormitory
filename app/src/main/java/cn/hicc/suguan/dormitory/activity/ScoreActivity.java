package cn.hicc.suguan.dormitory.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.shawnlin.numberpicker.NumberPicker;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.hicc.suguan.dormitory.R;
import cn.hicc.suguan.dormitory.adapter.HostelPersonAdapter;
import cn.hicc.suguan.dormitory.model.HostelPerson;
import cn.hicc.suguan.dormitory.model.SQLITE;
import cn.hicc.suguan.dormitory.utils.Logs;
import cn.hicc.suguan.dormitory.utils.TextUtils;
import cn.hicc.suguan.dormitory.utils.ToastUtil;
import cn.hicc.suguan.dormitory.utils.URL;
import cn.hicc.suguan.dormitory.utils.Utils;
import okhttp3.Call;

/**
 * 打分页面
 */
public class ScoreActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, NumberPicker.OnValueChangeListener {

    private int a = 1;
    private int b = 1;
    private int c = 1;
    private int d = 1;
    private int e = 1;
    private int f = 1;
    private int g = 1;
    private int h = 1;
    private int i = 1;
    private int j = 1;
    private int k = 1;
    private int l = 1;
    private int m = 1;
    private int n = 1;
    private int o = 1;
    private int p = 1;
    private int q = 1;
    private int r = 1;
    private int s = 0;
    private int t = 0;
    private int u = 0;
    private int v = 0;
    private int w = 0;
    private int x = 0;

    private TextView tv_title_score;
    private ScrollView sv_score;
    private RecyclerView rv_score_person;
    private HostelPersonAdapter adapter;
    private FloatingActionButton score_fab;
    private Button mButton;
    private ProgressDialog mProgressDialog;
    private List<HostelPerson> mlist = new ArrayList<HostelPerson>();
    private SQLiteDatabase db;

    private String scorestr;    // 评分详情
    private int score = 0;  // 总分

    private int photo_num = 1;
    private int high_num_photo = 0;

    private Toolbar toolbar;
    // 1.无睡懒觉现象(4分) 变量a
    private NumberPicker np_sleep;
    // 2.无玩游戏现象(4分) 变量b
    private NumberPicker np_play_game;
    // 3.配合现象(4分) 变量c
    private NumberPicker np_cooperate;
    // 4.着装文明得体(4分) 变量d
    private NumberPicker np_wear;
    // 5.关系融洽,互助互爱(4分) 变量e
    private NumberPicker np_help;
    // 6.张贴宿舍相关规定(10分) 变量f
    private NumberPicker np_hava_regular;
    // 7.地面洁净(10分) 变量g
    private NumberPicker np_floor_clean;
    // 8.床下洁净(10分) 变量h
    private NumberPicker np_underbed_clean;
    // 9.床上物品摆放整齐(5分) 变量i
    private NumberPicker np_bed_neat;
    // 10.被子叠放整齐(5分) 变量j
    private NumberPicker np_quilt_neat;
    // 11.门窗洁净(5分) 变量k
    private NumberPicker np_doorwindow_clean;
    // 12.墙壁洁净(5分) 变量l
    private NumberPicker np_wall_clean;
    // 13. 桌椅、书架干净，书籍等物品摆放整齐（5分） 变量m
    private NumberPicker np_table_clean;
    // 14.桌椅、电脑等摆放整齐(5分) 变量n
    private NumberPicker np_computer_clean;
    // 15.无乱搭,乱挂杂物（5分） 变量o
    private NumberPicker np_construct;
    // 16.暖气片周围无杂物(5分) 变量p
    private NumberPicker np_radiator_nowaste;
    // 17.阳台物品摆放整齐(5分) 变量q
    private NumberPicker np_balcony_neat;
    // 18.阳台地面干净，无垃圾(5分) 变量r
    private NumberPicker np_balcony_clean;
    // 19. (加分项)室内装饰整体美观、风格统一（10分） 变量s
    private NumberPicker np_room_beautiful;

    // 20.减分项(-20分) 1.宿舍内吸烟，乱扔烟头 变量t
    private CheckBox cb_room_smook;
    // 21.减分项(-20分) 2.宿舍无人时，电源插座没有关闭 变量u
    private CheckBox cb_room_power;
    // 22.减分项(-20分) 3.私拉电源线（包括电源插座放置在床铺或捆绑在床上等） 变量v
    private CheckBox cb_person_power;
    // 23.减分项(-20分) 8.饲养宠物 变量w
    private CheckBox cb_hava_pet;
    // 24.减分项(-20分) 5.存放或使用热得快、电水壶、电热杯、电热毯等违章电器 变量x
    private CheckBox cb_use_ban_electrical;

    private int build_code; // 宿舍楼编号
    private int build_num;  // 宿舍号
    private int checkType;  // 检查类型
    private int ischeck;    //是否已经评分
    private int weekCode;   // 当前周
    private int checkNid;   // 查宿编号
    private String account = ""; // 查宿人账号

    // 每隔300毫秒获取一次分数，并更新toolbar上的分数
    private Handler handler = new Handler();
    private Runnable task = new Runnable() {
        @Override
        public void run() {
            getScore();
            handler.postDelayed(task, 300);
            tv_title_score.setText(score + "");
            score = 0;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_score);

        // 获取上一个activity传过来的参数
        getActivityIntent();

        // 实例化控件
        initView();

        // 初始化数据
        initData();
    }

    private void initData() {
        // 打开数据库
        db = openOrCreateDatabase(SQLITE.db, MODE_APPEND, null);

        // 获取宿舍成员
        initPerople();

        /**
         * ischeck
         * 0：未检查
         * 1：已检查
         * 2：修改分数
         */
        switch (ischeck) {
            // 未评分，初始化24项分数
            case 0:
                initScore();
                break;
            // 如果已经评分，恢复评分项分数，将控件设置为不可用
            case 1:
                checkDetailsScore(false);
                break;
            // 修改成绩，恢复评分项分数，将控件设置为可用
            case 2:
                checkDetailsScore(true);
                break;
        }

        // 每隔300毫秒获取一次分数，并更新toolbar上的分数
        handler.postDelayed(task, 300);
    }

    /***
     * 恢复评分项分数
     * @param enabled 控件是否可用
     */
    private void checkDetailsScore(boolean enabled) {
        // 将24项都设置为不可用
        np_sleep.setEnabled(enabled);
        np_play_game.setEnabled(enabled);
        np_cooperate.setEnabled(enabled);
        np_wear.setEnabled(enabled);
        np_help.setEnabled(enabled);
        np_hava_regular.setEnabled(enabled);
        np_floor_clean.setEnabled(enabled);
        np_underbed_clean.setEnabled(enabled);
        np_bed_neat.setEnabled(enabled);
        np_quilt_neat.setEnabled(enabled);
        np_doorwindow_clean.setEnabled(enabled);
        np_wall_clean.setEnabled(enabled);
        np_table_clean.setEnabled(enabled);
        np_computer_clean.setEnabled(enabled);
        np_construct.setEnabled(enabled);
        np_radiator_nowaste.setEnabled(enabled);
        np_balcony_neat.setEnabled(enabled);
        np_balcony_clean.setEnabled(enabled);
        np_room_beautiful.setEnabled(enabled);

        cb_room_smook.setClickable(enabled);
        cb_room_power.setClickable(enabled);
        cb_person_power.setClickable(enabled);
        cb_use_ban_electrical.setClickable(enabled);
        cb_hava_pet.setClickable(enabled);

        // 隐藏提交按钮
        if (!enabled) {
            mButton.setVisibility(View.INVISIBLE);
            score_fab.setVisibility(View.INVISIBLE);
        }

        // 获取评分详情
        initDetailsScore();
    }

    private void initDetailsScore() {
        Logs.d("dorBui:"+TextUtils.GetBuildName(build_code));
        Logs.d("dorNo:"+build_num+"");
        Logs.d("checkType:"+TextUtils.getCheckTypeName(checkType));
        Logs.d("weekCode:"+weekCode+"");
        OkHttpUtils
                .get()
                .url(URL.GET_DETAILS_SCORE)
                .addParams("dorBui",TextUtils.GetBuildName(build_code))
                .addParams("dorNo",build_num+"")
                .addParams("checkType",TextUtils.getCheckTypeName(checkType))
                .addParams("weekCode",weekCode+"")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Logs.e("获取评分详情失败:"+e.toString());
                        ToastUtil.showShort("获取评分详情失败");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Logs.d(response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("flag")) {
                                JSONObject data = jsonObject.getJSONObject("data");
                                String scoreString = data.getString("scoreString").trim();
                                String[] scoreDetails = scoreString.split(" ");
                                if (!scoreDetails[0].equals("null")) {
                                    setDetailsScore(scoreDetails);
                                } else {
                                    ToastUtil.showShort("获取评分详情失败");
                                }
                            } else {
                                ToastUtil.showShort("获取评分详情失败");
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                            Logs.e("解析评分详情失败:"+e1.toString());
                            ToastUtil.showShort("获取评分详情失败");
                        }
                    }
                });
    }

    // 设置评分详情分数
    private void setDetailsScore(String[] scoreDetails) {
        a = (int) Double.parseDouble(scoreDetails[0]);
        b = (int) Double.parseDouble(scoreDetails[1]);
        c = (int) Double.parseDouble(scoreDetails[2]);
        d = (int) Double.parseDouble(scoreDetails[3]);
        e = (int) Double.parseDouble(scoreDetails[4]);
        f = (int) Double.parseDouble(scoreDetails[5]);
        g = (int) Double.parseDouble(scoreDetails[6]);
        h = (int) Double.parseDouble(scoreDetails[7]);
        i = (int) Double.parseDouble(scoreDetails[8]);
        j = (int) Double.parseDouble(scoreDetails[9]);
        k = (int) Double.parseDouble(scoreDetails[10]);
        l = (int) Double.parseDouble(scoreDetails[11]);
        m = (int) Double.parseDouble(scoreDetails[12]);
        n = (int) Double.parseDouble(scoreDetails[13]);
        o = (int) Double.parseDouble(scoreDetails[14]);
        p = (int) Double.parseDouble(scoreDetails[15]);
        q = (int) Double.parseDouble(scoreDetails[16]);
        r = (int) Double.parseDouble(scoreDetails[17]);
        s = (int) Double.parseDouble(scoreDetails[18]);

        t = (int) Double.parseDouble(scoreDetails[19]);
        u = (int) Double.parseDouble(scoreDetails[20]);
        v = (int) Double.parseDouble(scoreDetails[21]);
        w = (int) Double.parseDouble(scoreDetails[22]);
        x = (int) Double.parseDouble(scoreDetails[23]);

        np_sleep.setValue(a);
        np_play_game.setValue(b);
        np_cooperate.setValue(c);
        np_wear.setValue(d);
        np_help.setValue(e);
        np_hava_regular.setValue(f);
        np_floor_clean.setValue(g);
        np_underbed_clean.setValue(h);
        np_bed_neat.setValue(i);
        np_quilt_neat.setValue(j);
        np_doorwindow_clean.setValue(k);
        np_wall_clean.setValue(l);
        np_table_clean.setValue(m);
        np_computer_clean.setValue(n);
        np_construct.setValue(o);
        np_radiator_nowaste.setValue(p);
        np_balcony_neat.setValue(q);
        np_balcony_clean.setValue(r);
        np_room_beautiful.setValue(s);

        if (t == 1) {
            cb_room_smook.setChecked(true);
        } else {
            cb_room_smook.setChecked(false);
        }
        if (u == 1) {
            cb_room_power.setChecked(true);
        } else {
            cb_room_power.setChecked(false);
        }
        if (v == 1) {
            cb_person_power.setChecked(true);
        } else {
            cb_person_power.setChecked(false);
        }
        if (w == 1) {
            cb_use_ban_electrical.setChecked(true);
        } else {
            cb_use_ban_electrical.setChecked(false);
        }
        if (x == 1) {
            cb_hava_pet.setChecked(true);
        } else {
            cb_hava_pet.setChecked(false);
        }
    }

    public void getActivityIntent() {
        /**
         * ischeck
         * 0：未检查
         * 1：已检查
         * 2：修改分数
         */
        Intent intent = getIntent();
        build_code = intent.getIntExtra("buildCode", 0);
        build_num = intent.getIntExtra("buildNum", 0);
        checkType = intent.getIntExtra("checkType", 0);
        ischeck = intent.getIntExtra("ischeck", 0);
        weekCode = intent.getIntExtra("weekCode", 0);
        checkNid = intent.getIntExtra("checkNid", 0);
        if (ischeck == 2) {
            account = intent.getStringExtra("account");
        }
        Logs.d("build_code:"+build_code +",build_num:"+ build_num );
    }

    // 获取宿舍成员
    private void initPerople() {
        adapter = new HostelPersonAdapter(mlist);
        rv_score_person.setLayoutManager(new LinearLayoutManager(this));
        // 设置ItemAnimator 12204
        rv_score_person.setItemAnimator(new DefaultItemAnimator());
        // 设置固定大小
        rv_score_person.setHasFixedSize(true);
        rv_score_person.setAdapter(adapter);

        OkHttpUtils
                .post()
                .url(URL.GET_USER_NAME)
                .addParams("DorBuiCode", TextUtils.GetBuildName(build_code))   // 宿舍楼
                .addParams("DorBedNum", build_num + "")     // 宿舍号
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Logs.d("加载宿舍成员失败:"+e.toString());
                        ToastUtil.showShort("加载宿舍成员失败");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            mlist.clear();
                            Logs.i(response);
                            JSONArray array = new JSONArray(response);
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject ob = array.getJSONObject(i);
                                JSONObject object = ob.getJSONObject("data");
                                mlist.add(new HostelPerson(object.getString("studentName"), object.getInt("bedNumber")));
                                adapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
    }

    // 弹出确认提交对话框
    private void showConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("你当前提交的信息:\n" + TextUtils.GetBuildName(build_code) + build_num + "----" + "分数:"
                + score + "分");
        builder.setTitle("提示");
        builder.setCancelable(false);
        builder.setPositiveButton("提交成绩",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (ischeck == 0) {
                            // 如果是未检查，提交分数
                            upScore(scorestr);
                        } else if (ischeck == 2){
                            // 如果是修改分数
                            changeScore(scorestr);
                        }

                        dialog.dismiss();
                    }
                });
        builder.setNegativeButton("返回",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        score = 0;
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    // 获取评分详情
    private void getDetailsScore() {
        scorestr = a + " " + b + " " + c + " " + d + " " + e + " " + f + " " + g + " " + h + " " + i + " " + j + " "
                + k + " " + l + " " + m + " " + n + " " + o + " " + p + " " + q + " "
                + r + " " + s + " " + t + " " + u + " " + v + " " + w + " " + x;
    }

    // 修改分数
    private void changeScore(String scorestr) {
        showProgressDialog();
        getScore();
        Logs.d(scorestr+"   "+ score);
        OkHttpUtils
                .get()
                .url(URL.CHANGE_SCORE)
                .addParams("scoreele", scorestr)
                .addParams("score", score + "")
                .addParams("Nid", checkNid + "")
                //.addParams("dorBui", build_code + "")
                //.addParams("dorNo", build_num + "")
                //.addParams("checkType", TextUtils.getCheckTypeName(checkType))
                //.addParams("weekCode",weekCode+"")
                //.addParams("assistant", account)
                //.addParams("totalScore", score + "")
                //.addParams("scoreString", scorestr)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Logs.e(e.toString());
                        closeProgressDialog();
                        ToastUtil.showShort("修改失败，请检查网络");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject object = new JSONObject(response);
                            boolean flag = object.getBoolean("flag");
                            closeProgressDialog();
                            if (flag) {
                                ToastUtil.showShort("修改成绩成功");
                                finish();
                            } else {
                                ToastUtil.showShort("修改成绩失败，请稍后重试");
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                            ToastUtil.showShort("修改成绩失败，请稍后重试");
                        }
                    }
                });
    }

    // 提交分数
    private void upScore(String scorestr) {
        showProgressDialog();
        getScore();
        Logs.d(scorestr+"   "+ score);
        OkHttpUtils
                .get()
                .url(URL.UP_SCORE)
                .addParams("scoreele", scorestr)
                .addParams("score", score + "")
                .addParams("Nid", checkNid + "")
                //.addParams("user", SpUtil.getString(Constant.USERNAME))
                //.addParams("checkType", checkType + "")
                //.addParams("dorbui", build_code + "")
                //.addParams("dormitoryno", build_num + "")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Logs.e(e.toString());
                        closeProgressDialog();
                        ToastUtil.showShort("上传失败，请检查网络");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject object = new JSONObject(response);
                            boolean up = object.getBoolean("flag");
                            closeProgressDialog();
                            if (up) {
                                // 弹出提示对下一个宿舍评分对话框
                                showNextDorDialog();
                                ToastUtil.showShort("上传成绩成功,请稍后上传图片");
                            } else {
                                ToastUtil.showShort("上传失败,请检查网络");
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
    }

    // 弹出提示对下一个宿舍评分对话框
    private void showNextDorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("成绩提交成功,选择下一步");
        builder.setTitle("提示");
        builder.setCancelable(false);
        builder.setPositiveButton("下个寝室",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        initCheck();
                        initBuildCode();
                        score = 0;
                        photo_num = 1;
                        dialog.cancel();
                        initPerople();
                    }
                });

        builder.setNegativeButton("返回楼层",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    // 初始化宿舍号
    private void initBuildCode() {
        // 查询下一个宿舍号
        Cursor c = db.query(SQLITE.TABLE_HELPER_CHECK, null, "Hostel>?",
                new String[]{build_num + ""}, null, null, null);// 查询并获得游标
        if (c.moveToNext()){
            // 获取宿舍号  按查询的列的顺序 宿舍号下标为2
            build_num = c.getInt(2);
            checkNid = c.getInt(4);
            toolbar.setTitle(TextUtils.GetBuildName(build_code) + build_num);
            sv_score.scrollTo(0, 0);    // 滑到顶部
        } else {
            ToastUtil.showShort("该宿舍楼已经检查完");
        }

    }

    // 获取总分数
    protected void getScore() {
        score = a+b+c+d+e+f+g+h+i+j+k+l+m+n+o+p+q+r+s;

        if (t == 1 || u == 1 || v == 1 || w == 1
                || x == 1) {
            score -= 20;
        }
    }

    // 初始化checkbox的选中状态
    private void initCheck() {
        boolean e = false;

        cb_room_smook.setChecked(e);
        cb_room_power.setChecked(e);
        cb_person_power.setChecked(e);
        cb_use_ban_electrical.setChecked(e);
        cb_hava_pet.setChecked(e);
    }

    // 初始化24项分数
    private void initScore() {
        a = np_sleep.getValue();
        b = np_play_game.getValue();
        c = np_cooperate.getValue();
        d = np_wear.getValue();
        e = np_help.getValue();
        f = np_hava_regular.getValue();
        g = np_floor_clean.getValue();
        h = np_underbed_clean.getValue();
        i = np_bed_neat.getValue();
        j = np_quilt_neat.getValue();
        k = np_doorwindow_clean.getValue();
        l = np_wall_clean.getValue();
        m = np_table_clean.getValue();
        n = np_computer_clean.getValue();
        o = np_construct.getValue();
        p = np_radiator_nowaste.getValue();
        q = np_balcony_neat.getValue();
        r = np_balcony_clean.getValue();
        s = np_room_beautiful.getValue();
    }

    // 实例化控件
    private void initView() {
        // 24项打分项
        np_sleep = (NumberPicker) findViewById(R.id.np_sleep);
        np_play_game = (NumberPicker) findViewById(R.id.np_play_game);
        np_cooperate = (NumberPicker) findViewById(R.id.np_cooperate);
        np_wear = (NumberPicker) findViewById(R.id.np_wear);
        np_help = (NumberPicker) findViewById(R.id.np_help);
        np_hava_regular = (NumberPicker) findViewById(R.id.np_hava_regular);
        np_floor_clean = (NumberPicker) findViewById(R.id.np_floor_clean);
        np_underbed_clean = (NumberPicker) findViewById(R.id.np_underbed_clean);
        np_bed_neat = (NumberPicker) findViewById(R.id.np_bed_neat);
        np_quilt_neat = (NumberPicker) findViewById(R.id.np_quilt_neat);
        np_doorwindow_clean = (NumberPicker) findViewById(R.id.np_doorwindow_clean);
        np_wall_clean = (NumberPicker) findViewById(R.id.np_wall_clean);
        np_table_clean = (NumberPicker) findViewById(R.id.np_table_clean);
        np_computer_clean = (NumberPicker) findViewById(R.id.np_computer_clean);
        np_construct = (NumberPicker) findViewById(R.id.np_construct);
        np_radiator_nowaste = (NumberPicker) findViewById(R.id.np_radiator_nowaste);
        np_balcony_neat = (NumberPicker) findViewById(R.id.np_balcony_neat);
        np_balcony_clean = (NumberPicker) findViewById(R.id.np_balcony_clean);
        np_room_beautiful = (NumberPicker) findViewById(R.id.np_room_beautiful);

        cb_room_smook = (CheckBox) findViewById(R.id.cb_room_smook);
        cb_room_power = (CheckBox) findViewById(R.id.cb_room_power);
        cb_person_power = (CheckBox) findViewById(R.id.cb_person_power);
        cb_hava_pet = (CheckBox) findViewById(R.id.cb_hava_pet);
        cb_use_ban_electrical = (CheckBox) findViewById(R.id.cb_use_ban_electrical);

        tv_title_score = (TextView) findViewById(R.id.tv_title_score);
        sv_score = (ScrollView) findViewById(R.id.sv_score);
        rv_score_person = (RecyclerView) findViewById(R.id.rv_score_person);
        score_fab = (FloatingActionButton) findViewById(R.id.score_fab);
        mButton = (Button) findViewById(R.id.btn_up_score);

        // 设置选择监听时间
        np_sleep.setOnValueChangedListener(this);
        np_play_game.setOnValueChangedListener(this);
        np_cooperate.setOnValueChangedListener(this);
        np_wear.setOnValueChangedListener(this);
        np_help.setOnValueChangedListener(this);
        np_hava_regular.setOnValueChangedListener(this);
        np_floor_clean.setOnValueChangedListener(this);
        np_underbed_clean.setOnValueChangedListener(this);
        np_bed_neat.setOnValueChangedListener(this);
        np_quilt_neat.setOnValueChangedListener(this);
        np_doorwindow_clean.setOnValueChangedListener(this);
        np_wall_clean.setOnValueChangedListener(this);
        np_table_clean.setOnValueChangedListener(this);
        np_computer_clean.setOnValueChangedListener(this);
        np_construct.setOnValueChangedListener(this);
        np_radiator_nowaste.setOnValueChangedListener(this);
        np_balcony_neat.setOnValueChangedListener(this);
        np_balcony_clean.setOnValueChangedListener(this);
        np_room_beautiful.setOnValueChangedListener(this);

        cb_room_smook.setOnCheckedChangeListener(this);
        cb_room_power.setOnCheckedChangeListener(this);
        cb_person_power.setOnCheckedChangeListener(this);
        cb_use_ban_electrical.setOnCheckedChangeListener(this);
        cb_hava_pet.setOnCheckedChangeListener(this);

        // 设置toolbar标题文字  返回按钮点击事件
        toolbar = (Toolbar) findViewById(R.id.toolbar_score);
        toolbar.setTitle(TextUtils.GetBuildName(build_code) + build_num);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 设置fab点击事件，拍照
        score_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到拍照
                high_num_photo++;
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 1);
            }
        });

        // 提交分数按钮点击事件
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 获取评分详情
                getDetailsScore();

                // 获取总分
                getScore();

                // 弹出确认提交对话框
                showConfirmDialog();
            }
        });
    }

    // checkbox选择监听事件  24项
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.cb_room_smook:
                if (cb_room_smook.isChecked()) {
                    t = 1;
                } else {
                    t = 0;
                }
                break;
            case R.id.cb_room_power:
                if (cb_room_power.isChecked()) {
                    u = 1;
                } else {
                    u = 0;
                }
                break;
            case R.id.cb_person_power:
                if (cb_person_power.isChecked()) {
                    v = 1;
                } else {
                    v = 0;
                }
                break;
            case R.id.cb_hava_pet:
                if (cb_hava_pet.isChecked()) {
                    w = 1;
                } else {
                    w = 0;
                }
                break;
            case R.id.cb_use_ban_electrical:
                if (cb_use_ban_electrical.isChecked()) {
                    x = 1;
                } else {
                    x = 0;
                }
                break;
        }
    }

    // 24项选择监听事件
    @Override
    public void onValueChange(NumberPicker numberPicker, int i0, int i1) {
        switch (numberPicker.getId()) {
            case R.id.np_sleep:
                a = i1;
                break;
            case R.id.np_play_game:
                b = i1;
                break;
            case R.id.np_cooperate:
                c = i1;
                break;
            case R.id.np_wear:
                d = i1;
                break;
            case R.id.np_help:
                e = i1;
                break;
            case R.id.np_hava_regular:
                f = i1;
                break;
            case R.id.np_floor_clean:
                g = i1;
                break;
            case R.id.np_underbed_clean:
                h = i1;
                break;
            case R.id.np_bed_neat:
                i = i1;
                break;
            case R.id.np_quilt_neat:
                j = i1;
                break;
            case R.id.np_doorwindow_clean:
                k = i1;
                break;
            case R.id.np_wall_clean:
                l = i1;
                break;
            case R.id.np_computer_clean:
                m = i1;
                break;
            case R.id.np_table_clean:
                n = i1;
                break;
            case R.id.np_construct:
                o = i1;
                break;
            case R.id.np_radiator_nowaste:
                p = i1;
                break;
            case R.id.np_balcony_neat:
                q = i1;
                break;
            case R.id.np_balcony_clean:
                r = i1;
                break;
            case R.id.np_room_beautiful:
                s = i1;
                break;
            case R.id.cb_room_smook:
                t = i1;
                break;
            case R.id.cb_room_power:
                u = i1;
                break;
            case R.id.cb_person_power:
                v = i1;
                break;
            case R.id.cb_hava_pet:
                w = i1;
                break;
            case R.id.cb_use_ban_electrical:
                x = i1;
                break;
        }
    }

    // 从拍照界面返回后回调处理
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            String sdStatus = Environment.getExternalStorageState();
            if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
                Toast.makeText(ScoreActivity.this, "SD卡不可用", Toast.LENGTH_SHORT).show();
                Logs.i("SD卡不可用");
                return;
            }
            String mFiles = Environment.getExternalStorageDirectory().getPath();
            mFiles = mFiles + "/" + "dormitory/";
            String photoNum = "0" + photo_num;
            String photoName = Utils.GetTime() + "-" + build_code + "-" + build_num + "-" + photoNum + ".jpg";
            Toast.makeText(this, "文件保存在SD卡下dormitory文件夹内 文件名为:" + photoName, Toast.LENGTH_LONG).show();
            Bundle bundle = data.getExtras();
            Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式
            FileOutputStream b = null;
            String fileName = null;
            File file = new File(mFiles);
            file.mkdirs();// 创建文件夹
            fileName = mFiles + photoName;
            Logs.i(fileName);
            try {
                b = new FileOutputStream(fileName);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
                photo_num++;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    b.flush();
                    b.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(ScoreActivity.this);
        }
        mProgressDialog.setMessage("上传中...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
    }

    private void closeProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
