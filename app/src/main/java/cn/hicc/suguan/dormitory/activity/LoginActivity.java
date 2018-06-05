package cn.hicc.suguan.dormitory.activity;

/**
 * Created by 陈帅 on 2017/07/12/025.
 * 登录界面
 */

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.hicc.suguan.dormitory.R;
import cn.hicc.suguan.dormitory.utils.Constant;
import cn.hicc.suguan.dormitory.utils.SpUtil;
import cn.hicc.suguan.dormitory.utils.ToastUtil;
import cn.hicc.suguan.dormitory.utils.URL;
import cn.hicc.suguan.dormitory.utils.Utils;
import okhttp3.Call;

public class LoginActivity extends MainBaseActivity {

    private AppCompatButton bt_sign_in;
    private TextInputLayout text_input_password;
    private TextInputLayout text_input_username;
    private ProgressDialog mProgressDialog;
    private int checkType = 1;
    private RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();

        // 登录按钮点击事件
        loginClick();

        // 检查是否已经登录
        checkIsLogin();

        // 检查运行时权限
        checkPermission();
    }

    private void checkPermission() {
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.CAMERA);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(this, permissions, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int grantResult : grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            ToastUtil.showShort("我们需要获取一些权限才能正常使用");
                        }
                    }
                } else {
                    ToastUtil.showShort("出了个小错误");
                    finish();
                }
                break;
        }
    }

    // 检查是否已经登录
    private void checkIsLogin() {
        if (SpUtil.getBoolean(Constant.IS_LOGIN)) {
            text_input_username.getEditText().setText(SpUtil.getString(Constant.USERNAME));
            text_input_password.getEditText().setText(SpUtil.getString(Constant.PASSWORD));
            checkLoginForService(SpUtil.getString(Constant.USERNAME), SpUtil.getString(Constant.PASSWORD));
        } else {
            text_input_username.getEditText().setText(SpUtil.getString(Constant.USERNAME));
        }
    }

    // 登录按钮点击事件
    private void loginClick() {
        bt_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 登录逻辑
                login();
            }
        });
    }

    // 登录逻辑
    private void login() {
        // 是否能登录
        boolean isLogin = true;

        String username = text_input_username.getEditText().getText().toString().trim();
        String password = text_input_password.getEditText().getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            text_input_username.setError("用户名为空");
            isLogin = false;
        }
        if (TextUtils.isEmpty(password)) {
            text_input_password.setError("密码为空");
            isLogin = false;
        }

        if (isLogin) {
            // 检查是否登录成功
            checkLoginForService(username, password);
        }

        // 给输入框设置文字改变监听事件，如果输入框文字改变，并且不为空，就将错误信息置空
        text_input_username.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    text_input_username.setError("");
                }
                // 如果账号是hicc就把单选框隐藏
                if (s.toString().equals("1001") || s.toString().equals("1002") || s.toString().equals("1003")) {
                    radioGroup.setVisibility(View.GONE);
                } else {
                    radioGroup.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // 给输入框设置文字改变监听事件，如果输入框文字改变，并且不为空，就将错误信息置空
        text_input_password.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    text_input_password.setError("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    // 检查是否登录成功
    private void checkLoginForService(final String username, final String password) {
        showProgressDialog();
        OkHttpUtils
                .get()
                .url(URL.LOGIN_URL)
                .addParams("userName", username)
                .addParams("userPass", password)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtil.showShort("网络异常，请稍后重试");
                        closeProgressDialog();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean flag = jsonObject.getBoolean("flag");
                            if (flag) {
                                closeProgressDialog();
                                // 检查是否记住密码
                                checkRememberPassword(username, password);

                                JSONObject data = jsonObject.getJSONObject("data");
                                // 获取用户姓名
                                String name = data.getString("name");
                                SpUtil.putString(Constant.ASSISTANT_NAME, name);

                                // 获取用户等级
                                int level = data.getInt("level");
                                // 获取当前周数  接口暂时修改
                                int weekCode = data.getInt("weekCode");
                                //int weekCode = 11;

                                // 进入主界面
                                enterHome(level,weekCode);
                            } else {
                                closeProgressDialog();
                                ToastUtil.showShort("用户名或密码错误");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    // 根据用户等级进入不同的界面
    private void enterHome(int level, int weekCode) {
        switch (level) {
            // 查宿人员
            case 1:
                Intent intent = new Intent(LoginActivity.this, CheckOutActivity.class);
                intent.putExtra("type", checkType);
                intent.putExtra("weekCode", weekCode);
                startActivity(intent);
                break;
            // 导员
            case 3:
                Intent intent3 = new Intent(LoginActivity.this, TeacherActivity.class);
                intent3.putExtra("weekCode", weekCode);
                startActivity(intent3);
                break;
            // 学部
            case 2:
                Intent intent2 = new Intent(LoginActivity.this, DivisionActivity.class);
                intent2.putExtra("weekCode", weekCode);
                startActivity(intent2);
                break;
            // 学院领导
            case 5:
            case 6:
                Intent intent5 = new Intent(LoginActivity.this, LeaderActivity.class);
                intent5.putExtra("weekCode", weekCode);
                startActivity(intent5);
                break;

        }

        ToastUtil.showShort("登录成功");
        finish();
    }

    // 检查是否记住密码
    private void checkRememberPassword(String username, String password) {
        /*if (cb_password.isChecked()) {
            SpUtil.putBoolean(Constant.IS_LOGIN, true);
            SpUtil.putString(Constant.USERNAME, username);
            SpUtil.putString(Constant.PASSWORD, password);
        } else {
            SpUtil.putBoolean(Constant.IS_LOGIN, false);
            SpUtil.putString(Constant.USERNAME, username);
            SpUtil.putString(Constant.PASSWORD, password);
        }*/
        // 改为默认记住密码
        SpUtil.putBoolean(Constant.IS_LOGIN, true);
        SpUtil.putString(Constant.USERNAME, username);
        SpUtil.putString(Constant.PASSWORD, password);
    }

    private void initView() {
        text_input_username = (TextInputLayout) findViewById(R.id.text_input_username);
        text_input_password = (TextInputLayout) findViewById(R.id.text_input_password);

        radioGroup = (RadioGroup) findViewById(R.id.radio_group);
        // 单选框监听事件
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
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

        bt_sign_in = (AppCompatButton) findViewById(R.id.bt_sign_in);

        TextView tv_version = (TextView) findViewById(R.id.tv_version);
        tv_version.setText("v " + Utils.getVersionName());
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(LoginActivity.this);
        }
        mProgressDialog.setMessage("登录中...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
    }

    private void closeProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
