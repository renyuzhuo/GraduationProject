package com.renyuzhuo.chat.util;

import android.content.Context;

import com.renyuzhuo.chat.ChatApplication;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;

public class CrashUtil implements UncaughtExceptionHandler {

    private final Context context;
    private UncaughtExceptionHandler mDefaultHandler;
    String info = null;
    ByteArrayOutputStream baos = null;
    PrintStream printStream = null;

    public CrashUtil(Context context) {
        this.context = context;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(mDefaultHandler);
        LogUtil.elog("崩溃错误:" + ex.toString());
        ChatApplication.saveERR(true);
        try {
            baos = new ByteArrayOutputStream();
            printStream = new PrintStream(baos);
            ex.printStackTrace(printStream);
            byte[] data = baos.toByteArray();
            info = new String(data);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (printStream != null) {
                    printStream.close();
                }
                if (baos != null) {
                    baos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        LogUtil.elog("崩溃错误:" + info);
        LogUtil.exception(CrashUtil.class, info);
        if (Global.ISDEBUG) {

        } else {
            System.exit(0);
        }
    }
}
