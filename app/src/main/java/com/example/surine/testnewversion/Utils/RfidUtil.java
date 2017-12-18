package com.example.surine.testnewversion.Utils;

import android.content.Context;
import android.util.Log;

import com.ivrjack.ru01.IvrJackAdapter;
import com.ivrjack.ru01.IvrJackService;

import java.io.UnsupportedEncodingException;

/**
 * Created by Surine on 2017/12/15.
 * rfid卡工具（使用Rfid小精灵读卡器SDK封装，用于识别915M卡片）
 */

public class RfidUtil {

    private static IvrJackService service;
    private static int ret;
    private static IvrJackService.TagBlock result;

    /**
     * 初始化rfid
     * @param context(上下文)
     * @return int
     * */
    public static IvrJackService initRfid(Context context){
        service = new IvrJackService(context, (IvrJackAdapter) context,1);
        return service;
    }



    /**
     * 停止扫描
     * */
    public static int stopScan(){
        return service.setReadEpcStatus((byte) 0);
    }


    /**
     * epc验证
     * @param pswd epc(密码和epc)
     * @return int (验证状态)
     * */
    public static int epc_check(int pswd,byte[]  epc){
        int ret = service.selectTag(pswd, epc);
        return ret;
    }

    /**
     * 读卡
     * @param
     * @return TagBlock
     * */
    public static String read_915(String epc){
        ret = service.selectTag(0,convertBytes(epc,0));
        if (ret == 0) {
            result = new IvrJackService.TagBlock();
            int ret = service.readTag(getBlock("USER"), convertByte("0"), (byte)18, result);            if (ret == 0) {
            } else {
               //读卡失败
            }
        } else {
            //卡片跑路了……
            Log.d("TAD", String.valueOf(ret));

        }

        if(ret == 0 && result.data != null){
            Log.d("CCC","成功"+result.data);
        }else{
            Log.d("CCC","失败");
        }

        StringBuilder builder = new StringBuilder();
        for (byte b : result.data) {
            builder.append(String.format("%02X ", b));
        }

       return builder.toString();
    }

    /**
     * 写卡
     * @param epc，数据
     * @return int(写卡状态)
     * */
    public static int write_915(String epc,String a){
        Log.d("TAD", epc+a);
       ret = service.selectTag(0,convertBytes(epc,0));
       if (ret == 0) {
            ret = service.writeTag(getBlock("USER"), convertByte("0"), (byte)9, convertBytes(a,36));
            if (ret == 0) {
                //写卡成功
                return 1;
            } else {
                //写卡失败
                return 2;
            }
        } else {
           //卡片跑路了……
           Log.d("TAD", String.valueOf(ret));
            return 3;
        }
    }

    /**
     * 转换字节
     * */
    public static byte[]  convertBytes(String str, int expectLen) {
        str = str.replace(" ", "");
        if (str.length() % 2 != 0) {
            throw new NumberFormatException( " error length!");
        }
//        if (expectLen > 0 && str.length() != expectLen) {
//            throw new NumberFormatException( " error length!");
//        }
        byte[] bytes = new byte[str.length() / 2];
        for (int i=0; i<str.length(); i+=2) {
            try {
                bytes[i/2] = (byte) Integer.parseInt(str.substring(i, i + 2), 16);
            } catch (NumberFormatException e) {
                throw new NumberFormatException(" error format!");
            }
        }
        return bytes;
    }


    /**
     * 转换密码
     * */
    public static int convertPassword(String str) {
        if (str.length() != 8) {
            throw new NumberFormatException(" must be 8 bytes!");
        }
        try {
            return Integer.parseInt(str, 16);
        } catch (NumberFormatException e) {
            throw new NumberFormatException(" error format!");
        }
    }

    /**
     * 转换比特
     * */
    public static byte convertByte(String str) {
        try {
            return (byte) Integer.parseInt(str, 16);
        } catch (NumberFormatException e) {
            throw new NumberFormatException(" error format!");
        }
    }

    /**
     * 获取块标识
     * */
    public static byte getBlock(String a) {
        if(a.equals("EPC")){
            return 0;
        }else if(a.equals("USER")){
            return 1;
        }else if(a.equals("RFU")){
            return 2;
        }else if(a.equals("TID")){
            return 3;
        }else {
            return 1;
        }
    }
}
