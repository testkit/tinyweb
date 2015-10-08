/*
 * Copyright (C) 2013-2014 Intel Corporation, All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.intel.tinywebtestservice;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.example.tinywebtestservice.util.SystemUiHider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;

import com.intel.tinywebtestservice.R;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class FullscreenActivity extends Activity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;
    AssetManager assetManager;
    Handler mHideHandler = new Handler();
    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };
    private TextView tv;
    private String target_path;
    private String full_log = "";
    private Process p;

    //create a Handler for updating UI

    private Handler handler  = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String log = bundle.getString("LOG");
            tv.setText(log);
        }
    };

    private void printLog(String newlog) {
        full_log += ">" + newlog + "\n";
        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putString("LOG", full_log);
        message.setData(bundle);
        handler.sendMessage(message);
    }

        private void runService() {

        try {
            Thread t = new Thread() {
                public void run() {
                    try {
                        p = Runtime.getRuntime().exec(
                                target_path + "system/service.sh -p "
                                        + Build.CPU_ABI, null, null);
                        String line = "", res = "";

                        InputStream input = p.getInputStream();

                        BufferedReader osRes = new BufferedReader(new InputStreamReader(
                                input, "utf-8"));
                        while ((line = osRes.readLine()) != null)
                            res += line + "\n";
                        osRes.close();
                        input.close();

                        input = p.getErrorStream();
                        osRes = new BufferedReader(new InputStreamReader(input, "utf-8"));
                        while ((line = osRes.readLine()) != null)
                            res += line + "\n";

                        osRes.close();
                        input.close();
                        printLog(res);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            };
            t.start();
            t.wait(2000);
        /*	String line = "", res = "";

			InputStream input = p.getInputStream();

			BufferedReader osRes = new BufferedReader(new InputStreamReader(
					input, "utf-8"));
			while ((line = osRes.readLine()) != null)
				res += line + "\n";
			osRes.close();
			input.close();

			input = p.getErrorStream();
			osRes = new BufferedReader(new InputStreamReader(input, "utf-8"));
			while ((line = osRes.readLine()) != null)
				res += line + "\n";
			osRes.close();
			input.close();
			printLog(res); */

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        printLog("Service started");
    }

    private void killService() {
        try {
            Runtime.getRuntime().exec(target_path + "/system/service.sh -k");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        printLog("Service stopped");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        final View controlsView = findViewById(R.id.fullscreen_content_controls);
        final View contentView = findViewById(R.id.fullscreen_content);

        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.
        mSystemUiHider = SystemUiHider.getInstance(this, contentView,
                HIDER_FLAGS);
        mSystemUiHider.setup();
        mSystemUiHider
                .setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
                    // Cached values.
                    int mControlsHeight;
                    int mShortAnimTime;

                    @Override
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
                    public void onVisibilityChange(boolean visible) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                            // If the ViewPropertyAnimator API is available
                            // (Honeycomb MR2 and later), use it to animate the
                            // in-layout UI controls at the bottom of the
                            // screen.
                            if (mControlsHeight == 0) {
                                mControlsHeight = controlsView.getHeight();
                            }
                            if (mShortAnimTime == 0) {
                                mShortAnimTime = getResources().getInteger(
                                        android.R.integer.config_shortAnimTime);
                            }
                            controlsView
                                    .animate()
                                    .translationY(visible ? 0 : mControlsHeight)
                                    .setDuration(mShortAnimTime);
                        } else {
                            // If the ViewPropertyAnimator APIs aren't
                            // available, simply show or hide the in-layout UI
                            // controls.
                            controlsView.setVisibility(visible ? View.VISIBLE
                                    : View.GONE);
                        }

                        if (visible && AUTO_HIDE) {
                            // Schedule a hide().
                            delayedHide(AUTO_HIDE_DELAY_MILLIS);
                        }
                    }
                });

        // Set up the user interaction to manually show or hide the system UI.
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TOGGLE_ON_CLICK) {
                    mSystemUiHider.toggle();
                } else {
                    mSystemUiHider.show();
                }
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.refresh_button).setOnTouchListener(
                mDelayHideTouchListener);
        tv = (TextView) contentView;
        target_path = getFilesDir().getPath() + "/";
        assetManager = this.getAssets();

        try {
            File dir = new File(target_path + "system");
            if (dir.isDirectory())
                killService();
            copyDir("system");
            chmode(target_path + "system/service.sh", 511);
            chmode(target_path + "system/libs/" + Build.CPU_ABI + "/busybox", 511);

            dir = new File(target_path + "docroot");
            if (!dir.isDirectory()) {
                unzipDocroot();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        runService();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    private void copyDir(String path) {
        String assets[] = null;
        try {
            Log.i("tag", "copyFileOrDir() " + path);
            assets = assetManager.list(path);
            if (assets.length == 0) {
                copyFile(path);
            } else {
                String fullPath = target_path + path;
                Log.i("tag", "path=" + fullPath);
                File dir = new File(fullPath);
                if (!dir.exists() && !path.startsWith("images")
                        && !path.startsWith("sounds")
                        && !path.startsWith("webkit"))
                    if (!dir.mkdirs())
                        ;
                Log.i("tag", "could not create dir " + fullPath);
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
            Log.e("tag", "I/O Exception", ex);
        }
    }

    private void copyFile(String filename) {
        InputStream in = null;
        OutputStream out = null;
        String newFileName = null;
        try {
            Log.i("tag", "copyFile() " + filename);
            in = assetManager.open(filename);
            if (filename.endsWith(".jpg")) // extension was added to avoid
                // compression on APK file
                newFileName = target_path
                        + filename.substring(0, filename.length() - 4);
            else
                newFileName = target_path + filename;
            out = new FileOutputStream(newFileName);

            byte[] buffer = new byte[8192];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;

        } catch (Exception e) {
            Log.e("tag", "Exception in copyFile() of " + newFileName);
            Log.e("tag", "Exception in copyFile() " + e.toString());
        }

    }

    private int chmode(String filename, int mode) throws Exception {
        Class fileUtils = Class.forName("android.os.FileUtils");
        Method setPermissions = fileUtils.getMethod("setPermissions",
                String.class, int.class, int.class, int.class);
        return (Integer) setPermissions.invoke(null, filename, mode, -1, -1);
    }

    private void unzipDocroot() {
        try {
            //		chmode(target_path + "system/libs/" + Build.CPU_ABI + "/unzip", 511);

            String cmd = target_path
                    + "system/libs/" + Build.CPU_ABI + "/busybox unzip /sdcard/docroot.zip -o -d "
                    + target_path;
            Log.i("unzip", "unzip docroot begin(cmd=" + cmd);
            Process unzip = Runtime.getRuntime().exec(cmd);
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(unzip.getInputStream()));
            String output;
            while ((output = bufferedReader.readLine()) != null) {
                Log.i("unzip", "unzip output = " + output);
            }
            unzip.waitFor();
            Log.i("unzip", "unzip docroot end");
        } catch (Exception e) {
            Log.e("unzip", "error: " + e.getCause());
            e.printStackTrace();
        }
    }

    /**
     * Called when the user clicks the runService button
     */
    public void refreshData(View view) {
        // Do something in response to button
        killService();
        printLog("Refresh docroot...");
        unzipDocroot();
        runService();
    }

    public void stopService(View view) {
        printLog("Stop service...");
        killService();
    }

    public void startService(View view) {
        printLog("Start service...");
        runService();
    }
}
