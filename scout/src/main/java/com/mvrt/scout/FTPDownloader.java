package com.mvrt.scout;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by Lee Mracek on 10/20/14.
 * Provides a runnable thread for FTP
 * <p/>
 * DEVELOPMENT CEASED DUE TO HTTP
 */
public class FTPDownloader implements Runnable {
    Activity context;

    @Override
    public void run() {
        FTPClient ftpClient = connectingWithFTP("mvrt.com", "mvrtscouting", "MVRT115@comp");
        if (ftpClient != null) {
            downloadFile(ftpClient,
                    "qualificationSchedule.json", new File(context.getFilesDir(), "qualificationSchedule.json"));
        }
    }

    public FTPClient connectingWithFTP(String ip, String userName, String pass) {
        boolean status;
        try {
            FTPClient mFtpClient = new FTPClient();
            mFtpClient.setConnectTimeout(10 * 1000);
            mFtpClient.connect(InetAddress.getByName(ip));
            status = mFtpClient.login(userName, pass);
            Log.d(Constants.Logging.FTP_LOGCAT.getPath(), "FTP connected: " + status);
            if (FTPReply.isPositiveCompletion(mFtpClient.getReplyCode())) {
                mFtpClient.enterLocalPassiveMode();
                Log.d(Constants.Logging.FTP_LOGCAT.getPath(), "" + mFtpClient.getReplyCode());
                return mFtpClient;
            } else {
                Log.e(Constants.Logging.FTP_LOGCAT.getPath(), "FTP could not connect" + mFtpClient.getReplyCode());
                mFtpClient.disconnect();
            }
        } catch (SocketException e) {
            Log.e(Constants.Logging.FTP_LOGCAT.getPath(), "SocketException", e);
        } catch (UnknownHostException e) {
            Toaster.burnToastUI(context, "Could not securely connect to FTP", Toast.LENGTH_LONG);
            Log.e(Constants.Logging.FTP_LOGCAT.getPath(), "UnknownHost", e);
        } catch (IOException e) {
            Log.e(Constants.Logging.FTP_LOGCAT.getPath(), "IOException", e);
        }
        return null;
    }

    public boolean downloadFile(FTPClient ftpClient, String remotePath, File localPath) {
        OutputStream outputStream = null;
        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(localPath));
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            return ftpClient.retrieveFile(remotePath, outputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
}
