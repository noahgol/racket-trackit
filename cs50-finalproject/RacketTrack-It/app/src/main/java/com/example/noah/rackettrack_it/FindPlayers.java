package com.example.noah.rackettrack_it;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;


public class FindPlayers extends Activity {
    private EditText firstName;
    private EditText lastName;
    private Button findPlayerButton;
    private Button findAllButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_players);

        firstName = (EditText) findViewById(R.id.firstName);
        lastName = (EditText) findViewById(R.id.lastName);
        findPlayerButton = (Button) findViewById(R.id.namedPlayerButton);
        findAllButton = (Button) findViewById(R.id.allPlayersButton);

        findAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FindPlayers.this, ViewPlayers.class);
                intent.putExtra(ViewPlayers.FIND_ALL, true);
                startActivity(intent);
            }
        });

        findPlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String first = firstName.getText().toString().toLowerCase();
                String last = lastName.getText().toString().toLowerCase();
                Intent intent = new Intent(FindPlayers.this, ViewPlayers.class);
                intent.putExtra(ViewPlayers.FIRST_NAME, first);
                intent.putExtra(ViewPlayers.LAST_NAME, last);
                startActivity(intent);
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
        Intent intent = MainActivity.menuOptions(id, FindPlayers.this);
        if(intent == null) {
            return super.onOptionsItemSelected(item);
        }
        startActivity(intent);
        return true;
    }
}
