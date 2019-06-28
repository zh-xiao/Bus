package com.xiao.bus;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.zhangxiao.bus.Bus;

public class BottomFragment extends Fragment implements View.OnClickListener {

    Button btn_subscribe_aaa;
    Button btn_subscribe_bbb;
    Button btn_subscribe_ccc;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
        Bus.cancel(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_subscribe_aaa:
                Bus.onMainThread(this, "aaa", new Bus.OnPostListener<Integer>() {
                    @Override
                    public void onPost(final Integer eventData) {
                        Log.d("Bus", "响应线程:" + Thread.currentThread().getName());
                        btn_subscribe_aaa.setHint("接收到:" + eventData);
                    }
                });
                break;
            case R.id.btn_subscribe_bbb:
                Bus.onStickMainThread(this, "bbb", new Bus.OnPostListener<Integer>() {
                    @Override
                    public void onPost(final Integer eventData) {
                        Log.d("Bus", "响应线程:" + Thread.currentThread().getName());
                        btn_subscribe_bbb.setHint("接收到:" + eventData);
                    }
                });
                break;
            case R.id.btn_subscribe_ccc:
                Bus.onStickSubThread(this, "ccc", new Bus.OnPostListener<Integer>() {
                    @Override
                    public void onPost(final Integer eventData) {
                        Log.d("Bus", "响应线程:" + Thread.currentThread().getName());
                        //切到主线程更新UI
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                btn_subscribe_ccc.setHint("接收到:" + eventData);
                            }
                        });
                    }
                });
                break;
        }
    }
}
