package gameDev.graphics;

import gameDev.world.Area;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class RenderUtils {

    public static final int LW = 480, LH = 300;
    public static final int TW = 32, TH = 16;
    public static final int MAP = 32;

    public static Graphics2D g;
    public static int camX, camY;
    public static double time;
    public static final List<int[]> lights = new ArrayList<>();
    public static Area[] areas;
    public static BufferedImage mask = new BufferedImage(LW, LH, BufferedImage.TYPE_INT_ARGB);

    public static double PX, PY, WALK;
    public static int FACE = 1;
    public static boolean MOVING;
    public static int PSX, PSY;

    public static Color col(int rgb) { return new Color(rgb); }
    public static int cl(double v) { return (int) Math.max(0, Math.min(255, v)); }
    public static Color shade(Color c, double f) { return new Color(cl(c.getRed() * f), cl(c.getGreen() * f), cl(c.getBlue() * f), c.getAlpha()); }
    public static Color al(Color c, int a) { return new Color(c.getRed(), c.getGreen(), c.getBlue(), a); }
    public static Color mix(Color a, Color b, double t) {
        return new Color(cl(a.getRed() + (b.getRed() - a.getRed()) * t), cl(a.getGreen() + (b.getGreen() - a.getGreen()) * t), cl(a.getBlue() + (b.getBlue() - a.getBlue()) * t));
    }
    public static double hash(int x, int y, int s) {
        long h = x * 374761393L + y * 668265263L + s * 2147483647L;
        h = (h ^ (h >>> 13)) * 1274126177L;
        h ^= (h >>> 16);
        return (h & 0xffff) / 65535.0;
    }

    public static int sx(double x, double y) { return (int) Math.round((x - y) * TW / 2.0) - camX; }
    public static int sy(double x, double y, double z) { return (int) Math.round((x + y) * TH / 2.0 - z) - camY; }

    public static void quad(Color c, double... p) {
        int n = p.length / 3;
        int[] xs = new int[n], ys = new int[n];
        for (int i = 0; i < n; i++) { xs[i] = sx(p[3 * i], p[3 * i + 1]); ys[i] = sy(p[3 * i], p[3 * i + 1], p[3 * i + 2]); }
        g.setColor(c); g.fillPolygon(xs, ys, n); g.drawPolygon(xs, ys, n);
    }
    public static void fillq(Color c, double... p) {
        int n = p.length / 3;
        int[] xs = new int[n], ys = new int[n];
        for (int i = 0; i < n; i++) { xs[i] = sx(p[3 * i], p[3 * i + 1]); ys[i] = sy(p[3 * i], p[3 * i + 1], p[3 * i + 2]); }
        g.setColor(c); g.fillPolygon(xs, ys, n);
    }
    public static void ln(Color c, double x1, double y1, double z1, double x2, double y2, double z2) {
        g.setColor(c); g.drawLine(sx(x1, y1), sy(x1, y1, z1), sx(x2, y2), sy(x2, y2, z2));
    }
    public static void box(double x, double y, double w, double d, double z0, double h, Color base) {
        double z1 = z0 + h;
        quad(shade(base, .72), x, y + d, z0, x + w, y + d, z0, x + w, y + d, z1, x, y + d, z1);
        quad(shade(base, .50), x + w, y, z0, x + w, y + d, z0, x + w, y + d, z1, x + w, y, z1);
        quad(base, x, y, z1, x + w, y, z1, x + w, y + d, z1, x, y + d, z1);
        Color e = shade(base, .36);
        ln(e, x, y + d, z0, x + w, y + d, z0); ln(e, x + w, y + d, z0, x + w, y, z0);
        ln(e, x, y + d, z0, x, y + d, z1); ln(e, x + w, y + d, z0, x + w, y + d, z1); ln(e, x + w, y, z0, x + w, y, z1);
        Color t = shade(base, 1.28);
        ln(t, x, y + d, z1, x + w, y + d, z1); ln(t, x + w, y + d, z1, x + w, y, z1);
    }
    public static void wallL(double x, double y, double w, double d, double u0, double u1, double za, double zb, Color c) {
        quad(c, x + u0 * w, y + d, za, x + u1 * w, y + d, za, x + u1 * w, y + d, zb, x + u0 * w, y + d, zb);
    }
    public static void wallR(double x, double y, double w, double d, double u0, double u1, double za, double zb, Color c) {
        quad(c, x + w, y + u0 * d, za, x + w, y + u1 * d, za, x + w, y + u1 * d, zb, x + w, y + u0 * d, zb);
    }
    public static void shadowOval(int cx, int cy, int rx, int ry) {
        g.setColor(new Color(0, 0, 0, 70)); g.fillOval(cx - rx, cy - ry, rx * 2, ry * 2);
    }
    public static void light(double x, double y, double z, int r, int rgb) {
        lights.add(new int[]{sx(x, y), sy(x, y, z), r, rgb});
    }

    public static void blit(int a, int b, int r0, int r1, int dy) {
        int ax = PlayerSpriteLoader.SPR_AX;
        if (FACE > 0) g.drawImage(PlayerSpriteLoader.SPRITE, PSX - ax + a, dy + r0, PSX - ax + b, dy + r1, a, r0, b, r1, null);
        else g.drawImage(PlayerSpriteLoader.SPRITE, PSX + ax - a + 1, dy + r0, PSX + ax - b + 1, dy + r1, a, r0, b, r1, null);
    }

    public static void drawPlayer() {
        PSX = sx(PX, PY); PSY = sy(PX, PY, 0);
        int w = PlayerSpriteLoader.SPRITE.getWidth(), h = PlayerSpriteLoader.SPRITE.getHeight();
        int step = ((int) (WALK * 8)) % 2;
        int bob = (MOVING && step == 0) ? -1 : 0;
        int y0 = PSY - h + 2;
        shadowOval(PSX, PSY - 1, 11, 3);
        int liftL = (MOVING && step == 0) ? 2 : 0, liftR = (MOVING && step == 1) ? 2 : 0;
        blit(0, PlayerSpriteLoader.SPR_AX, PlayerSpriteLoader.SPR_LEG, h, y0 - liftL);
        blit(PlayerSpriteLoader.SPR_AX, w, PlayerSpriteLoader.SPR_LEG, h, y0 - liftR);
        blit(0, w, 0, PlayerSpriteLoader.SPR_LEG + 1, y0 + bob);
        light(PX, PY, 18, 34, 0xdfe3ff);
    }

    public static boolean behind(Area.Prop j, Area.Prop i) {
        boolean f = j.x1 <= i.x0 + 1e-6 || j.y1 <= i.y0 + 1e-6;
        boolean r = i.x1 <= j.x0 + 1e-6 || i.y1 <= j.y0 + 1e-6;
        return f && !r;
    }
    public static void paintSorted(Area a) {
        List<Area.Prop> vis = new ArrayList<>();
        for (Area.Prop p : a.props) {
            int cx = sx((p.x0 + p.x1) / 2, (p.y0 + p.y1) / 2), cy = sy((p.x0 + p.x1) / 2, (p.y0 + p.y1) / 2, 0);
            if (cx < -150 || cx > LW + 150 || cy < -60 || cy > LH + 240) continue;
            vis.add(p);
        }
        vis.add(new Area.Prop(PX - .2, PY - .2, PX + .2, PY + .2, RenderUtils::drawPlayer));
        vis.sort(Comparator.comparingDouble(p -> p.x0 + p.x1 + p.y0 + p.y1));
        int n = vis.size();
        int[] st = new int[n];
        for (int i = 0; i < n; i++) visit(vis, st, i);
    }
    public static void visit(List<Area.Prop> v, int[] st, int i) {
        if (st[i] != 0) return;
        st[i] = 1;
        for (int j = 0; j < v.size(); j++) if (j != i && st[j] == 0 && behind(v.get(j), v.get(i))) visit(v, st, j);
        st[i] = 2;
        v.get(i).p.paint();
    }

    public static final double[][] EMB = new double[80][5];
    static { Random r = new Random(5); for (double[] e : EMB) { e[0] = r.nextDouble() * (LW + 20); e[1] = r.nextDouble() * LH; e[2] = 4 + r.nextDouble() * 10; e[3] = 6 + r.nextDouble() * 12; e[4] = r.nextDouble(); } }

    public static void drawBackground() {
        for (int y = 0; y < LH; y += 6) {
            double t = y / (double) LH;
            g.setColor(mix(col(0x120d17), col(0x3a1f2a), t));
            g.fillRect(0, y, LW, 6);
        }
        Random r = new Random(3);
        for (int i = 0; i < 50; i++) { g.setColor(new Color(200, 180, 190, 40 + r.nextInt(60))); g.fillRect(r.nextInt(LW), r.nextInt(LH / 2), 1, 1); }
    }

    public static void drawFog() {
        for (int b = 0; b < 5; b++) {
            int y = 150 + b * 36;
            for (int x = -8; x < LW + 8; x += 8) {
                double w = Math.sin((x + time * (5 + b * 2)) * .035 + b * 1.7) * .5 + .5;
                int al = (int) (8 + w * 20);
                g.setColor(new Color(150, 120, 130, al));
                g.fillRect(x, y + (int) (Math.sin(x * .05 + b) * 4), 8, 14);
            }
        }
    }

    public static void lightPass() {
        Graphics2D m = mask.createGraphics();
        m.setComposite(AlphaComposite.Src); m.setColor(new Color(8, 4, 18, 125)); m.fillRect(0, 0, LW, LH);
        m.setComposite(AlphaComposite.DstOut);
        for (int i = 0; i < lights.size(); i++) {
            int[] L = lights.get(i);
            int r = (int) (L[2] * (1 + Math.sin(time * 7 + i * 1.9) * .04));
            for (int k = 0; k < 5; k++) { int rr = r * (5 - k) / 5; m.setColor(new Color(0, 0, 0, 70)); m.fillOval(L[0] - rr, L[1] - rr, rr * 2, rr * 2); }
        }
        m.dispose();
        g.drawImage(mask, 0, 0, null);
        for (int[] L : lights) {
            Color c = col(L[3]);
            for (int k = 0; k < 4; k++) { int rr = L[2] * (4 - k) / 4; g.setColor(al(c, 10)); g.fillOval(L[0] - rr, L[1] - rr, rr * 2, rr * 2); }
        }
    }

    public static void drawEmbers() {
        for (double[] e : EMB) {
            double x = (e[0] + time * e[2] * .6 + Math.sin(time * 1.3 + e[4] * 9) * 6) % (LW + 20) - 10;
            double y = ((e[1] - time * e[3]) % LH + LH) % LH;
            int a = (int) (90 + 120 * (Math.sin(time * 3 + e[4] * 20) * .5 + .5));
            g.setColor(new Color(255, 150 + (int) (e[4] * 70), 60, a));
            g.fillRect((int) x, (int) y, e[4] > .85 ? 2 : 1, e[4] > .85 ? 2 : 1);
        }
    }

    public static void drawWisps(Area a) {
        for (double[] w : a.wisps) {
            int x = sx(w[0], w[1]) + (int) Math.round(Math.sin(time * 1.4 + w[0]) * 4), y = sy(w[0], w[1], w[2] + Math.sin(time * 2 + w[1]) * 3);
            g.setColor(new Color(120, 255, 220, 70)); g.fillRect(x - 2, y - 2, 5, 5);
            g.setColor(new Color(200, 255, 245, 220)); g.fillRect(x - 1, y - 1, 2, 2);
            lights.add(new int[]{x, y, 22, 0x60f0d0});
        }
    }

    public static void drawVignette() {
        for (int i = 1; i <= 9; i++) {
            int w = i * 5;
            g.setColor(new Color(0, 0, 0, 20));
            g.fillRect(0, 0, LW, w); g.fillRect(0, LH - w, LW, w); g.fillRect(0, w, w, LH - 2 * w); g.fillRect(LW - w, w, w, LH - 2 * w);
        }
    }

    public static void drawExitArrow(Area a) {
        double gx = a.arrowDX > 0 ? MAP - 2.6 : 2.6;
        int bx = sx(gx, 16), by = sy(gx, 16, 66) + (int) Math.round(Math.sin(time * 4) * 2);
        double dx = a.arrowDX * 16, dy = a.arrowDX * 8, len = Math.hypot(dx, dy); dx /= len; dy /= len;
        double px = -dy, py = dx;
        int[] xs = {(int) (bx + dx * 9), (int) (bx - dx * 4 + px * 6), (int) (bx - dx * 4 - px * 6)};
        int[] ys = {(int) (by + dy * 9), (int) (by - dy * 4 + py * 6), (int) (by - dy * 4 - py * 6)};
        g.setColor(col(0xf2c14e)); g.fillPolygon(xs, ys, 3);
        g.setColor(col(0x3a2a14)); g.drawPolygon(xs, ys, 3);
    }

    public static void renderScene(BufferedImage buf, Area a, double fade, double titleA) {
        g = buf.createGraphics();
        lights.clear();
        drawBackground();
        g.drawImage(a.ground, -a.gox - camX, -a.goy - camY, null);
        paintSorted(a);
        drawWisps(a);
        drawFog();
        lightPass();
        drawExitArrow(a);
        drawEmbers();
        drawVignette();

        g.setColor(new Color(10, 6, 14, 150)); g.fillRect(6, 6, FontRenderer.textW(a.name, 2) + 12, 18);
        g.setColor(col(0x6a4a54)); g.drawRect(6, 6, FontRenderer.textW(a.name, 2) + 11, 17);
        FontRenderer.textS(g, a.name, 12, 10, 2, col(0xf0d9b8));
        FontRenderer.textS(g, "WASD / ARROWS: MOVE   SHIFT: RUN", 8, LH - 12, 1, new Color(230, 210, 190, 200));
        FontRenderer.textS(g, "AREA " + (a == areas[0] ? "1" : "2") + "/2", LW - 44, 10, 1, new Color(230, 210, 190, 200));
        if (titleA > 0) {
            int al = (int) (255 * Math.min(1, titleA));
            String s = a.name;
            FontRenderer.textS(g, s, (LW - FontRenderer.textW(s, 4)) / 2, 78, 4, new Color(245, 225, 190, al));
            String s2 = a == areas[0] ? "A RONIN WANDERS THE ASHES" : "THE SHRINE REMEMBERS NO ONE";
            FontRenderer.textS(g, s2, (LW - FontRenderer.textW(s2, 1)) / 2, 106, 1, new Color(200, 180, 160, al));
        }
        
        //g2 if false else 
        
        if (fade > 0) { g.setColor(new Color(0, 0, 0, (int) (255 * Math.min(1, fade)))); g.fillRect(0, 0, LW, LH); }
        g.dispose();
    }
}
