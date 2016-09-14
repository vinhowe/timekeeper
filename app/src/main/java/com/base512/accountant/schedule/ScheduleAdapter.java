package com.base512.accountant.schedule;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.base512.accountant.views.ItemTouchHelperAdapter;
import com.base512.accountant.R;
import com.base512.accountant.data.DynamicTask;
import com.base512.accountant.data.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.TaskViewHolder> implements ItemTouchHelperAdapter {

    private final List<Task> mTasks;
    private final ScheduleFragment.OnFragmentInteractionListener mListener;
    private final Context mContext;
    private final DatabaseReference mDatabaseReference;

    public ScheduleAdapter(Context context, ScheduleFragment.OnFragmentInteractionListener listener) {
        mTasks = new ArrayList<>();
        mListener = listener;
        mContext = context;
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_layout, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TaskViewHolder holder, int position) {
        holder.mTask = mTasks.get(position);
        holder.mLabelView.setText(mTasks.get(position).getLabel());
        if(holder.mTask instanceof DynamicTask) {
            //holder.mDurationView.setText(String.valueOf(((DynamicTask)mTasks.get(position)).getLastDuration()));
        } else {
           // holder.mDurationView.setText(String.valueOf(((StaticTask)mTasks.get(position)).getRequiredDuration()));
        }


        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mTask);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return (mTasks == null) ? 0 : mTasks.size();
    }

    public void updateSchedule(ArrayList<Task> taskArrayList) {
        if (taskArrayList == null) {
            Log.e(ScheduleAdapter.class.getSimpleName(), "Task array was empty");
            return;
        }
        this.mTasks.clear();
        this.mTasks.addAll(taskArrayList);

        notifyDataSetChanged();
    }

    @Override
    public void onItemDismiss(int position) {
        mTasks.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mTasks, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mTasks, i, i - 1);
            }
        }
        for(int i = 0; i < mTasks.size(); i++) {
/*            if(mTasks.get(i).getOrder() != i) {
                mDatabaseReference.child("tasks").child(mTasks.get(i).getId()).child("order").setValue(i);
                mTasks.get(i).setOrder(i);
            }*/
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mLabelView;
        public final TextView mDurationView;
        public Task mTask;
        public TaskViewHolder(View view) {
            super(view);
            mView = view;
            mLabelView = (TextView) view.findViewById(R.id.taskLabel);
            mDurationView = (TextView) view.findViewById(R.id.taskDuration);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mLabelView.getText() + "'";
        }
    }
}
