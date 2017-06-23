package com.example.jeonghyeon.termproj;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by JeongHyeon on 2017-06-21.
 */

public class InstAdapter extends ArrayAdapter<InstClass> {

    private ArrayList<InstClass> items;
    private LayoutInflater inflater;

    public InstAdapter(Context context, int textViewResourceId, ArrayList<InstClass> items){
        super(context,textViewResourceId,items);
        inflater = LayoutInflater.from(context);
        this.items = items;
    }

    public InstClass getItem(int pos){
        return super.getItem(pos);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = inflater.inflate(R.layout.instlist_layout, null);
        }
        InstClass ic = items.get(position);
        if (ic != null) {
            TextView id = (TextView) v.findViewById(R.id.instId);
            TextView title = (TextView) v.findViewById(R.id.instTitle);
            TextView author = (TextView) v.findViewById(R.id.instAuthor);
            if (id != null){
                id.setText(String.valueOf(ic.getId()));
            }
            if (title != null){
                title.setText(ic.getTitle());
            }
            if (author != null){
                author.setText(ic.getAuthor());
            }
        }
        return v;
    }

}
