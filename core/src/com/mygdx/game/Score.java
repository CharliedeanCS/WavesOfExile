package com.mygdx.game;

import com.badlogic.gdx.audio.Music;

//This class handles the player's score and progression through the levels
public class Score {

    int globalScore = 0; //refers to score across all levels (including time)
    int levelScore = 0; //refers to the score on the current level (needed to advance)
    int totalZombiesKilled = 0;
    int Level = 1;

    final int LEVEL2SCORE = 200; //score needed to advance to level 2
    final int LEVEL3SCORE = 500; //score needed to advance to level 3

    /**
     * Constructor for store class
     * @param level Level currently on (will be 1 by default)
     * @param levelScore Score of the level
     */
    public Score(int levelScore, int level)
    {
        this.globalScore = 0;
        this.levelScore = levelScore;
        this.totalZombiesKilled = 0;
        this.Level = level;
    }

    /**
     * Checks score to see if we can advance level
     * @param score Score the player reached so far
     */
    public int advanceLevel(int score) {
        if (score >= LEVEL2SCORE && Level == 1) {
            return 2;
        }
        if (score >= LEVEL3SCORE && Level == 2){
            return 3;
        }
        return 0;
    }

    //these are all as the name would indicate - note that global scores refers to score across all levels, local is for one level
    public int getGlobalScore() { return globalScore; }

    public void setGlobalScore(int score) { this.globalScore = score; }

    public int getLevelScore() { return levelScore; }

    public void setLevelScore(int score) { this.levelScore = score; }

    public int getTotalZombiesKilled() { return totalZombiesKilled; }

    public void setTotalZombiesKilled(int totalZombiesKilled) { this.totalZombiesKilled = totalZombiesKilled; }

    public int getLevel() { return Level; }

    public void setLevel(int level) { this.Level = level; }


}
