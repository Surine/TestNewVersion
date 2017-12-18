package com.example.surine.testnewversion.Fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.surine.testnewversion.Bean.SimpleEvent;
import com.example.surine.testnewversion.R;
import com.example.surine.testnewversion.Utils.HttpUtil;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Surine on 2017/12/17.
 */

public class ScanISBN extends Fragment {
    @BindView(R.id.textView3)
    TextView textView3;
    Unbinder unbinder;
    @BindView(R.id.imageButton)
    Button imageButton;
    @BindView(R.id.editText2)
    EditText editText2;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.activity_scan_isbn, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @OnClick({R.id.imageButton})
    public void onViewClicked(View view)     {
        switch (view.getId()) {
            case R.id.imageButton:
                if(editText2.getText().toString().length() == 13){
                    // TODO : 发送服务器验证（最后得到ISBN+序号的串）
                    EventBus.getDefault().post(new SimpleEvent(1,editText2.getText().toString()));
                }else{
                    Toast.makeText(getActivity(),"ISBN错误",Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
}
