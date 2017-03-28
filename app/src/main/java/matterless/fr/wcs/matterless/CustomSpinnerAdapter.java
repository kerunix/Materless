package matterless.fr.wcs.matterless;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import matterless.fr.wcs.matterless.R;

public class CustomSpinnerAdapter extends BaseAdapter {
    Context mContext;
    LayoutInflater mInflater;
    List<String> mDayList;

    public CustomSpinnerAdapter(Context applicationContext, List<String> dayList){
        mContext = applicationContext;
        mInflater = (LayoutInflater.from(applicationContext));
        mDayList = dayList;
    }

    @Override
    public int getCount() {
        return mDayList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDayList.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        view = mInflater.inflate(R.layout.simple_spinner_item, null);
        TextView textViewSpinnerItem = (TextView) view.findViewById(R.id.textViewSpinnerItem);
        textViewSpinnerItem.setText(mDayList.get(position));
        return view;
    }
}