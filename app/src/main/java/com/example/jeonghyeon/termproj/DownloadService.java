package com.example.jeonghyeon.termproj;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadService extends Service {
    String url = "http://roadofrhyme.esy.es/inst/";
    String fileName;
    String savePath;
    DownloadThread dThread;

    private NotificationManager notificationManager;
    private Notification.Builder notification;

    public DownloadService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 다운로드 경로를 외장메모리 사용자 지정 폴더로 함.
        String ext = Environment.getExternalStorageState();
        if (ext.equals(Environment.MEDIA_MOUNTED)) {
            savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/.gilrhyme_insts/";
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        fileName = intent.getStringExtra("FILE_NAME");

        File dir = new File(savePath);
        // 폴더가 존재하지 않을 경우 폴더를 만듦
        if (!dir.exists()) {
            dir.mkdir();
         }

        // 다운로드 폴더에 동일한 파일명이 존재하는지 확인
        if (new File(savePath + fileName).exists() == false) {
            dThread = new DownloadThread(url+fileName,savePath + fileName);
            dThread.start();
        } else {
            Log.d("TAG","파일 존재");

        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    class DownloadThread extends Thread {
        String ServerUrl;
        String LocalPath;

        DownloadThread(String serverPath, String localPath) {
            ServerUrl = serverPath;
            LocalPath = localPath;
            ServerUrl =  ServerUrl.replace(" ","%20");

        }

        @Override
        public void run() {
            URL imgurl;
            int Read;
            try {
                notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                notification = new Notification.Builder(getApplicationContext());
                notification.setSmallIcon(android.R.drawable.stat_sys_download);
                notification.setTicker("다운로드 시작");
                notification.setContentTitle("다운로드 중");
                notification.setContentText(fileName + "다운로드 중...");
                notification.setOngoing(true);
                notificationManager.notify(5000,notification.build());
                imgurl = new URL(ServerUrl);
                HttpURLConnection conn = (HttpURLConnection) imgurl.openConnection();
                int len = conn.getContentLength();
                Log.d("TAG","len =" + len);
                byte[] tmpByte = new byte[len];
                InputStream is = conn.getInputStream();
                File file = new File(LocalPath);
                FileOutputStream fos = new FileOutputStream(file);
                for (;;) {
                    Read = is.read(tmpByte);
                    if (Read <= 0) {
                        break;
                    }
                    fos.write(tmpByte, 0, Read);
                }
                is.close();
                fos.close();
                conn.disconnect();

            } catch (MalformedURLException e) {
                Log.e("ERROR1", e.getMessage());
            } catch (IOException e) {
                Log.e("ERROR2", e.getMessage());
                e.printStackTrace();
            }
            mAfterDown.sendEmptyMessage(0);
        }
    }

    Handler mAfterDown = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            Log.d("TAG","다운로드 완료");
            notificationManager.cancel(5000);

            notification = new Notification.Builder(getApplicationContext());
            notification.setSmallIcon(android.R.drawable.stat_sys_download_done);
            notification.setTicker("다운로드 완료");
            notification.setContentTitle("다운로드 완료");
            notification.setContentText(fileName + "다운로드 완료...");
            notification.setOngoing(false);
            notificationManager.notify(5000,notification.build());
            SongFrag.unlock();
            stopSelf();
        }
    };
}
