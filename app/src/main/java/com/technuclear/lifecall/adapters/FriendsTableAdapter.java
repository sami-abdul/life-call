package com.technuclear.lifecall.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.technuclear.lifecall.R;
import com.technuclear.lifecall.tables.FriendsTable;

public class FriendsTableAdapter extends ArrayAdapter<FriendsTable> {

    Context mContext;
    int mLayoutResourceId;

    public FriendsTableAdapter(Context context, int layoutResourceId) {
        super(context, layoutResourceId);

        mContext = context;
        mLayoutResourceId = layoutResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        final FriendsTable currentItem = getItem(position);

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(mLayoutResourceId, parent, false);
        }

        row.setTag(currentItem);
        TextView name = (TextView) row.findViewById(R.id.friends_list_name);
        TextView phone = (TextView) row.findViewById(R.id.friends_list_phone);
        TextView bloodGroup = (TextView) row.findViewById(R.id.friends_list_blood_group);

        name.setText(currentItem.getName());
        phone.setText(currentItem.getPhoneNumber());
        bloodGroup.setText(currentItem.getBloodGroup());

        return row;
    }
}
