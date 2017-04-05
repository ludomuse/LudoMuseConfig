package com.ihmtek.ludomuseconfig;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by IHMTEK Vibox 2 on 20/10/2016.
 */


public class JsonFileAdapter extends ArrayAdapter<Model> {

    private final Context context;
    private final ArrayList<Model> modelsArrayList;

    public JsonFileAdapter(Context context, ArrayList modelsArrayList)
    {
        super(context, R.layout.target_item, modelsArrayList);
        this.context = context;
        this.modelsArrayList = modelsArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.target_item, parent, false);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.item_icon);
        imageView.setImageResource(modelsArrayList.get(position).getIcon());

        TextView textView = (TextView) rowView.findViewById(R.id.text_item);
        textView.setText(modelsArrayList.get(position).getText());

        return rowView;
    }


}
