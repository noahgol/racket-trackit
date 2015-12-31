/**
 * Layout for this class taken from https://github.com/codepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView
 */

package com.example.noah.rackettrack_it;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * strokeadapter used to populate listview with rating bars (on enter report activity and on view report activity)
 */
public class StrokeAdapter extends ArrayAdapter<Stroke> {
    private boolean isIndicator;
    private static String[] STROKE_TYPES_SPACED = {"overall game", "forehand", "backhand", "first serve", "second serve", "net play"};
    public StrokeAdapter(Context context, ArrayList<Stroke> strokes, boolean indicate) {
        super(context, android.R.layout.simple_list_item_1, strokes);
        isIndicator = indicate;
    }

    // default constructor
    public StrokeAdapter(Context context, ArrayList<Stroke> strokes) {
        super(context, android.R.layout.simple_list_item_1, strokes);
        isIndicator = true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the stroke object for this position
        Stroke stroke = getItem(position);
        // Check if stroke represents the player (aka overall), or if it represents a specific stroke, choose layout accordingly
        if(stroke.getOverall()) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.overall_stroke, parent, false);
        }
        else {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.single_stroke, parent, false);
        }
        // Lookup view for data population
        // and Populate the data into the template view using the stroke object
        RatingBar power = (RatingBar) convertView.findViewById(R.id.power);
        power.setRating(stroke.getPower());
        power.setIsIndicator(isIndicator);
        RatingBar consistency = (RatingBar) convertView.findViewById(R.id.consistency);
        consistency.setRating(stroke.getConsistency());
        consistency.setIsIndicator(isIndicator);
        TextView header = (TextView) convertView.findViewById(R.id.header);
        header.setText(STROKE_TYPES_SPACED[position]);

        // populate additional fields if stroke represents overall player
        if(stroke.getOverall()) {
            RatingBar attack = (RatingBar) convertView.findViewById(R.id.attack);
            attack.setRating(stroke.getAttack());
            attack.setIsIndicator(isIndicator);
            RatingBar runSpeed = (RatingBar) convertView.findViewById(R.id.runSpeed);
            runSpeed.setRating(stroke.getRunSpeed());
            runSpeed.setIsIndicator(isIndicator);
        }

        // Return the completed view to render on screen
        return convertView;
    }
}
