package com.example.jeonghyeon.termproj;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by JeongHyeon on 2017-06-04.
 * 비트 DB 접근
 */

public class InstDBLoader {

    ImageView imView;
    Bitmap bmImg;
    ArrayList<InstClass> list = new ArrayList<InstClass>();
    phpDown task;
    boolean gotData = false;

    public void load(){
        gotData = false;
        task = new phpDown();
        task.execute("http://roadofrhyme.esy.es/myphp/instlist.php");//비트 db 접근용 php

    }

    public synchronized ArrayList<InstClass> getList() {
        return list;
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
                Log.d("TAG","doInBack1");
                // 연결 url 설정
                URL url = new URL(urls[0]);
                // 커넥션 객체 생성
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                // 연결되었으면.
                if (conn != null) {
                    conn.setConnectTimeout(10000);
                    conn.setUseCaches(false);

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
            Log.d("TAG","doInBack2");
            return jsonHtml.toString();

        }

        protected void onPostExecute(String str) {
            Log.d("TAG","ope1");
            str = str.substring(str.indexOf('{'));

            InstClass instElement;
            try{
                JSONObject root = new JSONObject(str);
                JSONArray ja = root.getJSONArray("results");
                for(int i=0; i<ja.length(); i++){
                    JSONObject jo = ja.getJSONObject(i);

                    instElement = new InstClass(jo.getInt("_id"),jo.getString("title"),jo.getString("author"));
                    list.add(instElement);
                }
                gotData = true;
                Log.d("TAG","ope2");
            }catch(JSONException e){
                e.printStackTrace();
            }

        }



    }


}
