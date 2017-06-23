package com.example.jeonghyeon.termproj;

/**
 * Created by JeongHyeon on 2017-06-21.
 */

public class InstClass {
    private int id;
    private String title;
    private String author;

    public InstClass(int id, String title, String author){
        this.id = id;
        this.title = title;
        this.author = author;
    }

    public int getId(){
        return id;
    }

    public String getTitle(){
        return title;
    }

    public String getAuthor(){
        return author;
    }

}
