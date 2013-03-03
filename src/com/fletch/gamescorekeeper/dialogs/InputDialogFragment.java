package com.fletch.gamescorekeeper.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.fletch.gamescorekeeper.constants.Constants;
import com.fletch.gamescorekeeper.listeners.InputDialogListener;

/**
 * Input dialog for entering the name of a new player or entering a new score
 * for a given player.
 * 
 * @author Greg Fletcher
 */
public class InputDialogFragment extends DialogFragment implements Constants {

    protected int title;
    protected String message;
    protected InputDialogType dialogType;
    protected EditText inputField;

    @Override
    public void setArguments(Bundle arguments) {

        title = arguments.getInt(TITLE);
        message = arguments.getString(MESSAGE);
        String type = arguments.getString(DIALOG_TYPE);

        if(type.equals(InputDialogType.NAME.toString())) {
            dialogType = InputDialogType.NAME;
        } else if(type.equals(InputDialogType.SCORE.toString())) {
            dialogType = InputDialogType.SCORE;
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        InputDialogListener activity = (InputDialogListener) getActivity();

                        if(dialogType.equals(InputDialogType.NAME)) {
                            activity.onFinishedNameInputDialog(inputField.getText().toString()
                                    .trim());
                        } else if(dialogType.equals(InputDialogType.SCORE)) {
                            activity.onFinishedScoreInputDialog(inputField.getText().toString()
                                    .trim());
                        }

                        dialog.dismiss();
                        dialog.cancel();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                        dialog.cancel();
                    }
                }).setOnCancelListener(new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialog) {

                        dialog.dismiss();
                        dialog.cancel();
                    }
                }).setView(inputField).create();

        inputField.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if(hasFocus) {
                    dialog.getWindow().setSoftInputMode(
                            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        return dialog;
    }

}
