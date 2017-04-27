package com.base512.accountant.data.source.tasks;

import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.base512.accountant.data.Task;

/**
 * Created by Thomas on 9/1/2016.
 */

public abstract class TaskViewHolder extends RecyclerView.ViewHolder {

    protected TypedArray mBackgroundColors;
    protected int[] textAttrs = {android.R.attr.textColorPrimary, android.R.attr.textColorSecondary};
    protected int[] textAttrsInverse = {android.R.attr.textColorPrimaryInverse, android.R.attr.textColorSecondaryInverse};
    protected final static int TEXT_INVERSE_POSITION = 4;

    public TaskViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void bind(Task task);
}