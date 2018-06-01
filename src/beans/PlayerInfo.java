/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import game.main.appstates.GameAppState.Character;
import java.time.LocalTime;

/**
 *
 * @author Robbo13
 */
public class PlayerInfo {
    
    private Character character;
    private int points = 0;
    private LocalTime drivenTime = LocalTime.MAX;
    private boolean died = false;
 
    public PlayerInfo() {
    }

    public void setDied(boolean died) {
        this.died = died;
    }

    public void setDrivenTime(LocalTime drivenTime) {
        this.drivenTime = drivenTime;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void setCharacter(Character character) {
        this.character = character;
    }

    public Character getCharacter() {
        return character;
    }

    public LocalTime getDrivenTime() {
        return drivenTime;
    }

    public int getPoints() {
        return points;
    }
    
}
