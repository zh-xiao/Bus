package com.xiao.bus;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.zhangxiao.bus.Bus;

public class TopFragment extends Fragment implements View.OnClickListener {
    Button btn_aaa;
    Button btn_bbb;
    Button btn_ccc;
    Button btn_clear_bbb;
    Button btn_clear_ccc;
    Button btn_clear_all;

    int aaa=1;
    int bbb=1;
    int ccc=1;

    public TopFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_top, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        btn_aaa = view.findViewById(R.id.btn_aaa);
        btn_bbb = view.findViewById(R.id.btn_bbb);
        btn_ccc = view.findViewById(R.id.btn_ccc);
        btn_clear_bbb = view.findViewById(R.id.btn_clear_bbb);
        btn_clear_ccc = view.findViewById(R.id.btn_clear_ccc);
        btn_clear_all = view.findViewById(R.id.btn_clear_all);

        btn_aaa.setOnClickListener(this);
        btn_bbb.setOnClickListener(this);
        btn_ccc.setOnClickListener(this);
        btn_clear_bbb.setOnClickListener(this);
        btn_clear_ccc.setOnClickListener(this);
        btn_clear_all.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_aaa:
                Bus.post("aaa",aaa);
                aaa++;
                btn_aaa.setHint("post(\"aaa\","+aaa+")");
                break;
            case R.id.btn_bbb:
                Bus.postStick("bbb",bbb);
                bbb++;
                btn_bbb.setHint("postStick(\"bbb\","+bbb+")");
                break;
            case R.id.btn_ccc:
                Bus.postStick("ccc",ccc);
                ccc++;
                btn_ccc.setHint("postStick(\"ccc\","+ccc+")");
                break;
            case R.id.btn_clear_bbb:
                Bus.removeStickEvent("bbb");
                break;
            case R.id.btn_clear_ccc:
                Bus.removeStickEvent("ccc");
                break;
            case R.id.btn_clear_all:
                Bus.removeAllStickEvent();
                break;
        }
    }
}
