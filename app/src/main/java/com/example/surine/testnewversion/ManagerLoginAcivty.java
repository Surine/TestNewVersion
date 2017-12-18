package com.example.surine.testnewversion;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Surine on 2017/12/17.
 */

public class ManagerLoginAcivty extends AppCompatActivity {
    @BindView(R.id.editText)
    EditText editText;
    @BindView(R.id.textView2)
    TextView textView2;
    @BindView(R.id.button7)
    Button button7;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.button7})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button7:
                if(editText.getText().toString().equals("123456")){
                    startActivity(new Intent(ManagerLoginAcivty.this,ManagerAcivty.class));
                }
                break;
        }
    }
}
