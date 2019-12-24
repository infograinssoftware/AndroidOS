package com.open_source.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.open_source.R;
import com.open_source.fragment.SellPropertyNext;

import java.util.ArrayList;

public class CountertopsAdapter extends BaseAdapter {

    public static ArrayList<String> Countertops = new ArrayList<>();
    private static LayoutInflater inflater;
    ArrayList<String> rowitem ;
    Context con;

    public CountertopsAdapter(Context context, ArrayList<String> rowlist) {
        con = context;
        rowitem = rowlist;

    }

    @Override
    public int getCount() {
        return rowitem.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
       ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            inflater = LayoutInflater.from(con);
            convertView = inflater.inflate(R.layout.row_disclosure, null);
            viewHolder.txt = (TextView) convertView.findViewById(R.id.txt);
            viewHolder.chkbox = (CheckBox) convertView.findViewById(R.id.checkbox);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.txt.setText(rowitem.get(position));
        viewHolder.chkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((CheckBox) view).isChecked()) {
                    Countertops.add(rowitem.get(position));
                } else {
                    Countertops.remove(rowitem.get(position));
                }
            }
        });
        return convertView;
    }

    private static class ViewHolder {
        TextView txt;
        CheckBox chkbox;
    }
}



