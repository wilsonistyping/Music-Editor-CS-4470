import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class Staff extends JComponent {
    private int x;
    private int y;
    private ArrayList<Note> notesList = new ArrayList<>();
    private boolean lastStaff;

    private static Note draggedNote;
    private static boolean draggedNoteExists = false;

    private static Accidental draggedAccidental;
    private static boolean draggedAccidentalExists = false;

    private static Note selectedNote;
    private static boolean selectedNoteExists = false;

    private static Accidental selectedAccidental;
    private static boolean selectedAccidentalExists = false;

    private static final int WIDTH = 1000;

    private static Image trebleClefImage;
    private static Image commonTimeImage;


    private static Image flatImage;
    private static Image sharpImage;


    static {
        try {
            trebleClefImage = ImageIO.read(Staff.class.getResource("/images/trebleClef.png"));
            commonTimeImage = ImageIO.read(Staff.class.getResource("/images/commonTime.png"));

            flatImage = ImageIO.read(Staff.class.getResource("/images/flat.png"));
            sharpImage = ImageIO.read(Staff.class.getResource("/images/sharp.png"));


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Staff(int x, int y, boolean lastStaff) {
        // Add padding of 30 px on top and left
        this.x = x + 30;
        this.y = y + 30;
        this.lastStaff = lastStaff;
    }


    @Override
    public void paint(Graphics g) {
        // Draw staff
        g.drawImage(trebleClefImage, x + 2, y - 7, null);
        g.drawImage(commonTimeImage, x + 50, y + 2, null);

        // Draw notes
        for (int i = 0; i < notesList.size(); i++) {
            Note note = notesList.get(i);
            note.paint(g);
        }

        // Draw any notes being dragged, if applicable
        if (draggedNoteExists) {
            draggedNote.paint(g);
        }

        // Draw any accidentals being dragged, if applicable
        if (draggedAccidentalExists) {
            draggedAccidental.paint(g);
            for (int i = 0; i < notesList.size(); i++) {
                Note currNote = notesList.get(i);
                currNote.drawOutline(g);
            }
        }
        // Draw any selected notes with an outline, if applicable
        if (selectedNoteExists) {
            selectedNote.paint(g);
            selectedNote.drawOutline(g);
        }
        if (selectedAccidentalExists) {
            selectedAccidental.drawOutline(g);
        }


        g.setColor(Color.black);
        // Horizontal lines
        g.drawLine(x, y, x + WIDTH, y);
        g.drawLine(x, 15 + y, x + WIDTH, 15 + y);
        g.drawLine(x, 30 + y, x + WIDTH, 30 + y);
        g.drawLine(x, 45 + y, x + WIDTH, 45 + y);
        g.drawLine(x, 60 + y, x + WIDTH, 60 + y);
        // Vertical lines
        if (lastStaff) {
            g.drawLine(x + 980, y, x + 980, 60 + y);
            g.fillRect(x + 990, y, 11, 60);
        } else {
            g.drawLine(x + WIDTH, y, x + WIDTH, 60 + y);
        }
        // Draw measures
        g.drawLine(x, y, x, 60 + y);
        g.drawLine(x + WIDTH/3, y, x + WIDTH/3, 60 + y);
        g.drawLine(x + (2 * WIDTH/3), y, x + (2 * WIDTH/3), 60 + y);
    }

    // Getters
    public int getX() { return this.x; }
    public int getY() { return this.y; }
    public ArrayList<Note> getNotes() { return notesList; }
    public boolean isLastStaff() { return lastStaff; }

    // Setters
    public void setX(int x) {
        this.x = x;
    }
    public void setY(int y) { this.y = y; }
    public void setPos(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public void setLastStaff(boolean val) { this.lastStaff = val; }


    public void addNote(Note note) { notesList.add(note); }
    public void removeNote(int index) {
        notesList.remove(index);
    }

    // Dragged stuff
    public void setDraggedNote(Note note) {
        draggedNote = note;
        draggedNoteExists = true;
    }
    public void setDraggedNoteExists(boolean bool) {
        draggedNoteExists = bool;
    }

    public void setDraggedAccidental(Accidental accidental) {
        draggedAccidental = accidental;
        draggedAccidentalExists = true;
    }
    public boolean addAccidentalIfValid(Accidental accidental) {
        int x = accidental.getX();
        int y = accidental.getY();

        for (int i = 0; i < notesList.size(); i++) {
            Note currNote = notesList.get(i);
            // Check if x and y are in bounds
            boolean inXBounds = (x >= currNote.getXPositionPoint() && x <= currNote.getXEnd());
            boolean inYBounds = (y >= currNote.getYPositionPoint() && y <= currNote.getYEnd());
            if (inXBounds && inYBounds) {
                String originalPitch = currNote.getPitch();
                currNote.setAccidental(accidental);
                Main.statusLabel.setText(originalPitch + " changed to " + currNote.getPitch());
                System.out.println(currNote.hasAccidental());
                return true;
            }
        }
        return false;
    }
    public void setDraggedAccidentalExists(boolean bool) {
        draggedAccidentalExists = bool;
    }

    // Selected stuff
    public void setSelectedNote(Note note) {
        selectedNote = note;
        selectedNoteExists = true;
    }
    public void setSelectedNoteExists(boolean bool) {
        selectedNoteExists = bool;
    }

    public void setSelectedAccidental(Accidental accidental, Note note) {
        selectedAccidental = accidental;
        Note associatedNote = note;
        selectedAccidentalExists = true;
    }
    public void setSelectedAccidentalExists(boolean bool) {
        selectedAccidentalExists = bool;
    }
}
