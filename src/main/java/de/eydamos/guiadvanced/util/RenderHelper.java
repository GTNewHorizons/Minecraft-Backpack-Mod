package de.eydamos.guiadvanced.util;

public class RenderHelper {
    public enum BackgroundRepeat {
        NONE,
        REPEAT,
        REPEAT_X,
        REPEAT_Y,
        STRETCH
    }

    public static void drawOuterCornerTopLeft(int posX, int posY) {
        Rectangle outerCornerTopLeft = new Rectangle(4, 4);
        outerCornerTopLeft.draw(posX, posY);
    }

    public static void drawOuterCornerTopRight(int posX, int posY) {
        Rectangle outerCornerTopRight = new Rectangle(4, 4);
        outerCornerTopRight.setBackgroundPosition(172, 0);
        outerCornerTopRight.draw(posX, posY);
    }

    public static void drawOuterCornerBottomLeft(int posX, int posY) {
        Rectangle outerCornerBottomLeft = new Rectangle(4, 4);
        outerCornerBottomLeft.setBackgroundPosition(0, 163);
        outerCornerBottomLeft.draw(posX, posY);
    }

    public static void drawOuterCornerBottomRight(int posX, int posY) {
        Rectangle outerCornerBottomRight = new Rectangle(4, 4);
        outerCornerBottomRight.setBackgroundPosition(172, 163);
        outerCornerBottomRight.draw(posX, posY);
    }

    public static void drawBorderTop(int posX, int posY, int width, int height) {
        Rectangle borderTop = new Rectangle(width, height);
        borderTop.setBackgroundPosition(4, 0);
        borderTop.setBackgroundSize(100, 4);
        borderTop.setBackgroundRepeat(BackgroundRepeat.REPEAT_X);
        borderTop.draw(posX, posY);
    }

    public static void drawBorderRight(int posX, int posY, int width, int height) {
        Rectangle borderRight = new Rectangle(width, height);
        borderRight.setBackgroundPosition(172, 4);
        borderRight.setBackgroundSize(4, 100);
        borderRight.setBackgroundRepeat(BackgroundRepeat.REPEAT_Y);
        borderRight.draw(posX, posY);
    }

    public static void drawBorderBottom(int posX, int posY, int width, int height) {
        Rectangle borderBottom = new Rectangle(width, height);
        borderBottom.setBackgroundPosition(4, 163);
        borderBottom.setBackgroundSize(100, 4);
        borderBottom.setBackgroundRepeat(BackgroundRepeat.REPEAT_X);
        borderBottom.draw(posX, posY);
    }

    public static void drawBorderLeft(int posX, int posY, int width, int height) {
        Rectangle borderLeft = new Rectangle(width, height);
        borderLeft.setBackgroundPosition(0, 4);
        borderLeft.setBackgroundSize(4, 100);
        borderLeft.setBackgroundRepeat(BackgroundRepeat.REPEAT_Y);
        borderLeft.draw(posX, posY);
    }

    public static void drawBackground(int posX, int posY, int width, int height) {
        Rectangle borderBottom = new Rectangle(width, height);
        borderBottom.setBackgroundPosition(4, 4);
        borderBottom.setBackgroundSize(100, 100);
        borderBottom.setBackgroundRepeat(BackgroundRepeat.REPEAT);
        borderBottom.draw(posX, posY);
    }
}
