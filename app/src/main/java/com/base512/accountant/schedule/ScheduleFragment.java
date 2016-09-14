package com.base512.accountant.schedule;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.base512.accountant.R;
import com.base512.accountant.data.Task;
import com.base512.accountant.tasks.TasksAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class ScheduleFragment extends Fragment implements ScheduleContract.View, TaskDialogFragment.TaskLabelDialogHandler {

    private OnFragmentInteractionListener mListener;

    @BindView(R.id.scheduleView) RecyclerView mRecyclerView;
    private ScheduleContract.Presenter mPresenter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ScheduleFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ScheduleFragment newInstance() {
        ScheduleFragment fragment = new ScheduleFragment();
/*        Bundle args = new Bundle();
        fragment.setArguments(args);*/
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        ButterKnife.bind(this, view);
        mRecyclerView.setHasFixedSize(true);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void setLoadingIndicator(boolean active) {

    }

    @Override
    public void setTasksAdapter(TasksAdapter tasksAdapter) {
        mRecyclerView.setAdapter(tasksAdapter);
    }

    @Override
    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        mRecyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void showLoadingTasksError() {
        // TOOD set up tasks loading error indicator
    }

    @Override
    public void setTouchHelper(ItemTouchHelper helper) {
        helper.attachToRecyclerView(mRecyclerView);
    }

    @Override
    public void setNoTasksIndicator(boolean visible) {
        // TOOD set up no tasks indicator
    }

    @Override
    public void showAddTaskUI(TaskDialogFragment taskDialogFragment) {
        taskDialogFragment.show(getChildFragmentManager(), "addTask");
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void setPresenter(ScheduleContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onDialogTaskSet(Task task, int position, boolean isNew) {
        mPresenter.addTask(task, isNew);
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Task task);
    }
}
