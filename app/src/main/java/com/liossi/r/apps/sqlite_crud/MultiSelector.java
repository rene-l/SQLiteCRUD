package com.liossi.r.apps.sqlite_crud;

import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;

/**
 * Created by Rene on 30/06/2016.
 */
public class MultiSelector {
    private SparseBooleanArray mSelectedPositions;
    private boolean inChoiceMode;

    public MultiSelector(){
        mSelectedPositions = new SparseBooleanArray();
        inChoiceMode = false;
    }

    private void setSelected(int position, boolean isSelected){
        mSelectedPositions.put(position, isSelected);
        setChoiceMode();
    }
    public SparseBooleanArray getSelected(){
        return mSelectedPositions;
    }

    public void setSelected(RecyclerView.ViewHolder holder, boolean isSelected) {
        setSelected(holder.getAdapterPosition(), isSelected);
    }

    public void clearSelected(){
        mSelectedPositions.clear();
        setChoiceMode();
    }

    public boolean isSelected(int position){
        return mSelectedPositions.get(position);
    }

    private void setChoiceMode(){
        if (mSelectedPositions.indexOfValue(true) != -1) {
            inChoiceMode = true;
        } else {
            inChoiceMode = false;
        }
    }

    public boolean isInChoiceMode(){
        return inChoiceMode;
    }

    public SparseBooleanArray getActiveItems(){
        SparseBooleanArray active = new SparseBooleanArray();
        for(int i = 0; i < mSelectedPositions.size(); i++) {
            int key = mSelectedPositions.keyAt(i);

            if (mSelectedPositions.get(key)) {
                active.put(key, mSelectedPositions.get(key));
            }
        }
        return active;
    }

    public int[] getActiveKeys(){
        int[] keys = new int[getActiveItems().size()];
        SparseBooleanArray selected = getActiveItems();

        for (int i = 0; i < keys.length; i++) {
            keys[i] = selected.keyAt(i);
        }
        return keys;
    }
}
