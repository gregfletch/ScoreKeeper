package com.fletch.gamescorekeeper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.fletch.gamescorekeeper.constants.Constants;
import com.fletch.gamescorekeeper.dialogs.Dialogs;
import com.fletch.gamescorekeeper.dialogs.InputDialogFragment;
import com.fletch.gamescorekeeper.dialogs.InputDialogType;
import com.fletch.gamescorekeeper.dialogs.NameInputDialogFragment;
import com.fletch.gamescorekeeper.dialogs.ScoreInputDialogFragment;
import com.fletch.gamescorekeeper.listeners.InputDialogListener;

public class ScoreBoardActivity extends FragmentActivity implements InputDialogListener, Constants {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
     * will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    private List<Player> playerList;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_board);
        if(savedInstanceState != null && savedInstanceState.getSerializable(PLAYER_LIST) != null) {
            playerList = (List<Player>) savedInstanceState.getSerializable(PLAYER_LIST);
        } else {
            playerList = new ArrayList<Player>();
        }

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        if(savedInstanceState != null) {
            mViewPager.setCurrentItem(savedInstanceState.getInt(CURRENT_POSITION));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.score_board, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        // If no players have been created yet, hide the edit and remove items.
        if(playerList == null || playerList.size() == 0) {
            MenuItem editItem = menu.findItem(R.id.edit_item);
            editItem.setVisible(false);
            editItem.setEnabled(false);

            MenuItem deleteItem = menu.findItem(R.id.delete_item);
            deleteItem.setVisible(false);
            deleteItem.setEnabled(false);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
        case R.id.add_player_item:
            getNewPlayerName();
            break;
        case R.id.edit_item:
            if(mViewPager.getCurrentItem() > 0) {
                getPointsToAdd();
            }
            break;
        case R.id.delete_item:
            if(mViewPager.getCurrentItem() > 0) {
                removePlayer(mViewPager.getCurrentItem());
            }
            break;
        default:
            break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        outState.putSerializable(PLAYER_LIST, (Serializable) playerList);
        outState.putInt(CURRENT_POSITION, mViewPager.getCurrentItem());
    }

    /**
     * Displays an input dialog for the user to enter the name of the new player
     * to add.
     */
    private void getNewPlayerName() {

        InputDialogFragment dialog = new NameInputDialogFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(TITLE, R.string.add_player_title);
        arguments.putString(MESSAGE, getString(R.string.add_player_message));
        arguments.putString(DIALOG_TYPE, InputDialogType.NAME.toString());

        FragmentManager fragmentManager = getSupportFragmentManager();

        dialog.setArguments(arguments);
        dialog.show(fragmentManager, getString(R.string.add_player_title));
    }

    /**
     * Displays an input dialog for the user to enter the number of points to
     * add or remove (if a negative number is entered).
     */
    private void getPointsToAdd() {

        Player player = playerList.get(mViewPager.getCurrentItem() - 1);

        InputDialogFragment dialog = new ScoreInputDialogFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(TITLE, R.string.edit_score_title);
        arguments.putString(MESSAGE, getString(R.string.edit_score_message, player.getName()));
        arguments.putString(DIALOG_TYPE, InputDialogType.SCORE.toString());

        FragmentManager fragmentManager = getSupportFragmentManager();

        dialog.setArguments(arguments);
        dialog.show(fragmentManager, getString(R.string.edit_score_title));
    }

    @Override
    public void onFinishedNameInputDialog(String inputText) {

        if(inputText == null || inputText.trim().equals("")) {
            Dialogs.displayAlert(this, getString(R.string.add_player_empty_name_error));
            return;
        }

        addPlayer(inputText);
    }

    @Override
    public void onFinishedScoreInputDialog(String inputText) {

        int pointsToAdd = 0;
        if(inputText.matches("^-?\\d+$")) {
            pointsToAdd = Integer.parseInt(inputText);
        } else {
            Dialogs.displayAlert(this, getString(R.string.edit_score_non_numeric_error));
            return;
        }

        editScore(mViewPager.getCurrentItem(), pointsToAdd);
    }

    /**
     * Adds a new player with the given name.
     * 
     * @param name
     *            The name to identify the new player.
     */
    private void addPlayer(String name) {

        Player player = new Player(name);
        playerList.add(player);
        sortPlayerList();
        invalidateOptionsMenu();
        mSectionsPagerAdapter.notifyDataSetChanged();
    }

    /**
     * Removes the current user.
     * 
     * @param position
     *            User to remove.
     */
    private void removePlayer(int position) {

        playerList.remove(position - 1);
        invalidateOptionsMenu();
        mSectionsPagerAdapter.notifyDataSetChanged();
    }

    /**
     * Changes the current user's score by adding the specified number of points
     * to their score
     * 
     * @param position
     *            User position.
     * @param pointsToAdd
     *            Number of points to add.
     */
    private void editScore(int position, int pointsToAdd) {

        Player player = playerList.get(position - 1);

        int currentScore = player.getScore();
        int newScore = currentScore + pointsToAdd;

        if(newScore < 0) {
            newScore = 0;
        }
        player.setScore(newScore);

        playerList.set(position - 1, player);
        sortPlayerList();
        mViewPager.setCurrentItem(getCurrentPlayerAfterSorting(player));
        invalidateOptionsMenu();
        mSectionsPagerAdapter.notifyDataSetChanged();
    }

    /**
     * Keep the list of players sorted by most points.
     */
    private void sortPlayerList() {

        Collections.sort(playerList, new Comparator<Player>() {

            @Override
            public int compare(Player player1, Player player2) {

                if(player1.getScore() > player2.getScore()) {
                    return -1;
                } else if(player1.getScore() < player2.getScore()) {
                    return 1;
                } else if(player1.getScore() == player2.getScore()) {
                    return player1.getName().compareTo(player2.getName());
                }

                return 0;
            }
        });
    }

    /**
     * Get the position of the current player after the list has been re-sorted,
     * as their position may have changed.
     * 
     * @param player
     *            The current player
     * @return current position
     */
    private int getCurrentPlayerAfterSorting(Player player) {

        for(int i = 0; i < playerList.size(); i++) {
            if(player.getName().equals(playerList.get(i).getName())) {
                return i + 1;
            }
        }
        return 0;
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            // getItem is called to instantiate the fragment for the given page.
            // Return a ScoreSectionFragment (defined as a static inner class
            // below) with the page number as its lone argument.
            Fragment fragment = new ScoreSectionFragment();
            Bundle args = new Bundle();
            args.putInt(ScoreSectionFragment.PLAYER_POSITION, position);
            args.putSerializable(ScoreSectionFragment.PLAYER_LIST, (Serializable) playerList);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {

            // One Tab per player plus 1 for the main score board.
            return (playerList == null) ? 1 : playerList.size() + 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            // Display the player name as the page/tab title.
            return (position == 0) ? getString(R.string.score_board_title) : playerList.get(
                    position - 1).getName();
        }

        @Override
        public int getItemPosition(Object object) {

            return POSITION_NONE;
        }
    }

    /**
     * A fragment representing an individual player which displays the player's
     * current score.
     */
    public static class ScoreSectionFragment extends Fragment {

        /**
         * The fragment argument representing the player whose score to show.
         */
        public static final String PLAYER_POSITION = "player_position";
        public static final String PLAYER_LIST = "player_list";

        public ScoreSectionFragment() {
        }

        @SuppressWarnings("unchecked")
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {

            int position = getArguments().getInt(PLAYER_POSITION);
            List<Player> playerList = (List<Player>) getArguments().getSerializable(PLAYER_LIST);

            if(position > 0) {
                return getPlayerView(inflater, container, position, playerList);
            } else {
                return getMainScoreBoardView(inflater, container, playerList);
            }
        }

        /**
         * Returns the view for the specified player which will display their
         * score.
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

            View rootView = inflater
                    .inflate(R.layout.fragment_score_board_player, container, false);
            TextView scoreTextView = (TextView) rootView.findViewById(R.id.score_label);

            scoreTextView.setText(Integer.toString(playerList.get(position - 1).getScore()));
            return rootView;
        }

        /**
         * Returns the view for the main scoreboard to display the overall
         * scores to the user.
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

            TableRow header = (TableRow) inflater
                    .inflate(R.layout.score_board_detail, table, false);

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
}
