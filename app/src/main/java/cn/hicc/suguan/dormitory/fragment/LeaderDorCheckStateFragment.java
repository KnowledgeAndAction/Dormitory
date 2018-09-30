package cn.hicc.suguan.dormitory.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.hicc.suguan.dormitory.R;
import cn.hicc.suguan.dormitory.adapter.DorCheckStateRecylerAdapter;
import cn.hicc.suguan.dormitory.model.DorCheckState;
import cn.hicc.suguan.dormitory.utils.Logs;
import cn.hicc.suguan.dormitory.utils.TextUtils;
import cn.hicc.suguan.dormitory.utils.ToastUtil;
import cn.hicc.suguan.dormitory.utils.URL;
import okhttp3.Call;

/**
 * Created by 陈帅 on 2018/6/12/031.
 * 领导  本周查宿情况
 */

public class LeaderDorCheckStateFragment extends Fragment implements View.OnClickListener{

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout swipeRefresh;
    private Button bt_shuyuan;
    private Button bt_sushe;
    private Button bt_wuchengji;
    private Button bt_select;
    private List<DorCheckState> mDorCheckStateList = new ArrayList<>();
    private List<DorCheckState> firstData = new ArrayList<>();  // 原始数据
    private DorCheckStateRecylerAdapter adapter;
    private String mSeleteDetailed1 = "全部";
    private String mSeleteDetailed2 = "全部";
    private String mSeleteDetailed3 = "不筛选";
    private int checkedItem1 = 0;
    private int checkedItem2 = 0;
    private int checkedItem3 = 0;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                // 成功
                case 0:
                    adapter.notifyDataSetChanged();
                    swipeRefresh.setRefreshing(false);
                    break;
                // 失败
                case 1:
                    ToastUtil.showShort("本周暂无数据");
                    swipeRefresh.setRefreshing(false);
                    break;
                // 解析失败
                case 2:
                    ToastUtil.showShort("获取数据失败，请稍后重试");
                    swipeRefresh.setRefreshing(false);
                    break;
                // 筛选数据
                case 3:
                    List<DorCheckState> newList = (List<DorCheckState>) msg.obj;
                    adapter.setItems(newList);
                    break;
            }
        }
    };

    public LeaderDorCheckStateFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leader_dor_check_state,container,false);

        initView(view);

        getScoreData();

        return view;
    }

    // 获取平均分数据
    private void getScoreData() {
        swipeRefresh.setRefreshing(true);
        OkHttpUtils
                .get()
                .url(URL.DOR_CHECK_STATE)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtil.showShort("获取数据失败，请稍后重试");
                        Logs.e("获取本周查宿状态失败:"+e.toString());
                        swipeRefresh.setRefreshing(false);
                    }

                    @Override
                    public void onResponse(final String response, int id) {
                        // 解析json
                        new Thread(){
                            @Override
                            public void run() {
                                super.run();
                                parseJson(response);
                            }
                        }.start();
                    }
                });
    }

    // 解析json
    private void parseJson(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            // 响应后清空数据
            mDorCheckStateList.clear();
            firstData.clear();
            if (jsonObject.getBoolean("flag")) {
                // 具体数据
                JSONArray data = jsonObject.getJSONArray("data");
                for (int i=0; i<data.length(); i++) {
                    JSONObject scoreData = data.getJSONObject(i);

                    int choucha = scoreData.getInt("choucha");
                    int pucha = scoreData.getInt("pucha");
                    String shuyuan = scoreData.getString("shuyuan");
                    String lou = scoreData.getString("lou");
                    String hao = scoreData.getString("hao");

                    DorCheckState dorCheckState = new DorCheckState(shuyuan,lou,hao,pucha,choucha);

                    mDorCheckStateList.add(dorCheckState);
                    // 保留原始数据
                    firstData.add(dorCheckState);
                }
                // 成功  更新数据
                mHandler.sendEmptyMessage(0);
            } else {
                // 失败
                mHandler.sendEmptyMessage(1);
                Logs.e("获取本周检查状态失败");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(2);
            Logs.e("获取本周检查状态失败:"+e.toString());
        }
    }

    private void initView(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        // 设置LinearLayoutManager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // 设置ItemAnimator
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        // 设置固定大小
        mRecyclerView.setHasFixedSize(true);
        // 初始化自定义的适配器
        adapter = new DorCheckStateRecylerAdapter(mDorCheckStateList);
        // 为mRecyclerView设置适配
        mRecyclerView.setAdapter(adapter);


        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        // 配置swipeRefresh
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary , R.color.colorAccent, R.color.colorPrimaryDark);
        // 设置刷新事件
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 获取数据
                getScoreData();
            }
        });

        // 配置按钮
        initButton(view);
    }

    // 配置按钮
    private void initButton(View view) {
        bt_shuyuan = (Button) view.findViewById(R.id.bt_shuyuan);
        bt_sushe = (Button) view.findViewById(R.id.bt_sushe);
        bt_wuchengji = (Button) view.findViewById(R.id.bt_wuchengji);
        bt_select = (Button) view.findViewById(R.id.bt_select);

        bt_shuyuan.setOnClickListener(this);
        bt_sushe.setOnClickListener(this);
        bt_wuchengji.setOnClickListener(this);
        bt_select.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 筛选书院
            case R.id.bt_shuyuan:
                showDetailedSingleDialog(1,TextUtils.ChoiceDialogAcademyName,"选择书院",checkedItem1);
                break;
            // 筛选宿舍楼
            case R.id.bt_sushe:
                showDetailedSingleDialog(2,TextUtils.ChoiceDialogBuildName,"选择宿舍楼",checkedItem2);
                break;
            // 筛选无成绩的
            case R.id.bt_wuchengji:
                String[] items = {"不筛选","普查0分","抽查0分"};
                showDetailedSingleDialog(3,items,"选择筛选无成绩检查类型",checkedItem3);
                break;
            // 进一步筛选
            case R.id.bt_select:
                selectScore();
                break;
        }
    }

    // 进一步筛选分数
    private void selectScore() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                List<DorCheckState> newList = new ArrayList<>();
                newList.clear();
                // 选择条件如果都为全部  查询全部
                if (mSeleteDetailed1.equals("全部") && mSeleteDetailed2.equals("全部") && mSeleteDetailed3.equals("不筛选")) {
                    for (DorCheckState dorCheckState : firstData) {
                        newList.add(dorCheckState);
                    }
                    // 根据条件1查询 书院
                } else if (!mSeleteDetailed1.equals("全部") && mSeleteDetailed2.equals("全部") && mSeleteDetailed3.equals("不筛选")){
                    for (DorCheckState dorCheckState : firstData) {
                        if (dorCheckState.getShuyuan().equals(mSeleteDetailed1)) {
                            newList.add(dorCheckState);
                        }
                    }
                    // 根据条件2查询 宿舍楼
                } else if (mSeleteDetailed1.equals("全部") && !mSeleteDetailed2.equals("全部") && mSeleteDetailed3.equals("不筛选")) {
                    for (DorCheckState dorCheckState : firstData) {
                        if (dorCheckState.getLou().equals(mSeleteDetailed2)) {
                            newList.add(dorCheckState);
                        }
                    }
                    // 根据条件3查询  无成绩的
                } else if (mSeleteDetailed1.equals("全部") && mSeleteDetailed2.equals("全部") && !mSeleteDetailed3.equals("不筛选")) {
                    for (DorCheckState dorCheckState : firstData) {
                        int score = 0;
                        // 看选择的是普查还是抽查  然后获取对应的分数
                        switch (mSeleteDetailed3) {
                            case "普查0分":
                                score = dorCheckState.getPucha();
                                break;
                            case "抽查0分":
                                score = dorCheckState.getChoucha();
                                break;
                        }
                        // 再判断该分数是否为0
                        if (score == 0) {
                            newList.add(dorCheckState);
                        }
                    }
                    // 根据书院 宿舍楼 查询
                } else if (!mSeleteDetailed1.equals("全部") && !mSeleteDetailed2.equals("全部") && mSeleteDetailed3.equals("不筛选")) {
                    for (DorCheckState dorCheckState : firstData) {
                        if (dorCheckState.getShuyuan().equals(mSeleteDetailed1) && dorCheckState.getLou().equals(mSeleteDetailed2)) {
                            newList.add(dorCheckState);
                        }
                    }
                    // 根据书院 无成绩 查询
                } else if (!mSeleteDetailed1.equals("全部") && mSeleteDetailed2.equals("全部") && !mSeleteDetailed3.equals("不筛选")) {
                    for (DorCheckState dorCheckState : firstData) {
                        int score = 0;
                        // 看选择的是普查还是抽查  然后获取对应的分数
                        switch (mSeleteDetailed3) {
                            case "普查0分":
                                score = dorCheckState.getPucha();
                                break;
                            case "抽查0分":
                                score = dorCheckState.getChoucha();
                                break;
                        }
                        if (dorCheckState.getShuyuan().equals(mSeleteDetailed1) && score == 0) {
                            newList.add(dorCheckState);
                        }
                    }
                    // 根据宿舍楼 无成绩 查询
                } else if (mSeleteDetailed1.equals("全部") && !mSeleteDetailed2.equals("全部") && !mSeleteDetailed3.equals("不筛选")) {
                    for (DorCheckState dorCheckState : firstData) {
                        int score = 0;
                        // 看选择的是普查还是抽查  然后获取对应的分数
                        switch (mSeleteDetailed3) {
                            case "普查0分":
                                score = dorCheckState.getPucha();
                                break;
                            case "抽查0分":
                                score = dorCheckState.getChoucha();
                                break;
                        }
                        if (dorCheckState.getLou().equals(mSeleteDetailed2) && score == 0) {
                            newList.add(dorCheckState);
                        }
                    }
                    // 根据 条件1 条件2 条件3 查询
                } else if (!mSeleteDetailed1.equals("全部") && !mSeleteDetailed2.equals("全部") && !mSeleteDetailed3.equals("不筛选")) {
                    for (DorCheckState dorCheckState : firstData) {
                        int score = 0;
                        // 看选择的是普查还是抽查  然后获取对应的分数
                        switch (mSeleteDetailed3) {
                            case "普查0分":
                                score = dorCheckState.getPucha();
                                break;
                            case "抽查0分":
                                score = dorCheckState.getChoucha();
                                break;
                        }
                        if (dorCheckState.getShuyuan().equals(mSeleteDetailed1) && dorCheckState.getLou().equals(mSeleteDetailed2) && score == 0) {
                            newList.add(dorCheckState);
                        }
                    }
                }

                // 更新数据
                Message message = Message.obtain();
                message.obj = newList;
                message.what = 3;
                mHandler.sendMessage(message);
            }
        }.start();
    }

    // 弹出单选框 选择查询条件
    private void showDetailedSingleDialog(final int condition, final String[] items, String title,int checkedItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title);
        builder.setIcon(R.mipmap.logo);
        builder.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (condition) {
                    case 1:
                        mSeleteDetailed1 = items[which];
                        checkedItem1 = which;
                        break;
                    case 2:
                        mSeleteDetailed2 = items[which];
                        checkedItem2 = which;
                        break;
                    case 3:
                        mSeleteDetailed3 = items[which];
                        checkedItem3 = which;
                        break;
                }
            }
        });
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (condition) {
                    case 1:
                        bt_shuyuan.setText(mSeleteDetailed1);
                        break;
                    case 2:
                        bt_sushe.setText(mSeleteDetailed2);
                        break;
                    case 3:
                        bt_wuchengji.setText(mSeleteDetailed3);
                        break;
                }
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
}
