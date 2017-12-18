package com.example.surine.testnewversion;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import android.widget.FrameLayout;

import com.example.surine.testnewversion.Bean.SimpleEvent;

import com.example.surine.testnewversion.Fragment.ScanISBN;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Surine on 2017/12/17.
 */

public class ManagerAcivty extends AppCompatActivity {
    private String Isbn;
    @BindView(R.id.frame)
    FrameLayout frame;
    private StringBuilder builder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_fragment);
        ButterKnife.bind(this);
        //eventbus
        EventBus.getDefault().register(this);
        replaceFragment(new ScanISBN());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    //fragment manager
    private void replaceFragment(Fragment fragment) {
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction tran = fm.beginTransaction();
        tran.replace(R.id.frame, fragment);
        tran.commit();
    }

    @Subscribe
    public void GetMessage(SimpleEvent event) {
        if(event.getId() == 1){
            //取得isbn
            Isbn =  event.getMessage();
            startActivity(new Intent(ManagerAcivty.this,AddNewBookActivity.class).putExtra("ISBN",Isbn));
        }
    }

}
