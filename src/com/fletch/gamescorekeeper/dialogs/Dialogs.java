package com.fletch.gamescorekeeper.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import com.fletch.gamescorekeeper.R;

public class Dialogs {

    private static Dialog constructAlertDialog(Context context, String message,
            OnClickListener listener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(android.R.string.dialog_alert_title).setIcon(R.drawable.ic_dialog_alert)
                .setMessage(message)
                .setPositiveButton(context.getString(android.R.string.ok), listener);

        return builder.show();
    }

    private static Dialog constructInfoDialog(Context context, String message, int title,
            OnClickListener listener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title).setIcon(R.drawable.ic_dialog_info).setMessage(message)
                .setPositiveButton(context.getString(android.R.string.ok), listener);

        return builder.show();
    }

    /**
     * Generate and display alert dialog with the given message and an OK
     * button.
     * 
     * @param context
     *            context in which to display the message
     * @param message
     *            Message to display
     * @return Dialog
     */
    public static Dialog displayAlert(Context context, String message) {

        return constructAlertDialog(context, message, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                dialog.cancel();
            }
        });
    }

    /**
     * Generate and display alert dialog with the given message and an OK
     * button.
     * 
     * @param context
     *            context in which to display the message
     * @param message
     *            Message to display
     * @return Dialog
     */
    public static Dialog displayInfo(Context context, String message, int title) {

        return constructInfoDialog(context, message, title, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                dialog.cancel();
            }
        });
    }
}
