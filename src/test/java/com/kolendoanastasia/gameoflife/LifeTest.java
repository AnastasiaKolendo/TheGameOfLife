package com.kolendoanastasia.gameoflife;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class LifeTest {
    private Life life;

    private void fill(String[] lines) {
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            for (int j = 0; j < line.length(); j++) {
                if (line.charAt(j) != ' ') {
                    life.setAlive(i, j, true);
                }
            }
        }
    }

    private void assertField(String[] lines) {
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            for (int j = 0; j < life.getNumberOfRows(); j++) {
                boolean expected = line.charAt(j) != ' ';
                assertEquals(life.isAlive(i, j), expected);
            }
        }
    }

    @Test
    public void blinker() {
        life = new Life(5, 5);
        fill(new String[]{
                "     ",
                "     ",
                " *** ",
                "     ",
                "     "
        });

        life.evolve();

        assertField(new String[]{
                "     ",
                "  *  ",
                "  *  ",
                "  *  ",
                "     "
        });
    }
    @Test
    public void beehive() {
        life = new Life(5, 6);
        fill(new String[]{
                "      ",
                "  **  ",
                " *  * ",
                "  **  ",
                "      "
        });

        life.evolve();

        assertField(new String[]{
                "      ",
                "  **  ",
                " *  * ",
                "  **  ",
                "      "
        });
    }
    @Test
    public void block() {
        life = new Life(4, 4);
        fill(new String[]{
                "    ",
                " ** ",
                " ** ",
                "    "
        });

        life.evolve();

        assertField(new String[]{
                "    ",
                " ** ",
                " ** ",
                "    "
        });
    }
    @Test
    public void toad() {
        life = new Life(6, 6);
        fill(new String[]{
                "      ",
                "      ",
                "  *** ",
                " ***  ",
                "      ",
                "      "
        });

        life.evolve();

        assertField(new String[]{
                "      ",
                "   *  ",
                " *  * ",
                " *  * ",
                "  *   ",
                "      "
        });
    }
    @Test
    public void beacon() {
        life = new Life(6, 6);
        fill(new String[]{
                "      ",
                " **   ",
                " **   ",
                "   ** ",
                "   ** ",
                "      "
        });

        life.evolve();

        assertField(new String[]{
                "      ",
                " **   ",
                " *    ",
                "    * ",
                "   ** ",
                "      "
        });
    }
    @Test
    public void loaf() {
        life = new Life(6, 6);
        fill(new String[]{
                "      ",
                "  **  ",
                " *  * ",
                "  * * ",
                "   *  ",
                "      "
        });

        life.evolve();

        assertField(new String[]{
                "      ",
                "  **  ",
                " *  * ",
                "  * * ",
                "   *  ",
                "      "
        });
    }

}