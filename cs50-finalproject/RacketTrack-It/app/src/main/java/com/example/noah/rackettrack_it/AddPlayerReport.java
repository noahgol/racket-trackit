package com.example.noah.rackettrack_it;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * allows the user to enter a report on a player
 * upon submitting, goes to AdvancedReport
 */
public class AddPlayerReport extends Activity {
    public static String PLAYER_REPORT_NAME = "name";
    public static String PLAYER_REPORT_ID = "id";
    private Button submitButton;
    private TextView nameText;
    private static int NUM_INPUTS = PlayerReportOpenHelper.NUM_STROKES + 1; // one input for each stroke, plus one for overall inputs
    private LinearLayout[] strokeLayouts = new LinearLayout[NUM_INPUTS];
    private LinearLayout ll;
    private RelativeLayout rl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_player_report);

        nameText = (TextView) findViewById(R.id.playerName);
        submitButton = (Button) findViewById(R.id.submitReportButton);
        ll = (LinearLayout) findViewById(R.id.reportForm);
        rl = (RelativeLayout) findViewById(R.id.relLayoutAddReport);

        // create array of strokes to display the report form with rating bars having all values initialized to 0
        ArrayList<Stroke> strokes = new ArrayList<Stroke>();
        strokes.add(new Stroke(0,0,0,0));
        for (String s : PlayerReportOpenHelper.STROKE_TYPES) {
            strokes.add(new Stroke(0,0));
        }

        StrokeAdapter strokeAdapter = new StrokeAdapter(AddPlayerReport.this, strokes, false);
        for(int i = 0, n = strokeAdapter.getCount(); i < n; i++) {
            View v = strokeAdapter.getView(i, null, ll);
            ll.addView(v);
        }


        // strokeLayouts store each of the sections on the form (each section corresponds to a particular stroke, or overall game)
        for(int i = 0; i < NUM_INPUTS; i ++) {
            strokeLayouts[i] = (LinearLayout) ll.getChildAt(i);
        }

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        final String name = bundle.getString(PLAYER_REPORT_NAME);
        final Integer id = bundle.getInt(PLAYER_REPORT_ID);
        nameText.setText(name);

        // enter data into database upon submission
        submitButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                // shouldSubmit tracks whether the input is valid
                boolean shouldSubmit = true;
                ContentValues cv = new ContentValues(PlayerReportOpenHelper.NUM_FIELDS);
                // must know which player the report corresponds to
                cv.put(PlayerReportOpenHelper.PLAYERID, id);
                // log the current date
                Date date = new Date();
                cv.put(PlayerReportOpenHelper.DT, date.getTime());

                // reads the user's input for the overall section of the form, stores data in cv
                // also checks for valid input
                for(int j = 2; j < 2 * PlayerReportOpenHelper.NUM_OVERALL_RATINGS + 1; j += 2) {
                    RatingBar rb = (RatingBar) strokeLayouts[0].getChildAt(j);
                    if(rb.getRating() == 0) {
                        shouldSubmit = false;
                    }
                    cv.put(PlayerReportOpenHelper.OVERALL_RATINGS[j/2 - 1], rb.getRating());
                }

                // reads the user's input for each of the specific strokes, stores data in cv
                // use base 5 to combine consistency and power to decrease the number of fields
                // also make sure user input is valid
                for(int i = 1; i < NUM_INPUTS; i ++) {
                    RatingBar consistency = (RatingBar) strokeLayouts[i].getChildAt(2);
                    RatingBar power = (RatingBar) strokeLayouts[i].getChildAt(4);
                    if(power.getRating() == 0 || consistency.getRating() == 0) {
                        shouldSubmit = false;
                    }
                    cv.put(PlayerReportOpenHelper.STROKE_TYPES[i - 1], MainActivity.STARS * (consistency.getRating() - 1) + (power.getRating() - 1));
                }
                if(shouldSubmit) {
                    // create helper and get access to database
                    PlayerReportOpenHelper reportHelper = new PlayerReportOpenHelper(AddPlayerReport.this);
                    SQLiteDatabase db = reportHelper.getWritableDatabase();
                    // newId stores the report Id, to be used in the follow form and below
                    int newId = (int) db.insert(PlayerReportOpenHelper.PLAYER_TABLE_NAME, null, cv);
                    db.close();
                    // insert row into advanced information database in case user does not submit next form by accident
                    // (the next form will then update this row)
                    SQLiteDatabase dbExtra = reportHelper.getWritableDatabase();
                    ContentValues cvExtra = new ContentValues(PlayerReportOpenHelper.NUM_FIELDS);
                    cvExtra.put(PlayerReportOpenHelper.REPORTID, newId);
                    dbExtra.insert(PlayerReportOpenHelper.ADVANCED_TABLE_NAME, null, cvExtra);
                    dbExtra.close();
                    reportHelper.close();
                    // go to the advanced information form, to give match score and comments about player
                    Intent intent = new Intent(AddPlayerReport.this, AdvancedReport.class);
                    intent.putExtra(AdvancedReport.PLAYER_ADVANCED_ID, id);
                    intent.putExtra(AdvancedReport.PLAYER_ADVANCED_NAME, name);
                    intent.putExtra(AdvancedReport.REPORT_ADVANCED_ID, newId);
                    startActivity(intent);
                }
                else {
                    // notify user of invalid input
                    AddPlayer.createPopup(rl, AddPlayerReport.this);
                }


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
        Intent intent = MainActivity.menuOptions(id, AddPlayerReport.this);
        if(intent == null) {
            return super.onOptionsItemSelected(item);
        }
        startActivity(intent);
        return true;
    }
}
