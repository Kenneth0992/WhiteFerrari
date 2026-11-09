package gameDev.graphics;

import gameDev.world.Area;
import java.awt.Color;
import java.util.Random;

public class PropRenderers {

    public static void drawGable(double y, double d, double z, double rh, Color roof, double xa, double xb, double ov) {
        RenderUtils.quad(RenderUtils.shade(roof, 1.2), xa, y - ov, z, xb, y - ov, z, xb, y + d / 2, z + rh, xa, y + d / 2, z + rh);
        RenderUtils.quad(RenderUtils.shade(roof, .86), xa, y + d + ov, z, xb, y + d + ov, z, xb, y + d / 2, z + rh, xa, y + d / 2, z + rh);
        RenderUtils.quad(RenderUtils.shade(roof, .52), xb, y - ov, z, xb, y + d + ov, z, xb, y + d / 2, z + rh);
        Color tl = RenderUtils.shade(roof, .58);
        for (int k = 1; k < 4; k++) {
            double f = k / 4.0;
            RenderUtils.ln(tl, xa, y + d / 2 + (d / 2 + ov) * f, z + rh * (1 - f), xb, y + d / 2 + (d / 2 + ov) * f, z + rh * (1 - f));
            RenderUtils.ln(RenderUtils.shade(roof, .9), xa, y + d / 2 - (d / 2 + ov) * f, z + rh * (1 - f), xb, y + d / 2 - (d / 2 + ov) * f, z + rh * (1 - f));
        }
        RenderUtils.ln(RenderUtils.shade(roof, 1.55), xa, y + d / 2, z + rh, xb, y + d / 2, z + rh);
        RenderUtils.ln(RenderUtils.shade(roof, .35), xa, y + d + ov, z, xb, y + d + ov, z);
        RenderUtils.ln(RenderUtils.shade(roof, .35), xb, y + d + ov, z, xb, y + d / 2, z + rh);
    }

    public static void drawRafters(double x0, double x1, double y, double d, double z, double rh) {
        if (x1 - x0 < .2) return;
        Color wood = RenderUtils.col(0x3b281c);
        int n = Math.max(2, (int) ((x1 - x0) * 1.6));
        for (int i = 0; i < n; i++) {
            double rx = x0 + (x1 - x0) * (i + .5) / n;
            RenderUtils.ln(wood, rx, y + d + .1, z, rx, y + d / 2, z + rh * .92);
            RenderUtils.ln(wood, rx, y - .1, z, rx, y + d / 2, z + rh * .92);
        }
        RenderUtils.ln(wood, x0, y + d / 2, z + rh * .92, x1, y + d / 2, z + rh * .92);
    }

    public static void drawHouse(double x, double y, double w, double d, double h, Color wall, Color roof, int v) {
        RenderUtils.fillq(new Color(0, 0, 0, 70), x + .3, y + .3, 0, x + w + .9, y + .3, 0, x + w + .9, y + d + .9, 0, x + .3, y + d + .9, 0);
        RenderUtils.box(x, y, w, d, 0, 3, RenderUtils.col(0x5d5852));
        double z0 = 3, z1 = 3 + h;
        RenderUtils.box(x, y, w, d, z0, h, wall);
        Color pl = RenderUtils.shade(wall, .62), pr = RenderUtils.shade(wall, .42);
        int nl = (int) Math.round(w * 2.2), nr = (int) Math.round(d * 2.2);
        for (int i = 1; i < nl; i++) RenderUtils.ln(pl, x + w * i / nl, y + d, z0, x + w * i / nl, y + d, z1);
        for (int i = 1; i < nr; i++) RenderUtils.ln(pr, x + w, y + d * i / nr, z0, x + w, y + d * i / nr, z1);
        RenderUtils.ln(RenderUtils.shade(wall, .5), x, y + d, z0 + h * .5, x + w, y + d, z0 + h * .5);
        Color dark = RenderUtils.col(0x140e12);
        RenderUtils.wallL(x, y, w, d, .12, .34, z0, z0 + h * .68, dark);
        RenderUtils.ln(RenderUtils.col(0x3a2a20), x + w * .12, y + d, z0 + h * .68, x + w * .34, y + d, z0 + h * .68);
        Color win = (v % 2 == 1) ? RenderUtils.col(0xd9892e) : dark;
        RenderUtils.wallL(x, y, w, d, .56, .78, z0 + h * .4, z0 + h * .76, win);
        RenderUtils.wallR(x, y, w, d, .3, .62, z0 + h * .38, z0 + h * .76, dark);
        if (v % 2 == 1) RenderUtils.light(x + w * .67, y + d + .3, z0 + h * .58, 26, 0xffa040);
        double rh = h * .55, ov = .25, xa, xb;
        if (v % 2 == 0) { xa = x - ov; xb = x + w * .6; } else { xa = x + w * .4; xb = x + w + ov; }
        drawGable(y, d, z1, rh, roof, xa, xb, ov);
        if (v % 2 == 0) drawRafters(xb + .1, x + w, y, d, z1, rh); else drawRafters(x, xa - .1, y, d, z1, rh);
        if (v >= 2) {
            RenderUtils.g.setColor(RenderUtils.col(0x4a3524));
            int bx = RenderUtils.sx(x + w + .6, y + d), by = RenderUtils.sy(x + w + .6, y + d, 0), tx = RenderUtils.sx(x + w, y + d - .2), ty = RenderUtils.sy(x + w, y + d - .2, h * .85);
            RenderUtils.g.drawLine(bx, by, tx, ty); RenderUtils.g.drawLine(bx + 1, by, tx + 1, ty); RenderUtils.g.drawLine(bx + 2, by, tx + 2, ty);
        }
        if (v == 1 || v == 3) {
            RenderUtils.box(x + w * .18, y + d * .3, .7, .7, z1 - 2, rh + 6, RenderUtils.col(0x6a5f58));
            RenderUtils.box(x + w * .18 + .1, y + d * .3 + .1, .5, .5, z1 + rh + 4, 2, RenderUtils.col(0x3a322e));
        }
    }

    public static void drawWall(double x, double y, double w, double d, double h) {
        RenderUtils.fillq(new Color(0, 0, 0, 60), x + .2, y + .2, 0, x + w + .6, y + .2, 0, x + w + .6, y + d + .6, 0, x + .2, y + d + .6, 0);
        Color c = RenderUtils.col(0x7d766f);
        RenderUtils.box(x, y, w, d, 0, h, c);
        boolean alongX = w > d;
        if (alongX) {
            RenderUtils.box(x + w * .05, y, w * .22, d, h, 7, c);
            RenderUtils.box(x + w * .6, y, w * .18, d, h, 12, c);
        } else {
            RenderUtils.box(x, y + d * .05, w, d * .22, h, 7, c);
            RenderUtils.box(x, y + d * .6, w, d * .18, h, 12, c);
        }
        for (int k = 1; k < 4; k++) RenderUtils.ln(RenderUtils.shade(c, .55), x, y + d, h * k / 4, x + w, y + d, h * k / 4);
    }

    public static void drawFence(double x, double y, int n, boolean alongX) {
        double step = .5;
        int[] px = new int[n], py = new int[n], ph = new int[n];
        for (int i = 0; i < n; i++) {
            double fx = alongX ? x + i * step : x, fy = alongX ? y : y + i * step;
            px[i] = RenderUtils.sx(fx, fy); py[i] = RenderUtils.sy(fx, fy, 0);
            ph[i] = (RenderUtils.hash(i, (int) (x * 3 + y), 4) < .25) ? 6 : 11 + (int) (RenderUtils.hash(i, (int) y, 6) * 4);
        }
        RenderUtils.g.setColor(RenderUtils.col(0x4a3626));
        for (int i = 1; i < n; i++) {
            if (RenderUtils.hash(i, (int) (x + y), 8) < .8) RenderUtils.g.drawLine(px[i - 1], py[i - 1] - 5, px[i], py[i] - 6);
            if (RenderUtils.hash(i, (int) (x + y), 9) < .5) RenderUtils.g.drawLine(px[i - 1], py[i - 1] - 9, px[i], py[i] - 10);
        }
        for (int i = 0; i < n; i++) {
            RenderUtils.g.setColor(RenderUtils.col(0x5a432f)); RenderUtils.g.fillRect(px[i] - 1, py[i] - ph[i], 2, ph[i]);
            RenderUtils.g.setColor(RenderUtils.col(0x7a5e42)); RenderUtils.g.fillRect(px[i] - 1, py[i] - ph[i], 1, 1);
        }
    }

    public static int toff(int r, int seed) { return (int) Math.round(Math.sin(r * .09 + seed) * 2.5 + r * .025 * ((seed & 1) == 0 ? 1 : -1)); }
    public static void drawTree(double x, double y, int H, int seed) {
        int bx = RenderUtils.sx(x, y), by = RenderUtils.sy(x, y, 0);
        RenderUtils.shadowOval(bx, by - 1, 10, 3);
        Color trunk = RenderUtils.col(0x2b2125), hi = RenderUtils.col(0x45353a);
        for (int r = 0; r < H; r++) {
            int off = toff(r, seed), wd = Math.max(1, 5 - r / (H / 4 + 1)) + (r < 5 ? 2 : 0);
            RenderUtils.g.setColor(trunk); RenderUtils.g.fillRect(bx + off - wd / 2, by - r, wd, 1);
            RenderUtils.g.setColor(hi); RenderUtils.g.fillRect(bx + off - wd / 2, by - r, 1, 1);
        }
        Random rr = new Random(seed * 977L);
        for (int i = 0; i < 6; i++) {
            int r = (int) (H * (.32 + .11 * i)), off = toff(r, seed);
            int dir = (i % 2 == 0) ? -1 : 1, len = 9 + rr.nextInt(12);
            int sxp = bx + off, syp = by - r, ex = sxp + dir * len, ey = syp - 5 - rr.nextInt(9);
            RenderUtils.g.setColor(trunk);
            RenderUtils.g.drawLine(sxp, syp, ex, ey); RenderUtils.g.drawLine(sxp, syp - 1, ex, ey - 1);
            if (i < 3) RenderUtils.g.drawLine(sxp, syp + 1, ex, ey + 1);
            RenderUtils.g.drawLine(ex, ey, ex + dir * 4, ey - 5); RenderUtils.g.drawLine(ex, ey, ex + dir * 6, ey - 1);
            RenderUtils.g.drawLine(ex - dir * 3, ey + 2, ex - dir * 2, ey - 4);
        }
    }

    public static void drawBanner(double x, double y, Color c) {
        int bx = RenderUtils.sx(x, y), by = RenderUtils.sy(x, y, 0);
        RenderUtils.shadowOval(bx, by, 4, 2);
        RenderUtils.g.setColor(RenderUtils.col(0x3a2a20)); RenderUtils.g.fillRect(bx - 1, by - 50, 2, 50);
        RenderUtils.g.fillRect(bx - 1, by - 52, 14, 2);
        int fl = (int) Math.round(Math.sin(RenderUtils.time * 3 + x) * 1.5);
        int[] xs = {bx + 1, bx + 12, bx + 12 + fl, bx + 10 + fl, bx + 12 + fl, bx + 6, bx + 1};
        int[] ys = {by - 50, by - 50, by - 36, by - 31, by - 24, by - 28, by - 24};
        RenderUtils.g.setColor(c); RenderUtils.g.fillPolygon(xs, ys, xs.length);
        RenderUtils.g.setColor(RenderUtils.shade(c, .6)); RenderUtils.g.drawPolygon(xs, ys, xs.length);
        RenderUtils.g.setColor(RenderUtils.col(0xe3d8c0)); RenderUtils.g.drawOval(bx + 4, by - 46, 5, 5); RenderUtils.g.drawLine(bx + 3, by - 38, bx + 10, by - 45);
    }

    public static void drawLamp(double x, double y) {
        int bx = RenderUtils.sx(x, y), by = RenderUtils.sy(x, y, 0);
        RenderUtils.shadowOval(bx, by, 5, 2);
        RenderUtils.g.setColor(RenderUtils.col(0x2c2226)); RenderUtils.g.fillRect(bx - 1, by - 30, 2, 30); RenderUtils.g.fillRect(bx - 1, by - 31, 9, 2);
        RenderUtils.g.setColor(RenderUtils.col(0x3a2c2a)); RenderUtils.g.fillRect(bx + 5, by - 29, 6, 8);
        RenderUtils.g.setColor(((int) (RenderUtils.time * 9 + x) % 5 == 0) ? RenderUtils.col(0xffd27a) : RenderUtils.col(0xffa844)); RenderUtils.g.fillRect(bx + 6, by - 28, 4, 6);
        RenderUtils.light(x + .4, y + .1, 26, 56, 0xff9a44);
    }

    public static void drawToro(double x, double y, int rgb) {
        Color st = RenderUtils.col(0x8a8884);
        RenderUtils.fillq(new Color(0, 0, 0, 55), x - .4, y - .4, 0, x + .9, y - .4, 0, x + .9, y + .9, 0, x - .4, y + .9, 0);
        RenderUtils.box(x - .3, y - .3, .6, .6, 0, 3, st);
        RenderUtils.box(x - .15, y - .15, .3, .3, 3, 9, RenderUtils.shade(st, .95));
        RenderUtils.box(x - .38, y - .38, .76, .76, 12, 6, st);
        RenderUtils.wallL(x - .38, y - .38, .76, .76, .22, .78, 13.5, 16.5, RenderUtils.col(rgb));
        RenderUtils.box(x - .52, y - .52, 1.04, 1.04, 18, 2, RenderUtils.shade(st, 1.1));
        RenderUtils.box(x - .26, y - .26, .52, .52, 20, 2, st);
        RenderUtils.light(x, y + .2, 15, 50, rgb);
    }

    public static void drawFire(double x, double y, boolean spirit) {
        int bx = RenderUtils.sx(x, y), by = RenderUtils.sy(x, y, 0);
        RenderUtils.shadowOval(bx, by, 14, 5);
        RenderUtils.g.setColor(new Color(20, 12, 10, 120)); RenderUtils.g.fillOval(bx - 11, by - 4, 22, 8);
        for (int i = 0; i < 10; i++) {
            double a = i * Math.PI * 2 / 10;
            int px = bx + (int) Math.round(Math.cos(a) * 11), py = by + (int) Math.round(Math.sin(a) * 5) - 1;
            RenderUtils.g.setColor(RenderUtils.col(i % 2 == 0 ? 0x6a6a6c : 0x4a4a4e)); RenderUtils.g.fillRect(px - 1, py - 1, 3, 2);
        }
        RenderUtils.g.setColor(RenderUtils.col(0x4a2f1c)); RenderUtils.g.drawLine(bx - 7, by - 1, bx + 7, by - 4); RenderUtils.g.drawLine(bx - 7, by - 4, bx + 7, by - 1);
        RenderUtils.g.drawLine(bx - 7, by - 2, bx + 7, by - 5);
        double t = RenderUtils.time * 10;
        int[] hs = {16 + (int) (Math.sin(t + x) * 3), 11 + (int) (Math.sin(t * 1.3 + 2) * 2), 6 + (int) (Math.sin(t * 1.7) * 2)};
        int[] ws = {9, 6, 3};
        Color[] cs = spirit ? new Color[]{RenderUtils.col(0x1f8f86), RenderUtils.col(0x37d1b8), RenderUtils.col(0xcafff0)} : new Color[]{RenderUtils.col(0xd8481f), RenderUtils.col(0xf28a26), RenderUtils.col(0xffd868)};
        for (int l = 0; l < 3; l++) {
            RenderUtils.g.setColor(cs[l]);
            for (int r = 0; r < hs[l]; r++) {
                int wd = Math.max(1, (int) Math.round(ws[l] * (1 - Math.pow(r / (double) hs[l], 1.7))));
                int off = (int) Math.round(Math.sin(t * .8 + r * .4 + l) * r / 6.0);
                RenderUtils.g.fillRect(bx - wd / 2 + off, by - 3 - r, wd, 1);
            }
        }
        double fl = 1 + Math.sin(RenderUtils.time * 13) * .08;
        RenderUtils.light(x, y, 10, (int) (74 * fl), spirit ? 0x40e0c8 : 0xff8a30);
    }

    public static void drawWell(double x, double y) {
        RenderUtils.fillq(new Color(0, 0, 0, 60), x, y, 0, x + 1.9, y + .2, 0, x + 1.9, y + 1.9, 0, x + .2, y + 1.9, 0);
        Color st = RenderUtils.col(0x7b766f), wood = RenderUtils.col(0x4c3828);
        RenderUtils.box(x, y, 1.4, 1.4, 0, 7, st);
        RenderUtils.quad(RenderUtils.col(0x0a0f17), x + .22, y + .22, 7.1, x + 1.18, y + .22, 7.1, x + 1.18, y + 1.18, 7.1, x + .22, y + 1.18, 7.1);
        RenderUtils.box(x + .02, y + .6, .13, .13, 7, 24, wood);
        RenderUtils.box(x + 1.25, y + .6, .13, .13, 7, 24, wood);
        RenderUtils.box(x - .05, y + .58, 1.5, .17, 29, 2, RenderUtils.shade(wood, 1.2));
        RenderUtils.ln(RenderUtils.col(0xb9a98a), x + .7, y + .66, 29, x + .7, y + .66, 14);
        RenderUtils.box(x + .6, y + .56, .2, .2, 11, 4, RenderUtils.col(0x6b5038));
    }

    public static void drawCrates(double x, double y) {
        RenderUtils.fillq(new Color(0, 0, 0, 60), x, y, 0, x + 1.5, y + .1, 0, x + 1.5, y + 1.3, 0, x + .1, y + 1.3, 0);
        Color a = RenderUtils.col(0x7a5a38), b = RenderUtils.col(0x6b4e30);
        RenderUtils.box(x, y, .7, .7, 0, 11, a); RenderUtils.box(x + .75, y + .25, .6, .6, 0, 8, b); RenderUtils.box(x + .06, y + .06, .55, .55, 11, 8, RenderUtils.shade(a, 1.1));
        RenderUtils.ln(RenderUtils.shade(a, .5), x, y + .7, 5, x + .7, y + .7, 5);
        RenderUtils.ln(RenderUtils.shade(b, .5), x + .75, y + .85, 4, x + 1.35, y + .85, 4);
    }

    public static void drawBarrel(double x, double y) {
        int bx = RenderUtils.sx(x, y), by = RenderUtils.sy(x, y, 0);
        RenderUtils.shadowOval(bx, by, 6, 2);
        RenderUtils.g.setColor(RenderUtils.col(0x6b4a2c)); RenderUtils.g.fillRect(bx - 4, by - 11, 9, 11);
        RenderUtils.g.setColor(RenderUtils.col(0x8a6339)); RenderUtils.g.fillRect(bx - 4, by - 11, 2, 11);
        RenderUtils.g.setColor(RenderUtils.col(0x2f2a2c)); RenderUtils.g.fillRect(bx - 4, by - 8, 9, 1); RenderUtils.g.fillRect(bx - 4, by - 3, 9, 1);
        RenderUtils.g.setColor(RenderUtils.col(0x8a6339)); RenderUtils.g.fillOval(bx - 4, by - 13, 9, 4);
        RenderUtils.g.setColor(RenderUtils.col(0x3a2a1c)); RenderUtils.g.fillOval(bx - 2, by - 12, 5, 2);
    }

    public static void drawCart(double x, double y) {
        RenderUtils.fillq(new Color(0, 0, 0, 60), x, y, 0, x + 2.8, y + .2, 0, x + 2.8, y + 1.7, 0, x + .2, y + 1.7, 0);
        Color wood = RenderUtils.col(0x5e4630);
        RenderUtils.box(x, y, 2.4, 1.3, 5, 4, wood);
        RenderUtils.box(x, y + 1.2, 2.4, .1, 9, 5, RenderUtils.shade(wood, .9));
        RenderUtils.box(x + 2.3, y, .1, 1.3, 9, 5, RenderUtils.shade(wood, .8));
        RenderUtils.ln(RenderUtils.col(0x2e2118), x, y + 1.3, 3, x - 1.6, y + 1.3, 6);
        int wx = RenderUtils.sx(x + 1.1, y + 1.35), wy = RenderUtils.sy(x + 1.1, y + 1.35, 5);
        RenderUtils.g.setColor(RenderUtils.col(0x2e2118)); RenderUtils.g.drawOval(wx - 6, wy - 6, 12, 12); RenderUtils.g.drawOval(wx - 5, wy - 5, 10, 10);
        RenderUtils.g.drawLine(wx - 6, wy, wx + 6, wy); RenderUtils.g.drawLine(wx, wy - 6, wx, wy + 6);
        RenderUtils.g.setColor(RenderUtils.col(0x8b7a56)); RenderUtils.g.fillOval(RenderUtils.sx(x + 1.2, y + .6) - 5, RenderUtils.sy(x + 1.2, y + .6, 9) - 6, 10, 7);
    }

    public static void drawTomb(double x, double y, int h, boolean cross) {
        RenderUtils.fillq(new Color(0, 0, 0, 50), x, y, 0, x + .9, y + .1, 0, x + .9, y + .55, 0, x + .1, y + .55, 0);
        Color st = RenderUtils.col(0x8d8b8a);
        RenderUtils.box(x, y, .7, .3, 0, h, st);
        if (cross) { RenderUtils.box(x - .15, y - .01, 1.0, .3, h * .6, 3, st); }
        else RenderUtils.box(x + .05, y, .6, .3, h, 2, RenderUtils.shade(st, 1.1));
        int mx = RenderUtils.sx(x + .3, y + .3), my = RenderUtils.sy(x + .3, y + .3, h * .4);
        RenderUtils.g.setColor(RenderUtils.col(0x3f6a4a)); RenderUtils.g.fillRect(mx - 3, my, 3, 2); RenderUtils.g.fillRect(mx + 1, my + 2, 2, 2);
    }

    public static void drawStatue(double x, double y) {
        RenderUtils.fillq(new Color(0, 0, 0, 60), x, y, 0, x + 2.8, y + .2, 0, x + 2.8, y + 2.8, 0, x + .2, y + 2.8, 0);
        Color st = RenderUtils.col(0x85827f);
        RenderUtils.box(x, y, 2, 2, 0, 6, st);
        RenderUtils.box(x + .15, y + .15, 1.7, 1.7, 6, 2, RenderUtils.shade(st, 1.1));
        int cx = RenderUtils.sx(x + 1, y + 1), cy = RenderUtils.sy(x + 1, y + 1, 8);
        int[] xs = {cx - 11, cx - 9, cx - 5, cx - 3, cx + 4, cx + 6, cx + 10, cx + 12};
        int[] ys = {cy, cy - 18, cy - 28, cy - 33, cy - 31, cy - 25, cy - 16, cy};
        RenderUtils.g.setColor(RenderUtils.shade(st, .85)); RenderUtils.g.fillPolygon(xs, ys, xs.length);
        RenderUtils.g.setColor(RenderUtils.shade(st, 1.15)); RenderUtils.g.fillPolygon(new int[]{cx - 11, cx - 9, cx - 5, cx - 4, cx - 6}, new int[]{cy, cy - 18, cy - 28, cy - 20, cy}, 5);
        RenderUtils.g.setColor(RenderUtils.shade(st, .55)); RenderUtils.g.drawPolygon(xs, ys, xs.length);
        RenderUtils.g.drawLine(cx - 6, cy - 14, cx + 7, cy - 12); RenderUtils.g.drawLine(cx - 4, cy - 10, cx + 8, cy - 8);
        RenderUtils.g.drawLine(cx, cy - 33, cx + 1, cy - 28);
        RenderUtils.g.setColor(RenderUtils.col(0x3f6a4a)); RenderUtils.g.fillRect(cx - 9, cy - 6, 3, 2); RenderUtils.g.fillRect(cx + 5, cy - 10, 2, 3);
        RenderUtils.g.setColor(RenderUtils.col(0x6f6c6a)); RenderUtils.g.fillOval(RenderUtils.sx(x + 2.3, y + 1.6) - 5, RenderUtils.sy(x + 2.3, y + 1.6, 0) - 6, 10, 7);
    }

    public static void drawTower(double x, double y, double s, double hgt) {
        RenderUtils.fillq(new Color(0, 0, 0, 60), x, y, 0, x + s + .6, y + .2, 0, x + s + .6, y + s + .6, 0, x + .2, y + s + .6, 0);
        Color wood = RenderUtils.col(0x52402e), wd = RenderUtils.shade(wood, .8);
        double p = .35;
        RenderUtils.box(x, y, p, p, 0, hgt, wood);
        RenderUtils.box(x + s - p, y, p, p, 0, hgt - 6, wood);
        RenderUtils.box(x, y + s - p, p, p, 0, hgt, wood);
        RenderUtils.box(x + s - p, y + s - p, p, p, 0, hgt - 3, wood);
        RenderUtils.ln(wd, x, y + s, 3, x + s, y + s, hgt - 8); RenderUtils.ln(wd, x + s, y + s, 3, x, y + s, hgt - 8);
        RenderUtils.ln(wd, x + s, y, 3, x + s, y + s, hgt - 8); RenderUtils.ln(wd, x + s, y + s, 3, x + s, y, hgt - 8);
        RenderUtils.box(x - .3, y - .3, s + .6, s + .6, hgt - 4, 2, RenderUtils.shade(wood, 1.15));
        RenderUtils.box(x - .25, y + s + .05, .15, .15, hgt - 2, 7, wood);
        RenderUtils.box(x + s * .5, y + s + .1, .15, .15, hgt - 2, 4, wood);
        RenderUtils.box(x + s + .1, y - .2, .15, .15, hgt - 2, 8, wood);
        double lx = x + s * .55;
        RenderUtils.ln(wd, lx, y + s + .4, 0, lx, y + s + .1, hgt - 4); RenderUtils.ln(wd, lx + .6, y + s + .4, 0, lx + .6, y + s + .1, hgt - 4);
        for (int i = 1; i < 8; i++) { double z = i * (hgt - 8) / 8.0; if (i != 5) RenderUtils.ln(RenderUtils.col(0x6a5238), lx, y + s + .35, z, lx + .6, y + s + .35, z); }
        int cx = RenderUtils.sx(x + s, y + s), cy = RenderUtils.sy(x + s, y + s, hgt - 6);
        RenderUtils.g.setColor(RenderUtils.col(0x7a2628)); RenderUtils.g.fillRect(cx - 2, cy, 5, 10); RenderUtils.g.fillRect(cx - 1, cy + 10, 3, 3);
    }

    public static void drawRock(double x, double y, int sz) {
        int bx = RenderUtils.sx(x, y), by = RenderUtils.sy(x, y, 0);
        RenderUtils.shadowOval(bx + 2, by, sz, sz / 3 + 1);
        int[] xs = {bx - sz, bx - sz * 2 / 3, bx + sz / 4, bx + sz, bx + sz * 2 / 3};
        int[] ys = {by, by - sz * 2 / 3 - 2, by - sz - 2, by - sz / 2, by};
        RenderUtils.g.setColor(RenderUtils.col(0x6d6a6b)); RenderUtils.g.fillPolygon(xs, ys, 5);
        RenderUtils.g.setColor(RenderUtils.col(0x8e8b8c)); RenderUtils.g.fillPolygon(new int[]{bx - sz, bx - sz * 2 / 3, bx + sz / 4, bx - sz / 4}, new int[]{by, by - sz * 2 / 3 - 2, by - sz - 2, by - sz / 3}, 4);
        RenderUtils.g.setColor(RenderUtils.col(0x3d3a3c)); RenderUtils.g.drawPolygon(xs, ys, 5);
    }

    public static void drawReeds(double x, double y) {
        int bx = RenderUtils.sx(x, y), by = RenderUtils.sy(x, y, 0);
        RenderUtils.g.setColor(RenderUtils.col(0x4a5a3a));
        for (int i = -3; i <= 3; i += 2) RenderUtils.g.drawLine(bx + i, by, bx + i + (i < 0 ? -1 : 1), by - 8 - (i & 3));
        RenderUtils.g.setColor(RenderUtils.col(0x6b4a2c)); RenderUtils.g.fillRect(bx - 1, by - 11, 2, 3);
    }

    public static void bx(boolean alongX, double u, double v, double du, double dv, double z0, double h, Color c) {
        if (alongX) RenderUtils.box(u, v, du, dv, z0, h, c); else RenderUtils.box(v, u, dv, du, z0, h, c);
    }

    public static void drawTorii(double cx, double cy, double span, boolean alongX, boolean broken, boolean beamOnly, int which) {
        Color red = RenderUtils.col(0xa83530), blk = RenderUtils.col(0x231b20);
        double half = span / 2, pw = .5;
        double u0 = (alongX ? cx : cy) - half, u1 = (alongX ? cx : cy) + half, v = (alongX ? cy : cx);
        if (!beamOnly && which == 0) { bx(alongX, u0 - pw / 2, v - pw / 2, pw, pw, 0, 44, red); bx(alongX, u0 - pw / 2 - .05, v - pw / 2 - .05, pw + .1, pw + .1, 0, 3, blk); }
        if (!beamOnly && which == 1) { bx(alongX, u1 - pw / 2, v - pw / 2, pw, pw, 0, broken ? 28 : 44, red); bx(alongX, u1 - pw / 2 - .05, v - pw / 2 - .05, pw + .1, pw + .1, 0, 3, blk); }
        if (beamOnly) {
            bx(alongX, u0, v - .17, span, .34, 33, 2.5, red);
            if (!broken) {
                bx(alongX, u0 - .7, v - .25, span + 1.4, .5, 41, 3, blk);
                bx(alongX, u0 - 1.0, v - .25, .4, .5, 43, 2, blk);
                bx(alongX, u1 + .6, v - .25, .4, .5, 43, 2, blk);
            } else {
                bx(alongX, u0 - .7, v - .25, span * .55, .5, 41, 3, blk);
                bx(alongX, u0 - 1.0, v - .25, .4, .5, 43, 2, blk);
                bx(alongX, u1 - .9, v - .2, .35, .4, 33, 6, RenderUtils.shade(blk, 1.4));
            }
            Color rope = RenderUtils.col(0xcdbb90);
            for (int i = 0; i < 6; i++) RenderUtils.ln(rope, alongX ? u0 + span * i / 6 : v, alongX ? v : u0 + span * i / 6, 31 - (i % 2), alongX ? u0 + span * (i + 1) / 6 : v, alongX ? v : u0 + span * (i + 1) / 6, 31 - ((i + 1) % 2));
        }
    }

    public static void drawShrine(double x, double y, double w, double d) {
        RenderUtils.fillq(new Color(0, 0, 0, 70), x + .3, y + .3, 0, x + w + 1, y + .3, 0, x + w + 1, y + d + 1, 0, x + .3, y + d + 1, 0);
        Color st = RenderUtils.col(0x8a867f);
        RenderUtils.box(x, y, w, d, 0, 4, st);
        RenderUtils.box(x + w * .36, y + d, w * .28, 1.0, 0, 2.5, RenderUtils.shade(st, 1.05));
        RenderUtils.box(x + w * .4, y + d + .5, w * .2, .5, 0, 1.2, RenderUtils.shade(st, 1.1));
        double hx = x + 1.2, hy = y + 1.0, hw = w - 2.4, hd = d - 2.0;
        Color wall = RenderUtils.col(0x5b4637), pil = RenderUtils.col(0x9a3a32);
        RenderUtils.box(hx, hy, hw, hd, 4, 20, wall);
        for (int i = 0; i <= 3; i++) { double px = hx + (hw - .35) * i / 3.0; RenderUtils.box(px, hy + hd - .05, .35, .35, 4, 20, pil); }
        RenderUtils.wallL(hx, hy, hw, hd + .3, .3, .7, 4, 18, RenderUtils.col(0x120c10));
        for (int k = 1; k < 4; k++) RenderUtils.ln(RenderUtils.col(0x241a20), hx + hw * (.3 + .1 * k), hy + hd + .3, 4, hx + hw * (.3 + .1 * k), hy + hd + .3, 18);
        RenderUtils.light(hx + hw * .5, hy + hd + .8, 12, 34, 0x3de0c8);
        double z1 = 24, rh = 11, ov = 1.1;
        Color roof = RenderUtils.col(0x4a3a40);
        drawGable(hy, hd, z1, rh, roof, hx - ov, hx + hw * .62, ov);
        drawRafters(hx + hw * .62 + .1, hx + hw + ov, hy, hd, z1, rh);
        RenderUtils.box(hx - ov, hy - ov, .3, hd + 2 * ov, z1 - 1.5, 1.5, RenderUtils.shade(roof, .7));
    }
}
