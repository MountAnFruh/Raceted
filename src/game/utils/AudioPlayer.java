/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.utils;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Kevin
 */
public class AudioPlayer {

    private AudioNode audioSource;
    private AssetManager asset;
    private Stack<Integer> idstack = new Stack<>();
    private HashMap<Integer, AudioNode> effectlist = new HashMap<>();

    {
        for (int i = 0; i < 200; i++) {
            idstack.add(i);
        }

    }

    //Spielt Musik ab
    public void playDaMusic(AssetManager asset, String filename, boolean loop) {
        audioSource = new AudioNode(asset, filename, AudioData.DataType.Buffer);
        audioSource.setName("Music");
        audioSource.play();
        audioSource.setPositional(false);
        audioSource.setLooping(loop);

    }

    public int playDaSound(AssetManager asset, String filename, boolean loop) {
        int id = -1;
        while (idstack.isEmpty()) {
            System.out.println("wait");
            throw new IndexOutOfBoundsException();
        }
        id = idstack.pop();
        audioSource = new AudioNode(asset, filename, AudioData.DataType.Buffer);
        audioSource.setName(id + "");
        audioSource.setLooping(loop);
        effectlist.put(id, audioSource);
        audioSource.setPositional(false);
        audioSource.play();
        return id;
    }

    public void pauseDaMusic() {
        audioSource.pause();
    }

    public void stopDaMusic() {
        audioSource.stop();
    }

    //Stoppt den Sound
    public void stopDaSound(int id) {
        effectlist.get(id).stop();
        effectlist.remove(id);
        idstack.add(id);

    }

    public void stopAllSounds() {
        for (Map.Entry<Integer, AudioNode> entry : effectlist.entrySet()) {
            entry.getValue().stop();
            idstack.add(entry.getKey());
            idstack.notify();
            effectlist.remove(entry.getKey());

        }

    }

}
