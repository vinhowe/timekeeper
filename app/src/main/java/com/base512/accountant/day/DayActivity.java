package com.base512.accountant.day;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.base512.accountant.AccountantApplication;
import com.base512.accountant.ApplicationModule;
import com.base512.accountant.R;
import com.base512.accountant.data.Task;
import com.base512.accountant.schedule.ScheduleActivity;
import com.base512.accountant.util.ActivityUtils;

import javax.inject.Inject;

public class DayActivity extends AppCompatActivity implements DayFragment.OnFragmentInteractionListener {

    @Inject DayPresenter mDayPresenter;


    public static void tintMenuIcon(Context context, MenuItem item, @ColorRes int color) {
        Drawable normalDrawable = item.getIcon();
        Drawable wrapDrawable = DrawableCompat.wrap(normalDrawable);
        DrawableCompat.setTint(wrapDrawable, ContextCompat.getColor(context, color));

        item.setIcon(wrapDrawable);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day);

        DayFragment dayFragment =
                (DayFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (dayFragment == null) {
            // Create the fragment
            dayFragment = DayFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), dayFragment, R.id.contentFrame);
        }

        // Create the presenter
        DaggerDayComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .repositoryComponent(((AccountantApplication) getApplication()).getTasksRepositoryComponent())
                .dayPresenterModule(new DayPresenterModule(dayFragment)).build()
                .inject(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem scheduleMenuItem = menu.findItem(R.id.action_schedule);

        if (scheduleMenuItem != null) {
            tintMenuIcon(DayActivity.this, scheduleMenuItem, android.R.color.white);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.action_schedule) {
            ActivityUtils.openActivity(this, ScheduleActivity.class);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onFragmentInteraction(Task task) {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
