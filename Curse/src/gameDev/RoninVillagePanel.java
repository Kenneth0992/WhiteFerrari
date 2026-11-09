package gameDev;

import gameDev.graphics.RenderUtils;
import gameDev.world.Area;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

public class RoninVillagePanel extends JPanel implements KeyListener {

    final BufferedImage buf = new BufferedImage(RenderUtils.LW, RenderUtils.LH, BufferedImage.TYPE_INT_RGB);
    final Set<Integer> keys = new HashSet<>();
    Area cur;
    double camXf, camYf;
    int trans = 0;
    double tTimer = 0, titleT = 3.2;
    int transTarget;
    long last = System.nanoTime();

    public RoninVillagePanel() {
        setPreferredSize(new Dimension(RenderUtils.LW * 3, RenderUtils.LH * 3));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        cur = RenderUtils.areas[0];
        RenderUtils.PX = 4.5;
        RenderUtils.PY = 16.2;
        double[] c = new double[2];
        snapCamera(c);
        camXf = c[0];
        camYf = c[1];
        new javax.swing.Timer(16, e -> {
            tick();
            repaint();
        }).start();
    }

    public static void snapCamera(double[] cam) {
        cam[0] = (RenderUtils.PX - RenderUtils.PY) * RenderUtils.TW / 2.0 - RenderUtils.LW / 2.0;
        cam[1] = (RenderUtils.PX + RenderUtils.PY) * RenderUtils.TH / 2.0 - (RenderUtils.LH / 2.0 + 24);
    }

    static boolean blocked(Area a, double x, double y) {
        if (x < .5 || y < .5 || x > RenderUtils.MAP - .5 || y > RenderUtils.MAP - .5) return true;
        double r = .22;
        for (double[] s : a.solids) {
            if (x > s[0] - r && x < s[0] + s[2] + r && y > s[1] - r && y < s[1] + s[3] + r) return true;
        }
        return false;
    }

    void tick() {
        long now = System.nanoTime();
        double dt = Math.min(.05, (now - last) / 1e9);
        last = now;
        RenderUtils.time += dt;
        if (titleT > 0) titleT -= dt;
        if (trans == 0) {
            double vx = 0, vy = 0;
            if (keys.contains(KeyEvent.VK_W) || keys.contains(KeyEvent.VK_UP)) vy -= 1;
            if (keys.contains(KeyEvent.VK_S) || keys.contains(KeyEvent.VK_DOWN)) vy += 1;
            if (keys.contains(KeyEvent.VK_A) || keys.contains(KeyEvent.VK_LEFT)) vx -= 1;
            if (keys.contains(KeyEvent.VK_D) || keys.contains(KeyEvent.VK_RIGHT)) vx += 1;
            RenderUtils.MOVING = vx != 0 || vy != 0;
            if (RenderUtils.MOVING) {
                if (vx != 0 && vy != 0) vy *= .5;
                double len = Math.hypot(vx, vy);
                vx /= len;
                vy /= len;
                boolean run = keys.contains(KeyEvent.VK_SHIFT);
                double sp = run ? 130 : 80;
                vx *= sp;
                vy *= sp;
                double dx = (vx / 16.0 + vy / 8.0) / 2 * dt, dy = (vy / 8.0 - vx / 16.0) / 2 * dt;
                if (!blocked(cur, RenderUtils.PX + dx, RenderUtils.PY)) RenderUtils.PX += dx;
                if (!blocked(cur, RenderUtils.PX, RenderUtils.PY + dy)) RenderUtils.PY += dy;
                if (vx > 1) RenderUtils.FACE = 1;
                else if (vx < -1) RenderUtils.FACE = -1;
                RenderUtils.WALK += dt * (run ? 1.5 : 1);
            }
            if (RenderUtils.PX >= cur.exX0 && RenderUtils.PX <= cur.exX1 && RenderUtils.PY >= cur.exY0 && RenderUtils.PY <= cur.exY1) {
                trans = 1;
                tTimer = 0;
                transTarget = cur.target;
                RenderUtils.MOVING = false;
            }
        } else {
            RenderUtils.MOVING = false;
            tTimer += dt;
            if (trans == 1 && tTimer >= .4) {
                Area from = cur;
                cur = RenderUtils.areas[transTarget];
                RenderUtils.PX = from.spawnX;
                RenderUtils.PY = from.spawnY;
                RenderUtils.FACE = (from.arrowDX > 0) ? 1 : -1;
                double[] c = new double[2];
                snapCamera(c);
                camXf = c[0];
                camYf = c[1];
                trans = 2;
                tTimer = 0;
                titleT = 3.2;
            } else if (trans == 2 && tTimer >= .4) trans = 0;
        }
        double[] tc = new double[2];
        snapCamera(tc);
        double k = 1 - Math.exp(-dt * 7);
        camXf += (tc[0] - camXf) * k;
        camYf += (tc[1] - camYf) * k;
    }

    @Override
    protected void paintComponent(Graphics gr) {
        super.paintComponent(gr);
        RenderUtils.camX = (int) Math.round(camXf);
        RenderUtils.camY = (int) Math.round(camYf);
        double fade = trans == 1 ? tTimer / .4 : trans == 2 ? 1 - tTimer / .4 : 0;
        RenderUtils.renderScene(buf, cur, fade, titleT > 2.4 ? 1 : titleT / 2.4);
        Graphics2D g2 = (Graphics2D) gr;
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        double sc = Math.min(getWidth() / (double) RenderUtils.LW, getHeight() / (double) RenderUtils.LH);
        int w = (int) (RenderUtils.LW * sc), h = (int) (RenderUtils.LH * sc);
        g2.drawImage(buf, (getWidth() - w) / 2, (getHeight() - h) / 2, w, h, null);
    }

    @Override public void keyPressed(KeyEvent e) { keys.add(e.getKeyCode()); if (e.getKeyCode() == KeyEvent.VK_ESCAPE) System.exit(0); }
    @Override public void keyReleased(KeyEvent e) { keys.remove(e.getKeyCode()); }
    @Override public void keyTyped(KeyEvent e) { }
}
