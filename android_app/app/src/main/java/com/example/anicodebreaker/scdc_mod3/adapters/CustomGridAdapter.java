package com.example.anicodebreaker.scdc_mod3.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.anicodebreaker.scdc_mod3.R;
import com.example.anicodebreaker.scdc_mod3.config.Config;
import com.example.anicodebreaker.scdc_mod3.model.DataGrid;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by anicodebreaker on 25/12/16.
 */

public class CustomGridAdapter extends BaseAdapter {

    ArrayList<DataGrid> datas;
    Context ctx;

    public CustomGridAdapter(Context context, String[] a, String[] b) {
        datas = new ArrayList<DataGrid>();
        this.ctx = context;
        for (int i=0; i<a.length; ++i) {
            datas.add(new DataGrid(a[i], b[i]));
        }
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // a class ro hold views to refresh the gridview, to improve perfromance of the app
    class ViewHolder {
        TextView t,d;
        ViewHolder(View v) {
            t = (TextView) v.findViewById(R.id.titGrid);
            d = (TextView) v.findViewById(R.id.datGrid);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;
        if(row == null){
            LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(ctx.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.single_grid_item, parent, false);
            holder = new ViewHolder(row);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        /*
        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        row.setBackgroundColor(color);
        */

        double val = Double.parseDouble(datas.get(position).getDataG());
        // color setting code
        switch(position) {
            case 0:
                if (val<= Config.HIGH_G && val>=Config.LOW_G) row.setBackgroundColor(this.ctx.getResources().getColor(R.color.normVal));
                else row.setBackgroundColor(this.ctx.getResources().getColor(R.color.abNormVal));
                break;
            case 1:
                if (val<= Config.HIGH_T && val>=Config.LOW_T) row.setBackgroundColor(this.ctx.getResources().getColor(R.color.normVal));
                else row.setBackgroundColor(this.ctx.getResources().getColor(R.color.abNormVal));
                break;
            case 2:
                if (val<= Config.HIGH_H && val>=Config.LOW_H) row.setBackgroundColor(this.ctx.getResources().getColor(R.color.normVal));
                else row.setBackgroundColor(this.ctx.getResources().getColor(R.color.abNormVal));
                break;
            case 3:
                if (val<= Config.HIGH_B && val>=Config.LOW_B) row.setBackgroundColor(this.ctx.getResources().getColor(R.color.normVal));
                else row.setBackgroundColor(this.ctx.getResources().getColor(R.color.abNormVal));
                break;
            default:
                Random rnd = new Random();
                int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                row.setBackgroundColor(color);
        }

        DataGrid dt = datas.get(position);

        holder.t.setText(dt.getTitleG());
        holder.d.setText(dt.getDataG());
        return row;
    }
}
