package com.fletch.gamescorekeeper;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * A fragment representing an individual player which displays the player's
 * current score, as well as the main scoreboard.
 */
public class ScoreSectionFragment extends Fragment {

    /**
     * The fragment argument representing the player whose score to show.
     */
    public static final String PLAYER_POSITION = "player_position";
    public static final String PLAYER_LIST = "player_list";

    public ScoreSectionFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        int position = getArguments().getInt(PLAYER_POSITION);
        List<Player> playerList = ((ScoreBoardActivity) getActivity()).getPlayerList();

        if(position > 0) {
            return getPlayerView(inflater, container, position, playerList);
        } else {
            return getMainScoreBoardView(inflater, container, playerList);
        }
    }

    /**
     * Returns the view for the specified player which will display their score.
     * 
     * @param inflater
     *            Used to inflate the view layout
     * @param container
     *            View container
     * @param position
     *            User which to display
     * @param playerList
     *            List of players currently added
     * @return View
     */
    private View getPlayerView(LayoutInflater inflater, ViewGroup container, int position,
            List<Player> playerList) {

        View rootView = inflater.inflate(R.layout.fragment_score_board_player, container, false);
        TextView scoreTextView = (TextView) rootView.findViewById(R.id.score_label);

        scoreTextView.setText(Integer.toString(playerList.get(position - 1).getScore()));
        return rootView;
    }

    /**
     * Returns the view for the main scoreboard to display the overall scores to
     * the user.
     * 
     * @param inflater
     *            Used to inflate the view layout
     * @param container
     *            View container.
     * @param playerList
     *            List of players currently added
     * @return View
     */
    private View getMainScoreBoardView(LayoutInflater inflater, ViewGroup container,
            List<Player> playerList) {

        View view = inflater.inflate(R.layout.fragment_score_board, container, false);

        TableLayout table = (TableLayout) view.findViewById(R.id.main_scoreboard);
        addHeaders(inflater, table);

        if(playerList != null) {
            for(Player player : playerList) {
                addPlayerRow(inflater, table, player);
            }
        }

        return view;
    }

    /**
     * Adds the Name and Score headers to the main scoreboard.
     * 
     * @param inflater
     *            Used to inflate the view layout
     * @param table
     *            The scoreboard.
     */
    private void addHeaders(LayoutInflater inflater, TableLayout table) {

        TableRow header = (TableRow) inflater.inflate(R.layout.score_board_detail, table, false);

        TextView nameColumn = (TextView) header.findViewById(R.id.name);
        nameColumn.setText(R.string.score_board_name);
        nameColumn.setTextSize(25);

        TextView scoreColumn = (TextView) header.findViewById(R.id.score);
        scoreColumn.setText(R.string.score_board_score);
        scoreColumn.setTextSize(25);

        table.addView(header);
    }

    /**
     * Adds the player and their score to the main scoreboard.
     * 
     * @param inflater
     *            Used to inflate the view layout
     * @param table
     *            The scoreboard.
     * @param player
     *            Player to add to the scoreboard
     */
    private void addPlayerRow(LayoutInflater inflater, TableLayout table, Player player) {

        TableRow row = (TableRow) inflater.inflate(R.layout.score_board_detail, table, false);

        TextView nameColumn = (TextView) row.findViewById(R.id.name);
        nameColumn.setText(player.getName());
        nameColumn.setTextSize(25);

        TextView scoreColumn = (TextView) row.findViewById(R.id.score);
        scoreColumn.setText(Integer.toString(player.getScore()));
        scoreColumn.setTextSize(25);

        table.addView(row);
    }
}
