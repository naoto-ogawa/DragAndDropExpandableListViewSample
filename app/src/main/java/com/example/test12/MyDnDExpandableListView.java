package com.example.test12;

import android.content.ClipData;
import android.content.Context;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;

import static android.view.DragEvent.ACTION_DRAG_ENDED;
import static android.view.DragEvent.ACTION_DRAG_EXITED;
import static android.view.DragEvent.ACTION_DRAG_LOCATION;
import static android.view.DragEvent.ACTION_DRAG_STARTED;
import static android.view.DragEvent.ACTION_DROP;

public class MyDnDExpandableListView extends ExpandableListView
        implements ExpandableListView.OnChildClickListener,
                   AdapterView.OnItemLongClickListener,
                   View.OnDragListener {

    public MyDnDExpandableListView(Context context) {
        super(context);
        addListener();
    }

    public MyDnDExpandableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        addListener();
    }

    public MyDnDExpandableListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        addListener();
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        // event location
        float x = event.getX();
        float y = event.getY();

        // current position
        int pos = pointToPosition((int) x, (int) y);

        DragInfo dragInfo = ((DragInfo) event.getLocalState());

        MyExpandableListAdapter adapter = getMyAdapter();

        switch (event.getAction()) {
            case ACTION_DRAG_STARTED:
                dragInfo.setXY(x, y);
                break;
            case ACTION_DRAG_LOCATION:
                dragInfo.isDragDown(x, y);
                break;
            case ACTION_DROP:
                boolean down = dragInfo.isDragDown(x, y);

                long packPos = this.getExpandableListPosition(pos);
                int nowPackedPosType = ExpandableListView.getPackedPositionType(packPos);
                int nowPackedPosGroup = ExpandableListView.getPackedPositionGroup(packPos);
                int nowPackedPosChild = ExpandableListView.getPackedPositionChild(packPos);

                adapter.moveItem(
                        dragInfo.getType(),
                        dragInfo.getPackedPositionGroup(),
                        dragInfo.getPetPackedPositionChild(),
                        nowPackedPosType,
                        nowPackedPosGroup,
                        nowPackedPosChild,
                        down);
                break;
            case ACTION_DRAG_EXITED:
                break;
            case ACTION_DRAG_ENDED:
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        long expListPos = this.getExpandableListPosition(position);
        int packedPosType = ExpandableListView.getPackedPositionType(expListPos);
        int packedPosGroup = ExpandableListView.getPackedPositionGroup(expListPos);
        int packedPosChild = ExpandableListView.getPackedPositionChild(expListPos);

        DragInfo dragInfo = new DragInfo();
        dragInfo.setPosition(packedPosType, packedPosGroup, packedPosChild);
        ClipData data = ClipData.newPlainText("dragging item no", "" + position);

        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
        view.startDrag(data, shadowBuilder, dragInfo, 0);
        return true;
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        getMyAdapter().toggleSelect(groupPosition, childPosition);
        return false;
    }

    private void addListener() {
        setOnItemLongClickListener(this);
        setOnDragListener(this);
        this.setOnChildClickListener(this);
    }


    private MyExpandableListAdapter getMyAdapter() {
        return (MyExpandableListAdapter) getExpandableListAdapter();
    }

    private class DragInfo {

        private int type;
        private float x0;
        private float y0;
        private int packedPositionGroup;
        private int petPackedPositionChild;


        public void setPosition(int type, int packedPositionGroup, int petPackedPositionChild) {
            this.type = type;
            this.packedPositionGroup = packedPositionGroup;
            this.petPackedPositionChild = petPackedPositionChild;
        }


        public int getPackedPositionGroup() {
            return packedPositionGroup;
        }

        public void setPackedPositionGroup(int packedPositionGroup) {
            this.packedPositionGroup = packedPositionGroup;
        }

        public int getPetPackedPositionChild() {
            return petPackedPositionChild;
        }

        public void setPetPackedPositionChild(int petPackedPositionChild) {
            this.petPackedPositionChild = petPackedPositionChild;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public boolean isGroup() {
            return PACKED_POSITION_TYPE_CHILD == type;
        }

        public boolean isChild() {
            return PACKED_POSITION_TYPE_GROUP == type;
        }

        public boolean isNull() {
            return PACKED_POSITION_TYPE_NULL == type;
        }

        public void setXY(float x, float y) {
            this.x0 = x;
            this.y0 = y;
        }


        boolean isDragDown(float x1, float y1) {
            boolean ret = y1 > y0;
            x0 = x1;
            y0 = y1;
            return ret;
        }
    }

}
