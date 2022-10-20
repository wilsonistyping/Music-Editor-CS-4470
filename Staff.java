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

            Image noteImage;
            noteImage = getNoteType(note);
            g.drawImage(noteImage, note.getXPositionPoint(), note.getYPositionPoint(), null);
            if (note.hasAccidental()) { drawAccidental(note, g); }
            if (note.isSelected()) {
                System.out.println("Note is selected");
                note.drawOutline(g);

                if (note.hasAccidental()) { note.getAccidental().drawOutline(g); }
                g.setColor(Color.black);
            }


        }

        // Dragged note
        if (draggedNoteExists) {
            Image draggedNoteType = getNoteType(draggedNote);
            g.drawImage(draggedNoteType, draggedNote.getXPositionPoint(), draggedNote.getYPositionPoint(), null);

            // If there's an accidental associated with the note, draw that too
            if (draggedNote.hasAccidental()) {
                System.out.println("Dragged note has accidental!");
                Accidental accidental = draggedNote.getAccidental();
                Image selectedAccidentalImage = getAccidentalType(accidental);
                g.drawImage(selectedAccidentalImage, accidental.getX(), accidental.getY(), null);
                g.drawRect(accidental.getX(), accidental.getY(), accidental.getWidth(), accidental.getHeight());
            }
        }
        // Dragged accidental
        if (draggedAccidentalExists) {
            Image draggedAccidentalType = getAccidentalType(draggedAccidental);
            g.drawImage(draggedAccidentalType, draggedAccidental.getX(), draggedAccidental.getY(), null);
            for (int i = 0; i < notesList.size(); i++) {
                Note currNote = notesList.get(i);
                g.setColor(Color.blue);
                g.drawRect(currNote.getXPositionPoint(), currNote.getYPositionPoint(), currNote.getWidth(), currNote.getHeight());
            }
        }
        // Selected note
        if (selectedNoteExists) {
            Image selectedNoteImage = getNoteType(selectedNote);
            g.drawImage(selectedNoteImage, selectedNote.getXPositionPoint(), selectedNote.getYPositionPoint(), null);
            g.setColor(Color.blue);
            g.drawRect(selectedNote.getXPositionPoint(), selectedNote.getYPositionPoint(), selectedNote.getWidth(), selectedNote.getHeight());

            // If there's an accidental associated with the note, draw that too
            if (selectedNote.hasAccidental()) {
                Accidental accidental = selectedNote.getAccidental();
                Image selectedAccidentalImage = getAccidentalType(accidental);
                g.drawImage(selectedAccidentalImage, accidental.getX(), accidental.getY(), null);
                g.drawRect(accidental.getX(), accidental.getY(), accidental.getWidth(), accidental.getHeight());
            }
        }
        if (selectedAccidentalExists) {
            g.drawRect(selectedAccidental.getX(), selectedAccidental.getY(), selectedAccidental.getWidth(), selectedAccidental.getHeight());
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

    private void drawAccidental(Note note, Graphics g) {
        Image accidentalImage;
        accidentalImage = getAccidentalType(note.getAccidental());
        if (note.getDuration() == MusicConstants.WHOLE_NOTE) {
            g.drawImage(accidentalImage, note.getXPositionPoint() - 10, note.getYPositionPoint() - 8, null);
        }
        else if (note.getDuration() == MusicConstants.HALF_NOTE) {
            g.drawImage(accidentalImage, note.getXPositionPoint(), note.getYPositionPoint() + 20, null);
        }
        else if (note.getDuration() == MusicConstants.QUARTER_NOTE) {
            g.drawImage(accidentalImage, note.getXPositionPoint() - 10, note.getYPositionPoint() + 20, null);
        }
        else if (note.getDuration() == MusicConstants.EIGHTH_NOTE) {
            g.drawImage(accidentalImage, note.getXPositionPoint(), note.getYPositionPoint() + 20, null);
        }
        else if (note.getDuration() == MusicConstants.SIXTEENTH_NOTE) {
            g.drawImage(accidentalImage, note.getXPositionPoint() - 10, note.getYPositionPoint() + 20, null);
        }
    }

    private Image getNoteType(Note note) {
        int type = note.getType();
        int duration = note.getDuration();

        if (type == MusicConstants.SYMBOL_NOTE) {
            switch (duration) {
                case MusicConstants.WHOLE_NOTE:
                    return wholeNoteImage;
                case MusicConstants.HALF_NOTE:
                    return halfNoteImage;
                case MusicConstants.QUARTER_NOTE:
                    return quarterNoteImage;
                case MusicConstants.EIGHTH_NOTE:
                    return eightNoteImage;
                case MusicConstants.SIXTEENTH_NOTE:
                    return sixteenthNoteImage;
            }
        }
        else if (type == MusicConstants.SYMBOL_REST) {
            switch (duration) {
                case MusicConstants.WHOLE_NOTE:
                    return wholeRestImage;
                case MusicConstants.HALF_NOTE:
                    return halfRestImage;
                case MusicConstants.QUARTER_NOTE:
                    return quarterRestImage;
                case MusicConstants.EIGHTH_NOTE:
                    return eightRestImage;
                case MusicConstants.SIXTEENTH_NOTE:
                    return sixteenthRestImage;
            }
        }
        else {
            System.out.println("Something bad happened in getNoteType() :(");
        }
        return null;
    }

    private Image getAccidentalType(Accidental accidental) {
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
    public void addAccidentalIfValid(Accidental accidental) {
        int x = accidental.getX();
        int y = accidental.getY();

        for (int i = 0; i < notesList.size(); i++) {
            Note currNote = notesList.get(i);
            // Check if x and y are in bounds
            boolean inXBounds = (x >= currNote.getXPositionPoint() && x <= currNote.getXEnd());
            boolean inYBounds = (y >= currNote.getYPositionPoint() && y <= currNote.getYEnd());
            if (inXBounds && inYBounds) {
                currNote.setAccidental(accidental);
                break;
            }
        }
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
