package com.example.sudoku;

public class Score {
    private int gamesPlayed, gamesWon, gamesLost, topScore, lowScore;
    private double winRate, lossRate;
    private long quickestGame, longestGame;

    public Score() {

    }

    public void resetValues() {
        gamesPlayed = 0;
        gamesWon = 0;
        gamesLost = 0;
        topScore = 0;
        lowScore = 0;
        winRate = 0;
        lossRate = 0;
        quickestGame = 0;
        longestGame = 0;
    }

    public void calculateTimes(long time) {
        calculateQuickestTime(time);
        calculateLongestTime(time);
    }

    public void calculateQuickestTime(long time) {
        if((time > 0 && time <= quickestGame) || quickestGame <= 0)
            quickestGame = time;
    }

    public void calculateLongestTime(long time) {
        if(time >= longestGame)
            longestGame = time;
    }

    public long getQuickestGame() {
        return quickestGame;
    }

    public void setQuickestGame(long quickestGame) {
        this.quickestGame = quickestGame;
    }

    public long getLongestGame() {
        return longestGame;
    }

    public void setLongestGame(long longestGame) {
        this.longestGame = longestGame;
    }

    public void calculateWinRate() {
        winRate = (gamesWon / gamesPlayed) * 100;
    }

    public double getWinRate() {
        return winRate;
    }

    public void setWinRate(float winRate) {
        this.winRate = winRate;
    }

    public void calculateLossRate() {
        lossRate = (gamesLost / gamesPlayed) * 100;
    }

    public double getLossRate() {
        return lossRate;
    }

    public void setLossRate(float lossRate) {
        this.lossRate = lossRate;
    }

    public void incrementGamesPlayed() {
        gamesPlayed++;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public void incrementGamesWon() {
        gamesWon++;
    }

    public int getGamesWon() {
        return gamesWon;
    }

    public void setGamesWon(int gamesWon) {
        this.gamesWon = gamesWon;
    }

    public void incrementGamesLost() {
        gamesLost++;
    }

    public int getGamesLost() {
        return gamesLost;
    }

    public void setGamesLost(int gamesLost) {
        this.gamesLost = gamesLost;
    }

    public void calculateScores(int score) {
        if ((score > 0 && score <= lowScore) || lowScore <= 0)
            lowScore = score;
        if (score >= topScore)
             topScore = score;
    }

    public int getTopScore() {
        return topScore;
    }

    public void setTopScore(int topScore) {
        this.topScore = topScore;
    }

    public int getLowScore() {
        return lowScore;
    }

    public void setLowScore(int lowScore) {
        this.lowScore = lowScore;
    }

    public void calculateRates() {
        winRate = (double) gamesWon / gamesPlayed;
        winRate *= 100;
        lossRate = (double) gamesLost / gamesPlayed;
        lossRate *= 100;
    }
}
