package com.example.noah.rackettrack_it;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Switch;

/**
 * add a player to the database
 */

public class AddPlayer extends Activity {
    private Button submitButton;
    private EditText firstName;
    private EditText lastName;
    private EditText town;
    private EditText birthYear;
    private Switch gender;
    private Switch hand;
    private RelativeLayout rl;
    public static int MAX_YEAR = 9999; // must update in 7,085 years
    public static int MIN_YEAR = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_player);

        submitButton = (Button) findViewById(R.id.submit);
        firstName = (EditText) findViewById(R.id.firstName);
        lastName = (EditText) findViewById(R.id.lastName);
        town = (EditText) findViewById(R.id.town);
        birthYear = (EditText) findViewById(R.id.birthYear);
        gender = (Switch) findViewById(R.id.gender);
        hand = (Switch) findViewById(R.id.hand);
        rl = (RelativeLayout) findViewById(R.id.relLayoutAddPlayer);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlayerOpenHelper playerHelper = new PlayerOpenHelper(AddPlayer.this);
                SQLiteDatabase db = playerHelper.getWritableDatabase();
                ContentValues cv = new ContentValues(PlayerOpenHelper.NUM_FIELDS);
                // get values from the text fields, and check to make sure they are valid.
                String year = birthYear.getText().toString();
                if(year.length() == 0) {
                    createPopup(rl, AddPlayer.this);
                }
                else if(!(firstName.getText().toString().matches("[a-zA-Z_0-9]{1,15}") && lastName.getText().toString().matches("[a-zA-Z_0-9]{1,15}")
                        && town.getText().toString().matches("[a-zA-Z_0-9]{1,20}"))) {
                    createPopup(rl, AddPlayer.this);
                }
                else if(Integer.parseInt(year) < MIN_YEAR || Integer.parseInt(year) > MAX_YEAR) {
                    createPopup(rl, AddPlayer.this);
                }
                else {
                    // insert values into the databse, converting all fields to lowercase
                    cv.put(PlayerOpenHelper.FIRSTNAME, firstName.getText().toString().toLowerCase());
                    cv.put(PlayerOpenHelper.LASTNAME, lastName.getText().toString().toLowerCase());
                    cv.put(PlayerOpenHelper.TOWN, town.getText().toString().toLowerCase());
                    cv.put(PlayerOpenHelper.BIRTH, Integer.parseInt(birthYear.getText().toString()));
                    cv.put(PlayerOpenHelper.GENDER, gender.isChecked() ? "m" : "f");
                    cv.put(PlayerOpenHelper.HAND, hand.isChecked() ? "r" : "l");

                    db.insert(PlayerOpenHelper.PLAYER_TABLE_NAME, null, cv);
                    db.close();
                    // redirect to mainactivity
                    Intent intent = new Intent(AddPlayer.this, MainActivity.class);
                    startActivity(intent);
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
        Intent intent = MainActivity.menuOptions(id, AddPlayer.this);
        if(intent == null) {
            return super.onOptionsItemSelected(item);
        }
        startActivity(intent);
        return true;
    }

    // method taken from http://android-er.blogspot.in/2012/03/example-of-using-popupwindow.html
    public static void createPopup(ViewGroup parent, Context context) {
        View popupView = LayoutInflater.from(context).inflate(R.layout.activity_invalid_input_popup, parent, false);
        final PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        Button btnDismiss = (Button) popupView.findViewById(R.id.dismiss);
        btnDismiss.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }});
        popupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
    }
}
