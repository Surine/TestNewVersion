package com.example.surine.testnewversion;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.surine.testnewversion.Utils.NfcUtil;
import com.example.surine.testnewversion.Utils.RfidUtil;
import com.ivrjack.ru01.IvrJackAdapter;
import com.ivrjack.ru01.IvrJackService;
import com.ivrjack.ru01.IvrJackStatus;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.surine.testnewversion.Utils.NfcUtil.byteArrayToHexString;

/**
 * Created by Surine on 2017/12/17.
 */

public class BroActivity extends AppCompatActivity implements IvrJackAdapter {
    @BindView(R.id.textView3)
    TextView textView3;
    @BindView(R.id.textView4)
    TextView textView4;
    @BindView(R.id.button10)
    Button button10;
    @BindView(R.id.button8)
    Button button8;
    @BindView(R.id.button12)
    Button button12;
    private String text;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mIntentFilter;
    private String[][] mTechList;
    private NfcAdapter mNfcAdapter;
    private IvrJackService service;
    private StringBuilder builder;
    private VolumnBroadcast volumeBroadcast;
    private String data;
    private String epc_s;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bro);
        ButterKnife.bind(this);

        button12.setEnabled(false);
        //初始化rfid
        service = RfidUtil.initRfid(this);
        //获取default nfc适配器
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            toast("手机不支持NFC");
        }
        if (!mNfcAdapter.isEnabled()) {
            toast("手机NFC功能没有打开");
            Intent setNfc = new Intent(Settings.ACTION_NFC_SETTINGS);
            startActivity(setNfc);
        } else {
            init();
        }
    }

    /**
     * 初始化
     */
    private void init() {
        // NFCActivity 一般设置为: SingleTop模式 ，并且锁死竖屏，以避免屏幕旋转Intent丢失
        Intent intent = new Intent(BroActivity.this, BroActivity.class);

        // 私有的请求码
        final int REQUEST_CODE = 1 << 16;

        final int FLAG = 0;
        mPendingIntent = PendingIntent.getActivity(BroActivity.this, REQUEST_CODE, intent, FLAG);

        // 三种过滤器
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        IntentFilter tech = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        IntentFilter tag = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        mIntentFilter = new IntentFilter[]{ndef, tech, tag};

        // 只针对ACTION_TECH_DISCOVERED
        mTechList = new String[][]{
                {IsoDep.class.getName()}, {NfcA.class.getName()}, {NfcB.class.getName()},
                {NfcV.class.getName()}, {NfcF.class.getName()}, {Ndef.class.getName()}};
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        NfcA a_card = NfcA.get(tagFromIntent);
        text = "";
        if (a_card == null) {
            String info = "读取卡信息失败\n";
            toast(info);
            return;
        }
        try {
            a_card.connect();
            String info = "读取卡信息成功\n";
            toast(info);
            //获取卡ID，
            text += "校园卡ID："+byteArrayToHexString(tagFromIntent.getId());
            button10.setText(text+"\n");
            button12.setEnabled(true);

        } catch (IOException e) {
            e.printStackTrace();
            text += "NFC卡IO异常";
            button10.setText(text+"\n");
        }
    }

    /**
     * 开启检测,检测到卡后，onNewIntent() 执行
     * enableForegroundDispatch()只能在onResume() 方法中，否则会报：
     * Foreground dispatch can only be enabled when your activity is resumed
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (mNfcAdapter == null) return;
        mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, mIntentFilter, mTechList);
    }

    /**
     * 关闭检测
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mNfcAdapter == null) return;
        mNfcAdapter.disableForegroundDispatch(this);
    }




    private void toast(String info) {
        Toast.makeText(BroActivity.this, info, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnect(String s) {
        button8.setText("读卡器已连接,请将书籍标签放在RFID识别区，\n点击按钮开始快速绑定");
    }

    @Override
    public void onDisconnect() {
        button8.setText("读卡器断开连接");
    }

    @Override
    public void onStatusChange(IvrJackStatus status) {
        switch (status) {
            case ijsDetecting:
                button8.setText("卡片检测中");
                break;
            case ijsRecognized:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        button8.setText("识别到卡片");
                    }
                });

                break;
            case ijsUnRecognized:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        button8.setText("未识别到卡片");
                    }
                });

                break;
            case ijsPlugout:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        button8.setText("读卡器掉线");
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
                button8.setText("EPC：" + builder.toString() + "\n");
                epc_s = builder.toString();
                ReadCard(builder.toString());
            }
        });

        //停止寻卡
        service.setReadEpcStatus((byte) 0);
    }

    private void ReadCard(String s) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(BroActivity.this);
        builder.setTitle("EPC:");
        builder.setMessage(s);
        builder.setPositiveButton("查询信息", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        data = RfidUtil.read_915(epc_s);
                        //TODO,带着data去服务器请求书籍信息
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                              AlertDialog.Builder builder1 = new AlertDialog.Builder(BroActivity.this);
                              builder1.setTitle("书籍信息");
                              builder1.setCancelable(false);
                              builder1.setMessage("ISBN"+data);
                              builder1.setNegativeButton("取消借阅", new DialogInterface.OnClickListener() {
                                  @Override
                                  public void onClick(DialogInterface dialog, int which) {
                                      finish();
                                  }
                              });
                              builder1.setPositiveButton("确定借阅", new DialogInterface.OnClickListener() {
                                  @Override
                                  public void onClick(DialogInterface dialog, int which) {
                                    Bro();
                                  }
                              });
                              builder1.show();
                            }
                        });
                    }
                }).start();
            }
        });
        builder.setCancelable(false);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
    }

    private void Bro() {
        //TODO 修改书籍状态，在卡片上和服务器
        //TODO 添加借阅记录
    }

    private void writeInfo(String s) {
        //写卡
        button8.setText("EPC读取成功，开始写入ISBN信息");
      //  String data = Isbn + "0";
       // button8.setText("写入:"+data);
        int bef = 0;
      //  bef = RfidUtil.write_915(s, data);
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




    @OnClick({R.id.button10, R.id.button8, R.id.button12})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button10:
                break;
            case R.id.button8:
                break;
            case R.id.button12:
                button8.setText("正在寻卡");
                new Thread(new Runnable() {
                    public int bat;

                    @Override
                    public void run() {

                        bat = service.setReadEpcStatus((byte) 1);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (bat != 0) {
                                    button8.setText("启动读卡失败,重新开始读卡" + bat);
                                }
                            }
                        });

                    }
                }).start();
                break;
        }
    }



    private class VolumnBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        }

    }
}
