package com.fletch.gamescorekeeper;

import java.io.Serializable;

public class Player implements Serializable {

    private static final long serialVersionUID = -6444967563449830757L;

    private int score;
    private String name;

    public Player(String name) {

        score = 0;
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
