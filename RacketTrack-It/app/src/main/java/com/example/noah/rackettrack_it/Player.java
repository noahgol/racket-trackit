package com.example.noah.rackettrack_it;

import java.util.ArrayList;

/**
 * Created by noah on 11/27/14.
 */
public class Player {
    private String name;
    private int birthyear;
    private boolean gender; // false = male, true = female
    private boolean hand; // false = left, true = right
    private byte attack; // 0 = defensive, 5 = offensive
    private byte consistency; // 0 = inconsistent, 5 = consistent
    private byte power; // 0 = soft, 5 = hits hard
    private byte runSpeed; // 0 = slow, 5 = fast
    private Stroke firstServe;
    private Stroke secondServe;
    private Stroke forehand;
    private Stroke backhand;
    private Stroke netPlay;
    private String comments;


    public String getName(){
        return name;
    }
    public int getBirthyear(){
        return birthyear;
    }
    public boolean getGender(){
        return gender;
    }
    public boolean getHand(){
        return hand;
    }
    public byte getAttack() { return attack; }
    public byte getConsistency() { return consistency; }
    public byte getPower() { return power; }
    public byte getRunSpeed() { return runSpeed; }
    public Stroke getFirstServe() { return firstServe; }
    public Stroke getSecondServe() { return secondServe; }
    public Stroke getForehand() { return forehand; }
    public Stroke getBackhand() { return backhand; }
    public Stroke getNetPlay() { return netPlay; }
    public String getComments() { return comments; }
}
