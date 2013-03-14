package com.fletch.gamescorekeeper.utils;

import java.util.ArrayList;
import java.util.List;

import com.fletch.gamescorekeeper.Player;

public class NfcUtils {

    public static String getPlayerListAsString(List<Player> playerList) {

        String message = "";

        for(Player player : playerList) {
            message += player.getName() + "," + player.getScore() + ",";
        }

        return message.equals("") ? "" : message.substring(0, message.length() - 1);
    }

    public static String getPlayerAsString(Player player) {

        return player.getName() + "," + player.getScore();
    }

    public static List<Player> getPlayerListFromString(String message) {

        List<Player> playerList = new ArrayList<Player>();
        String[] messageParts = message.split(",");

        for(int i = 0; i < messageParts.length; i += 2) {
            String name = messageParts[i];
            int score = Integer.parseInt(messageParts[i + 1]);

            Player player = new Player(name, score);
            playerList.add(player);
        }

        return playerList;
    }

}
