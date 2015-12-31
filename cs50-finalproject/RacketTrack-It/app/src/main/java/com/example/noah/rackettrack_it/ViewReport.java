package com.example.noah.rackettrack_it;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * allows the user to view a specific report, or an average of all reports
 */

public class ViewReport extends Activity {
    public static String REPORT_ID = "reportid";
    public static String PLAYER_ID = "playerid";
    // public static String[] STROKE_TYPES = {"forehand", "backhand", "firstserve", "secondserve", "netplay"};
    private ListView lv;
    private Button deleteReport;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_report);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        // reportId stores the report id of the report that this activity displays, or -1 if this activity should display an average of all reports
        final Integer reportId = bundle.getInt(REPORT_ID);
        int tempId = -1;
        if(reportId == -1) {
            tempId = bundle.getInt(PLAYER_ID);
        }
        // playerId is only used if reportId == -1 (it is used to find all reports on the player with that playerId)
        final Integer playerId = tempId;

        // variables to display the report
        lv = (ListView) findViewById(R.id.strokesView);
        deleteReport = (Button) findViewById(R.id.deleteReportButton);

        // set onclicklistener of delete report
        deleteReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlayerReportOpenHelper reportOpenHelper = new PlayerReportOpenHelper(ViewReport.this);
                SQLiteDatabase dbDelete = reportOpenHelper.getWritableDatabase();
                // delete this report and the corresponding advanced portion in the advanced table
                // if reportID = -1, this will do nothing
                dbDelete.delete(PlayerReportOpenHelper.PLAYER_TABLE_NAME, " " + PlayerReportOpenHelper.ID + " = " + reportId.toString() + " ", null);
                dbDelete.delete(PlayerReportOpenHelper.ADVANCED_TABLE_NAME, " " + PlayerReportOpenHelper.REPORTID + " = " + reportId.toString() + " ", null);
                Intent intent = new Intent(ViewReport.this, ViewPlayers.class);
                // redirect to list of all players
                intent.putExtra(ViewPlayers.FIND_ALL, true);
                startActivity(intent);
            }
        });

        // arraylist of strokes to display in the form
        ArrayList<Stroke> strokes = new ArrayList<Stroke>();
        PlayerReportOpenHelper reportHelper = new PlayerReportOpenHelper(ViewReport.this);
        SQLiteDatabase db = reportHelper.getReadableDatabase();

        // will display a particular report
        if(reportId >= 0) {
            // query db to find report matching reportId
            Cursor result = db.query(PlayerReportOpenHelper.PLAYER_TABLE_NAME, null, " " + PlayerOpenHelper.ID + "  = " + reportId.toString() + " ", null, null, null, null, null);
            result.moveToNext();
            // first stroke represents overall player
            strokes.add(new Stroke(result.getInt(result.getColumnIndex(PlayerReportOpenHelper.POWER)), result.getInt(result.getColumnIndex(PlayerReportOpenHelper.CONSISTENCY)),
                    result.getInt(result.getColumnIndex(PlayerReportOpenHelper.ATTACK)), result.getInt(result.getColumnIndex(PlayerReportOpenHelper.RUNSPEED))));
            for (String s : PlayerReportOpenHelper.STROKE_TYPES) {
                // find power and consistency for each of the specific types of strokes
                int score = result.getInt(result.getColumnIndex(s));
                int power = (score % MainActivity.STARS) + 1;
                int consistency = (score / MainActivity.STARS) + 1;
                Stroke nextStroke = new Stroke(power, consistency);
                // add stroke to strokes
                strokes.add(nextStroke);
            }
        }
        // will display an average of all reports
        else {
            // query db to find all reports matching playerId
            Cursor result = db.query(PlayerReportOpenHelper.PLAYER_TABLE_NAME, null, " " + PlayerReportOpenHelper.PLAYERID + " = " + playerId.toString() + " ", null, null, null, null, null);
            // allstats stores the statistics to be put in each rating bar (for each of the specific stroke types, it has
            // 2 elements, one for power and one for consistency
            int[] allStats = new int[PlayerReportOpenHelper.NUM_OVERALL_RATINGS + 2 * PlayerReportOpenHelper.NUM_STROKES];
            // strokeTypes stores the field names for each of the overall ratings and stroke types, and is used to query the database
            String[] strokeTypes = new String[PlayerReportOpenHelper.NUM_OVERALL_RATINGS + PlayerReportOpenHelper.NUM_STROKES];
            int index = 0;
            for(String s : PlayerReportOpenHelper.OVERALL_RATINGS) {
                strokeTypes[index] = s;
                index++;
            }
            for(String s : PlayerReportOpenHelper.STROKE_TYPES) {
                strokeTypes[index] = s;
                index++;
            }
            int numReports = result.getCount();
            // for each row in result returned by db query, add up the ratings for each category in allStats
            while(result.moveToNext()) {
                int counter = 0;
                for(int i = 0; i < strokeTypes.length; i++) {
                    if(counter < PlayerReportOpenHelper.NUM_OVERALL_RATINGS) {
                        // overall statistics
                        allStats[counter] += result.getInt(result.getColumnIndex(strokeTypes[i]));
                    }
                    else {
                        // power and consistency for each specific type of stroke
                        int score = result.getInt(result.getColumnIndex(strokeTypes[i]));
                        int power = (score % MainActivity.STARS) + 1;
                        int consistency = (score / MainActivity.STARS) + 1;
                        allStats[counter] += power;
                        counter++;
                        allStats[counter] += consistency;
                    }
                    counter++;
                }
            }
            // round the averages to the nearest integer
            for(int i = 0; i < allStats.length; i++) {
                if(numReports > 0)
                    allStats[i] = allStats[i]/numReports;
                else
                    allStats[i] = 0; // if no reports entered yet, then display 0 for all fields
            }
            // populate strokes with the average values of the ratings for each type of stroke
            strokes.add(new Stroke(allStats[0], allStats[1], allStats[2], allStats[3]));
            int counter = PlayerReportOpenHelper.NUM_OVERALL_RATINGS;
            for (String s : PlayerReportOpenHelper.STROKE_TYPES) {
                int power = allStats[counter];
                int consistency = allStats[counter+1];
                counter += 2;
                Stroke nextStroke = new Stroke(power, consistency);
                strokes.add(nextStroke);
            }
        }

        // create adapter to populate listview with information in strokes
        StrokeAdapter strokeAdapter = new StrokeAdapter(ViewReport.this, strokes);
        lv.setAdapter(strokeAdapter);

        // only allow user to click on each stroke if not showing averages
        if(reportId >= 0) {
            AdapterView.OnItemClickListener mMessageClickedHandler = new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView parent, View v, int position, long id) {
                    // display advanced information for each stroke the user clicks on
                    Intent intent = new Intent(ViewReport.this, OverallViewAdvanced.class);
                    String strokeType = (position == 0) ? PlayerReportOpenHelper.OVERALL : PlayerReportOpenHelper.STROKE_TYPES[position - 1];
                    intent.putExtra(OverallViewAdvanced.ADVANCED_TYPE, strokeType);
                    intent.putExtra(OverallViewAdvanced.REPORT_ID, (int) reportId);
                    startActivity(intent);
                }
            };
            lv.setOnItemClickListener(mMessageClickedHandler);
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
        Intent intent = MainActivity.menuOptions(id, ViewReport.this);
        if(intent == null) {
            return super.onOptionsItemSelected(item);
        }
        startActivity(intent);
        return true;
    }
}
