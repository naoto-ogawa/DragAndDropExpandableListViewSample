package com.example.test12;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ExpandableListView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((ExpandableListView) findViewById(R.id.expandableListView))
                .setAdapter(new MyExpandableListAdapter(this));

    }

}
