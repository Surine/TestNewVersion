package com.example.surine.testnewversion;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.surine.testnewversion.Bean.SimpleEvent;
import com.example.surine.testnewversion.Utils.RfidUtil;
import com.ivrjack.ru01.IvrJackAdapter;
import com.ivrjack.ru01.IvrJackService;
import com.ivrjack.ru01.IvrJackStatus;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by Surine on 2017/12/17.
 */

public class AddNewBookActivity extends AppCompatActivity implements IvrJackAdapter {
    @BindView(R.id.textView3)
    TextView textView3;
    @BindView(R.id.imageButton)
    Button imageButton;
    @BindView(R.id.textView6)
    TextView textView6;
    @BindView(R.id.button11)
    Button button11;
    private StringBuilder builder;
    private IvrJackService service;
    private int bat;
    private ProgressDialog viewVolume;
    private VolumnBroadcast volumeBroadcast;
    private String Isbn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_card);
        ButterKnife.bind(this);
        //音量监听器（没啥卵用）
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        volumeBroadcast = new VolumnBroadcast();
        registerReceiver(volumeBroadcast, new IntentFilter("android.media.VOLUME_CHANGED_ACTION"));
        Isbn = getIntent().getStringExtra("ISBN");
        button11.setEnabled(false);

        //初始化rfid
        service = RfidUtil.initRfid(this);
    }


    @Override
    public void onConnect(String s) {
        textView6.setText("读卡器已连接,请将卡片放在RFID识别区，\n点击按钮开始快速绑定");
    }

    @Override
    public void onDisconnect() {
        textView6.setText("读卡器断开连接");
    }

    @Override
    public void onStatusChange(IvrJackStatus status) {
        switch (status) {
            case ijsDetecting:
                textView6.setText("卡片检测中");
                break;
            case ijsRecognized:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView6.setText("识别到卡片");
                    }
                });

                break;
            case ijsUnRecognized:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView6.setText("未识别到卡片");
                    }
                });

                break;
            case ijsPlugout:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView6.setText("读卡器掉线");
                    }
                });

                break;
        }
    }

    @Override
    public void onInventory(final byte[] epc) {
        builder = new StringBuilder();
        for (int i = 0; i < epc.length; i++) {
            builder.append(String.format("%02X", epc[i]));
            if ((i + 1) % 4 == 0) builder.append(" ");
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView3.setText("EPC：" + builder.toString() + "\n");
                button11.setEnabled(true);
            }
        });

        //停止寻卡
        service.setReadEpcStatus((byte) 0);
    }

    private void writeInfo(String s) {
        //写卡
        textView6.setText("EPC读取成功，开始写入ISBN信息");
        String data = Isbn + "0";
        textView6.setText("写入:"+data);
        int bef = 0;
        bef = RfidUtil.write_915(s, data);
        if (bef == 1) {
            textView3.setText("写卡成功,即将退出……");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                        finish();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else if (bef == 2) {
            textView3.setText("写卡失败");
        } else if (bef == 3) {
            textView3.setText("卡片不在识别区");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        service.open();
    }

    @Override
    protected void onStop() {
        super.onStop();
        service.close();
    }



    @OnClick({R.id.textView3, R.id.imageButton, R.id.textView6})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.textView3:
                break;
            case R.id.imageButton:

                textView6.setText("正在寻卡");
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        bat = service.setReadEpcStatus((byte) 1);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (bat != 0) {
                                    textView6.setText("启动读卡失败,重新开始读卡" + bat);
                                }
                            }
                        });

                    }
                }).start();

                break;
            case R.id.textView6:
                break;
        }
    }

    @OnClick(R.id.button11)
    public void onViewClicked() {
        //写卡
        writeInfo(builder.toString());
    }


    private class VolumnBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        }

    }

}
