package com.base512.accountant.schedule;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;

import com.base512.accountant.AccountantApplication;
import com.base512.accountant.ApplicationModule;
import com.base512.accountant.R;
import com.base512.accountant.data.Task;
import com.base512.accountant.util.ActivityUtils;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ScheduleActivity extends AppCompatActivity implements TaskDialogFragment.TaskLabelDialogHandler {

    @Inject SchedulePresenter mSchedulePresenter;

    private ScheduleFragment mScheduleFragment;

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.fabAddTask) FloatingActionButton mFAB;
    @BindView(R.id.spinner_nav) Spinner mSpinner;

    public static void tintMenuIcon(Context context, MenuItem item, @ColorRes int color) {
        Drawable normalDrawable = item.getIcon();
        Drawable wrapDrawable = DrawableCompat.wrap(normalDrawable);
        DrawableCompat.setTint(wrapDrawable, ContextCompat.getColor(context, color));

        item.setIcon(wrapDrawable);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mScheduleFragment =
                (ScheduleFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (mScheduleFragment == null) {
            // Create the fragment
            mScheduleFragment = ScheduleFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), mScheduleFragment, R.id.contentFrame);
        }


        final ScheduleFragment finalScheduleFragment = mScheduleFragment;
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSchedulePresenter.createTask();
            }
        });

        // Create the presenter
        DaggerScheduleComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .repositoryComponent(((AccountantApplication) getApplication()).getTasksRepositoryComponent())
                .schedulePresenterModule(new SchedulePresenterModule(mScheduleFragment)).build()
                .inject(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_schedule, menu);

        MenuItem scheduleMenuItem = menu.findItem(R.id.action_schedule);

        if (scheduleMenuItem != null) {
            tintMenuIcon(ScheduleActivity.this, scheduleMenuItem, android.R.color.white);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.app_bar_accept) {
            mSchedulePresenter.saveTasks();
            finish();
        } else {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onDialogTaskSet(Task task, int position, boolean isNew) {
        mScheduleFragment.onDialogTaskSet(task, position, isNew);
    }
}
