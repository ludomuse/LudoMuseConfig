package com.ihmtek.ludomuseconfig;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by IHMTEK Vibox 2 on 20/10/2016.
 */


public class JsonFileAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final ArrayList<String> modelsArrayList;

    public JsonFileAdapter(Context context, ArrayList modelsArrayList)
    {
        super(context, R.layout.text_view, modelsArrayList);
        this.context = context;
        this.modelsArrayList = modelsArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = null;

        rowView = inflater.inflate(R.layout.target_item, parent, false);

        TextView textView = (TextView) rowView.findViewById(R.id.text_item);
        textView.setText(modelsArrayList.get(position));

        return rowView;
    }

}
