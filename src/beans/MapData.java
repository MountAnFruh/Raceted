/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Vector3f;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Andreas
 */
public class MapData {
    
    private List<BoundingBox> checkpoints;
    
    private BoundingBox start;
    
    public MapData() {
        this.checkpoints = new ArrayList<>();
    }

    public MapData(List<BoundingBox> checkpoints, BoundingBox start) {
        this.checkpoints = checkpoints;
        this.start = start;
    }

    public List<BoundingBox> getCheckpoints() {
        return checkpoints;
    }

    public void setCheckpoints(List<BoundingBox> checkpoints) {
        this.checkpoints = checkpoints;
    }

    public BoundingBox getStart() {
        return start;
    }

    public void setStart(BoundingBox start) {
        this.start = start;
    }
}
