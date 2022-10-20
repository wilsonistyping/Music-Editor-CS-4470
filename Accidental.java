import java.awt.*;

public class Accidental {
    private int x;
    private int y;
    private int width;
    private int height;
    private int type;
    private boolean isSelected = false;

    public Accidental(int x, int y, int type) {
        this.x = x;
        this.y = y;
        this.type = type;

        this.width = (type == MusicConstants.SYMBOL_FLAT) ? 10 : 8;
        this.height = (type == MusicConstants.SYMBOL_FLAT) ? 20 : 24;


    }

    public int getType() {
        return type;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setX(int x) {
        this.x = x;
    }
    public void setY(int y) {
        this.y = y;
    }
    public boolean isSelected() {
        return this.isSelected;
    }
    public void setSelected(boolean bool) {
        this.isSelected = bool;
    }

    public void drawOutline(Graphics g) {
        g.setColor(Color.blue);
        g.drawRect(this.getX(), this.getY(), this.getWidth(), this.getHeight());
        g.setColor(Color.black);
    }
}
