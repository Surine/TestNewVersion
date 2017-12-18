package com.example.surine.testnewversion;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.surine.testnewversion.Utils.RfidUtil;
import com.ivrjack.ru01.IvrJackAdapter;
import com.ivrjack.ru01.IvrJackService;
import com.ivrjack.ru01.IvrJackStatus;

import static com.example.surine.testnewversion.Utils.RfidUtil.convertByte;
import static com.example.surine.testnewversion.Utils.RfidUtil.convertBytes;
import static com.example.surine.testnewversion.Utils.RfidUtil.convertPassword;
import static com.example.surine.testnewversion.Utils.RfidUtil.getBlock;

public class ScanActivity extends AppCompatActivity implements IvrJackAdapter {

    private IvrJackService service;
    private Button button;
    private ProgressBar viewVolume;
    private BroadcastReceiver volumeBroadcast;
    private StringBuilder builder;
    private int bat;
    private EditText acc;
    private EditText addr;
    private EditText block;
    private EditText length;
    private EditText data_ed;
    private Button read;
    private Button write;
    private TextView info;
    private TextView epc_t;
    private byte length_s;
    private byte block_s;
    private byte[] epc_s;
    private int accpwd_s;
    private byte address_s;
    private int ret;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        button = findViewById(R.id.button);
        viewVolume = findViewById(R.id.viewVolume);
        acc = findViewById(R.id.accpwd);
        addr = findViewById(R.id.startaddr);
        block = findViewById(R.id.memoryblock);
        length = findViewById(R.id.length);
        read = findViewById(R.id.read);
        write = findViewById(R.id.write);
        info = findViewById(R.id.info);
        epc_t = findViewById(R.id.epc);
        data_ed = findViewById(R.id.data);
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        viewVolume.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        viewVolume.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        volumeBroadcast = new VolumnBroadcast();
        registerReceiver(volumeBroadcast, new IntentFilter("android.media.VOLUME_CHANGED_ACTION"));

        //初始化rfid
        service = RfidUtil.initRfid(this);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(button.getText().toString().contains("开始")){
                           button.setText("正在寻卡……");
                            new Thread(new Runnable() {
                               @Override
                               public void run() {

                                   bat = service.setReadEpcStatus((byte) 1);
                                   runOnUiThread(new Runnable() {
                                       @Override
                                       public void run() {
                                           if(bat != 0){
                                               button.setText("启动读卡失败,重新开始读卡"+bat);
                                           }
                                       }
                                   });

                               }
                           }).start();
                        }
                    }
                });


          read.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  try {

                      //获取标签epc
                      epc_s = convertBytes( epc_t.getText().toString(), 0);
                      //密码
                      accpwd_s = convertPassword( acc.getText().toString());
                      //起始地址
                      address_s = convertByte( addr.getText().toString());
                      //块
                      block_s = getBlock(block.getText().toString()) ;
                      //数据长度
                      //初始化
                      info.setText("信息读取中...");
                      data_ed.setText("");

                      new Thread(new Runnable() {
                          @Override
                          public void run() {
                              Log.d("CCC",accpwd_s+"");
                              Log.d("CCC",epc_s+"");
                              ret = service.selectTag(accpwd_s, epc_s);
                              if (ret == 0) {
                                  final IvrJackService.TagBlock result = new IvrJackService.TagBlock();
                                  ret = service.readTag(block_s, address_s, length_s, result);
                                  if (ret == 0) {
                                      runOnUiThread(new Runnable() {
                                          @Override
                                          public void run() {
                                              info.setText("读卡成功");
                                              data_ed.setText(new String(result.data));
                                          }
                                      });
                                  } else {
                                      runOnUiThread(new Runnable() {
                                          @Override
                                          public void run() {
                                              info.setText("读卡失败"+ret);
                                          }
                                      });
                                  }
                              } else {
                                  runOnUiThread(new Runnable() {
                                      @Override
                                      public void run() {
                                          info.setText("选择失败"+ret);
                                      }
                                  });
                              }
                          }
                      }).start();
                  } catch (NumberFormatException e) {
                      Toast.makeText(ScanActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                  }
              }
          });


          write.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  try {

                      //获取标签epc
                      epc_s = convertBytes( epc_t.getText().toString(), 0);
                      //密码
                      accpwd_s = convertPassword( acc.getText().toString());
                      //起始地址
                      address_s = convertByte( addr.getText().toString());
                      //块
                      block_s = getBlock(block.getText().toString()) ;
                      //数据长度
                      length_s = convertByte(data_ed.getText().toString());
                      //初始化
                      info.setText("准备写入...");

                      new Thread(new Runnable() {
                          @Override
                          public void run() {
                              ret = service.selectTag(accpwd_s, epc_s);
                              if (ret == 0) {
                                  String a = data_ed.getText().toString();
                                  final IvrJackService.TagBlock result = new IvrJackService.TagBlock();
                                  ret = service.writeTag(block_s, address_s, length_s, convertBytes( a, length_s * 4));
                                  if (ret == 0) {
                                      runOnUiThread(new Runnable() {
                                          @Override
                                          public void run() {
                                              info.setText("写卡成功");
                                          }
                                      });
                                  } else {
                                      runOnUiThread(new Runnable() {
                                          @Override
                                          public void run() {
                                              info.setText("写卡失败"+ret);
                                          }
                                      });
                                  }
                              } else {
                                  runOnUiThread(new Runnable() {
                                      @Override
                                      public void run() {
                                          info.setText("选择失败"+ret);
                                      }
                                  });
                              }
                          }
                      }).start();
                  } catch (NumberFormatException e) {
                      Toast.makeText(ScanActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                  }
              }
          });
    }









    @Override
    public void onConnect(String s) {
       button.setText("读卡器已连接,点击开始读卡");
    }

    @Override
    public void onDisconnect() {
        button.setText("读卡器断开连接");
    }

    @Override
    public void onStatusChange(IvrJackStatus status) {
        switch (status) {
            case ijsDetecting:
                button.setText("卡片检测中");
                AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                viewVolume.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
                viewVolume.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
                if (viewVolume.getMax() != viewVolume.getProgress()) {
                    new AlertDialog.Builder(this)
                            .setTitle("请设置手机音量到最大")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                }
                break;
            case ijsRecognized:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        button.setText("识别到卡片");
                    }
                });

                break;
            case ijsUnRecognized:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        button.setText("未识别到卡片");
                    }
                });

                break;
            case ijsPlugout:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        button.setText("读卡器掉线");
                    }
                });

                break;
        }
    }

    @Override
    public void onInventory(final byte[] epc) {
       builder = new StringBuilder();
        for (int i=0; i<epc.length; i++) {
            builder.append(String.format("%02X", epc[i]));
            if ((i+1)%4==0) builder.append(" ");
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                button.setText("卡片标识:"+builder.toString()+"\n"+"点击重新开始读卡");
                epc_t.setText(builder.toString());
            }
        });

        //停止寻卡
        service.setReadEpcStatus((byte) 0);
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
            viewVolume.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            viewVolume.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        }

    }


}
