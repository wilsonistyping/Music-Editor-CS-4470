import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class Accidental extends JComponent {
    private int x;
    private int y;
    private int width;
    private int height;
    private int type;
    private boolean isSelected = false;

    private static Image flatImage;
    private static Image sharpImage;

    static {
        try {
            flatImage = ImageIO.read(Staff.class.getResource("/images/flat.png"));
            sharpImage = ImageIO.read(Staff.class.getResource("/images/sharp.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Accidental(int x, int y, int type) {
        this.x = x;
        this.y = y;
        this.type = type;

        this.width = (type == MusicConstants.SYMBOL_FLAT) ? 10 : 8;
        this.height = (type == MusicConstants.SYMBOL_FLAT) ? 20 : 24;
    }

    @Override
    public void paint(Graphics g) {
        Image accidentalImage = Accidental.getAccidentalType(this);
        g.drawImage(accidentalImage, this.getX(), this.getY(), null);
    }

    public void paint(Note note, Graphics g) {
        if (note.getDuration() == MusicConstants.WHOLE_NOTE) {
            this.x = note.getXPositionPoint() - 10;
            this.y = note.getYPositionPoint() - 8;
        }
        else if (note.getDuration() == MusicConstants.HALF_NOTE) {
            this.x = note.getXPositionPoint();
            this.y = note.getYPositionPoint() + 20;
        }
        else if (note.getDuration() == MusicConstants.QUARTER_NOTE) {
            this.x = note.getXPositionPoint() - 10;
            this.y = note.getYPositionPoint() + 20;
        }
        else if (note.getDuration() == MusicConstants.EIGHTH_NOTE) {
            this.x = note.getXPositionPoint();
            this.y = note.getYPositionPoint() + 20;
        }
        else if (note.getDuration() == MusicConstants.SIXTEENTH_NOTE) {
            this.x = note.getXPositionPoint() - 10;
            this.y = note.getYPositionPoint() + 20;
        }
        this.paint(g);
    }

    public static Image getAccidentalType(Accidental accidental) {
        int type = accidental.getType();
        if (type == MusicConstants.SYMBOL_FLAT) {
            return flatImage;
        }
        else if (type == MusicConstants.SYMBOL_SHARP) {
            return sharpImage;
        }
        else {
            System.out.println("Something bad happened in getAccidentalType() :(");
        }
        return null;
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
