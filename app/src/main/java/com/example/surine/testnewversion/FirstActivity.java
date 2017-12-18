package com.example.surine.testnewversion;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FirstActivity extends AppCompatActivity {

    @BindView(R.id.button2)
    Button button2;
    @BindView(R.id.button5)
    Button button5;
    @BindView(R.id.button6)
    Button button6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_1);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.button2, R.id.button5, R.id.button6})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button2:
                //启动借书
                intent(BroActivity.class);
                break;
            case R.id.button5:
                //启动还书
                intent(BackActivty.class);
                break;
            case R.id.button6:
                //启动管理员
                intent(ManagerLoginAcivty.class);
                break;
        }
    }

    public void intent(Class<?> activity){
        startActivity(new Intent(FirstActivity.this,activity));
    }
}
