package com.example.noah.rackettrack_it;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class MainActivity extends Activity {
    private Button reportButton;
    private Button findReportButton;

    // global constants
    public static int STARS = 5;
    public static int GAMES = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        reportButton = (Button) findViewById(R.id.report_button);
        findReportButton = (Button) findViewById(R.id.findReportButton);

        reportButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddPlayer.class);
                startActivity(intent);
            }
        });

        findReportButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FindPlayers.class);
                startActivity(intent);
            }
        });
    }

    public static Intent menuOptions(int id, Context context) {
        Intent intent;
        switch(id) {
            case R.id.go_to_main:
                intent = new Intent(context, MainActivity.class);
                return intent;
            case R.id.add_player:
                intent = new Intent(context, AddPlayer.class);
                return intent;
            case R.id.find_players:
                intent = new Intent(context, ViewPlayers.class);
                intent.putExtra(ViewPlayers.FIND_ALL, true);
                return intent;
            default:
                return null;
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
        Intent intent = menuOptions(id, MainActivity.this);
        if(intent == null) {
            return super.onOptionsItemSelected(item);
        }
        startActivity(intent);
        return true;
    }
}
