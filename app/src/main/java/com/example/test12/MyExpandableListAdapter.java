package com.example.test12;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyExpandableListAdapter extends BaseExpandableListAdapter {

    public static final String TAG = "xxx";
    private List<Map<String, Object>> parent;

    private List<List<Map<String, Object>>> child;

    private Context context;

    public MyExpandableListAdapter(Context context) {
        super();
        this.context = context;
        init();
    }

    private static String getRandomString(int maxlength) {
        //http://stackoverflow.com/a/25447172
        String result = "";
        int i = 0;
        int min = 33;
        int max = 122;
        while (i < maxlength) {
            int n = (int) (Math.random() * (max - min) + min);
            if (n >= 33 && n < 123) {
                result += (char) n;
                ++i;
            }
        }
        return result;
    }

    @Override
    public int getGroupCount() {
        return parent.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return child.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return parent.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return child.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return (Long) parent.get(groupPosition).get("id");
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return (Long) child.get(groupPosition).get(childPosition).get("id");
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View v = null;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.group, parent, false);
        } else {
            v = convertView;
        }
        ((TextView) v.findViewById(R.id.group)).setText(
                this.parent.get(groupPosition).get("id") + " : " +
                        this.parent.get(groupPosition).get("value") + " : " +
                        getChildrenCount(groupPosition)
        );
        v.setBackgroundColor((groupPosition % 2 == 0) ? Color.parseColor("#FFA9E9F9") : Color.parseColor("#FF7FF6E2"));
        return v;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View v = null;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.item, parent, false);
        } else {
            v = convertView;
        }
        TextView tv = (TextView) v.findViewById(R.id.item);
        tv.setText(
                getChildVal(groupPosition, childPosition, "id")
                 + ":"
                 + getChildVal(groupPosition, childPosition, "value")
        );
        tv.setBackgroundColor(
                (Boolean) this.child.get(groupPosition).get(childPosition).get("selected")
                        ? Color.YELLOW : Color.WHITE
        );
        return v;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void moveItem(int oriType, int oriGroupPos, int oriChildPos,
                         int newType, int newGroupPos, int newChildPos,
                         boolean down) {

        Log.d(TAG, String.format("oriType=%d,oriGroupPos=%d, oriChildPos=%d, ,newType=%d, newGroupPos=%d, newChildPos=%d, down=%s",
                oriType, oriGroupPos, oriChildPos, newType, newGroupPos, newChildPos, "" + down));

        if (oriType != newType) {
            Log.d(TAG, "type not match");
            return;
        }

        if (oriType == 0) {
            Map<String, Object> oriParent = parent.remove(oriGroupPos);
            parent.add(newGroupPos, oriParent);
        }
        if (oriType == 1) {
            List<Map<String, Object>> oriChild = child.get(oriGroupPos);
            Map<String, Object> myData = oriChild.remove(oriChildPos);
            List<Map<String, Object>> newChild = child.get(newGroupPos);
            newChild.add(newChildPos, myData);
        }
        updateList();
    }

    public void toggleSelect(int groupPosition, int childPosition) {
        Map<String, Object> childData = child.get(groupPosition).get(childPosition);
        childData.put("selected", !(Boolean) childData.get("selected"));
        updateList();
    }

    private void updateList() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    private String getChildVal(int groupPos, int childPos, String key) {
        return this.child.get(groupPos).get(childPos).get(key).toString();
    }

    private void init() {
        parent = new ArrayList<>();
        child = new ArrayList<List<Map<String, Object>>>();

        for (int i = 0; i < 40; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", Long.valueOf(i));
            map.put("value", getRandomString(15));
            parent.add(map);
            List childList = new ArrayList<>();
            for (int j = 0; j < (int) (Math.random() * 10); j++) {
                Map<String, Object> mapC = new HashMap<>();
                mapC.put("id", Long.valueOf(i * 1000 + j));
                mapC.put("value", getRandomString(30));
                mapC.put("selected", false);
                childList.add(mapC);
            }
            child.add(childList);
        }
    }

}
