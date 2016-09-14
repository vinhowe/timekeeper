package com.base512.accountant.day;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.base512.accountant.R;
import com.base512.accountant.data.Day;
import com.base512.accountant.data.Task;
import com.base512.accountant.tasks.TasksContract;
import com.base512.accountant.util.TimeUtils;
import com.squareup.phrase.Phrase;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DayFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DayFragment extends Fragment implements DayContract.View {

    private OnFragmentInteractionListener mListener;

    private DayContract.Presenter mPresenter;

    @BindView(R.id.dayUpcomingTasksView) RecyclerView mRecyclerView;

    @BindView(R.id.dayTaskTitle) TextView mCurrentTaskTitle;

    @BindView(R.id.dayTaskDuration) TextView mCurrentTaskDuration;

    @BindView(R.id.dayTaskAverageDuration) TextView mCurrentTaskAverageDuration;

    @BindView(R.id.toolbar) Toolbar mToolbar;

    @BindView(R.id.noUpcomingTasksLabel) TextView mNoUpcomingTasksLabel;

    public DayFragment() {
        // Required empty public constructor
    }

    public static DayFragment newInstance() {
        return new DayFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_day, container, false);
        ButterKnife.bind(this, root);
        mToolbar = (Toolbar) root.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(null);

        return root;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
        mPresenter.updateTime();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.stopUpdatingTime();
        //mPresenter.pauseTask();
        mPresenter.saveDay();
    }

    @Override
    public void setLoadingIndicator(boolean active) {

    }

    @Override
    public void showCurrentTask(Task task) {
        mCurrentTaskTitle.setText(task.getLabel());

        mCurrentTaskAverageDuration.setText("average duration stub");
    }

    @Override
    public void setUpcomingTaskAdapter(RecyclerView.Adapter<?> taskAdapter) {
        mRecyclerView.setAdapter(taskAdapter);
    }

    @Override
    public void showDayTaskDetailsUI(String taskId) {

    }

    @Override
    public void showLoadingUpcomingTasksError() {

    }

    @Override
    public void setNoUpcomingTasksIndicator(boolean visible) {
        mNoUpcomingTasksLabel.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        mRecyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public boolean isActive() {
        return this.isAdded();
    }

    @Override
    public void setTimeElapsed(long totalTime) {
        String currentTime = TimeUtils.formatMinutes(totalTime / 1000 / 60, true);
        mCurrentTaskDuration.setText(Phrase.from("{time} so far").put("time", currentTime).format());
    }

    @Override
    public void updateTimingRunnable(Runnable timeUpdateRunnable, long delay) {
        if(delay > 0) {
            mCurrentTaskDuration.postDelayed(timeUpdateRunnable, delay);
        } else {
            removeTimingRunnable(timeUpdateRunnable);
            mCurrentTaskDuration.post(timeUpdateRunnable);
        }
    }

    @Override
    public void removeTimingRunnable(Runnable timeUpdateRunnable) {
        mCurrentTaskDuration.removeCallbacks(timeUpdateRunnable);
    }

    @Override
    public void setPresenter(DayContract.Presenter presenter) {
        mPresenter = presenter;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Task task);

        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
