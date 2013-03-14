package com.fletch.gamescorekeeper.dialogs;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.fletch.gamescorekeeper.Player;
import com.fletch.gamescorekeeper.PlayerSpinnerAdapter;
import com.fletch.gamescorekeeper.R;
import com.fletch.gamescorekeeper.constants.Constants;
import com.fletch.gamescorekeeper.listeners.InputDialogListener;

public class SelectPlayerScoreInputDialogFragment extends DialogFragment implements Constants {

    private List<Player> playerList;

    @SuppressWarnings("unchecked")
    @Override
    public void setArguments(Bundle arguments) {

        playerList = (List<Player>) arguments.getSerializable(PLAYER_LIST);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.fragment_edit_score, null);

        final Spinner playerSpinner = (Spinner) view.findViewById(R.id.player_spinner);
        SpinnerAdapter spinnerAdapter = new PlayerSpinnerAdapter(getActivity(), playerList);
        playerSpinner.setAdapter(spinnerAdapter);
        playerSpinner.setAdapter(spinnerAdapter);

        final EditText pointsText = (EditText) view.findViewById(R.id.player_points_edit_text);

        final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(R.string.edit_score_title)
                .setMessage(R.string.dialog_select_player_edit)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        InputDialogListener activity = (InputDialogListener) getActivity();

                        activity.onFinishedSelectPlayerScoreInputDialog(
                                playerSpinner.getSelectedItemPosition(), pointsText.getText()
                                        .toString().trim());

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
                }).create();

        return dialog;
    }
}
