package cn.hicc.suguan.dormitory.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import cn.hicc.suguan.dormitory.R;

/**
 * 宿舍详细成绩
 */
public class DorDetailScoreActivity extends AppCompatActivity {

    private String dorBui;
    private String dorNo;
    private int weekCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dor_detail_score);

        getActivityIntent();

        initView();
    }

    private void getActivityIntent() {
        Intent intent = getIntent();
        dorBui = intent.getStringExtra("dorBui");
        dorNo = intent.getStringExtra("dorNo");
        weekCode = intent.getIntExtra("weekCode",0);
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        toolbar.setTitle("宿舍详细信息");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
