package com.fletch.gamescorekeeper.dialogs;

import android.app.Dialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.InputFilter;
import android.widget.EditText;

import com.fletch.gamescorekeeper.R;

public class ScoreInputDialogFragment extends InputDialogFragment {

    @Override
    public void setArguments(Bundle arguments) {

        super.setArguments(arguments);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        inputField = new EditText(getActivity());
        inputField.setId(1);
        inputField.setSingleLine();
        inputField.setFilters(new InputFilter[] { new InputFilter.LengthFilter(getResources()
                .getInteger(R.integer.max_score_length)) });
        inputField.setRawInputType(Configuration.KEYBOARD_QWERTY);

        return super.onCreateDialog(savedInstanceState);
    }
}
