package com.coursera.mike.dailyselfie;

import android.widget.BaseAdapter;
import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Mike on 4/24/2015.
 */
public class SelfieAdapter extends BaseAdapter {
    private ArrayList<SelfieRecord> list = new ArrayList<SelfieRecord>();
    private static LayoutInflater inflater = null;
    private Context mContext;

    public SelfieAdapter(Context context) {
        mContext = context;
        inflater = LayoutInflater.from(mContext);
    }

    public int getCount() {
        return list.size();
    }

    public Object getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View newView = convertView;
        ViewHolder holder;

        SelfieRecord curr = list.get(position);

        if (null == convertView) {
            holder = new ViewHolder();
            newView = inflater
                    .inflate(R.layout.selfie_layout, parent, false);
            holder.selfieImage = (ImageView) newView.findViewById(R.id.selfie_image);
            holder.selfieDesc = (TextView) newView.findViewById(R.id.selfie_desc);

            newView.setTag(holder);

        } else {
            holder = (ViewHolder) newView.getTag();
        }

        holder.selfieImage.setImageBitmap(curr.getSelfieBitmap());
        holder.selfieDesc.setText(curr.getSelfieDesc());


        return newView;
    }

    static class ViewHolder {

        ImageView selfieImage;
        TextView selfieDesc;

    }

    public void add(SelfieRecord listItem) {
        list.add(listItem);
        notifyDataSetChanged();
    }

    public ArrayList<SelfieRecord> getList() {
        return list;
    }

    public void removeAllViews() {
        list.clear();
        this.notifyDataSetChanged();
    }
}