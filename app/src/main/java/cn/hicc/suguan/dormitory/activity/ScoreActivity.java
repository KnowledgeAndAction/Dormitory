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
import android.view.MenuItem;
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
import cn.hicc.suguan.dormitory.utils.Constant;
import cn.hicc.suguan.dormitory.utils.Logs;
import cn.hicc.suguan.dormitory.utils.SpUtil;
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

    private TextView tv_title_score;
    private ScrollView sv_score;
    private RecyclerView rv_score_person;
    private FloatingActionButton score_fab;
    private Button mButton;

    private String scorestr;
    private int score = 0;
    private boolean aa = true;

    private int build_code;
    private int build_num;
    private SQLiteDatabase db;
    private HostelPersonAdapter adapter;
    private List<HostelPerson> mlist = new ArrayList<HostelPerson>();
    private int photo_num = 1;
    private int high_num_photo = 0;

    private Handler handler = new Handler();
    private Runnable task = new Runnable() {
        @Override
        public void run() {
            GetScore();
            handler.postDelayed(task, 300);
            tv_title_score.setText(score + "");
            score = 0;
        }
    };
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
    private int checkType;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_score);

        getActivityIntent();

        initView();

        initData();
    }

    private void initData() {
        db = openOrCreateDatabase(SQLITE.db, MODE_APPEND, null);

        initPerople();
        initDormi();

        handler.postDelayed(task, 300);
    }


    public void getActivityIntent() {
        Intent intent = getIntent();
        build_code = intent.getIntExtra("buildCode", 0);
        build_num = intent.getIntExtra("buildNum", 0);
        checkType = intent.getIntExtra("checkType", 0);
        Logs.d("build_code:"+build_code +",build_num:"+ build_num );
    }

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
                .url(URL.Get_User_Name)
                .addParams("DorBuiCode", build_code + "")   // 宿舍楼
                .addParams("DorBedNum", build_num + "")     // 宿舍号
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Logs.d("加载宿舍成员失败");
                        showFalseData();
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

    private void showFalseData() {
        mlist.clear();
        for (int i = 0; i < 6; i++) {
            mlist.add(new HostelPerson("张三" + i, i));
        }
        adapter.notifyDataSetChanged();
    }

    private void initDormi() {
        initScore();

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scorestr = a + " " + b + " " + c + " " + d + " " + e + " " + f + " " + g + " " + h + " " + i + " " + j + " "
                        + k + " " + l + " " + m + " " + n + " " + o + " " + p + " " + q + " "
                        + r + " " + s + " " + t + " " + u + " " + v + " " + w + " " + x;

                GetScore();

                AlertDialog.Builder builder = new AlertDialog.Builder(ScoreActivity.this);
                builder.setMessage("你当前提交的信息:\n" + TextUtils.GetBuildName(build_code) + build_num + "----" + "分数:"
                        + score + "分");
                builder.setTitle("提示");
                builder.setCancelable(false);
                builder.setPositiveButton("提交成绩",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                UPSCORE(scorestr);
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
        });
    }

    private void UPSCORE(String scorestr) {
        showProgressDialog();
        GetScore();
        Logs.d(scorestr+"   "+ score);
        OkHttpUtils
                .get()
                .url(URL.UP_SCORE)
                .addParams("scoreele", scorestr)
                .addParams("user", SpUtil.getString(Constant.USERNAME))
                .addParams("score", score + "")
                .addParams("checkType", checkType + "")
                .addParams("dorbui", build_code + "")
                .addParams("dormitoryno", build_num + "")
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
                                CreatDialog();
                                //finish();
                                Toast.makeText(ScoreActivity.this, "上传成绩成功,请稍后上传图片", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ScoreActivity.this, "上传失败,请检查网络", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
    }


    private void CreatDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ScoreActivity.this);
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

    private void initBuildCode() {
        Cursor c = db.query(SQLITE.TABLE_HELPER_CHECK, null, "Hostel>?",
                new String[]{build_num + ""}, null, null, null);// 查询并获得游标
        c.moveToNext();
        build_num = c.getInt(2);
        //score += 4;
        toolbar.setTitle(TextUtils.GetBuildName(build_code) + build_num);
        setSupportActionBar(toolbar);
        sv_score.scrollTo(0, 0);
    }

    protected void GetScore() {
        score = a+b+c+d+e+f+g+h+i+j+k+l+m+n+o+p+q+r+s;


        if (t == 1 || u == 1 || v == 1 || w == 1
                || x == 1) {
            score -= 20;
            //   totalscore-=20;
        }
    }

    private void initCheck() {
        boolean e = false;

        cb_room_smook.setChecked(e);
        cb_room_power.setChecked(e);
        cb_person_power.setChecked(e);
        cb_use_ban_electrical.setChecked(e);
        cb_hava_pet.setChecked(e);
    }

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

    private void initView() {
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



        toolbar = (Toolbar) findViewById(R.id.toolbar_score);
        toolbar.setTitle(TextUtils.GetBuildName(build_code) + build_num);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        score_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                high_num_photo++;
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 1);
            }
        });

        NumberPicker np_sleep = (NumberPicker) findViewById(R.id.np_sleep);

    }

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


            default:
                break;
        }


    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:// 点击返回图标事件
                this.finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return false;
    }


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
