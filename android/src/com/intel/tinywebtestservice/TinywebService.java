package com.intel.tinywebtestservice;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;

public class TinywebService extends Service {

    private static final String LOG_TAG = "TinywebService";

    private Process binary_process = null;

    private AssetManager assetManager;

    private String file_path;

    private StringBuffer logString;

    public TinywebService() {
    }

    private void printLog(String newlog) {
        String str = ">" + newlog;
        File logFile = new File(Constants.LOG.SERVICE_LOG_FILE);
        if (!logFile.exists())
        {
            try
            {
                logFile.createNewFile();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        try
        {
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(str);
            buf.newLine();
            buf.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        // Used only in case of bound services.
        return null;
    }

    private int chmode(String filename, int mode) throws Exception {
        Class fileUtils = Class.forName("android.os.FileUtils");
        Method setPermissions = fileUtils.getMethod("setPermissions",
                String.class, int.class, int.class, int.class);
        return (Integer) setPermissions.invoke(null, filename, mode, -1, -1);
    }

    private void copyFile(String filename) {
        InputStream in = null;
        OutputStream out = null;
        String newFileName = null;
        try {
            Log.i(LOG_TAG, "copyFile() " + filename);
            in = assetManager.open(filename);
            if (filename.endsWith(".jpg")) // extension was added to avoid
                // compression on APK file
                newFileName = file_path
                        + filename.substring(0, filename.length() - 4);
            else
                newFileName = file_path + filename;
            out = new FileOutputStream(newFileName);

            byte[] buffer = new byte[8192];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            out.flush();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Exception in copyFile() of " + newFileName);
            Log.e(LOG_TAG, "Exception in copyFile() " + e.toString());
        }finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            in = null;
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            out = null;
        }

    }

    private void copyDir(String path) {
        String assets[] = null;
        try {
            Log.i(LOG_TAG, "copyFileOrDir() " + path);
            assets = assetManager.list(path);
            if (assets.length == 0) {
                copyFile(path);
            } else {
                String fullPath = file_path + path;
                Log.i(LOG_TAG, "path=" + fullPath);
                File dir = new File(fullPath);
                if (!dir.exists() && !path.startsWith("images")
                        && !path.startsWith("sounds")
                        && !path.startsWith("webkit"))
                    if (!dir.mkdirs())
                        ;
                Log.i(LOG_TAG, "could not create dir " + fullPath);
                for (int i = 0; i < assets.length; ++i) {
                    String p;
                    if (path.equals(""))
                        p = "";
                    else
                        p = path + "/";

                    if (!path.startsWith("images")
                            && !path.startsWith("sounds")
                            && !path.startsWith("webkit"))
                        copyDir(p + assets[i]);
                }
            }
        } catch (IOException ex) {
            Log.e(LOG_TAG, "I/O Exception", ex);
        }
    }

    private void unzipDocroot() {
        try {
            //		chmode(target_path + "system/libs/" + Build.CPU_ABI + "/unzip", 511);

            String cmd = file_path
                    + "system/libs/" + Build.CPU_ABI + "/busybox unzip /sdcard/docroot.zip -o -d "
                    + file_path;
            Log.i(LOG_TAG, "unzip docroot begin(cmd=" + cmd);
            Process unzip = Runtime.getRuntime().exec(cmd);
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(unzip.getInputStream()));
            String output;
            while ((output = bufferedReader.readLine()) != null) {
                Log.i(LOG_TAG, "unzip output = " + output);
            }
            unzip.waitFor();
            Log.i(LOG_TAG, "unzip docroot end");
        } catch (Exception e) {
            Log.e(LOG_TAG, "error: " + e.getCause());
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.file_path = getFilesDir().getPath() + "/";
        this.assetManager = this.getAssets();
        this.logString = new StringBuffer();
        try {
            File dir = new File(file_path + "system");
            if (dir.isDirectory())
                stopTinywebServer();
            copyDir("system");
            chmode(file_path + "system/service.sh", 511);
            chmode(file_path + "system/libs/" + Build.CPU_ABI + "/busybox", 511);

            dir = new File(file_path + "docroot");
            if (!dir.isDirectory()) {
                unzipDocroot();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "In onDestroy");
        if(null != binary_process){
            this.stopTinywebServer();
        }
    }

    private void startTinywebServer(){
        try {
            Thread t = new Thread() {
                private final static String THREADLOGTAG = "BinaryThread";
                public void run() {
                    try {
                        Log.i(THREADLOGTAG, "beginning");
                        binary_process = Runtime.getRuntime().exec(
                                file_path + "system/service.sh -p "
                                        + Build.CPU_ABI + " > " + Constants.LOG.SERVICE_LOG_FILE, null, null);
                        String line = "", res = "";

                        InputStream input = binary_process.getInputStream();

                        BufferedReader osRes = new BufferedReader(new InputStreamReader(
                                input, "utf-8"));
                        while ((line = osRes.readLine()) != null)
                            res += line + "\n";
                        osRes.close();
                        input.close();

                        if(null != binary_process) {
                            input = binary_process.getErrorStream();
                            osRes = new BufferedReader(new InputStreamReader(input, "utf-8"));
                            while ((line = osRes.readLine()) != null)
                                res += line + "\n";
                            printLog(res);
                            osRes.close();
                            input.close();
                        }
                        Log.e(THREADLOGTAG, res);
                        Log.i(THREADLOGTAG, "end");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            t.start();
            synchronized(t){
                t.wait(2000);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        printLog("Service started");

    }

    private void stopTinywebServer(){
        try {
            Runtime.getRuntime().exec(file_path + "/system/service.sh -k");
            this.binary_process = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        printLog("Service stopped");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {
            Log.i(LOG_TAG, "Received Start TestkitStubService Intent ");
            if(null == this.binary_process) {
                startTinywebServer();
            }
            Intent notificationIntent = new Intent(this, FullscreenActivity.class);
            notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                    notificationIntent, 0);

            Bitmap icon = BitmapFactory.decodeResource(getResources(),
                    R.drawable.ic_launcher);

            Notification notification = new NotificationCompat.Builder(this)
                    .setContentTitle("TinyWeb Service is running")
                    .setTicker("TinyWeb Service")
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setLargeIcon(
                            Bitmap.createScaledBitmap(icon, 128, 128, false))
                    .setContentIntent(pendingIntent)
                    .setOngoing(true)
                    .build();
            startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                    notification);
        } else if (intent.getAction().equals(Constants.ACTION.REFRESHFOREGROUND_ACTION)) {
            Log.i(LOG_TAG, "Received Refresh Foreground Intent");
            stopTinywebServer();
            unzipDocroot();
            startTinywebServer();
        } else if (intent.getAction().equals(
                Constants.ACTION.STOPFOREGROUND_ACTION)) {
            Log.i(LOG_TAG, "Received Stop Foreground Intent");
            stopTinywebServer();
            stopForeground(true);
            stopSelf();
        }
        return START_STICKY;
    }


}
