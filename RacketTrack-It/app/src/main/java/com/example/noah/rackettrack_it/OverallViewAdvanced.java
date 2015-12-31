package com.example.noah.rackettrack_it;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

/**
 * allows a user to view match score and/or comments (aka "advanced" information) pertaining to a certain report
 */

public class OverallViewAdvanced extends Activity {
    private TextView header;
    private TextView comments;
    public static String ADVANCED_TYPE = "stroketype";
    public static String REPORT_ID = "reportid";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overall_view_advanced);

        header = (TextView) findViewById(R.id.commentHeader);
        comments = (TextView) findViewById(R.id.comments);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String stroke = bundle.getString(ADVANCED_TYPE);
        int reportId = bundle.getInt(REPORT_ID);

        // read data from the advanced table corresponding to the reportid that was passed to this activity (by clicking on a certain item in ViewReport)
        PlayerReportOpenHelper reportHelper = new PlayerReportOpenHelper(OverallViewAdvanced.this);
        SQLiteDatabase db = reportHelper.getReadableDatabase();
        Cursor result = db.query(PlayerReportOpenHelper.ADVANCED_TABLE_NAME, null, " " + PlayerReportOpenHelper.REPORTID + " = " + reportId + " ", null, null, null, null, null);
        result.moveToNext();
        // the activity has several possibilities as to what it will display: if it is passed 'overall' as the stroke type, it will
        // display the match score and overall comments; otherwise, it will display the comments corresponding to
        // the stroke it was passed.
        if(!stroke.equals(PlayerReportOpenHelper.OVERALL)) {
            // will display comments corresponding to a specific stroke
            header.setText(stroke + " comments");
        }
        else {
            int firstset = result.getInt(result.getColumnIndex(PlayerReportOpenHelper.FIRSTSET));
            int secondset = result.getInt(result.getColumnIndex(PlayerReportOpenHelper.SECONDSET));
            int thirdset = result.getInt(result.getColumnIndex(PlayerReportOpenHelper.THIRDSET));
            // only display a score if the user enters nonzero scores for at least the first set
            if(firstset != 0) {
                // score holds the string that will be displayed to the user in the header TextView
                String score = "you ";
                if(result.getString(result.getColumnIndex(PlayerReportOpenHelper.WINLOSS)) == null)
                    score += "lost ";
                else
                    score += result.getString(result.getColumnIndex(PlayerReportOpenHelper.WINLOSS)).equals("w") ? "won " : "lost ";
                score += appendScore(firstset);
                if (secondset != 0) {
                    // only display scores for second and third sets if nonzero
                    score += "; ";
                    score += appendScore(secondset);
                    if(thirdset != 0) {
                        score += "; ";
                        score += appendScore(thirdset);
                    }
                }
                score += ".";
                header.setText(score);
            }
        }
        // set comments corresponding to the specific stroke (or overall)
        if(result.getString(result.getColumnIndex(stroke)) != null && (!result.getString(result.getColumnIndex(stroke)).isEmpty())) {
            comments.setText(result.getString(result.getColumnIndex(stroke)));
        }
    }

    private String appendScore(int set) {
        // scores were stored in the database as one integer (rather than 6 to 4, for instance) to decrease the number of fields
        // here the score is "broken up" again
        Integer first = set / (MainActivity.GAMES + 1);
        Integer second = set % (MainActivity.GAMES + 1);
        return first.toString() + "-" + second.toString();
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
        Intent intent = MainActivity.menuOptions(id, OverallViewAdvanced.this);
        if(intent == null) {
            return super.onOptionsItemSelected(item);
        }
        startActivity(intent);
        return true;
    }
}
