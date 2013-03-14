package com.fletch.gamescorekeeper.listeners;

public interface InputDialogListener {

    public void onFinishedNameInputDialog(String inputText);

    public void onFinishedScoreInputDialog(String inputText);

    public void onFinishedSelectPlayerScoreInputDialog(int position, String inputText);

    public void onFinishedSelectRemovePlayerInputDialog(int position);
}
