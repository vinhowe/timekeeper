/*
 * Copyright (C) 2016 Thomas Howe - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by Thomas Howe <tentothehundredthpower@gmail.com>, 8/2016
 */
package com.base512.accountant.schedule;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.TextView;
import com.base512.accountant.R;

import com.base512.accountant.data.DynamicTask;
import com.base512.accountant.data.Task;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.Gravity.TOP;
import static android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN;

/**
 * DialogFragment to edit label.
 */
public class TaskDialogFragment extends DialogFragment {

    private static final String KEY_LABEL = "label";
    private static final String KEY_TASK = "task";
    private static final String KEY_POSITION = "position";
    private static final String KEY_TASKS = "tasks";

    @BindView(R.id.taskTitleLabel) AutoCompleteTextView mLabelView;
    @BindView(R.id.taskDurationLabel) AppCompatEditText mDurationView;
    @BindView(R.id.taskDynamicCheckbox) CheckBox mDynamicCheckboxView;
    private Task mTask;
    private Task[] mTasks;
    private int mPosition;
    private boolean mEditingDuration = false;
    private boolean mIsUnique = true;

    public static TaskDialogFragment newInstance(Task task, int position, Task[] tasks) {
        final Bundle args = new Bundle();
        args.putParcelable(KEY_TASK, task);
        args.putParcelableArray(KEY_TASKS, tasks);
        args.putInt(KEY_POSITION, position);
        args.putString(KEY_LABEL, task.getLabel());

        final TaskDialogFragment frag = new TaskDialogFragment();
        frag.setArguments(args);
        return frag;
    }

    public static TaskDialogFragment newInstance(int position, Task[] tasks) {
        return newInstance(new Task("", 0), position, tasks);
    }

    public static TaskDialogFragment newInstance(Task[] tasks) {
        return newInstance(new Task("", 0), -1, tasks);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_LABEL, mLabelView.getText().toString());
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Bundle bundle = getArguments();
        mTask = bundle.getParcelable(KEY_TASK);
        mPosition = bundle.getInt(KEY_POSITION);
        mTasks = (Task[]) bundle.getParcelableArray(KEY_TASKS);

        final String label = savedInstanceState != null ?
                savedInstanceState.getString(KEY_LABEL) : bundle.getString(KEY_LABEL);

        final Context context = getActivity();

        View rootView = LayoutInflater.from(context).inflate(R.layout.schedule_task_edit, null);

        ButterKnife.bind(this, rootView);

        mLabelView.setOnEditorActionListener(new ImeDoneListener());
        mLabelView.addTextChangedListener(new TextChangeListener(context));
        mLabelView.setSingleLine();
        mLabelView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        mLabelView.setText(label);
        mLabelView.selectAll();

        mDurationView.addTextChangedListener(new DurationTextChangeListener(context));

        String[] taskLabels = new String[mTasks.length];
        for(int i = 0; i < mTasks.length; i++) {
            taskLabels[i] = mTasks[i].getLabel();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_dropdown_item_1line, taskLabels);

        mLabelView.setAdapter(adapter);

        mDurationView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    String digits = mDurationView.getText().toString().replaceAll("\\D", "");
                    mDurationView.setSelection(digits.length());
                }
            }
        });

        mDurationView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    String digits = mDurationView.getText().toString().replaceAll("\\D", "");
                    mDurationView.requestFocus();
                    mDurationView.setSelection(digits.length());
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    v.performClick();
                }
                return true;
            }
        });

                //final int padding = getResources().getDimensionPixelSize(R.dimen.label_edittext_padding);
        final AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setView(rootView)
                .setPositiveButton(R.string.dialog_set, new OkListener())
                .setNegativeButton(R.string.dialog_cancel, new CancelListener())
                .setMessage(R.string.addTaskLabel)
                .create();

        WindowManager.LayoutParams params = alertDialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = TOP;
        params.verticalMargin = 0.1f;
        alertDialog.getWindow().setAttributes(params);

        alertDialog.getWindow().setSoftInputMode(SOFT_INPUT_ADJUST_PAN);
        return alertDialog;
    }

    private void setProperties() {
        String label = mLabelView.getText().toString();
        boolean dynamic = mDynamicCheckboxView.isChecked();
        int duration = Integer.parseInt(mDurationView.getText().toString().substring(0, mDurationView.getText().toString().indexOf(" ")));
        if (label.trim().isEmpty()) {
            // Don't allow user to input label with only whitespace.
            label = "";
        }

        if (mTask != null) {
            if(mTask.getId().isPresent() && !mIsUnique) {
                ((TaskLabelDialogHandler) getActivity()).onDialogTaskSet(mTask, mPosition, mIsUnique);
            } else {
                mTask = dynamic ? new DynamicTask(label, duration) : new Task(label, duration);
                ((TaskLabelDialogHandler) getActivity()).onDialogTaskSet(mTask, mPosition, mIsUnique);
            }


        }
    }

    public interface TaskLabelDialogHandler {
        void onDialogTaskSet(Task task, int position, boolean isNew);
    }

    /**
     * Alters the UI to indicate when input is valid or invalid.
     */
    private class TextChangeListener implements TextWatcher {

        protected final int colorAccent;
        protected final int colorControlNormal;

        public TextChangeListener(Context context) {
            colorAccent = ContextCompat.getColor(context, R.color.colorAccent);
            colorControlNormal = ContextCompat.getColor(context, android.R.color.white);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            final int color = TextUtils.isEmpty(s) ? colorControlNormal : colorAccent;
            mLabelView.setBackgroundTintList(ColorStateList.valueOf(color));

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void afterTextChanged(Editable editable) {
            for(Task task : mTasks) {
                if(editable.toString().trim().toLowerCase().equals(task.getLabel().toLowerCase())) {
                    mDurationView.setEnabled(false);
                    mDynamicCheckboxView.setEnabled(false);
                    mIsUnique = false;
                    mTask = task;
                    return;
                }
            }
            mDurationView.setEnabled(true);
            mDynamicCheckboxView.setEnabled(true);
            mIsUnique = true;
        }
    }

    private class DurationTextChangeListener extends TextChangeListener {


        public DurationTextChangeListener(Context context) {
            super(context);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            final int color = TextUtils.isEmpty(s) ? colorControlNormal : colorAccent;
            mLabelView.setBackgroundTintList(ColorStateList.valueOf(color));

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void afterTextChanged(Editable editable) {
            if (!mEditingDuration) {
                mEditingDuration = true;
                String digits = editable.toString().replaceAll("\\D", "");
                digits = (digits.length() > 0) ? digits : "0";
                if(digits.length() > 1) {
                    // Remove leading 0s: http://stackoverflow.com/a/2800839/1979008
                    digits = digits.replaceFirst("^0+(?!$)", "");
                }
                // Save the digit length for selection after changing the string
                int digitsLength = digits.length();
                String minutesPlurality = (Integer.parseInt(digits) == 1) ? "minute" : "minutes";

                digits = !digits.contains(" "+minutesPlurality) ? (digits + " "+minutesPlurality) : digits;
                mDurationView.setText(digits);
                mDurationView.setSelection(digitsLength);
                    //s.replace(0, s.length()+1, formatted);
                mEditingDuration = false;
            }
        }
    }

    /**
     * Handles completing the label edit from the IME keyboard.
     */
    private class ImeDoneListener implements TextView.OnEditorActionListener {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                setProperties();
                dismiss();
                return true;
            }
            return false;
        }
    }

    /**
     * Handles completing the label edit from the Ok button of the dialog.
     */
    private class OkListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            setProperties();
            dismiss();
        }
    }

    /**
     * Handles discarding the label edit from the Cancel button of the dialog.
     */
    private class CancelListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dismiss();
        }
    }
}