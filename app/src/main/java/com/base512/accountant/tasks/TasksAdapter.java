package com.base512.accountant.tasks;

import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.base512.accountant.R;
import com.base512.accountant.data.DataObject;
import com.base512.accountant.data.Task;
import com.base512.accountant.data.User;
import com.base512.accountant.data.source.BaseDataSource;
import com.base512.accountant.data.source.tasks.TaskViewHolder;
import com.base512.accountant.data.source.user.UserRepository;
import com.base512.accountant.views.ItemTouchHelperAdapter;
import com.base512.accountant.views.ItemTouchHelperViewHolder;

import org.joda.time.LocalTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.base512.accountant.util.TimeUtils.formatMinutes;

/**
 * Created by Thomas on 9/1/2016.
 */

public class TasksAdapter extends RecyclerView.Adapter implements ItemTouchHelperAdapter {

    public ArrayList<Task> mTasks = new ArrayList<>();

    private boolean mIsSchedule = false;

    private static int mWakeupTime;

    private ArrayList<ScheduleChangeListener> scheduleChangeListeners;

    public TasksAdapter(UserRepository userRepository) {
        scheduleChangeListeners = new ArrayList<>();
        userRepository.getUser(new BaseDataSource.GetDataCallback() {
            @Override
            public void onDataLoaded(DataObject data) {
                mWakeupTime = ((User) data).getWakeupTime();
            }

            @Override
            public void onDataError() {
                throw new NullPointerException();
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final TaskViewHolder taskViewHolder = mIsSchedule ? new ScheduleTasksViewHolder(parent, this) : new OverviewTaskViewHolder(parent);
        if(mIsSchedule) {
            scheduleChangeListeners.add((ScheduleChangeListener) taskViewHolder);
        }
        taskViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRepositoryItemClicked(taskViewHolder.getAdapterPosition());
            }
        });
        return taskViewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        Task task = mTasks.get(position);
        ((TaskViewHolder) holder).bind(mTasks.get(position));

    }

    @Override
    public int getItemViewType(int position) {
        return mTasks.get(position).getId().isPresent() ? 1 : 0;
    }

    @Override
    public int getItemCount() {
        return mTasks.size();
    }

    private void onRepositoryItemClicked(int adapterPosition) {
        //repositoriesListActivity.onRepositoryClick(repositories.get(adapterPosition));
    }

    public void updateTasksList(ArrayList<Task> tasks, boolean schedule) {
        mIsSchedule = schedule;
        mTasks.clear();
        mTasks.addAll(tasks);
        notifyDataSetChanged();
    }

    protected static String getTimeForPosition(int position, TasksAdapter tasksAdapter) {
        LocalTime taskTime = new LocalTime().withMillisOfDay(mWakeupTime*60*1000);
        for(int i = 0; i < position; i++) {
            taskTime = taskTime.plusMinutes(tasksAdapter.mTasks.get(i).getDuration());
        }

        return taskTime.toString("H:mm");
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mTasks, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        updateTimes();
    }

    @Override
    public void onItemDismiss(int position) {
        mTasks.remove(position);
        notifyItemRemoved(position);
        updateTimes();
    }

    public void updateTimes() {
        Iterator<ScheduleChangeListener> listenerIterator = scheduleChangeListeners.iterator();
        while(listenerIterator.hasNext()) {
            listenerIterator.next().onScheduleChange();
        }
    }

    static class OverviewTaskViewHolder extends TaskViewHolder {

        @BindView(R.id.taskLabel) TextView mTaskLabel;
        @BindView(R.id.taskDuration) TextView mTaskDuration;
        @BindView(R.id.taskDurationIcon) ImageView mTaskDurationIcon;

        // The position at which text color is inverted to contrast background

        public OverviewTaskViewHolder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.task_layout, parent, false));
            ButterKnife.bind(this, itemView);
            mBackgroundColors = itemView.getContext().getResources().obtainTypedArray(R.array.palette);
        }

        @Override
        public void bind(Task task) {
            mTaskLabel.setText(task.getLabel());
            // TODO Make a utility class for time parsing
            mTaskDuration.setText(task.getDuration()+" minutes");
            itemView.setBackgroundColor(mBackgroundColors.getColor(getAdapterPosition(),0));
            if(getAdapterPosition()%mBackgroundColors.length() >= TEXT_INVERSE_POSITION) {
                invertText();
            }
        }

        private void invertText() {
            TypedArray colorValues = itemView.getContext().getTheme().obtainStyledAttributes(R.style.AppTheme, textAttrs);
            int primaryText = colorValues.getColor(0, Color.WHITE);
            int secondaryText = colorValues.getColor(1, Color.WHITE);

            mTaskLabel.setTextColor(primaryText);
            mTaskDuration.setTextColor(secondaryText);
            mTaskDurationIcon.setImageDrawable(itemView.getResources().getDrawable(R.drawable.ic_schedule_white_24dp, itemView.getContext().getTheme()));
        }

    }

    static class ScheduleTasksViewHolder extends TaskViewHolder implements ItemTouchHelperViewHolder, ScheduleChangeListener {

        @BindView(R.id.taskBackground) View mTaskBackground;
        @BindView(R.id.taskStartTimeLabel) TextView mTaskStartTimeLabel;
        @BindView(R.id.taskTitleLabel) TextView mTaskTitleLabel;
        @BindView(R.id.taskDurationIcon) ImageView mTaskDurationIcon;
        @BindView(R.id.taskDurationLabel) TextView mTaskDurationLabel;

        protected final TasksAdapter mTasksAdapter;

        public ScheduleTasksViewHolder(ViewGroup parent, TasksAdapter tasksAdapter) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_task, parent, false));
            mTasksAdapter = tasksAdapter;
            ButterKnife.bind(this, itemView);
            mBackgroundColors = itemView.getContext().getResources().obtainTypedArray(R.array.palette);
        }

        @Override
        public void bind(Task task) {
            mTaskTitleLabel.setText(task.getLabel());
            mTaskDurationLabel.setText(formatMinutes(task.getDuration(), true));
            mTaskBackground.setBackgroundColor(mBackgroundColors.getColor(getAdapterPosition()%mBackgroundColors.length(),0));
            mTaskStartTimeLabel.setText(getTimeForPosition(getAdapterPosition(), mTasksAdapter));
            if(getAdapterPosition()%mBackgroundColors.length() >= TEXT_INVERSE_POSITION) {
                invertText();
            }
        }

        private void invertText() {
            TypedArray colorValues = itemView.getContext().getTheme().obtainStyledAttributes(R.style.AppTheme, textAttrs);
            int primaryText = colorValues.getColor(0, Color.WHITE);
            int secondaryText = colorValues.getColor(1, Color.WHITE);

            mTaskTitleLabel.setTextColor(primaryText);
            mTaskDurationLabel.setTextColor(secondaryText);
            mTaskDurationIcon.setImageDrawable(itemView.getResources().getDrawable(R.drawable.ic_schedule_white_24dp, itemView.getContext().getTheme()));
        }

        @Override
        public void onItemSelected() {

        }

        @Override
        public void onItemClear() {

        }

        @Override
        public void onScheduleChange() {
            mTaskStartTimeLabel.setText(getTimeForPosition(getAdapterPosition(), mTasksAdapter));
        }
    }

    static class ScheduleTasksViewHolderEditable extends ScheduleTasksViewHolder {

        public ScheduleTasksViewHolderEditable(ViewGroup parent, TasksAdapter tasksAdapter) {
            super((ViewGroup) LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_task_edit, parent, false), tasksAdapter);
        }

        @Override
        public void bind(Task task) {
            mTaskTitleLabel.setText(task.getLabel());
            // TODO Make a utility class for time parsing
            mTaskDurationLabel.setText(task.getDuration()+" minutes");
            mTaskStartTimeLabel.setText(getTimeForPosition(getAdapterPosition(), mTasksAdapter));
            mTaskBackground.setBackgroundResource(R.drawable.border);
        }
    }

    private interface ScheduleChangeListener{
        void onScheduleChange();
    }
}
