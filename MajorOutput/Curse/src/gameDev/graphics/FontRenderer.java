package gameDev.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;

public class FontRenderer {

    public static final Map<Character, String> FONT = new HashMap<>();
    static {
        String[] f = {
            " 000000000000000", "A010101111101101", "B110101110101110", "C011100100100011", "D110101101101110",
            "E111100110100111", "F111100110100100", "G011100101101011", "H101101111101101", "I111010010010111",
            "J001001001101010", "K101101110101101", "L100100100100111", "M101111111101101", "N110101101101101",
            "O010101101101010", "P110101110100100", "Q010101101111011", "R110101110101101", "S011100010001110",
            "T111010010010010", "U101101101101111", "V101101101101010", "W101101111111101", "X101101010101101",
            "Y101101010010010", "Z111001010100111", "0111101101101111", "1010110010010111", "2110001010100111",
            "3110001010001110", "4101101111001001", "5111100110001110", "6011100111101111", "7111001010010010",
            "8111101111101111", "9111101111001110", "-000000111000000", ".000000000000010", ":000010000010000",
            "/001001010100100", ">100010001010100", "<001010100010001", "!010010010000010", ",000000000010100"
        };
        for (String s : f) FONT.put(s.charAt(0), s.substring(1));
    }

    public static void text(Graphics2D g, String s, int x, int y, int sc, Color c) {
        g.setColor(c);
        for (char ch : s.toUpperCase().toCharArray()) {
            String bits = FONT.get(ch);
            if (bits != null) {
                for (int r = 0; r < 5; r++) {
                    for (int k = 0; k < 3; k++) {
                        if (bits.charAt(r * 3 + k) == '1') {
                            g.fillRect(x + k * sc, y + r * sc, sc, sc);
                        }
                    }
                }
            }
            x += 4 * sc;
        }
    }

    public static void textS(Graphics2D g, String s, int x, int y, int sc, Color c) {
        text(g, s, x + sc, y + sc, sc, new Color(0, 0, 0, c.getAlpha()));
        text(g, s, x, y, sc, c);
    }

    public static int textW(String s, int sc) {
        return s.length() * 4 * sc - sc;
    }
}
