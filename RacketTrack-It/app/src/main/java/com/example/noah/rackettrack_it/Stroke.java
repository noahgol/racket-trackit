package com.example.noah.rackettrack_it;

/**
 * class to represent a stroke (used in StrokeAdapter to display reports)
 */
public class Stroke {
    private int power; // 1 = soft, 5 = hard
    private int consistency; // 1 = soft, 5 = hard
    private int attack; // 1 = defensive, 5 = aggressive
    private int runspeed; // 1 = slow, 5 = fast
    private boolean overall; // if overall is true, then all 4 above fields can have values (so stroke represents a whole player)
    // if overall is false, then only the first 2 fields can have values (so stroke represents a player's stroke)

    Stroke(int pow, int consis) {
        power = pow;
        consistency = consis;
        overall = false;
    }

    Stroke(int pow, int consis, int att, int run) {
        overall = true;
        power = pow;
        consistency = consis;
        attack = att;
        runspeed = run;
    }

    public int getPower() {
        return power;
    }
    public int getConsistency() {
        return consistency;
    }
    public int getAttack() {
        return overall ?  attack : 0;
    }
    public int getRunSpeed() {
        return overall ? runspeed : 0;
    }
    public boolean getOverall() {
        return overall;
    }
}
