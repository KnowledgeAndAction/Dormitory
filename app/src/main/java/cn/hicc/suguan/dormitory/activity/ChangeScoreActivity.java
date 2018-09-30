package cn.hicc.suguan.dormitory.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import cn.hicc.suguan.dormitory.R;
import cn.hicc.suguan.dormitory.utils.Logs;
import cn.hicc.suguan.dormitory.utils.TextUtils;
import cn.hicc.suguan.dormitory.utils.ToastUtil;
import cn.hicc.suguan.dormitory.utils.URL;
import okhttp3.Call;

/**
 * 修改宿舍成绩
 */
public class ChangeScoreActivity extends AppCompatActivity implements View.OnClickListener{

    private int checkedItem = 0;
    private AppCompatEditText et_dor_num;
    private EditText et_account;
    private int checkType = 1;
    private AppCompatButton bt_dor;
    private int buildCode = 0;
    private int weekCode = 0;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_score);

        Intent intent = getIntent();
        weekCode = intent.getIntExtra("weekCode",0);

        initView();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        toolbar.setTitle("修改宿舍成绩");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        bt_dor = (AppCompatButton) findViewById(R.id.bt_dor);
        et_dor_num = (AppCompatEditText) findViewById(R.id.et_dor_num);
        RadioGroup radio_group = (RadioGroup) findViewById(R.id.radio_group);
        et_account = (EditText) findViewById(R.id.et_account);
        Button bt_change = (Button) findViewById(R.id.bt_change);

        bt_dor.setOnClickListener(this);
        bt_change.setOnClickListener(this);

        // 为radiogroup设置选择监听事件
        radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    // 普查
                    case R.id.cb_all:
                        checkType = 1;
                        break;
                    // 抽查
                    case R.id.cb_random:
                        checkType = 2;
                        break;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 选择宿舍楼
            case R.id.bt_dor:
                showDorSingleDialog();
                break;
            // 跳转到修改界面
            case R.id.bt_change:
                changeScore();
                break;
        }
    }

    // 判断是否有分数
    private void getDorScore(String dorBui, String dorNo, String checkType) {
        showDialog();
        Logs.d("dorBui:"+dorBui);
        Logs.d("dorNo:"+dorNo);
        Logs.d("checkType:"+checkType);
        Logs.d("weekCode:"+weekCode);
        OkHttpUtils
                .get()
                .url(URL.GET_DETAILS_SCORE)
                .addParams("dorBui",dorBui)
                .addParams("dorNo",dorNo)
                .addParams("checkType",checkType)
                .addParams("weekCode",weekCode+"")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        closeDialog();
                        Logs.e("获取宿舍分数失败:"+e.toString());
                        ToastUtil.showShort("获取宿舍信息失败，请稍后重试");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Logs.d(response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            closeDialog();
                            if (jsonObject.getBoolean("flag")) {
                                gotoChangeScore();
                            } else {
                                ToastUtil.showShort("本周暂无成绩，或没有该宿舍，请检查宿舍信息是否输入正确");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            ToastUtil.showShort("获取宿舍信息失败，请稍后重试");
                        }
                    }
                });
    }

    // 跳转到修改界面
    private void changeScore() {
        String buildNum = et_dor_num.getText().toString().trim();
        String account = et_account.getText().toString().trim();
        if (!buildNum.equals("") && !account.equals("") && buildCode!=0 && weekCode!=0) {
            getDorScore(TextUtils.GetBuildName(buildCode),buildNum,TextUtils.getCheckTypeName(checkType));
        } else {
            ToastUtil.showShort("请将信息填写完整");
        }
    }

    // 跳转到修改界面
    private void gotoChangeScore() {
        String buildNum = et_dor_num.getText().toString().trim();
        String account = et_account.getText().toString().trim();
        if (!buildNum.equals("") && !account.equals("") && buildCode!=0 && weekCode!=0) {
            Intent intent = new Intent(this, ScoreActivity.class);
            intent.putExtra("buildCode", buildCode);
            intent.putExtra("buildNum", Integer.parseInt(buildNum));
            intent.putExtra("checkType", checkType);
            intent.putExtra("ischeck",2);
            intent.putExtra("weekCode",weekCode);
            intent.putExtra("account",account);
            startActivity(intent);
        } else {
            ToastUtil.showShort("请将信息填写完整");
        }
    }

    // 弹出单选框 选择宿舍楼
    private void showDorSingleDialog() {
        final String[] items = TextUtils.buildName;
        final int[] params = {10,11,12,13,14,15,16,17,18,19,20,21,22};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择宿舍楼");
        builder.setIcon(R.mipmap.logo);
        builder.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                        checkedItem = which;
            }
        });
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                        buildCode = params[checkedItem];
                        bt_dor.setText(items[checkedItem]);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void showDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
        }
        mProgressDialog.setMessage("加载中...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
    }

    private void closeDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }
}
