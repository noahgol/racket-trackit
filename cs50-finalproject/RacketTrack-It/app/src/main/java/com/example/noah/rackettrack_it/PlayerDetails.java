package com.example.noah.rackettrack_it;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * displays the details (name, town, birthyear, and a list of reports) about a specific player
 */

public class PlayerDetails extends Activity {
    public static String PLAYER_NAME = "name";
    public static String PLAYER_ID = "id";

    private TextView nameText;
    private Button addReportButton;
    private Button editPlayerButton;
    private TextView openDetails;
    private ListView lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_details);

        nameText = (TextView) findViewById(R.id.playerName);
        addReportButton = (Button) findViewById(R.id.addReportButton);
        openDetails = (TextView) findViewById(R.id.openDetails);
        lv = (ListView) findViewById(R.id.reportsList);
        editPlayerButton = (Button) findViewById(R.id.editButton);

        Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();
        final String name = bundle.getString(PLAYER_NAME);
        nameText.setText(name);
        final Integer playerId = bundle.getInt(PLAYER_ID);

        // set onclicklisteners for editplayer and addreport buttons, passing along appropriate information (e.g. playerId)
        addReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PlayerDetails.this, AddPlayerReport.class);
                intent.putExtra(AddPlayerReport.PLAYER_REPORT_NAME, name);
                intent.putExtra(AddPlayerReport.PLAYER_REPORT_ID, bundle.getInt(PLAYER_ID));
                startActivity(intent);
            }
        });
        editPlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PlayerDetails.this, EditPlayer.class);
                intent.putExtra(EditPlayer.EDIT_PLAYER_ID, (int) playerId);
                startActivity(intent);
            }
        });

        // give the quick details about the player
        PlayerOpenHelper playerHelper = new PlayerOpenHelper(PlayerDetails.this);
        SQLiteDatabase db = playerHelper.getReadableDatabase();
        // query database for information about this player in particular
        Cursor result = db.query(PlayerOpenHelper.PLAYER_TABLE_NAME, new String[] {PlayerOpenHelper.GENDER, PlayerOpenHelper.HAND,
                PlayerOpenHelper.TOWN, PlayerOpenHelper.BIRTH}, " " + PlayerOpenHelper.ID + " = " + playerId.toString() + " ",  null, null, null, null, null);
        result.moveToNext();
        String gender = (result.getString(result.getColumnIndex(PlayerOpenHelper.GENDER)).charAt(0) == 'm') ? "male" : "female";
        String hand = (result.getString(result.getColumnIndex(PlayerOpenHelper.HAND)).charAt(0) == 'l') ? "lefty" : "righty";
        String town = result.getString(result.getColumnIndex(PlayerOpenHelper.TOWN));
        Integer birthyr = result.getInt(result.getColumnIndex(PlayerOpenHelper.BIRTH));
        result.close();
        db.close();
        // concatenate the information obtained above to display at the top of the activity
        openDetails.append(gender + " " + hand + " from " + town + " born in " + birthyr + ".");

        // displays all reports about the player
        getAllReports(lv, playerId);
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
        Intent intent = MainActivity.menuOptions(id, PlayerDetails.this);
        if(intent == null) {
            return super.onOptionsItemSelected(item);
        }
        startActivity(intent);
        return true;
    }

    // finds all reports corresponding to this player
    private void getAllReports(ListView lv, final Integer playerId) {
        PlayerReportOpenHelper reportHelper = new PlayerReportOpenHelper(PlayerDetails.this);
        SQLiteDatabase db = reportHelper.getReadableDatabase();
        // query database for all reports matching this playerId
        Cursor result = db.query(PlayerReportOpenHelper.PLAYER_TABLE_NAME, new String[] {PlayerReportOpenHelper.DT, PlayerReportOpenHelper.ID},
                " " + PlayerReportOpenHelper.PLAYERID + " = " + playerId.toString() + " ", null, null, null, null, null);
        // create array of dates and ids to store the date of each report and its id in the report table
        final ArrayList<String> dates = new ArrayList<String>();
        final ArrayList<Integer> ids = new ArrayList<Integer>();
        // populate dates and ids with the results from SQL query
        while(result.moveToNext()) {
            Date nextDate = new Date(result.getLong(result.getColumnIndex(PlayerReportOpenHelper.DT)));
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
            // display date in pretty format
            dates.add("report from " + dateFormat.format(nextDate));
            ids.add(result.getInt(result.getColumnIndex("id")));
        }
        result.close();
        db.close();

        // arrayadapter to populate the listview with the reports (the information shown is the date)
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(PlayerDetails.this, android.R.layout.simple_list_item_1, dates);
        lv.setAdapter(adapter);

        // create a header to link to the averages activity
        TextView tv = new TextView(PlayerDetails.this);
        tv.setText("View Averages");
        tv.setTextSize(30);
        tv.setTypeface(Typeface.create("sans-serif-thin", Typeface.NORMAL));
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        lv.addHeaderView(tv);

        // allow the user to click on any report (or on the average header) to see the corresponding report
        AdapterView.OnItemClickListener mMessageClickedHandler = new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                if(position > 0) {
                    // non-header item
                    Intent intent = new Intent(PlayerDetails.this, ViewReport.class);
                    intent.putExtra(ViewReport.REPORT_ID, ids.get(position - 1));
                    startActivity(intent);
                }
                else {
                    // header
                    Intent intent = new Intent(PlayerDetails.this, ViewReport.class);
                    intent.putExtra(ViewReport.REPORT_ID, -1);
                    intent.putExtra(ViewReport.PLAYER_ID, playerId);
                    startActivity(intent);
                }
            }
        };

        lv.setOnItemClickListener(mMessageClickedHandler);
    }
}
