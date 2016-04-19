package com.intel.tinywebtestservice;

public class Constants {
    public interface ACTION {
        public static String MAIN_ACTION = "com.intel.testkit.action.main";
        public static String STARTFOREGROUND_ACTION = "com.intel.testkit.action.startforeground";
        public static String STOPFOREGROUND_ACTION = "com.intel.testkit.action.stopforeground";
        public static String REFRESHFOREGROUND_ACTION = "com.intel.testkit.action.refreshforeground";
    }

    public interface LOG {
        public static String SERVICE_LOG_FILE = "/sdcard/servicelog.log";
    }

    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 101;
    }
}
