/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.utils;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import com.sun.org.apache.bcel.internal.generic.AALOAD;
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
    private HashMap<Integer, AudioNode> playlist = new HashMap<>();

    {
        for (int i = 0; i < 200; i++) {
            idstack.add(i);
        }
    }

    //Spielt Musik ab
    public int playDaMusic(AssetManager asset, String filename) {
        int id = -1;
        while (idstack.isEmpty()) {
            try {
                idstack.wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(AudioPlayer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        id = idstack.pop();
        audioSource = new AudioNode(asset, "Sounds/", AudioData.DataType.Buffer);
        audioSource.setName(id + "");
        audioSource.play();
        playlist.put(id, audioSource);
        return id;
    }

    public void pauseDaMusic() {
        audioSource.pause();
    }

    //Stoppt die Musik
    public void stopDaMusic(int id) {
        playlist.get(id).stop();
        playlist.remove(id);
        idstack.notify();
        idstack.add(id);
    }

    public void stopAllMusics() {

        for (Map.Entry<Integer, AudioNode> entry : playlist.entrySet()) {
            entry.getValue().stop();
            idstack.add(entry.getKey());
            idstack.notify();
            playlist.remove(entry.getKey());

        }

    }

}
