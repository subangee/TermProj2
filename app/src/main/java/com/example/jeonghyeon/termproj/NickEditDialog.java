package com.example.jeonghyeon.termproj;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by JeongHyeon on 2017-06-10.
 */

public class NickEditDialog extends Dialog {
    private Button nickEditBtn;
    private EditText nickEdit;
    private Handler nickHandler;
    private NickChecker nickChecker;
    private UserInserter userInserter;
    private String userId;

    public NickEditDialog(@NonNull Context context,String id) {
        super(context);
        userId = id;
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.nickedit);

        nickEditBtn = (Button) findViewById(R.id.nickEditBtn);
        nickEdit = (EditText)findViewById(R.id.nickEdit);
        nickHandler = new Handler();
        nickChecker =  new NickChecker();
        userInserter = new UserInserter();


        // 클릭 이벤트 셋팅
        nickEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nickChecker.load(nickEdit.getText().toString());//중복된 닉네임이 있는 지 확인한다
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (!nickChecker.Flag()){//결과가 나올 때 까지
                            try {
                                Thread.sleep(100);//대기
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        nickHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if(nickChecker.getCanUse()){
                                    ((TextView)findViewById(R.id.nickErr)).setText("");//메세지를 없애고
                                    userInserter.load(userId, nickEdit.getText().toString());//사용자 DB에 ID와 닉네임을 삽입
                                    NickEditDialog.super.dismiss();
                                    MainActivity.userNickname = nickEdit.getText().toString();
                                    Toast toast = Toast.makeText(getContext(), "환영합니다! " + nickEdit.getText().toString() +"님.", Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                                else{
                                    ((TextView)findViewById(R.id.nickErr)).setText("이미 사용중인 닉네임입니다.");//중복되면 메세지
                                }
                            }
                        });

                    }
                }).start();

            }
        });

    }
}
