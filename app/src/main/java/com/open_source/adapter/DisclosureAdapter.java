package com.open_source.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.open_source.R;
import com.open_source.fragment.SellPropertyNext;

import java.util.ArrayList;

/**
 * Created by and-02 on 14/6/18.
 */

public class DisclosureAdapter extends BaseAdapter {
    public static ArrayList<String> box1 = new ArrayList<>();
    private static LayoutInflater inflater;
    ArrayList<String> rowitem = new ArrayList<>();
    Context con;
    String seldesc;

    public DisclosureAdapter(Context context, ArrayList<String> rowlist, String seldesc) {
        con = context;
        rowitem = rowlist;
        this.seldesc = seldesc;
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
        if (seldesc.contains(rowitem.get(position))) {
            viewHolder.chkbox.setChecked(true);
            box1.add(rowitem.get(position));
            Log.e("=======size: ", String.valueOf(box1.size()));
        }
        viewHolder.chkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((CheckBox) view).isChecked()) {
                    if (position == 6) {
                        SellPropertyNext.ed_other.setVisibility(View.VISIBLE);
                        box1.add(rowitem.get(position));
                    } else {
                        box1.add(rowitem.get(position));
                    }
                } else {
                    if (position == 6) {
                        SellPropertyNext.ed_other.setVisibility(View.GONE);
                        SellPropertyNext.ed_other.setText("");
                        box1.remove(rowitem.get(position));
                    } else {
                        box1.remove(rowitem.get(position));
                    }
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
