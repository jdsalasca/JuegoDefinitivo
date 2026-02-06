package com.juegodefinitivo.autobook.domain;

public class GameSession {
    private String playerName;
    private String bookPath;
    private String bookTitle;
    private int currentScene;
    private int life;
    private int knowledge;
    private int courage;
    private int focus;
    private int score;
    private boolean completed;

    public GameSession() {
        this.playerName = "Aventurero";
        this.bookPath = "";
        this.bookTitle = "";
        this.currentScene = 0;
        this.life = 100;
        this.knowledge = 0;
        this.courage = 0;
        this.focus = 0;
        this.score = 0;
        this.completed = false;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getBookPath() {
        return bookPath;
    }

    public void setBookPath(String bookPath) {
        this.bookPath = bookPath;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public int getCurrentScene() {
        return currentScene;
    }

    public void setCurrentScene(int currentScene) {
        this.currentScene = Math.max(0, currentScene);
    }

    public int getLife() {
        return life;
    }

    public void setLife(int life) {
        this.life = clamp(life, 0, 100);
    }

    public int getKnowledge() {
        return knowledge;
    }

    public void setKnowledge(int knowledge) {
        this.knowledge = Math.max(0, knowledge);
    }

    public int getCourage() {
        return courage;
    }

    public void setCourage(int courage) {
        this.courage = Math.max(0, courage);
    }

    public int getFocus() {
        return focus;
    }

    public void setFocus(int focus) {
        this.focus = Math.max(0, focus);
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = Math.max(0, score);
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public void advanceScene() {
        this.currentScene++;
    }

    public void addLife(int delta) {
        setLife(this.life + delta);
    }

    public void addKnowledge(int delta) {
        setKnowledge(this.knowledge + delta);
    }

    public void addCourage(int delta) {
        setCourage(this.courage + delta);
    }

    public void addFocus(int delta) {
        setFocus(this.focus + delta);
    }

    public void addScore(int delta) {
        setScore(this.score + delta);
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
