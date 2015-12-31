package com.example.noah.rackettrack_it;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;

/**
 * allows a user to edit a players information (name, birth year, town, etc.)
 */

public class EditPlayer extends Activity {
    private Button submitButton;
    private EditText firstName;
    private EditText lastName;
    private EditText town;
    private EditText birthYear;
    private Switch gender;
    private Switch hand;
    private RelativeLayout rl;
    public static String EDIT_PLAYER_ID = "editplayerid";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_player);

        submitButton = (Button) findViewById(R.id.submit);
        firstName = (EditText) findViewById(R.id.firstName);
        lastName = (EditText) findViewById(R.id.lastName);
        town = (EditText) findViewById(R.id.town);
        birthYear = (EditText) findViewById(R.id.birthYear);
        gender = (Switch) findViewById(R.id.gender);
        hand = (Switch) findViewById(R.id.hand);
        rl = (RelativeLayout) findViewById(R.id.relLayoutEditPlayer);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        final int playerId = bundle.getInt(EDIT_PLAYER_ID);

        // look at the database to populate text fields
        final PlayerOpenHelper playerHelper = new PlayerOpenHelper(EditPlayer.this); // check the context
        SQLiteDatabase readDb = playerHelper.getReadableDatabase();
        Cursor result = readDb.query(PlayerOpenHelper.PLAYER_TABLE_NAME, null, " " + PlayerOpenHelper.ID + " = " + playerId + " ", null, null, null, null, null);
        result.moveToNext();
        firstName.setText(result.getString(result.getColumnIndex(PlayerOpenHelper.FIRSTNAME)));
        lastName.setText(result.getString(result.getColumnIndex(PlayerOpenHelper.LASTNAME)));
        town.setText(result.getString(result.getColumnIndex(PlayerOpenHelper.TOWN)));
        birthYear.setText(((Integer)result.getInt(result.getColumnIndex(PlayerOpenHelper.BIRTH))).toString());
        gender.setChecked(result.getString(result.getColumnIndex(PlayerOpenHelper.GENDER)).equals("m"));
        hand.setChecked(result.getString(result.getColumnIndex(PlayerOpenHelper.HAND)).equals("r"));
        readDb.close();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // update the database based on user input
                SQLiteDatabase db = playerHelper.getWritableDatabase();
                ContentValues cv = new ContentValues(PlayerOpenHelper.NUM_FIELDS);
                String year = birthYear.getText().toString();
                // validate user input
                if(year.length() == 0) {
                    AddPlayer.createPopup(rl, EditPlayer.this);
                    db.close();
                    playerHelper.close();
                }
                else if(!(firstName.getText().toString().matches("[a-zA-Z_0-9]{1,15}") && lastName.getText().toString().matches("[a-zA-Z_0-9]{1,15}")
                        && town.getText().toString().matches("[a-zA-Z_0-9]{1,20}"))) {
                    AddPlayer.createPopup(rl, EditPlayer.this);
                    db.close();
                    playerHelper.close();
                }
                else if(Integer.parseInt(year) < AddPlayer.MIN_YEAR || Integer.parseInt(year) > AddPlayer.MAX_YEAR) {
                    AddPlayer.createPopup(rl, EditPlayer.this);
                    db.close();
                    playerHelper.close();
                }
                else {
                    // insert user information into the database
                    cv.put(PlayerOpenHelper.FIRSTNAME, firstName.getText().toString().toLowerCase());
                    cv.put(PlayerOpenHelper.LASTNAME, lastName.getText().toString().toLowerCase());
                    cv.put(PlayerOpenHelper.TOWN, town.getText().toString().toLowerCase());
                    cv.put(PlayerOpenHelper.BIRTH, Integer.parseInt(birthYear.getText().toString()));
                    cv.put(PlayerOpenHelper.GENDER, gender.isChecked() ? "m" : "f");
                    cv.put(PlayerOpenHelper.HAND, hand.isChecked() ? "r" : "l");

                    db.update(PlayerOpenHelper.PLAYER_TABLE_NAME, cv, " " + PlayerOpenHelper.ID + " = " + playerId + " ", null);
                    db.close();
                    playerHelper.close();
                    Intent intent = new Intent(EditPlayer.this, MainActivity.class);
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
        Intent intent = MainActivity.menuOptions(id, EditPlayer.this);
        if(intent == null) {
            return super.onOptionsItemSelected(item);
        }
        startActivity(intent);
        return true;
    }
}
