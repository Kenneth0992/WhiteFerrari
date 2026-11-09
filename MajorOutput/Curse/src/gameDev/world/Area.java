package gameDev.world;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Area {
    public interface Painter { void paint(); }

    public static class Prop {
        public double x0, y0, x1, y1;
        public Painter p;
        public Prop(double x0, double y0, double x1, double y1, Painter p) {
            this.x0 = x0; this.y0 = y0; this.x1 = x1; this.y1 = y1; this.p = p;
        }
    }

    public String name;
    public int seed;
    public List<Prop> props = new ArrayList<>();
    public List<double[]> solids = new ArrayList<>();
    public List<double[]> path = new ArrayList<>();
    public List<double[]> circ = new ArrayList<>();
    public List<double[]> rects = new ArrayList<>();
    public double[] water;
    public List<double[]> wisps = new ArrayList<>();
    public Color[] dirt;
    public Color pathC;
    public Color grass;
    public double exX0, exY0, exX1, exY1;
    public int target;
    public double spawnX, spawnY;
    public double arrowDX;
    public BufferedImage ground;
    public int gox, goy;

    public void solid(double x, double y, double w, double d) {
        solids.add(new double[]{x, y, w, d});
    }

    public void prop(double x0, double y0, double x1, double y1, Painter p) {
        props.add(new Prop(x0, y0, x1, y1, p));
    }
}
