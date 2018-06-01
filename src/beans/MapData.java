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
    
    private float minRouteHeight;
    private float outsideHeight;
    
    private List<BoundingBox> checkpoints;
    
    private BoundingBox start;
    
    public MapData() {
        this.checkpoints = new ArrayList<>();
    }

    public MapData(List<BoundingBox> checkpoints, BoundingBox start, float minRouteHeight, float outsideHeight) {
        this.checkpoints = checkpoints;
        this.start = start;
        this.minRouteHeight = minRouteHeight;
        this.outsideHeight = outsideHeight;
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

    public float getMinRouteHeight() {
        return minRouteHeight;
    }

    public void setMinRouteHeight(float minRouteHeight) {
        this.minRouteHeight = minRouteHeight;
    }

    public float getOutsideHeight() {
        return outsideHeight;
    }

    public void setOutsideHeight(float outsideHeight) {
        this.outsideHeight = outsideHeight;
    }
    
}
