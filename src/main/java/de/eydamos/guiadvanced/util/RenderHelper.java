package de.eydamos.guiadvanced.util;

public class RenderHelper {

    public enum BackgroundRepeat {
        NONE,
        REPEAT,
        REPEAT_X,
        REPEAT_Y,
        STRETCH
    }

    public static void drawOuterCornerTopLeft(Rectangle rect, int posX, int posY) {
        rect.setWidth(4);
        rect.setHeight(4);
        rect.setBackgroundPosition(0, 0);
        rect.setBackgroundSize(4, 4);
        rect.addBoxVertices(posX, posY);
    }

    public static void drawOuterCornerTopRight(Rectangle rect, int posX, int posY) {
        rect.setWidth(4);
        rect.setHeight(4);
        rect.setBackgroundSize(4, 4);
        rect.setBackgroundPosition(172, 0);
        rect.addBoxVertices(posX, posY);
    }

    public static void drawOuterCornerBottomLeft(Rectangle rect, int posX, int posY) {
        rect.setWidth(4);
        rect.setHeight(4);
        rect.setBackgroundSize(4, 4);
        rect.setBackgroundPosition(0, 163);
        rect.addBoxVertices(posX, posY);
    }

    public static void drawOuterCornerBottomRight(Rectangle rect, int posX, int posY) {
        rect.setWidth(4);
        rect.setHeight(4);
        rect.setBackgroundSize(4, 4);
        rect.setBackgroundPosition(172, 163);
        rect.addBoxVertices(posX, posY);
    }

    public static void drawBorderTop(Rectangle rect, int posX, int posY, int width, int height) {
        rect.setWidth(width);
        rect.setHeight(height);
        rect.setBackgroundPosition(4, 0);
        rect.setBackgroundSize(100, 4);
        rect.setBackgroundRepeat(BackgroundRepeat.REPEAT_X);
        rect.addBoxVertices(posX, posY);
    }

    public static void drawBorderRight(Rectangle rect, int posX, int posY, int width, int height) {
        rect.setWidth(width);
        rect.setHeight(height);
        rect.setBackgroundPosition(172, 4);
        rect.setBackgroundSize(4, 100);
        rect.setBackgroundRepeat(BackgroundRepeat.REPEAT_Y);
        rect.addBoxVertices(posX, posY);
    }

    public static void drawBorderBottom(Rectangle rect, int posX, int posY, int width, int height) {
        rect.setWidth(width);
        rect.setHeight(height);
        rect.setBackgroundPosition(4, 163);
        rect.setBackgroundSize(100, 4);
        rect.setBackgroundRepeat(BackgroundRepeat.REPEAT_X);
        rect.addBoxVertices(posX, posY);
    }

    public static void drawBorderLeft(Rectangle rect, int posX, int posY, int width, int height) {
        rect.setWidth(width);
        rect.setHeight(height);
        rect.setBackgroundPosition(0, 4);
        rect.setBackgroundSize(4, 100);
        rect.setBackgroundRepeat(BackgroundRepeat.REPEAT_Y);
        rect.addBoxVertices(posX, posY);
    }

    public static void drawBackground(Rectangle rect, int posX, int posY, int width, int height) {
        rect.setWidth(width);
        rect.setHeight(height);
        rect.setBackgroundPosition(4, 4);
        rect.setBackgroundSize(100, 100);
        rect.setBackgroundRepeat(BackgroundRepeat.REPEAT);
        rect.addBoxVertices(posX, posY);
    }
}
