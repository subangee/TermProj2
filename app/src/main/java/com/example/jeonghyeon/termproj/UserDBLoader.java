package com.example.jeonghyeon.termproj;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
 * 사용자 DB에 접근한다.
 * 등록된 사용자인지 아닌지 확인하기 위함이다
 */

public class UserDBLoader {

    ImageView imView;
    Bitmap bmImg;
    String uid;
    String nick;
    phpDown task;
    String cid;
    boolean gotData = false;//서버 통신 완료 확인

    public void load(String str){
        gotData = false;
        nick = null;
        uid = null;
        cid = str;
        task = new phpDown();
        task.execute("http://roadofrhyme.esy.es/myphp/nick.php");//id를 이용해서 닉네임을 찾는다
    }

    public String getUid(){
        return uid;
    }

    public String getNick(){
        return nick;
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
                    writer.write("uid="+cid); //요청 파라미터를 입력
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
            str = str.substring(str.indexOf('{'));

                    try{
                        JSONObject root = new JSONObject(str);
                        JSONArray ja = root.getJSONArray("results");
                        for(int i=0; i<ja.length(); i++){//조건문으로 바꾸어서 테스트 필요
                            JSONObject jo = ja.getJSONObject(i);
                            uid = jo.getString("user_id");
                            nick = jo.getString("nick");
                        }
                        gotData = true;
                    }catch(JSONException e){
                        e.printStackTrace();
                    }



        }



    }


}
