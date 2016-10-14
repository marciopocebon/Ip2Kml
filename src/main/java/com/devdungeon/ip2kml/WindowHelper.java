/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.devdungeon.ip2kml;

import java.awt.Image;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author NanoDano <nanodano@devdungeon.com>
 */
public class WindowHelper {

    public static void setLookAndFeel() {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TipsWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TipsWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TipsWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TipsWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

    }

    public static List<Image> loadIcons() {
        List<Image> icons = new ArrayList<>();
        icons.add(loadIcon("/logos/devdungeon16x16.png"));
        icons.add(loadIcon("/logos/devdungeon32x32.png"));
        icons.add(loadIcon("/logos/devdungeon48x48.png"));
        icons.add(loadIcon("/logos/devdungeon64x64.png"));
        icons.add(loadIcon("/logos/devdungeon128x128.png"));
        icons.add(loadIcon("/logos/devdungeon256x256.png"));
        return icons;
    }

    public static Image loadIcon(String name) {
        Image icon = Toolkit.getDefaultToolkit().getImage(Main.class.getResource(name));
        return icon;
    }
}
