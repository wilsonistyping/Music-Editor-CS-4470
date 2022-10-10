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
    private static Note selectedNote;
    private static int selectedNoteIndex;
    private static boolean selectedNoteExists = false;

    private static final int WIDTH = 1000;

    private static Image trebleClefImage;
    private static Image commonTimeImage;

    private static Image sixteenthNoteImage;
    private static Image eightNoteImage;
    private static Image quarterNoteImage;
    private static Image halfNoteImage;
    private static Image wholeNoteImage;

    private static Image flatImage;
    private static Image sharpImage;
    private static Image naturalImage;

    private static Image sixteenthRestImage;
    private static Image eightRestImage;
    private static Image quarterRestImage;
    private static Image halfRestImage;
    private static Image wholeRestImage;

    static {
        try {
            trebleClefImage = ImageIO.read(Staff.class.getResource("/images/trebleClef.png"));
            commonTimeImage = ImageIO.read(Staff.class.getResource("/images/commonTime.png"));

            flatImage = ImageIO.read(Staff.class.getResource("/images/flat.png"));
            sharpImage = ImageIO.read(Staff.class.getResource("/images/sharp.png"));
            naturalImage = ImageIO.read(Staff.class.getResource("/images/natural.png"));

            sixteenthNoteImage = ImageIO.read(MusicView.class.getResource("/images/sixteenthNote.png"));
            eightNoteImage = ImageIO.read(MusicView.class.getResource("/images/eighthNote.png"));
            quarterNoteImage = ImageIO.read(MusicView.class.getResource("/images/quarterNote.png"));
            halfNoteImage = ImageIO.read(MusicView.class.getResource("/images/halfNote.png"));
            wholeNoteImage = ImageIO.read(MusicView.class.getResource("/images/wholeNote.png"));

            sixteenthRestImage = ImageIO.read(Staff.class.getResource("/images/sixteenthRest.png"));
            eightRestImage = ImageIO.read(Staff.class.getResource("/images/eighthRest.png"));
            quarterRestImage = ImageIO.read(Staff.class.getResource("/images/quarterRest.png"));
            halfRestImage = ImageIO.read(Staff.class.getResource("/images/halfRest.png"));
            wholeRestImage = ImageIO.read(Staff.class.getResource("/images/wholeRest.png"));
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

            // When selection is on...
//            System.out.println("SelectedNoteExists = " + selectedNoteExists);
//            System.out.println("SelectedNoteIndex = " + selectedNoteIndex);
//            System.out.println("-------");
            Image noteType;
            noteType = getNoteType(note);
            g.drawImage(noteType, note.getXPositionPoint(), note.getYPositionPoint(), null);
        }

        // Dragged note
        if (draggedNoteExists) {
            Image draggedNoteType = getNoteType(draggedNote);
            g.drawImage(draggedNoteType, draggedNote.getXPositionPoint(), draggedNote.getYPositionPoint(), null);
        }
        if (selectedNoteExists) {
            Image selectedNoteType = getNoteType(selectedNote);
            g.drawImage(selectedNoteType, selectedNote.getXPositionPoint(), selectedNote.getYPositionPoint(), null);
            g.setColor(Color.blue);
            g.drawRect(selectedNote.getXPositionPoint(), selectedNote.getYPositionPoint(), selectedNote.getWidth(), selectedNote.getHeight());
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
        g.drawLine(x, y, x, 60 + y);
        g.drawLine(x + WIDTH/3, y, x + WIDTH/3, 60 + y);
        g.drawLine(x + (2 * WIDTH/3), y, x + (2 * WIDTH/3), 60 + y);
    }

    private Image getNoteType(Note note) {
        /* Note type:
         * 0 - note
         * 1 - rest
         * 2 - flat
         * 3 - sharp
         */

        /* Note duration:
         * 0 - whole
         * 1 - half
         * 2 - quarter
         * 3 - eighth
         * 4 - sixteenth
         * */
        int type = note.getType();
        int duration = note.getDuration();

        if (type == 0) {
            switch (duration) {
                case 0:
                    return wholeNoteImage;
                case 1:
                    return halfNoteImage;
                case 2:
                    return quarterNoteImage;
                case 3:
                    return eightNoteImage;
                case 4:
                    return sixteenthNoteImage;
            }
        }
        else if (type == 1) {
            switch (duration) {
                case 0:
                    return wholeRestImage;
                case 1:
                    return halfRestImage;
                case 2:
                    return quarterRestImage;
                case 3:
                    return eightRestImage;
                case 4:
                    return sixteenthRestImage;
            }
        }
        else if (type == 2) {
            return flatImage;
        }
        else if (type == 3) {
            return sharpImage;
        } else {
            System.out.println("Didn't work lol");
        }
        return null;
    }

    public static int getNoteHeight(Note note) {
        /* Note type:
         * 0 - note
         * 1 - rest
         * 2 - flat
         * 3 - sharp
         */

        /* Note duration:
         * 0 - whole
         * 1 - half
         * 2 - quarter
         * 3 - eighth
         * 4 - sixteenth
         * */
        int type = note.getType();
        int duration = note.getDuration();

        if (type == 0) {
            switch (duration) {
                case 0:
                    return wholeNoteImage.getHeight(null);
                case 1:
                    return halfNoteImage.getHeight(null);
                case 2:
                    return quarterNoteImage.getHeight(null);
                case 3:
                    return eightNoteImage.getHeight(null);
                case 4:
                    return sixteenthNoteImage.getHeight(null);
            }
        }
        else if (type == 1) {
            switch (duration) {
                case 0:
                    return wholeRestImage.getHeight(null);
                case 1:
                    return halfRestImage.getHeight(null);
                case 2:
                    return quarterRestImage.getHeight(null);
                case 3:
                    return eightRestImage.getHeight(null);
                case 4:
                    return sixteenthRestImage.getHeight(null);
            }
        }
        else if (type == 2) {
            return flatImage.getHeight(null);
        }
        else if (type == 3) {
            return sharpImage.getHeight(null);
        } else {
            System.out.println("Didn't work lol");
        }
        return -1;
    }

    public static int getNoteWidth(Note note) {
        /* Note type:
         * 0 - note
         * 1 - rest
         * 2 - flat
         * 3 - sharp
         */

        /* Note duration:
         * 0 - whole
         * 1 - half
         * 2 - quarter
         * 3 - eighth
         * 4 - sixteenth
         * */
        int type = note.getType();
        int duration = note.getDuration();

        if (type == 0) {
            switch (duration) {
                case 0:
                    return wholeNoteImage.getWidth(null);
                case 1:
                    return halfNoteImage.getWidth(null);
                case 2:
                    return quarterNoteImage.getWidth(null);
                case 3:
                    return eightNoteImage.getWidth(null);
                case 4:
                    return sixteenthNoteImage.getWidth(null);
            }
        }
        else if (type == 1) {
            switch (duration) {
                case 0:
                    return wholeRestImage.getWidth(null);
                case 1:
                    return halfRestImage.getWidth(null);
                case 2:
                    return quarterRestImage.getWidth(null);
                case 3:
                    return eightRestImage.getWidth(null);
                case 4:
                    return sixteenthRestImage.getWidth(null);
            }
        }
        else if (type == 2) {
            return flatImage.getWidth(null);
        }
        else if (type == 3) {
            return sharpImage.getWidth(null);
        } else {
            System.out.println("Didn't work lol");
        }
        return -1;
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
    public void setDraggedNote(Note note) {
        draggedNote = note;
        draggedNoteExists = true;
    }
    public void setDraggedNoteExists(boolean bool) {
        draggedNoteExists = bool;
    }
    public void setSelectedNote(Note note) {
        selectedNote = note;
        selectedNoteExists = true;
    }
    public void setSelectedNoteIndex(int i) {
        selectedNoteIndex = i;
    }
    public void setSelectedNoteExists(boolean bool) {
        selectedNoteExists = bool;
    }
}
