package com.fletch.gamescorekeeper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcEvent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;

import com.fletch.gamescorekeeper.constants.Constants;
import com.fletch.gamescorekeeper.dialogs.Dialogs;
import com.fletch.gamescorekeeper.dialogs.InputDialogFragment;
import com.fletch.gamescorekeeper.dialogs.InputDialogType;
import com.fletch.gamescorekeeper.dialogs.NameInputDialogFragment;
import com.fletch.gamescorekeeper.dialogs.RemovePlayerDialogFragment;
import com.fletch.gamescorekeeper.dialogs.ScoreInputDialogFragment;
import com.fletch.gamescorekeeper.dialogs.SelectPlayerScoreInputDialogFragment;
import com.fletch.gamescorekeeper.listeners.InputDialogListener;
import com.fletch.gamescorekeeper.utils.NfcUtils;

public class ScoreBoardActivity extends FragmentActivity implements InputDialogListener, Constants,
        CreateNdefMessageCallback {

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
    NfcAdapter mNfcAdapter;
    private PendingIntent pendingIntent;
    private IntentFilter[] intentFilterArray;

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

        // Create the adapter that will return a fragment for each of the
        // players added.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        if(savedInstanceState != null) {
            mViewPager.setCurrentItem(savedInstanceState.getInt(CURRENT_POSITION));
        }

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(mNfcAdapter == null) { // NFC not available on this device.
            return;
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            // Register a callback for creating NDEF messages.
            mNfcAdapter.setNdefPushMessageCallback(this, this);
        }

        pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndef.addDataType("application/vnd.com.fletch.gamescorekeeper");
        } catch(MalformedMimeTypeException e) {
            Log.e("ScoreBoard", "Invalid filter...", e);
        }
        intentFilterArray = new IntentFilter[] { ndef };

        PagerTabStrip tabStrip = (PagerTabStrip) findViewById(R.id.pager_tab_strip);
        tabStrip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        tabStrip.setDrawFullUnderline(true);
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
            } else {
                getSelectPlayerPointsToAdd();
            }
            break;
        case R.id.delete_item:
            if(mViewPager.getCurrentItem() > 0) {
                removePlayer(mViewPager.getCurrentItem());
            } else {
                getSelectPlayerToRemove();
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

    @Override
    protected void onResume() {

        super.onResume();

        if(mNfcAdapter != null) {
            mNfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilterArray, null);
        }

        // Device supports NFC and activity was started by Android NFC beam
        if(mNfcAdapter != null && NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            processIntent(getIntent());
        }
    }

    @Override
    protected void onPause() {

        super.onPause();

        if(mNfcAdapter != null) {
            mNfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {

        // onResume method handles the NFC intent.
        setIntent(intent);
    }

    @SuppressLint("NewApi")
    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            String message;
            if(mViewPager.getCurrentItem() == 0) {
                message = NfcUtils.getPlayerListAsString(playerList);
            } else {
                message = NfcUtils
                        .getPlayerAsString(playerList.get(mViewPager.getCurrentItem() - 1));
            }
            NdefMessage ndefMessage = new NdefMessage(new NdefRecord[] {
                    NdefRecord.createMime("application/vnd.com.fletch.gamescorekeeper",
                            message.getBytes()),
                    NdefRecord.createApplicationRecord("com.fletch.gamescorekeeper") });

            return ndefMessage;
        }

        return null;
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

    /**
     * Displays an input dialog for the user to select a player to edit and add
     * or remove points for the selected player.
     */
    private void getSelectPlayerPointsToAdd() {

        SelectPlayerScoreInputDialogFragment dialog = new SelectPlayerScoreInputDialogFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(PLAYER_LIST, (Serializable) playerList);

        FragmentManager fragmentManager = getSupportFragmentManager();

        dialog.setArguments(arguments);
        dialog.show(fragmentManager, getString(R.string.edit_score_title));
    }

    /**
     * Displays an input dialog allowing the user to select a player to remove.
     */
    private void getSelectPlayerToRemove() {

        RemovePlayerDialogFragment dialog = new RemovePlayerDialogFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(PLAYER_LIST, (Serializable) playerList);

        FragmentManager fragmentManager = getSupportFragmentManager();

        dialog.setArguments(arguments);
        dialog.show(fragmentManager, getString(R.string.remove_player_title));
    }

    /**
     * Parses the received NDEF message and updates the player list (and thus
     * the scoreboard) accordingly.
     * 
     * @param intent
     *            NFC intent
     */
    private void processIntent(Intent intent) {

        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

        // only one message sent during the beam
        NdefMessage ndefMessage = (NdefMessage) rawMsgs[0];

        // record 0 contains the MIME type, record 1 is the AAR, if present
        List<Player> nfcPlayerList = NfcUtils.getPlayerListFromString(new String(ndefMessage
                .getRecords()[0].getPayload()));
        if(nfcPlayerList.size() == 1) {
            updatePlayerList(nfcPlayerList.get(0));
        } else {
            playerList = nfcPlayerList;
            sortPlayerList();
            invalidateOptionsMenu();
            mSectionsPagerAdapter.notifyDataSetChanged();
        }
    }

    private void updatePlayerList(Player player) {

        boolean found = false;
        for(int i = 0; i < playerList.size(); i++) {
            if(playerList.get(i).getName().equals(player.getName())) {
                found = true;
                int pointsToAdd = player.getScore() - playerList.get(i).getScore();
                editScore(i + 1, pointsToAdd);
                mViewPager.setCurrentItem(0);
            }
        }

        if(!found) {
            addPlayer(player.getName(), player.getScore());
        }
    }

    public List<Player> getPlayerList() {
        return this.playerList;
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

    @Override
    public void onFinishedSelectPlayerScoreInputDialog(int position, String inputText) {

        int pointsToAdd = 0;
        if(inputText.matches("^-?\\d+$")) {
            pointsToAdd = Integer.parseInt(inputText);
        } else {
            Dialogs.displayAlert(this, getString(R.string.edit_score_non_numeric_error));
            return;
        }

        editScore(position + 1, pointsToAdd);
        mViewPager.setCurrentItem(0);
    }

    @Override
    public void onFinishedSelectRemovePlayerInputDialog(int position) {

        removePlayer(position + 1);
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
     * Adds a new player with the given name.
     * 
     * @param name
     *            The name to identify the new player.
     */
    private void addPlayer(String name, int score) {

        Player player = new Player(name, score);
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
            Log.i("ScoreBoard", "GET ITEM - PLAYER LIST SIZE = " + playerList.size());
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
}
