package gameDev;

import gameDev.graphics.RenderUtils;
import gameDev.world.Area;
import gameDev.world.WorldBuilder;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Main entry point for Ronin Village game.
 */
public class Main {

    public static void setup() {
        RenderUtils.areas = new Area[]{WorldBuilder.buildVillage(), WorldBuilder.buildShrine()};
        for (Area a : RenderUtils.areas) {
            WorldBuilder.bakeGround(a);
        }
    }

    public static void screenshots() throws Exception {
        setup();
        Object[][] shots = {
            {0, 8.0, 16.4, 1, "area1_ashen_village.png"},
            {1, 14.4, 15.2, 1, "area2_hollow_shrine.png"},
        };
        for (Object[] s : shots) {
            Area a = RenderUtils.areas[(Integer) s[0]];
            RenderUtils.PX = (Double) s[1];
            RenderUtils.PY = (Double) s[2];
            RenderUtils.FACE = (Integer) s[3];
            RenderUtils.MOVING = false;
            RenderUtils.time = 1.4;

            double[] c = new double[2];
            RoninVillagePanel.snapCamera(c);
            RenderUtils.camX = (int) Math.round(c[0]);
            RenderUtils.camY = (int) Math.round(c[1]);

            BufferedImage lo = new BufferedImage(RenderUtils.LW, RenderUtils.LH, BufferedImage.TYPE_INT_RGB);
            RenderUtils.renderScene(lo, a, 0, 0);

            BufferedImage big = new BufferedImage(RenderUtils.LW * 3, RenderUtils.LH * 3, BufferedImage.TYPE_INT_RGB);
            Graphics2D bg = big.createGraphics();
            bg.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            bg.drawImage(lo, 0, 0, RenderUtils.LW * 3, RenderUtils.LH * 3, null);
            bg.dispose();

            ImageIO.write(big, "png", new File((String) s[4]));
            System.out.println("Wrote " + s[4]);
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length > 0 && args[0].equals("--screenshots")) {
            screenshots();
            return;
        }
        setup();
        SwingUtilities.invokeLater(() -> {
            Dimension scr = Toolkit.getDefaultToolkit().getScreenSize();
            int sc = (int) Math.max(1, Math.min(4, Math.floor(Math.min(scr.width * .92 / RenderUtils.LW, scr.height * .86 / RenderUtils.LH))));
            JFrame f = new JFrame("Ronin Village");
            RoninVillagePanel p = new RoninVillagePanel();
            p.setPreferredSize(new Dimension(RenderUtils.LW * sc, RenderUtils.LH * sc));
            f.setContentPane(p);
            f.pack();
            f.setLocationRelativeTo(null);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setVisible(true);
            p.requestFocusInWindow();
        });
    }
}
