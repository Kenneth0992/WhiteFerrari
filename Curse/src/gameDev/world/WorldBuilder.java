package gameDev.world;

import gameDev.graphics.PropRenderers;
import gameDev.graphics.RenderUtils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class WorldBuilder {

    public static boolean onPath(Area a, double x, double y) {
        for (double[] s : a.path) {
            double dx = s[2] - s[0], dy = s[3] - s[1];
            double t = Math.max(0, Math.min(1, ((x - s[0]) * dx + (y - s[1]) * dy) / (dx * dx + dy * dy)));
            double px = s[0] + dx * t, py = s[1] + dy * t;
            if (Math.hypot(x - px, y - py) < s[4]) return true;
        }
        return false;
    }

    public static void bakeGround(Area a) {
        int ox0 = RenderUtils.MAP * RenderUtils.TW / 2 + 16, oy0 = 16;
        BufferedImage img = new BufferedImage(RenderUtils.MAP * RenderUtils.TW + 32, RenderUtils.MAP * RenderUtils.TH + 44, BufferedImage.TYPE_INT_ARGB);
        Graphics2D old = RenderUtils.g;
        int ocx = RenderUtils.camX, ocy = RenderUtils.camY;
        RenderUtils.g = img.createGraphics();
        RenderUtils.camX = -ox0;
        RenderUtils.camY = -oy0;

        for (int i = 0; i < RenderUtils.MAP; i++) {
            Color s1 = (i % 3 == 0) ? RenderUtils.col(0x2a1f20) : RenderUtils.col(0x33262a);
            RenderUtils.quad(s1, i, RenderUtils.MAP, 0, i + 1, RenderUtils.MAP, 0, i + 1, RenderUtils.MAP, -14, i, RenderUtils.MAP, -14);
            Color s2 = (i % 3 == 0) ? RenderUtils.col(0x1b1416) : RenderUtils.col(0x241a1d);
            RenderUtils.quad(s2, RenderUtils.MAP, i, 0, RenderUtils.MAP, i + 1, 0, RenderUtils.MAP, i + 1, -14, RenderUtils.MAP, i, -14);
            RenderUtils.ln(RenderUtils.col(0x4a3a36), i, RenderUtils.MAP, -4, i + 1, RenderUtils.MAP, -4);
            RenderUtils.ln(RenderUtils.col(0x15100f), i, RenderUtils.MAP, -9, i + 1, RenderUtils.MAP, -9);
        }
        for (int ty = 0; ty < RenderUtils.MAP; ty++) {
            for (int tx = 0; tx < RenderUtils.MAP; tx++) {
                double cx = tx + .5, cy = ty + .5;
                Color base = a.dirt[(int) (RenderUtils.hash(tx, ty, a.seed) * a.dirt.length) % a.dirt.length];
                boolean water = a.water != null && cx >= a.water[0] && cx < a.water[0] + a.water[2] && cy >= a.water[1] && cy < a.water[1] + a.water[3];
                if (water) {
                    base = (RenderUtils.hash(tx, ty, 5) > .5) ? RenderUtils.col(0x1d3b4a) : RenderUtils.col(0x234759);
                } else {
                    for (double[] r : a.rects) if (cx >= r[0] && cx < r[0] + r[2] && cy >= r[1] && cy < r[1] + r[3]) base = RenderUtils.mix(base, RenderUtils.col((int) r[4]), .8);
                    if (onPath(a, cx, cy)) base = RenderUtils.mix(base, a.pathC, .72 + RenderUtils.hash(tx, ty, 9) * .2);
                    for (double[] c : a.circ) if (Math.hypot(cx - c[0], cy - c[1]) < c[2]) base = RenderUtils.mix(base, RenderUtils.col((int) c[3]), .8 + RenderUtils.hash(tx, ty, 3) * .15);
                    if (RenderUtils.hash(tx, ty, 77) < .12) base = RenderUtils.shade(base, .76);
                }
                RenderUtils.quad(base, tx, ty, 0, tx + 1, ty, 0, tx + 1, ty + 1, 0, tx, ty + 1, 0);

                for (int k = 0; k < 4; k++) {
                    double u = RenderUtils.hash(tx * 7 + k, ty * 13 + k, a.seed + 1), v = RenderUtils.hash(tx * 5 + k, ty * 11 + k, a.seed + 2);
                    RenderUtils.g.setColor(RenderUtils.shade(base, k % 2 == 0 ? 1.22 : .78));
                    RenderUtils.g.fillRect(RenderUtils.sx(tx + u, ty + v), RenderUtils.sy(tx + u, ty + v, 0), 1, 1);
                }
                if (water) {
                    if (RenderUtils.hash(tx, ty, 41) > .55) {
                        RenderUtils.g.setColor(RenderUtils.col(0x4f8aa0));
                        RenderUtils.g.fillRect(RenderUtils.sx(tx + .3, ty + .5), RenderUtils.sy(tx + .3, ty + .5, 0), 3, 1);
                    }
                    continue;
                }
                double hh = RenderUtils.hash(tx, ty, 123);
                if (hh > .86 && !onPath(a, cx, cy)) {
                    int px = RenderUtils.sx(tx + .5, ty + .5), py = RenderUtils.sy(tx + .5, ty + .5, 0);
                    RenderUtils.g.setColor(a.grass);
                    RenderUtils.g.fillRect(px, py - 3, 1, 3);
                    RenderUtils.g.fillRect(px - 2, py - 2, 1, 2);
                    RenderUtils.g.fillRect(px + 2, py - 2, 1, 2);
                    RenderUtils.g.setColor(RenderUtils.shade(a.grass, 1.35));
                    RenderUtils.g.fillRect(px, py - 4, 1, 1);
                } else if (hh < .05) {
                    int px = RenderUtils.sx(tx + .4, ty + .6), py = RenderUtils.sy(tx + .4, ty + .6, 0);
                    RenderUtils.g.setColor(RenderUtils.col(0x6f6c6e));
                    RenderUtils.g.fillRect(px, py - 2, 3, 2);
                    RenderUtils.g.setColor(RenderUtils.col(0x8d8a8c));
                    RenderUtils.g.fillRect(px, py - 2, 2, 1);
                    RenderUtils.g.setColor(RenderUtils.col(0x4b494d));
                    RenderUtils.g.fillRect(px + 4, py - 1, 2, 1);
                }
            }
        }
        RenderUtils.g.dispose();
        RenderUtils.g = old;
        RenderUtils.camX = ocx;
        RenderUtils.camY = ocy;
        a.ground = img;
        a.gox = ox0;
        a.goy = oy0;
    }

    public static void house(Area a, double x, double y, double w, double d, double h, int wall, int roof, int v) {
        a.solid(x, y, w, d);
        a.prop(x, y, x + w, y + d, () -> PropRenderers.drawHouse(x, y, w, d, h, RenderUtils.col(wall), RenderUtils.col(roof), v));
    }
    public static void tree(Area a, double x, double y, int h, int seed) {
        a.solid(x - .3, y - .3, .6, .6);
        a.prop(x - .4, y - .4, x + .4, y + .4, () -> PropRenderers.drawTree(x, y, h, seed));
    }
    public static void banner(Area a, double x, double y, int rgb) {
        a.prop(x - .2, y - .2, x + .2, y + .2, () -> PropRenderers.drawBanner(x, y, RenderUtils.col(rgb)));
    }
    public static void lamp(Area a, double x, double y) {
        a.solid(x - .15, y - .15, .3, .3);
        a.prop(x - .3, y - .3, x + .3, y + .3, () -> PropRenderers.drawLamp(x, y));
    }
    public static void toro(Area a, double x, double y, int rgb) {
        a.solid(x - .45, y - .45, .9, .9);
        a.prop(x - .55, y - .55, x + .55, y + .55, () -> PropRenderers.drawToro(x, y, rgb));
    }
    public static void fence(Area a, double x, double y, int n, boolean alongX) {
        double L = n * .5;
        if (alongX) {
            a.solid(x - .1, y - .15, L, .3);
            a.prop(x - .2, y - .2, x + L, y + .2, () -> PropRenderers.drawFence(x, y, n, true));
        } else {
            a.solid(x - .15, y - .1, .3, L);
            a.prop(x - .2, y - .2, x + .2, y + L, () -> PropRenderers.drawFence(x, y, n, false));
        }
    }
    public static void wall(Area a, double x, double y, double w, double d, double h) {
        a.solid(x, y, w, d);
        a.prop(x, y, x + w, y + d, () -> PropRenderers.drawWall(x, y, w, d, h));
    }
    public static void rock(Area a, double x, double y, int sz) {
        a.solid(x - sz / 16.0, y - sz / 16.0, sz / 8.0, sz / 8.0);
        a.prop(x - .4, y - .4, x + .4, y + .4, () -> PropRenderers.drawRock(x, y, sz));
    }
    public static void gate(Area a, double cx, double cy, double span, boolean alongX, boolean broken) {
        double u0 = (alongX ? cx : cy) - span / 2, u1 = (alongX ? cx : cy) + span / 2;
        for (int w = 0; w < 2; w++) {
            final int which = w;
            double u = (w == 0) ? u0 : u1;
            double px = alongX ? u : cx, py = alongX ? cy : u;
            a.solid(px - .3, py - .3, .6, .6);
            a.prop(px - .4, py - .4, px + .4, py + .4, () -> PropRenderers.drawTorii(cx, cy, span, alongX, broken, false, which));
        }
        if (alongX) a.prop(u0, cy - .25, u1, cy + .25, () -> PropRenderers.drawTorii(cx, cy, span, true, broken, true, 0));
        else a.prop(cx - .25, u0, cx + .25, u1, () -> PropRenderers.drawTorii(cx, cy, span, false, broken, true, 0));
    }

    public static Area buildVillage() {
        Area a = new Area();
        a.name = "Ashen Village";
        a.seed = 11;
        a.dirt = new Color[]{RenderUtils.col(0x4a3d36), RenderUtils.col(0x544539), RenderUtils.col(0x3f342f), RenderUtils.col(0x5d4c3f)};
        a.pathC = RenderUtils.col(0x76634f);
        a.grass = RenderUtils.col(0x6a6a34);
        a.path.add(new double[]{0, 16, 32, 16, 1.5});
        a.path.add(new double[]{13, 16, 13, 4, 1.2});
        a.path.add(new double[]{13, 16, 13, 32, 1.2});
        a.circ.add(new double[]{13, 16, 3.6, 0x655c52});
        a.rects.add(new double[]{14.2, 17.2, 2.2, 2, 0x2a2220});
        a.exX0 = RenderUtils.MAP - 1.7;
        a.exX1 = RenderUtils.MAP;
        a.exY0 = 13;
        a.exY1 = 19;
        a.target = 1;
        a.spawnX = 3.8;
        a.spawnY = 16;
        a.arrowDX = 1;
        gate(a, RenderUtils.MAP - 2.6, 16, 6.6, false, false);

        house(a, 3, 5, 5, 4, 26, 0x7a6a58, 0x5a2e2c, 0);
        house(a, 16, 4, 5, 4.5, 28, 0x6f6252, 0x4f2f2c, 1);
        house(a, 24.5, 6, 4, 4, 24, 0x756753, 0x5c3230, 2);
        house(a, 4, 22, 5, 5, 27, 0x74685a, 0x562c2a, 1);
        house(a, 16.5, 22, 5, 4, 25, 0x6e6150, 0x5a3431, 0);
        house(a, 24.5, 21.5, 4, 4, 24, 0x786a58, 0x4e2a2a, 3);

        a.solid(10.4, 13, 1.4, 1.4);
        a.prop(10.4, 13, 11.8, 14.4, () -> PropRenderers.drawWell(10.4, 13));
        a.solid(14.6, 17.6, 1.2, 1.2);
        a.prop(14.6, 17.6, 15.8, 18.8, () -> PropRenderers.drawFire(15.2, 18.2, false));
        a.solid(18.5, 19.2, 2.4, 1.3);
        a.prop(18.5, 19.2, 20.9, 20.5, () -> PropRenderers.drawCart(18.5, 19.2));
        a.solid(8.6, 10.5, 1.4, .8);
        a.prop(8.6, 10.5, 10, 11.3, () -> PropRenderers.drawCrates(8.6, 10.5));
        a.solid(22, 11, 1.4, .8);
        a.prop(22, 11, 23.4, 11.8, () -> PropRenderers.drawCrates(22, 11));
        a.solid(28.2, 26.6, 1.4, .8);
        a.prop(28.2, 26.6, 29.6, 27.4, () -> PropRenderers.drawCrates(28.2, 26.6));
        for (double[] b : new double[][]{{9.2, 19.7}, {9.9, 20.2}, {22.4, 19.4}, {5.6, 11.6}}) {
            a.solid(b[0] - .3, b[1] - .3, .6, .6);
            a.prop(b[0] - .35, b[1] - .35, b[0] + .35, b[1] + .35, () -> PropRenderers.drawBarrel(b[0], b[1]));
        }
        for (double[] l : new double[][]{{6.8, 14.4}, {6.8, 17.6}, {19.5, 14.3}, {19.5, 17.7}, {23.5, 14.3}, {23.5, 17.7}}) lamp(a, l[0], l[1]);
        banner(a, 27.4, 13.5, 0x7a1e22);
        banner(a, 27.4, 18.5, 0x7a1e22);
        banner(a, 9.6, 11.2, 0x6a1a24);
        fence(a, 4, 11.2, 6, true);
        fence(a, 17, 10.8, 5, true);
        fence(a, 22.8, 6.5, 6, false);
        fence(a, 3, 20, 5, true);
        wall(a, 8.6, 5, .4, 3, 20);
        wall(a, 21.6, 4.2, .4, 3.2, 24);
        wall(a, 2, 28.6, 3, .4, 17);
        double[][] ts = {{1.5, 3, 130}, {1.8, 12.5, 110}, {2, 19, 120}, {1.4, 29.5, 100}, {7, 2.2, 125}, {10, 29.5, 115}, {28.6, 2.4, 135},
                {29.6, 12, 105}, {29.4, 28.8, 120}, {22.5, 30, 110}, {11, 3, 95}, {20.5, 12.2, 100}, {13.5, 24.5, 80}, {5.5, 30, 90}};
        for (int i = 0; i < ts.length; i++) tree(a, ts[i][0], ts[i][1], (int) ts[i][2], 3 + i * 5);
        rock(a, 7.8, 12.8, 7);
        rock(a, 20.4, 28.4, 8);
        rock(a, 28, 9.2, 6);
        return a;
    }

    public static Area buildShrine() {
        Area a = new Area();
        a.name = "Hollow Shrine";
        a.seed = 23;
        a.dirt = new Color[]{RenderUtils.col(0x3a3d3a), RenderUtils.col(0x434640), RenderUtils.col(0x30332f), RenderUtils.col(0x4b4e47)};
        a.pathC = RenderUtils.col(0x6a695f);
        a.grass = RenderUtils.col(0x3f7a66);
        a.path.add(new double[]{0, 16, 16, 16, 1.5});
        a.path.add(new double[]{16, 16, 16, 9, 1.3});
        a.path.add(new double[]{16, 16, 32, 16, 1.3});
        a.rects.add(new double[]{19, 20, 11, 10, 0x2a2826});
        a.circ.add(new double[]{16, 11.2, 3.6, 0x6f6d64});
        a.water = new double[]{3, 22, 7, 6};
        a.exX0 = 0;
        a.exX1 = 1.7;
        a.exY0 = 13;
        a.exY1 = 19;
        a.target = 0;
        a.spawnX = RenderUtils.MAP - 3.8;
        a.spawnY = 16;
        a.arrowDX = -1;
        gate(a, 2.6, 16, 6.6, false, true);

        a.solid(12, 2.5, 8, 6.5);
        a.prop(12, 2.5, 20, 10, () -> PropRenderers.drawShrine(12, 2.5, 8, 6.5));
        gate(a, 16, 12.4, 6, true, false);
        a.solid(24.5, 11, 2, 2);
        a.prop(24.5, 11, 27.5, 13.8, () -> PropRenderers.drawStatue(24.5, 11));
        a.solid(25, 3, 2.6, 2.6);
        a.prop(25, 3, 28.2, 5.6, () -> PropRenderers.drawTower(25, 3, 2.6, 46));
        a.solid(3, 22, 7, 6);
        for (double[] r : new double[][]{{3.6, 21.8}, {6.2, 21.7}, {9.4, 21.9}, {2.8, 24.5}, {10.3, 26}})
            a.prop(r[0] - .2, r[1] - .2, r[0] + .2, r[1] + .2, () -> PropRenderers.drawReeds(r[0], r[1]));
        for (double[] t : new double[][]{{9, 14.4}, {9, 17.6}, {13, 14.4}, {13, 17.6}, {19.5, 14.4}, {19.5, 17.6}, {12.4, 10.6}, {19.6, 10.6}})
            toro(a, t[0], t[1], 0x6af0d4);
        a.solid(6.4, 12.4, 1.2, 1.2);
        a.prop(6.4, 12.4, 7.6, 13.6, () -> PropRenderers.drawFire(7, 13, true));
        banner(a, 11.2, 11.4, 0x2a5a60);
        banner(a, 20.8, 11.4, 0x2a5a60);

        for (int j = 0; j < 4; j++) {
            for (int i = 0; i < 5; i++) {
                if (RenderUtils.hash(i, j, 99) < .15) continue;
                final double tx = 20.5 + 2.1 * i, ty = 21.2 + 2.2 * j;
                final int th = 6 + (int) (RenderUtils.hash(i, j, 5) * 5);
                final boolean cross = RenderUtils.hash(i, j, 6) > .65;
                a.solid(tx, ty, .7, .3);
                a.prop(tx - .15, ty, tx + .85, ty + .35, () -> PropRenderers.drawTomb(tx, ty, th, cross));
            }
        }
        fence(a, 19, 19.6, 22, true);
        for (double[] w : new double[][]{{22, 24.5, 5}, {26.5, 27, 4}, {23.5, 29, 6}}) a.wisps.add(new double[]{w[0], w[1], w[2]});
        for (double[] w : new double[][]{{6, 15, 12}, {18, 6, 20}}) a.wisps.add(w);
        double[][] ts = {{3, 4, 120}, {6, 8.5, 105}, {9, 3, 125}, {2.5, 10.5, 100}, {28.8, 8.6, 115}, {29.5, 17.8, 95}, {28.4, 22, 110},
                {12.6, 24, 105}, {14.8, 29, 120}, {1.8, 29.6, 100}, {30, 2, 130}, {22.8, 7.6, 90}, {1.6, 20, 95}, {16.5, 26.6, 85}, {10.5, 7.5, 85}};
        for (int i = 0; i < ts.length; i++) tree(a, ts[i][0], ts[i][1], (int) ts[i][2], 7 + i * 4);
        rock(a, 7.8, 6.8, 8);
        rock(a, 27.6, 20.4, 7);
        rock(a, 11.2, 29.4, 9);
        rock(a, 3.4, 18.4, 6);
        return a;
    }
}
