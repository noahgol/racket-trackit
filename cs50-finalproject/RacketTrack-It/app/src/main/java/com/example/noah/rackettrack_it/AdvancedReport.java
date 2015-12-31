package com.example.noah.rackettrack_it;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;


public class AdvancedReport extends Activity {
    public static String PLAYER_ADVANCED_ID = "playerid";
    public static String REPORT_ADVANCED_ID = "reportid";
    public static String PLAYER_ADVANCED_NAME = "playername";
    private LinearLayout ll;
    private LinearLayout set1;
    private LinearLayout set2;
    private LinearLayout set3;
    private Button submitButton;
    private Switch winLoss;
    // nps contains the numberpicker objects for each of the numberpickers which display scores for each set
    private ArrayList<NumberPicker> nps = new ArrayList<NumberPicker>();
    // comments contains hte edittext objects for each of the fields where user can enter comments about each stroke
    private ArrayList<EditText> comments = new ArrayList<EditText>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_report);

        comments.add((EditText)findViewById(R.id.overallComments));
        final Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        final int reportId = bundle.getInt(REPORT_ADVANCED_ID);
        final int playerId = bundle.getInt(PLAYER_ADVANCED_ID);
        final String fullName = bundle.getString(PLAYER_ADVANCED_NAME);
        submitButton = (Button) findViewById(R.id.submitButton);
        ll = (LinearLayout) findViewById(R.id.advancedlinLayout);
        set1 = (LinearLayout) findViewById(R.id.set1);
        set2 = (LinearLayout) findViewById(R.id.set2);
        set3 = (LinearLayout) findViewById(R.id.set3);
        winLoss = (Switch) findViewById(R.id.winLoss);

        // populate nps with the number pickers for each set (to eventually use when inserting scores into database)
        for(LinearLayout set : new LinearLayout[] {set1, set2, set3}) {
            nps.add((NumberPicker)set.getChildAt(1));
            nps.add((NumberPicker)set.getChildAt(3));
        }

        for(NumberPicker np : nps) {
            np.setMaxValue(MainActivity.GAMES);
            np.setMinValue(0);
        }

        for(String s : PlayerReportOpenHelper.STROKE_TYPES_PRETTY) {
            View nextView = LayoutInflater.from(AdvancedReport.this).inflate(R.layout.comments, ll, false);
            TextView tv = (TextView) nextView.findViewById(R.id.commentName);
            tv.setText(s + " comments");
            comments.add((EditText)nextView.findViewById(R.id.strokeComment));
            ll.addView(nextView);
        }
        // set up the submit button on click listener
        submitButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                ContentValues cv = new ContentValues(PlayerReportOpenHelper.NUM_FIELDS);
                cv.put(PlayerReportOpenHelper.REPORTID, reportId);
                cv.put(PlayerReportOpenHelper.WINLOSS, winLoss.isChecked() ? "w" : "l");
                for(int i = 0; i < comments.size(); i ++) {
                    EditText et = comments.get(i);
                    cv.put(PlayerReportOpenHelper.STROKE_TYPES_BIG[i], et.getText().toString());
                }
                // run through nps in sets of 2 to enter data into cv
                for(int i = 0; i < nps.size(); i += 2) {
                    NumberPicker np1 = nps.get(i);
                    NumberPicker np2 = nps.get(i+1);
                    // store the score for each set in base 11 to decrease the number of fields
                    cv.put(PlayerReportOpenHelper.SETS[i / 2], np1.getValue() * (1 + MainActivity.GAMES) + np2.getValue());
                }
                PlayerReportOpenHelper reportHelper = new PlayerReportOpenHelper(AdvancedReport.this);
                SQLiteDatabase db = reportHelper.getWritableDatabase();
                db.update(PlayerReportOpenHelper.ADVANCED_TABLE_NAME, cv, " " + PlayerReportOpenHelper.REPORTID + " = " + reportId + " ", null);
                db.close();
                reportHelper.close();
                // redirect back to playerdetails
                Intent newIntent = new Intent(AdvancedReport.this, PlayerDetails.class);
                newIntent.putExtra(PlayerDetails.PLAYER_NAME, fullName);
                newIntent.putExtra(PlayerDetails.PLAYER_ID, playerId);
                startActivity(newIntent);
            }
        });

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
        Intent intent = MainActivity.menuOptions(id, AdvancedReport.this);
        if(intent == null) {
            return super.onOptionsItemSelected(item);
        }
        startActivity(intent);
        return true;
    }
}
