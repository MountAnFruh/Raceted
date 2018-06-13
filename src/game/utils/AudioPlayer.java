/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.utils;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioSource;
import java.util.HashMap;

/**
 *
 * @author Kevin Albrich
 */
public class AudioPlayer {

    private final AssetManager assetManager;
    private final HashMap<String, AudioNode> soundNodes = new HashMap<>();
    
    private AudioNode musicNode;
    
    public AudioPlayer(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    //Spielt Musik ab
    public void playMusic(String filename, boolean loop, float volume) {
        if(musicNode != null) return;
        
        musicNode = new AudioNode(assetManager, filename, AudioData.DataType.Buffer);
        musicNode.setName("Music");
        musicNode.play();
        musicNode.setVolume(volume);
        musicNode.setPositional(false);
        musicNode.setLooping(loop);
    }

    public void playSound(String name, String filename, boolean loop, float volume) {
        if(soundNodes.get(name) != null) return;
        
        AudioNode soundNode = new AudioNode(assetManager, filename, AudioData.DataType.Buffer);
        soundNode.setVolume(volume);
        soundNode.setName(name);
        soundNode.setLooping(loop);
        soundNodes.put(name, soundNode);
        soundNode.setPositional(false);
        soundNode.play();
    }
    
    public boolean isSoundPlaying(String name) {
        return soundNodes.get(name) != null;
    }
    
    public boolean isMusicPlaying() {
        return musicNode != null;
    }

    public void pauseMusic() {
        if(musicNode == null) return;
        if(musicNode.getStatus() == AudioSource.Status.Paused) return;
        musicNode.pause();
    }

    public void stopMusic() {
        if(musicNode == null) return;
        musicNode.stop();
        musicNode = null;
    }

    //Stoppt den Sound
    public void stopSound(String name) {
        if(soundNodes.get(name) == null) return;
        soundNodes.get(name).stop();
        soundNodes.remove(name);
    }

    public void stopAllSounds() {
        for (String name : soundNodes.keySet()) {
            stopSound(name);
        }
    }

}
