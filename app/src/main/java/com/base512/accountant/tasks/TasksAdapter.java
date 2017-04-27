package com.base512.accountant.tasks;

import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.base512.accountant.R;
import com.base512.accountant.data.DataObject;
import com.base512.accountant.data.DayOfWeekSchedule;
import com.base512.accountant.data.Schedule;
import com.base512.accountant.data.Task;
import com.base512.accountant.data.User;
import com.base512.accountant.data.source.BaseDataSource;
import com.base512.accountant.data.source.tasks.TaskViewHolder;
import com.base512.accountant.data.source.user.UserRepository;
import com.base512.accountant.schedule.ScheduleContract;
import com.base512.accountant.schedule.SchedulePresenter;
import com.base512.accountant.views.ItemTouchHelperAdapter;
import com.base512.accountant.views.ItemTouchHelperViewHolder;

import org.joda.time.LocalTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

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

    private ArrayList<ScheduleInteractionListener> scheduleInteractionListeners;

    private ArrayList<ScheduleContract.View.RoutineModificationListener> routineModificationListeners;

    private static final String TAG = TasksAdapter.class.getSimpleName();

    public TasksAdapter(UserRepository userRepository) {
        scheduleChangeListeners = new ArrayList<>();
        scheduleInteractionListeners = new ArrayList<>();
        routineModificationListeners = new ArrayList<>();
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
            taskViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onScheduleTaskClicked(taskViewHolder.getAdapterPosition());
                }
            });
        }

        return taskViewHolder;
    }

    private void onScheduleTaskClicked(int adapterPosition) {
        Task selectedTask = mTasks.get(adapterPosition);
        if(!selectedTask.getId().isPresent()) {
            return;
        }
        for(ScheduleInteractionListener interactionListener : scheduleInteractionListeners) {

            interactionListener.onTaskClick(selectedTask);
        }
    }

    private void onScheduleTaskOptionsClicked(int adapterPosition) {
        for(ScheduleInteractionListener interactionListener : scheduleInteractionListeners) {
            Task selectedTask = mTasks.get(adapterPosition);
            if(!selectedTask.getId().isPresent()) {
                return;
            }
            interactionListener.onTaskOptionClick(selectedTask);
        }
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

/*    private void onTaskClicked(int adapterPosition) {
        //repositoriesListActivity.onRepositoryClick(repositories.get(adapterPosition));

    }*/

    public void addChangeListener(ScheduleChangeListener scheduleChangeListener) {
        scheduleChangeListeners.add(scheduleChangeListener);
    }

    public void addInteractionListener(ScheduleInteractionListener scheduleInteractionListener) {
        scheduleInteractionListeners.add(scheduleInteractionListener);
    }

    public void addRoutineModificationListener(ScheduleContract.View.RoutineModificationListener routineModificationListener) {
        routineModificationListeners.add(routineModificationListener);
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

        return taskTime.toString("HH:mm");
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mTasks, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        Iterator<ScheduleChangeListener> listenerIterator = scheduleChangeListeners.iterator();
        while(listenerIterator.hasNext()) {
            listenerIterator.next().onScheduleChanged();
        }
    }

    @Override
    public void onItemDismiss(int position) {
        notifyItemRemoved(position);
        Iterator<ScheduleChangeListener> listenerIterator = scheduleChangeListeners.iterator();
        while(listenerIterator.hasNext()) {
            listenerIterator.next().onScheduleChanged();
        }

        for (ScheduleContract.View.RoutineModificationListener modificationListener : routineModificationListeners) {
            modificationListener.onScheduleTaskRemoved(mTasks.get(position).getId().get());
        }

        mTasks.remove(position);
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
        public void bind(Task conditionalTask) {
            mTaskLabel.setText(conditionalTask.getLabel());
            mTaskDuration.setText(formatMinutes(conditionalTask.getDuration()));
            float timePosition = (Math.min(1, (float)conditionalTask.getDuration() / 60f) * mBackgroundColors.length())-1f;
            itemView.setBackgroundColor(mBackgroundColors.getColor((int)timePosition,0));
            if(timePosition >= TEXT_INVERSE_POSITION) {
                invertText(true);
            } else {
                invertText(false);
            }
        }

        private void invertText(boolean isInverted) {
            TypedArray colorValues = itemView.getContext().getTheme().obtainStyledAttributes(R.style.AppTheme, isInverted ? textAttrsInverse : textAttrs);
            int primaryText = colorValues.getColor(0, isInverted ? Color.WHITE : Color.BLACK);
            int secondaryText = colorValues.getColor(1, isInverted ? Color.WHITE : Color.BLACK);

            mTaskLabel.setTextColor(primaryText);
            mTaskDuration.setTextColor(secondaryText);
            mTaskDurationIcon.setImageDrawable(itemView.getResources().getDrawable(R.drawable.ic_hourglass_full_white_24dp, itemView.getContext().getTheme()));
            mTaskDurationIcon.setColorFilter(isInverted ? Color.WHITE : Color.BLACK);
        }

    }

    static class ScheduleTasksViewHolder extends TaskViewHolder implements ItemTouchHelperViewHolder, ScheduleChangeListener, View.OnClickListener {

        @BindView(R.id.taskBackground) View mTaskBackground;
        @BindView(R.id.taskStartTimeLabel) TextView mTaskStartTimeLabel;
        @BindView(R.id.taskTitleLabel) TextView mTaskTitleLabel;
        @BindView(R.id.taskDurationIcon) ImageView mTaskDurationIcon;
        @BindView(R.id.taskDurationLabel) TextView mTaskDurationLabel;
        @BindView(R.id.taskConditionLabel) TextView mTaskConditionLabel;
        @BindView(R.id.taskOptionsButton) ImageButton mTaskOptionsButton;

        protected final TasksAdapter mTasksAdapter;

        public ScheduleTasksViewHolder(ViewGroup parent, TasksAdapter tasksAdapter) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_task, parent, false));
            mTasksAdapter = tasksAdapter;
            ButterKnife.bind(this, itemView);
            mBackgroundColors = itemView.getContext().getResources().obtainTypedArray(R.array.palette);

        }

        @Override
        public void bind(final Task task) {
            mTaskTitleLabel.setText(task.getLabel());
            mTaskDurationLabel.setText(formatMinutes(task.getDuration()));
            Log.d(TAG, task.getLabel()+" "+task.getDuration());
            float timePosition = (Math.min(1, (float)task.getDuration() / 60f) * mBackgroundColors.length())-1f;
            mTaskBackground.setBackgroundColor(mBackgroundColors.getColor((int)timePosition,0));
            mTaskStartTimeLabel.setText(getTimeForPosition(getAdapterPosition(), mTasksAdapter));

            if(task.getSchedule() != null) {
                if(task.getSchedule() instanceof DayOfWeekSchedule) {
                    DayOfWeekSchedule weekSchedule = (DayOfWeekSchedule) task.getSchedule();
                    mTaskConditionLabel.setText(weekSchedule.getHumanSummary().toLowerCase());
                }
            } else {
                mTaskConditionLabel.setVisibility(View.GONE);
            }
            if(timePosition >= TEXT_INVERSE_POSITION) {
                invertText(true);
            } else {
                invertText(false);
            }

            mTaskOptionsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, task.getLabel()+" was clicked");
                    mTasksAdapter.onScheduleTaskOptionsClicked(getAdapterPosition());
                }
            });
        }

        private void invertText(boolean isInverted) {
            TypedArray colorValues = itemView.getContext().getTheme().obtainStyledAttributes(R.style.AppTheme, isInverted ? textAttrsInverse : textAttrs);
            int primaryText = colorValues.getColor(0, isInverted ? Color.WHITE : Color.BLACK);
            int secondaryText = colorValues.getColor(1, isInverted ? Color.WHITE : Color.BLACK);

            mTaskTitleLabel.setTextColor(primaryText);
            mTaskDurationLabel.setTextColor(secondaryText);
            mTaskDurationIcon.setImageDrawable(itemView.getResources().getDrawable(R.drawable.ic_hourglass_full_white_24dp, itemView.getContext().getTheme()));
            mTaskDurationIcon.setColorFilter(isInverted ? Color.WHITE : Color.BLACK);
        }

        @Override
        public void onItemSelected() {

        }

        @Override
        public void onItemClear() {

        }

        @Override
        public void onScheduleChanged() {
/*            Task task = mTasksAdapter.mTasks.get(getLayoutPosition());
            float timePosition = (Math.min(1, (float)task.getDuration() / 60f) * mBackgroundColors.length())-1f;
            mTaskBackground.setBackgroundColor(mBackgroundColors.getColor((int)timePosition,0));*/
            mTaskStartTimeLabel.setText(getTimeForPosition(getAdapterPosition(), mTasksAdapter));
            /*if(timePosition >= TEXT_INVERSE_POSITION) {
                invertText();
            }*/
        }

        @Override
        public void onClick(View view) {
            Log.d(TasksAdapter.class.getSimpleName(), "Clicked on "+mTasksAdapter.mTasks.get(getAdapterPosition()).getLabel());
        }
    }

    public interface ScheduleChangeListener{
        void onScheduleChanged();
    }

    public interface ScheduleInteractionListener {
        void onTaskClick(Task task);
        void onTaskOptionClick(Task task);
    }


}
