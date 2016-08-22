package com.renyuzhuo.chat.util;

import android.os.Environment;
import android.util.Log;

import com.renyuzhuo.chat.ChatApplication;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.File;

import de.mindpipe.android.logging.log4j.LogConfigurator;

public class LogUtil {

    private static Logger log;

    public LogUtil(Class clazz) {
        LogConfigurator logConfigurator = new LogConfigurator();
        logConfigurator.setFileName(Environment.getExternalStorageDirectory()
                + File.separator + "Chat" + File.separator + "logs"
                + File.separator + "log.txt");
        logConfigurator.setRootLevel(Level.DEBUG);
        logConfigurator.setLevel("org.apache", Level.ERROR);
        logConfigurator.setFilePattern("%d %-5p [%c{2}]-[%L] %m%n");
        logConfigurator.setMaxFileSize(1024 * 1024 * 5);
        logConfigurator.setImmediateFlush(true);
        logConfigurator.configure();
        log = Logger.getLogger(clazz);
        log.info("My Application Created");
    }

    private static void logWrite(String s) {
        log.info(s);
    }

    public static void log(String tag, String message) {
        Log.i(tag, message);
        logWrite(tag + ": [" + message + "]");
    }

    public static void log(String message) {
        log("info", message);
    }

    public static void elog(String tag, String message) {
        Log.e(tag, message);
        logWrite(tag + ": [" + message + "]");
    }

    public static void elog(String message) {
        elog("ERR", message);
    }

    public static void exception(Class<CrashUtil> clazz, String exceptionMessage) {
        if (exceptionMessage != null && exceptionMessage.length() != 0) {
            LogConfigurator logConfigurator = new LogConfigurator();
            logConfigurator.setFileName(Environment.getExternalStorageDirectory()
                    + File.separator + "Chat" + File.separator + "logs"
                    + File.separator + "errorlog.txt");
            logConfigurator.setRootLevel(Level.DEBUG);
            logConfigurator.setLevel("org.apache", Level.ERROR);
            logConfigurator.setFilePattern("%d %-5p [%c{2}]-[%L] %m%n");
            logConfigurator.setMaxFileSize(1024 * 1024 * 5);
            logConfigurator.setImmediateFlush(true);
            logConfigurator.configure();
            Logger errlog = Logger.getLogger(clazz);
            errlog.info("username: " + ChatApplication.getNickname() + ", userid: " + ChatApplication.getUserId());
            errlog.info(exceptionMessage);
        }
    }

}
