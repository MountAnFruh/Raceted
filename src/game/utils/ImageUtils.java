/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.utils;

import com.jme3.math.ColorRGBA;
import com.jme3.texture.Image;
import com.jme3.texture.image.ColorSpace;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Andreas Fruhwirt
 */
public class ImageUtils {
    
    public static final String IMAGES_FOLDER = "Textures/Images/";
    
    public static final String RACETED_TEXT = IMAGES_FOLDER + "raceted_title.png";
    public static final String RACETED_ICON = IMAGES_FOLDER + "raceted_icon.png";
    
    /**
     * Manipulate a pixel inside an image
     * @param image to get/set the color on
     * @param x location
     * @param y location
     * @param color color to get/set
     * @param write to write the color or not
     */
    public static void manipulatePixel(Image image, int x, int y, ColorRGBA color, boolean write){
        ByteBuffer buf = image.getData(0);
        int width = image.getWidth();

        int position = (y * width + x) * 4;

        if ( position> buf.capacity()-1 || position<0 )
            return;
        
        buf.position( position );
        ColorRGBA bufColor = new ColorRGBA();
        
        switch (image.getFormat()){
            case RGBA8:
                bufColor.set(byte2float(buf.get()), byte2float(buf.get()), byte2float(buf.get()), byte2float(buf.get()));
                if(!write) {
                    color.set(bufColor);
                } else {
                    if(bufColor.equals(color)) return;
                    buf.put(float2byte(color.r))
                       .put(float2byte(color.g))
                       .put(float2byte(color.b))
                       .put(float2byte(color.a));
                }
                break;
            case ABGR8:
                float a = byte2float(buf.get());
                float b = byte2float(buf.get());
                float g = byte2float(buf.get());
                float r = byte2float(buf.get());
                bufColor.set(r,g,b,a);
                if(!write) {
                    color.set(bufColor);
                } else {
                    if(bufColor.equals(color)) return;
                    buf.put(float2byte(color.a))
                       .put(float2byte(color.b))
                       .put(float2byte(color.g))
                       .put(float2byte(color.r));
                }
                break;
            default:
                throw new UnsupportedOperationException("Unsupported Image format: "+image.getFormat());
        }
        image.setData(0, buf);
    }
    
    /**
     * Copies another Image
     * @param image the Image
     * @return exact copy of {@code image}
     */
    public static Image copyImage(Image image) {
        Image img = image.clone();
        ByteBuffer original = image.getData(0);
        ByteBuffer clone = ByteBuffer.allocateDirect(original.capacity());
        original.rewind();
        clone.put(original);
        original.rewind();
        clone.flip();
        img.setData(0, clone);
        return img;
    }

    private static float byte2float(byte b){
        return ((float)(b & 0xFF)) / 255f;
    }

    private static byte float2byte(float f){
        return (byte) (f * 255f);
    }
}