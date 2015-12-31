package com.example.noah.rackettrack_it;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * view a listView containing all players
 */

public class ViewPlayers extends Activity {
    public static String FIND_ALL = "findallplayers";
    public static String FIRST_NAME = "first-name";
    public static String LAST_NAME = "last-name";
    private ListView lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_players);

        lv = (ListView) findViewById(R.id.playersList);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        // find all players if the user clicked on find all players, otherwise just find players like the ones the user entered.
        if(extras.getBoolean(FIND_ALL)) {
            getAllPlayers(lv);
        }
        else{
            String first = extras.getString(FIRST_NAME);
            String last = extras.getString(LAST_NAME);
            getCertainPlayers(lv, first, last);
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle cer.licks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent intent = MainActivity.menuOptions(id, ViewPlayers.this);
        if(intent == null) {
            return super.onOptionsItemSelected(item);
        }
        startActivity(intent);
        return true;
    }

    /* displays all players in the database in the listview lv */
    private void getAllPlayers(ListView lv) {
        PlayerOpenHelper playerHelper = new PlayerOpenHelper(ViewPlayers.this);
        SQLiteDatabase db = playerHelper.getWritableDatabase();
        Cursor result = db.query(PlayerOpenHelper.PLAYER_TABLE_NAME, null, null, null, null, null, null, null);

        // stores names of each player, to display in the listView
        final ArrayList<String> names = new ArrayList<String>();
        // stores ids of each player, to pass on to PlayerDetails in case the player is clicked on
        final ArrayList<Integer> ids = new ArrayList<Integer> ();
        while(result.moveToNext()) {
            names.add(result.getString(result.getColumnIndex(PlayerOpenHelper.FIRSTNAME)) + " " + result.getString(result.getColumnIndex(PlayerOpenHelper.LASTNAME)));
            ids.add(result.getInt(result.getColumnIndex(PlayerOpenHelper.ID)));
        }
        result.close();
        db.close();

        // adapter renders the names in the listView
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ViewPlayers.this, android.R.layout.simple_list_item_1, names);
        lv.setAdapter(adapter);

        // set up onclicklistener so that the user is directed to PlayerDetails when they click on a player
        AdapterView.OnItemClickListener mMessageClickedHandler = new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                Intent intent = new Intent(ViewPlayers.this, PlayerDetails.class);
                intent.putExtra(PlayerDetails.PLAYER_NAME, names.get(position));
                intent.putExtra(PlayerDetails.PLAYER_ID, ids.get(position));
                startActivity(intent);
            }
        };

        lv.setOnItemClickListener(mMessageClickedHandler);
    }

    private void getCertainPlayers(ListView lv, String first, String last) {
        PlayerOpenHelper playerHelper = new PlayerOpenHelper(ViewPlayers.this);
        SQLiteDatabase db = playerHelper.getWritableDatabase();
        first = "'%" + first + "%'";
        last = "'%" + last + "%'";
        Cursor result = db.query(PlayerOpenHelper.PLAYER_TABLE_NAME, null, " " + PlayerOpenHelper.FIRSTNAME + " LIKE " +
                first + " AND " + PlayerOpenHelper.LASTNAME + " LIKE " + last + " ", null, null, null, null, null);

        // stores names of each player, to display in the listView
        final ArrayList<String> names = new ArrayList<String>();
        // stores ids of each player, to pass on to PlayerDetails in case the player is clicked on
        final ArrayList<Integer> ids = new ArrayList<Integer> ();
        while(result.moveToNext()) {
            names.add(result.getString(result.getColumnIndex(PlayerOpenHelper.FIRSTNAME)) + " " + result.getString(result.getColumnIndex(PlayerOpenHelper.LASTNAME)));
            ids.add(result.getInt(result.getColumnIndex(PlayerOpenHelper.ID)));
        }
        result.close();
        db.close();

        // adapter renders the names in the listView
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ViewPlayers.this, android.R.layout.simple_list_item_1, names);
        lv.setAdapter(adapter);

        // set up onclicklistener so that the user is directed to PlayerDetails when they click on a player
        AdapterView.OnItemClickListener mMessageClickedHandler = new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                Intent intent = new Intent(ViewPlayers.this, PlayerDetails.class);
                intent.putExtra(PlayerDetails.PLAYER_NAME, names.get(position));
                intent.putExtra(PlayerDetails.PLAYER_ID, ids.get(position));
                startActivity(intent);
            }
        };

        lv.setOnItemClickListener(mMessageClickedHandler);
    }
}
