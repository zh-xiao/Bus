package com.xiao.bus;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.zhangxiao.bus.Bus;

public class BottomFragment extends Fragment implements View.OnClickListener {

    Button btn_subscribe_aaa;
    Button btn_subscribe_bbb;
    Button btn_subscribe_ccc;

    Bus bus = new Bus();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bus.regist(this);
        View view = inflater.inflate(R.layout.fragment_bottom, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        btn_subscribe_aaa = view.findViewById(R.id.btn_subscribe_aaa);
        btn_subscribe_bbb = view.findViewById(R.id.btn_subscribe_bbb);
        btn_subscribe_ccc = view.findViewById(R.id.btn_subscribe_ccc);
        btn_subscribe_aaa.setOnClickListener(this);
        btn_subscribe_bbb.setOnClickListener(this);
        btn_subscribe_ccc.setOnClickListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bus.unRegist(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_subscribe_aaa:
                bus.subscribe("aaa", new Bus.OnPostListener<Integer>() {
                    @Override
                    public void onPost(Integer eventData) {
                        btn_subscribe_aaa.setHint("接收到:"+eventData);
                    }
                });
                break;
            case R.id.btn_subscribe_bbb:
                bus.subscribeStick("bbb", new Bus.OnPostListener<Integer>() {
                    @Override
                    public void onPost(Integer eventData) {
                        btn_subscribe_bbb.setHint("接收到:"+eventData);
                    }
                });
                break;
            case R.id.btn_subscribe_ccc:
                bus.subscribeStick("ccc", new Bus.OnPostListener<Integer>() {
                    @Override
                    public void onPost(Integer eventData) {
                        btn_subscribe_ccc.setHint("接收到:"+eventData);
                    }
                });
                break;
        }
    }
}
