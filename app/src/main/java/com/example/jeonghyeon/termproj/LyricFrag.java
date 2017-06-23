package com.example.jeonghyeon.termproj;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by JeongHyeon on 2017-04-15.
 */

public class LyricFrag extends Fragment {

    public View LView;
    public ListView lyricList;
    public FloatingActionButton makeNote;
    LyricDBhelper lhelper;
    SQLiteDatabase ldb;
    public TextView showTitle;
    public TextView showLyric;
    public EditText editTitle;
    public EditText editLyric;
    public Button saveNote;
    public Button editNote;
    public Button deleteNote;
    public Button listBtn;
    public LinearLayout noteBtn;
    static boolean editMode;
    static boolean newNoteMode;
    SimpleCursorAdapter noteAdapter;
    Cursor ldbC;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LView = inflater.inflate(R.layout.fragment_second, container, false);
        lyricList = (ListView)LView.findViewById(R.id.lyricList);

        makeNote = (FloatingActionButton) LView.findViewById(R.id.makeNote);

        showTitle = (TextView)LView.findViewById(R.id.showTitle);
        showLyric = (TextView)LView.findViewById(R.id.showLyric);
        showLyric.setMovementMethod(new ScrollingMovementMethod());
        editLyric = (EditText)LView.findViewById(R.id.editLyric);
        editTitle = (EditText)LView.findViewById(R.id.editTitle);

        listBtn = (Button)LView.findViewById(R.id.listBtn);
        saveNote = (Button)LView.findViewById(R.id.saveNote);
        editNote = (Button)LView.findViewById(R.id.editNote);
        deleteNote = (Button)LView.findViewById(R.id.deleteNote);

        noteBtn = (LinearLayout)LView.findViewById(R.id.noteBtn);


        lhelper = new LyricDBhelper(LView.getContext(), "note_" + MainActivity.userNickname +".db", null, 1);//각 사용자별 별개의 DB
        ldb = lhelper.getWritableDatabase();
        lhelper.onCreate(ldb);

        ldbC = ldb.query("lyricNote", null, null, null, null, null, null, null);
        noteAdapter = new SimpleCursorAdapter(LView.getContext(), android.R.layout.simple_list_item_1, ldbC,
                            new String[] {"title"}, new int[] {android.R.id.text1}, 0);
        lyricList.setAdapter(noteAdapter);

        lyricList.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ldbC.moveToPosition(position);//커서를 선택한 아이템으로 이동

                newNoteMode = false;//편집 모드
                lyricList.setVisibility(View.GONE);
                makeNote.setVisibility(View.GONE);
                editTitle.setText(ldbC.getString(1));
                showTitle.setText(ldbC.getString(1));
                editLyric.setText(ldbC.getString(2));
                showLyric.setText(ldbC.getString(2));
                deleteNote.setText("삭제");
                showTitle.setVisibility(View.VISIBLE);
                editTitle.setVisibility(View.GONE);
                showLyric.setVisibility(View.VISIBLE);
                editLyric.setVisibility(View.GONE);
                noteBtn.setVisibility(View.VISIBLE);

                editMode = false;//읽기 모드

            }

        });

        listBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editMode){
                    Toast toast = Toast.makeText(LView.getContext(), "수정을 완료해주세요", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else{//수정이 완료 된 상태
                    ContentValues values = new ContentValues();
                    values.put("title", showTitle.getText().toString());
                    values.put("lyric", showLyric.getText().toString());
/*
                    if(newNoteMode) {//생성 모드
                        ldb.insert("lyricNote", null, values);//삽입
                    }
                    else{//편집 모드
                        ldb.update("lyricNote",values, "_id=?",new String[]{ldbC.getString(0)});//수정
                    }*/
                    ldbC = ldb.query("lyricNote", null, null, null, null, null, null, null);
                    noteAdapter.changeCursor(ldbC);//리스트 다시 작성
                    showTitle.setVisibility(View.GONE);
                    showLyric.setVisibility(View.GONE);
                    lyricList.setVisibility(View.VISIBLE);
                    makeNote.setVisibility(View.VISIBLE);
                    noteBtn.setVisibility(View.GONE);


                }
            }
        });

        makeNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newNoteMode = true;//생성 모드
                lyricList.setVisibility(View.GONE);
                makeNote.setVisibility(View.GONE);
                showTitle.setText("제목");
                showLyric.setText("가사 입력");
                deleteNote.setText("취소");
                showTitle.setVisibility(View.VISIBLE);
                editTitle.setVisibility(View.GONE);
                showLyric.setVisibility(View.VISIBLE);
                editLyric.setVisibility(View.GONE);
                noteBtn.setVisibility(View.VISIBLE);
                editMode = false;//읽기 모드
            }
        });

        editNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//모드 변경. 읽기<->수정
                if(editMode){
                    editNote.setText("수정");
                    showTitle.setVisibility(View.VISIBLE);
                    editTitle.setVisibility(View.GONE);
                    showLyric.setVisibility(View.VISIBLE);
                    editLyric.setVisibility(View.GONE);
                    showTitle.setText(editTitle.getText().toString());
                    showLyric.setText(editLyric.getText().toString());
                    editMode = false;
                }
                else{
                    editNote.setText("완료");
                    showTitle.setVisibility(View.GONE);
                    editTitle.setVisibility(View.VISIBLE);
                    showLyric.setVisibility(View.GONE);
                    editLyric.setVisibility(View.VISIBLE);
                    editMode = true;
                }
            }
        });

        saveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editMode){
                    Toast toast = Toast.makeText(LView.getContext(), "수정을 완료해주세요", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else{//수정이 완료 된 상태
                    ContentValues values = new ContentValues();
                    values.put("title", showTitle.getText().toString());
                    values.put("lyric", showLyric.getText().toString());

                    if(newNoteMode) {//생성 모드
                        ldb.insert("lyricNote", null, values);//삽입
                    }
                    else{//편집 모드
                        ldb.update("lyricNote",values, "_id=?",new String[]{ldbC.getString(0)});//수정
                    }
                    ldbC = ldb.query("lyricNote", null, null, null, null, null, null, null);
                    noteAdapter.changeCursor(ldbC);//리스트 다시 작성
                    showTitle.setVisibility(View.GONE);
                    showLyric.setVisibility(View.GONE);
                    lyricList.setVisibility(View.VISIBLE);
                    makeNote.setVisibility(View.VISIBLE);
                    noteBtn.setVisibility(View.GONE);


                }
            }
        });

        deleteNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder delAlert = new AlertDialog.Builder(LView.getContext());//삭제 경고 다이얼로그
                if(!newNoteMode) {
                    delAlert.setTitle("삭제");
                    delAlert.setMessage("정말로 삭제하시겠습니까?");
                }
                else{
                    delAlert.setTitle("취소");
                    delAlert.setMessage("정말로 취소하시겠습니까?");
                }
                delAlert.setCancelable(false)
                        .setPositiveButton("예",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (!newNoteMode) {
                                    ldb.delete("lyricNote", "_id=?", new String[]{ldbC.getString(0)});
                                }
                                ldbC = ldb.query("lyricNote", null, null, null, null, null, null, null);
                                noteAdapter.changeCursor(ldbC);
                                showTitle.setVisibility(View.GONE);
                                showLyric.setVisibility(View.GONE);
                                editTitle.setVisibility(View.GONE);
                                editLyric.setVisibility(View.GONE);
                                lyricList.setVisibility(View.VISIBLE);
                                makeNote.setVisibility(View.VISIBLE);
                                noteBtn.setVisibility(View.GONE);
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                AlertDialog del = delAlert.create();
                del.show();

            }
        });

        return LView;
    }


    public static LyricFrag newInstance() {

        LyricFrag lyricFrag = new LyricFrag();
        Bundle bundle = new Bundle();
        lyricFrag.setArguments(bundle);
        return lyricFrag;
    }
}
