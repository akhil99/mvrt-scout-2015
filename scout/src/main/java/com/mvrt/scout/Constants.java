package com.mvrt.scout;

/**
 * Created by Lee Mracek on 10/16/2014.
 * Holds constants
 */
public class Constants {
    public enum Logging {
        MAIN_LOGCAT("com.mvrt.scout"), FTP_LOGCAT("com.mvrt.scout.FTP"), TOAST_LOGCAT("com.mvrt.scout.Toaster"), HTTP_LOGCAT("com.mvrt.scout.HTTP"), VIEWPAGER_LOGCAT("com.mvrt.scout.Pager");
        private String path;

        private Logging(String path) {
            this.path = path;
        }

        public String getPath() {
            return path;
        }
    }
}
