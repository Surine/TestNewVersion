package com.example.surine.testnewversion;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.example.surine.testnewversion.Utils.RfidUtil;
import com.ivrjack.ru01.IvrJackAdapter;
import com.ivrjack.ru01.IvrJackService;
import com.ivrjack.ru01.IvrJackStatus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Surine on 2017/12/17.
 */

public class BackActivty extends AppCompatActivity implements IvrJackAdapter {
    @BindView(R.id.textView4)
    TextView textView4;
    @BindView(R.id.button_back)
    Button buttonBack;
    @BindView(R.id.button13)
    Button button13;
    private IvrJackService service;
    private StringBuilder builder;
    private String epc_s;
    private String data;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_back);
        ButterKnife.bind(this);
        //初始化rfid
        service = RfidUtil.initRfid(this);
    }

    @OnClick(R.id.button_back)
    public void onViewClicked() {
        button13.setText("正在寻卡");
        new Thread(new Runnable() {
            public int bat;

            @Override
            public void run() {

                bat = service.setReadEpcStatus((byte) 1);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (bat != 0) {
                            button13.setText("启动读卡失败,重新开始读卡" + bat);
                        }
                    }
                });

            }
        }).start();

    }

    @Override
    public void onConnect(String s) {
        button13.setText("读卡器已连接,请将书籍标签放在RFID识别区，\n点击按钮开始识别");
    }

    @Override
    public void onDisconnect() {
        button13.setText("读卡器断开连接");
    }

    @Override
    public void onStatusChange(IvrJackStatus status) {
        switch (status) {
            case ijsDetecting:
                button13.setText("卡片检测中");
                break;
            case ijsRecognized:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        button13.setText("识别到卡片");
                    }
                });

                break;
            case ijsUnRecognized:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        button13.setText("未识别到卡片");
                    }
                });

                break;
            case ijsPlugout:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        button13.setText("读卡器掉线");
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
                button13.setText("EPC：" + builder.toString() + "\n");
                epc_s = builder.toString();
                ReadCard(builder.toString());
            }
        });

        //停止寻卡
        service.setReadEpcStatus((byte) 0);
    }

    private void ReadCard(String s) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(BackActivty.this);
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
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(BackActivty.this);
                                builder1.setTitle("书籍信息");
                                builder1.setCancelable(false);
                                builder1.setMessage("ISBN:" + data);
                                builder1.setNegativeButton("取消还书", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                });
                                builder1.setPositiveButton("确定还书", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Back();
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

    private void Back() {
        //TODO 修改书籍状态，在卡片上和服务器
        //TODO 删除借阅记录
    }

    private void writeInfo(String s) {
        //写卡
        button13.setText("EPC读取成功，开始写入ISBN信息");
        //  String data = Isbn + "0";
        // button8.setText("写入:"+data);
        int bef = 0;
        //  bef = RfidUtil.write_915(s, data);
        if (bef == 1) {
            button13.setText("写卡成功,即将退出……");
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
            button13.setText("写卡失败");
        } else if (bef == 3) {
            button13.setText("卡片不在识别区");
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

    private class VolumnBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        }

    }

}
