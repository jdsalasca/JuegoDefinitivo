package com.juegodefinitivo.autobook.domain;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
    private int correctAnswers;
    private int discoveries;
    private boolean completed;
    private final Map<String, Integer> inventory;

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
        this.correctAnswers = 0;
        this.discoveries = 0;
        this.completed = false;
        this.inventory = new LinkedHashMap<>();
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

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = Math.max(0, correctAnswers);
    }

    public int getDiscoveries() {
        return discoveries;
    }

    public void setDiscoveries(int discoveries) {
        this.discoveries = Math.max(0, discoveries);
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public Map<String, Integer> getInventory() {
        return inventory;
    }

    public void replaceInventory(Map<String, Integer> newInventory) {
        this.inventory.clear();
        this.inventory.putAll(newInventory);
    }

    public void addInventoryItem(String itemId, int amount) {
        if (amount <= 0) {
            return;
        }
        inventory.merge(itemId, amount, Integer::sum);
    }

    public boolean removeInventoryItem(String itemId, int amount) {
        int current = inventory.getOrDefault(itemId, 0);
        if (amount <= 0 || current < amount) {
            return false;
        }
        int next = current - amount;
        if (next == 0) {
            inventory.remove(itemId);
        } else {
            inventory.put(itemId, next);
        }
        return true;
    }

    public void advanceScene() {
        currentScene++;
    }

    public void addLife(int delta) {
        setLife(life + delta);
    }

    public void addKnowledge(int delta) {
        setKnowledge(knowledge + delta);
    }

    public void addCourage(int delta) {
        setCourage(courage + delta);
    }

    public void addFocus(int delta) {
        setFocus(focus + delta);
    }

    public void addScore(int delta) {
        setScore(score + delta);
    }

    public void addCorrectAnswer() {
        setCorrectAnswers(correctAnswers + 1);
    }

    public void addDiscovery() {
        setDiscoveries(discoveries + 1);
    }

    public List<StoryQuest> evaluateQuests() {
        List<StoryQuest> quests = new ArrayList<>();
        quests.add(new StoryQuest(
                "reader",
                "Cronista aprendiz",
                "Responde correctamente 3 retos de lectura.",
                correctAnswers >= 3
        ));
        quests.add(new StoryQuest(
                "explorer",
                "Explorador curioso",
                "Descubre 2 objetos durante la aventura.",
                discoveries >= 2
        ));
        quests.add(new StoryQuest(
                "survivor",
                "Guardia del Reino",
                "Termina la historia con al menos 30 de vida.",
                completed && life >= 30
        ));
        return quests;
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
