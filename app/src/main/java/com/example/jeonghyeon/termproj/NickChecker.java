package com.example.jeonghyeon.termproj;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by JeongHyeon on 2017-06-04.
 * 중복된 닉네임인지 체크
 */

public class NickChecker {

    ImageView imView;
    Bitmap bmImg;
    String inputNick;
    phpDown task;
    boolean canUse; // 사용가능한 닉네임인가
    boolean gotData = false;//서버와의 통신 완료 확인

    public void load(String str){
        gotData = false;
        inputNick = str;
        task = new phpDown();
        task.execute("http://roadofrhyme.esy.es/myphp/nickcheck.php"); //닉네임 중복 체크 위한 php
    }

    public boolean getCanUse(){
        return canUse;
    }


    public boolean Flag(){
        return gotData;
    }

    private class back extends AsyncTask<String, Integer, Bitmap> {


        @Override
        protected Bitmap doInBackground(String... urls) {
            // TODO Auto-generated method stub
            try {
                URL myFileUrl = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
                conn.setDoInput(true);
                conn.connect();
                InputStream is = conn.getInputStream();

                bmImg = BitmapFactory.decodeStream(is);


            } catch (IOException e) {
                e.printStackTrace();
            }
            return bmImg;
        }

        protected void onPostExecute(Bitmap img) {
            imView.setImageBitmap(bmImg);
        }

    }

    private class phpDown extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... urls) {
            StringBuilder jsonHtml = new StringBuilder();

            try {
                // 연결 url 설정
                URL url = new URL(urls[0]);
                // 커넥션 객체 생성
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                // 연결되었으면.
                if (conn != null) {
                    conn.setConnectTimeout(10000);
                    conn.setUseCaches(false);

                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8")); //캐릭터셋 설정
                    writer.write("inputNick="+inputNick); //요청 파라미터를 입력
                    writer.flush();
                    writer.close();
                    os.close();

                    conn.connect();

                    // 연결되었음 코드가 리턴되면.
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        for (; ; ) {
                            // 웹상에 보여지는 텍스트를 라인단위로 읽어 저장.
                            String line = br.readLine();
                            if (line == null) break;
                            // 저장된 텍스트 라인을 jsonHtml에 붙여넣음
                            jsonHtml.append(line + "\n");
                        }
                        br.close();
                    }
                    conn.disconnect();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return jsonHtml.toString();

        }

        protected void onPostExecute(String str) {
            char n = str.charAt(str.indexOf('{')+1);// {는 구분을 하기 위한 표식
            if(n == '0'){//DB에 없음
                canUse = true;
            }
            else{
                canUse = false;
            }

            gotData = true;
        }



    }


}
