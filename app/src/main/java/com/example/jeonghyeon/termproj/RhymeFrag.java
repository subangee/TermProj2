package com.example.jeonghyeon.termproj;

import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.jeonghyeon.termproj.ArrayUsing.findFirstIndex;
import static com.example.jeonghyeon.termproj.KorJaso.seperateKor;

/**
 * Created by JeongHyeon on 2017-04-14.
 * 입력한 단어와 라임이 맞는 단어를 출력한다
 */

public class RhymeFrag extends Fragment {

    public View RView;

    public EditText inputText;
    public Button select_btn;
    public ListView list;
    public Button close_btn;
    public WebView dic;
    ArrayAdapter<String> adapter;
    WordDBLoader wordLoader;
    ProgressDialog pd;

    //초중종성의 각 자소를 유사한 발음끼리 묶어 둔 것. -1은 끝을 표시하기 위한 것
    int[][] cho = {{0, 1, 3, 4, 7, 8, 15, 16, 17},
            {2, 5, 6},
            {9, 10, 12, 13, 14},
            {11, 18},
            {-1}};

    int[][] jung = {{0, 2, 9},
            {4, 6, 8, 12, 14},
            {13, 17, 18},
            {16, 19, 20},
            {1, 3, 5, 7, 10, 11, 15},
            {-1}};

    int[][] jong = {{4, 5, 6, 16, 21},
            {8, 9, 10, 11, 12, 13, 14, 15},
            {0, 1, 2, 3, 7, 17, 18, 19, 20, 22, 23, 24, 25, 26, 27},
            {-1}};

    public String makeREGEX(int[] index) {
        String reg = "";
        int findCho, findJung, findJong;
        if (index == null) {
            return null;
        }
        for (int i = 0; i < index.length; i = i + 3) {
            findCho = findFirstIndex(index[i], cho);//입력된 단어의 자소가 위치한 행을 찾는다.
            findJung = findFirstIndex(index[i + 1], jung);
            findJong = findFirstIndex(index[i + 2], jong);
            reg = reg + "[";
            for (int i_1 = 0; i_1 < cho[findCho].length; i_1++)//각 행에 존재하는 원소의 모든 조합을 찾기위한 반복문
                for (int i_2 = 0; i_2 < jung[findJung].length; i_2++)
                    for (int i_3 = 0; i_3 < jong[findJong].length; i_3++) {
                        int uniChar = 0xAC00 + ((cho[findCho][i_1] * 21) + jung[findJung][i_2]) * 28 + jong[findJong][i_3];//0xAC00는 '가'에 해당된다
                        reg = reg + Character.toString((char) uniChar);
                    }
            reg = reg + "]";
        }
        return reg;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RView = inflater.inflate(R.layout.fragment_main, container, false);

        inputText = (EditText) RView.findViewById(R.id.inputText);
        select_btn = (Button) RView.findViewById(R.id.select_btn);
        list = (ListView) RView.findViewById(R.id.list);
        close_btn = (Button)RView.findViewById(R.id.dic_close_btn);
        dic = (WebView)RView.findViewById(R.id.dic_view);

        select_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) RView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(inputText.getWindowToken(), 0);
                String str = inputText.getText().toString();
                int[] index = seperateKor(str);
                String reg = makeREGEX(index);


                wordLoader = new WordDBLoader();//단어 DB에 접근
                wordLoader.load(reg);//함수로 만들어진 정규표현식을 php에 post할 것이다
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        while(!wordLoader.Flag()){
                            RView.post(new Runnable() {
                            @Override
                            public void run() {//핸들러 대신 View.post()사용
                                    pd = ProgressDialog.show(RView.getContext(), "", "검색중입니다...", true);
                            }
                        });
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        RView.post(new Runnable() {
                            @Override
                            public void run() {//
                                pd.dismiss();
                                adapter = new ArrayAdapter<String>(RView.getContext(),android.R.layout.simple_list_item_1,wordLoader.getList());
                                list.setAdapter(adapter);
                            }
                        });
                    }
                }).start();
            }
        });

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final TextView searchWord = (TextView) view;//리스트의 항목을 TextView로 변환
                PopupMenu popupMenu = new PopupMenu(RView.getContext(), view);
                popupMenu.getMenuInflater().inflate(R.menu.wordmenu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.dict:
                                RView.findViewById(R.id.searchMode).setVisibility(View.GONE);
                                RView.findViewById(R.id.dicMode).setVisibility(View.VISIBLE);
                                dic.loadUrl("http://m.krdic.naver.com/search/all/0/" + searchWord.getText().toString() +"?format=HTML&isMobile=true");
                                break;
                            case R.id.copy:
                                ClipboardManager clip = (ClipboardManager)RView.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                                clip.setText(searchWord.getText());
                                Toast toast = Toast.makeText(RView.getContext(), "클립보드에 복사했습니다.", Toast.LENGTH_SHORT);
                                toast.show();
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
                return false;
            }
        });

        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RView.findViewById(R.id.dicMode).setVisibility(View.GONE);
                RView.findViewById(R.id.searchMode).setVisibility(View.VISIBLE);
            }
        });

        return RView;
    }

    public static RhymeFrag newInstance() {

        RhymeFrag rhymeFrag = new RhymeFrag();
        Bundle bundle = new Bundle();

        rhymeFrag.setArguments(bundle);

        return rhymeFrag;
    }
}
