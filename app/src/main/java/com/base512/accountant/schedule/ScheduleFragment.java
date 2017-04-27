package com.base512.accountant.schedule;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.base512.accountant.R;
import com.base512.accountant.data.Schedule;
import com.base512.accountant.data.Task;
import com.base512.accountant.tasks.TasksAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Fragment for modifying schedule
 */
public class ScheduleFragment extends Fragment implements ScheduleContract.View, TaskDialogFragment.TaskLabelDialogHandler, AdapterView.OnItemSelectedListener {

    //private OnFragmentInteractionListener mListener;
    private AdapterView.OnItemSelectedListener mSpinnerListener;

    @BindView(R.id.scheduleView) RecyclerView mRecyclerView;
    Spinner mSpinner;


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

        mSpinner = (Spinner) getActivity().findViewById(R.id.spinner_nav);
        mSpinner.setOnItemSelectedListener(this);

        mRecyclerView.setHasFixedSize(true);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        mPresenter.saveTasks();
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
    public void setSpinnerItems(String[] spinnerStrings) {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, spinnerStrings);
        mSpinner.setAdapter(spinnerAdapter);
    }

    @Override
    public void setSpinnerItemSelectedListener(AdapterView.OnItemSelectedListener spinnerItemSelectedListener) {
        mSpinnerListener = spinnerItemSelectedListener;
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
    public void onDialogTaskSet(Task task, boolean isNew) {
        mPresenter.addTask(task, isNew);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(mSpinnerListener != null) {
            mSpinnerListener.onItemSelected(adapterView, view, i, l);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // Don't think we care but this might be important
    }

    /*public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Task task);
    }*/


}
