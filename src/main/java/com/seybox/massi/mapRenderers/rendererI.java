package com.seybox.massi.mapRenderers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.map.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;

public class rendererI extends MapRenderer {
    BufferedImage image;

    public rendererI() {
        super();
        try {
            BufferedImage img = ImageIO.read(new URL("http://b-ssl.duitang.com/uploads/item/201605/07/20160507075418_ENPCv.thumb.700_0.jpeg"));
            image = new BufferedImage(128,128, img.getType());
            Graphics2D g = image.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(img,0,0,128,128,0,0,img.getWidth(),img.getHeight(),null);
            g.dispose();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(MapView mapView, MapCanvas mapCanvas, Player player) {
        /*try{
        BufferedImage image = ImageIO.read(new URL("http://img4.imgtn.bdimg.com/it/u=2512908468,229426930&fm=26&gp=0.jpg"));
        mapCanvas.drawImage(10,10,image);
        }catch (Exception e){
            Bukkit.getLogger().log(Level.ALL,e.getMessage());
        }*/
        for(int i=0;i<20;i++)
            mapCanvas.setPixel(10,i, MapPalette.RED);
        mapCanvas.drawImage(0,0,image);
    }
}
