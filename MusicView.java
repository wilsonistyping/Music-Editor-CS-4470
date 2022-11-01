import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class MusicView extends JComponent implements MouseListener, MouseMotionListener, KeyListener {
    private final static int INITIAL_STAVES = 4;
    private static int preferredWidth = 1060;
    private static int preferredHeight = 480;
    private static int lastMouseX, lastMouseY;

    public static Note selectedSymbol = null;
    public static Accidental selectedAccidental = null;
    public static Note selectedAccidentalNote = null;
    public static int lastAssociatedStaffIndex;

    private ArrayList<Staff> staffArrayList = new ArrayList<>();
    private int indexOfCurrentStaff;

    // Stroke recognition variables
    ArrayList<Point2D> stroke = new ArrayList<>();
    DollarRecognizer recognizer = new DollarRecognizer();

    public MusicView() {
        setPreferredSize(new Dimension(preferredWidth, preferredHeight));

        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
        setFocusable(true);

        for (int i = 0; i < INITIAL_STAVES; i++) {
            int y_offset = i * 120;
            boolean lastStaff = (i == INITIAL_STAVES - 1);
            this.staffArrayList.add(new Staff(0, y_offset, lastStaff));
        }
        this.indexOfCurrentStaff = 0;
    }

    @Override
    protected void paintComponent(Graphics g) {
        // Draw all staves
        for (int i = 0; i < staffArrayList.size(); i++) {
            Staff currStaff = staffArrayList.get(i);
            currStaff.paint(g);
        }

        Graphics2D g2 = (Graphics2D) g;
        g.setColor(Color.red);
        g2.setStroke(new BasicStroke(2));
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Draw all line segments of the stroke
        if (!stroke.isEmpty()) {
            // Edge case of only one stroke:
            if (stroke.size() == 1) {
                int x1 = (int) stroke.get(0).getX();
                int y1 = (int) stroke.get(0).getY();
                g.drawLine(x1, y1, x1, y1);
            } else {
                for (int i = 0; i < stroke.size() - 1; i++) {
                    int x1 = (int) stroke.get(i).getX();
                    int x2 = (int) stroke.get(i + 1).getX();
                    int y1 = (int) stroke.get(i).getY();
                    int y2 = (int) stroke.get(i + 1).getY();
                    g.drawLine(x1, y1, x2, y2);
                }
            }
        }
        g.setColor(Color.black);
    }

    public void newStaff() {
        staffArrayList.add(new Staff(0, 120 * staffArrayList.size(), true));
        staffArrayList.get(getIndexOfLastStaff() - 1).setLastStaff(false);
        preferredHeight += 120;

        updateViewportDimensions();
    }

    public void deleteStaff() {
        staffArrayList.remove(getIndexOfLastStaff());
        staffArrayList.get(getIndexOfLastStaff()).setLastStaff(true);
        preferredHeight -= 120;

        updateViewportDimensions();
    }

    protected void updateViewportDimensions() {
        setPreferredSize(new Dimension(preferredWidth, preferredHeight));
        setSize(preferredWidth, preferredHeight);
        repaint();
    }

    // Event listeners


    @Override
    public void mousePressed(MouseEvent e) {
        requestFocusInWindow();
        int mouseX = e.getX();
        int mouseY = e.getY();
        int associatedStaffIndex = mouseY / 120;
        Staff associatedStaff = staffArrayList.get(associatedStaffIndex);

        // Pen mode
        if (Main.penOn) {
            stroke = new ArrayList<Point2D>();
            stroke.add(new Point2D.Double(mouseX, mouseY));
            repaint();
        }

        // Drag mode
        else if (Main.selectOn) {

            // Case 1 - note is selected, user clicks outside border
            if (selectedSymbol != null) {
                int x = selectedSymbol.getXPositionPoint();
                int y = selectedSymbol.getYPositionPoint();
                boolean inXBounds = (mouseX >= x && mouseX <= x + selectedSymbol.getWidth());
                boolean inYBounds = (mouseY >= y && mouseY <= y + selectedSymbol.getHeight());

                if (!(inXBounds && inYBounds)) {
                    selectedSymbol.setSelected(false);
                    staffArrayList.get(lastAssociatedStaffIndex).addNote(selectedSymbol);
                    staffArrayList.get(lastAssociatedStaffIndex).setSelectedNoteExists(false);
                    selectedSymbol = null;
                    associatedStaff.setSelectedNote(null);
                    associatedStaff.setSelectedNoteExists(false);
                    lastAssociatedStaffIndex = -1;
                }
            }

            if (selectedAccidental != null) {
                int x = selectedAccidental.getX();
                int y = selectedAccidental.getY();
                boolean inAccXBounds = (mouseX >= selectedAccidental.getX() && mouseX <= (selectedAccidental.getX() + selectedAccidental.getWidth()));
                boolean inAccYBounds = (mouseY >= selectedAccidental.getY() && mouseY <= (selectedAccidental.getY() + selectedAccidental.getHeight()));
                if (!(inAccXBounds && inAccYBounds)) {
                    selectedAccidental.setSelected(false);
                    staffArrayList.get(lastAssociatedStaffIndex).setSelectedAccidentalExists(false);
                    selectedAccidental = null;
                    associatedStaff.setSelectedAccidental(null, null);
                    associatedStaff.setSelectedAccidentalExists(false);
                    lastAssociatedStaffIndex = -1;
                }
            }

            // Case 2 - note is selected, user clicks inside border
            if (selectedSymbol != null) {
                // nothing happens lol
            }
            // Case 3 - no note selected, user clicks inside note region
            else {
                for (int i = 0; i < associatedStaff.getNotes().size(); i++) {
                    Note note = associatedStaff.getNotes().get(i);

                    // If mouse is within the note's accidental's bounds, then set the selectedAccidental variable to that and don't select its note.
                    Accidental acc = note.getAccidental();
                    if (acc != null) {
                        boolean inAccXBounds = (mouseX >= acc.getX() && mouseX <= (acc.getX() + acc.getWidth()));
                        boolean inAccYBounds = (mouseY >= acc.getY() && mouseY <= (acc.getY() + acc.getHeight()));
                        System.out.println("X bounds for the accidental are " + acc.getX() + " to " + (acc.getX() + acc.getWidth()));
                        System.out.println("Y bounds for the accidental are " + acc.getY() + " to " + (acc.getY() + acc.getHeight()));
                        System.out.println("Your (x, y) is " + mouseX + ", " + mouseY);
                        System.out.println(inAccXBounds);
                        System.out.println(inAccYBounds);
                        if (inAccXBounds && inAccYBounds) {
                            System.out.println("In x and y bounds");
                            selectedAccidental = acc;
                            selectedAccidentalNote = note;
                            associatedStaff.setSelectedAccidental(acc, note);
                            associatedStaff.setSelectedAccidentalExists(true);
                            lastAssociatedStaffIndex = associatedStaffIndex;
                        }
                    }

                    // Mouse is not in the note's accidental's bounds. Check if it's in the note's bounds.
                    if (selectedAccidental == null) {
                        int x = note.getXPositionPoint();
                        int y = note.getYPositionPoint();
                        boolean inXBounds = (mouseX >= x && mouseX <= x + note.getWidth());
                        boolean inYBounds = (mouseY >= y && mouseY <= y + note.getHeight());

                        // Mouse is within the note bounds
                        if (inXBounds && inYBounds) {
                            note.setSelected(true);
                            selectedSymbol = note;

                            // Turn the selected symbol into the SelectedNote type in the Staff
                            associatedStaff.setSelectedNote(note);
                            associatedStaff.setSelectedNoteExists(true);
                            lastAssociatedStaffIndex = associatedStaffIndex;

                            // Remove the symbol from its notesList in the associated staff
                            associatedStaff.getNotes().remove(i);

                            break;
                        } else {
                            selectedSymbol = null;
                            associatedStaff.setSelectedNoteExists(false);
                        }
                    }


                }
            }


        }
        // Insert mode
        else {
            // Flag for a dragged note if the symbol is note (0) or rest (1)
            if (isNoteOrRest()) {
                if (isNote()) { mouseX = xSnapAdjust(mouseX, associatedStaff); }
                Note note = new Note(mouseX, mouseY, Main.currentDuration, Main.currentTool, associatedStaffIndex);
                associatedStaff.setDraggedNote(note);
            }
            // Flag for a dragged accidental if the symbol is flat (2) or sharp (3)
            else if (isAccidental()) {
                Accidental accidental = new Accidental(mouseX, mouseY, Main.currentTool);
                associatedStaff.setDraggedAccidental(accidental);
            }
        }

        repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();
        int associatedStaffIndex = mouseY / 120;
        Staff associatedStaff = staffArrayList.get(associatedStaffIndex);

        // Pen mode
        if (Main.penOn) {
            stroke.add(new Point2D.Double(mouseX, mouseY));
            System.out.println("Drawing!");
            repaint();
        }

        // Drag mode
        else if (Main.selectOn) {
            // If there is a symbol selected, then change its position according to the mouse position
            if (selectedSymbol != null) {
                Note note = new Note(mouseX, mouseY, selectedSymbol.getDuration(), selectedSymbol.getType(), associatedStaffIndex);
                note.setAccidental(selectedSymbol.getAccidental());
                selectedSymbol.setX(mouseX);
                selectedSymbol.setY(mouseY);
                lastAssociatedStaffIndex = associatedStaffIndex;
                Main.statusLabel.setText("Pitch of this note is " + note.getPitch());
                associatedStaff.setSelectedNote(note);
            }
        }
        // Insert mode
        else {
            if (isNoteOrRest()) {
                if (isNote()) {
                    mouseX = xSnapAdjust(mouseX, associatedStaff);
                }
                Note movedNote = new Note(mouseX, mouseY, Main.currentDuration, Main.currentTool, associatedStaffIndex);
                Main.statusLabel.setText("Pitch of this note is " + movedNote.getPitch());
                associatedStaff.setDraggedNote(movedNote);
            }
            else if (isAccidental()) {
                Accidental movedAccidental = new Accidental(mouseX, mouseY, Main.currentTool);
                associatedStaff.setDraggedAccidental(movedAccidental);
            }
        }
        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();

        int associatedStaffIndex = mouseY / 120;
        Staff associatedStaff = staffArrayList.get(associatedStaffIndex);


        // Pen mode
        if (Main.penOn) {
            System.out.println(stroke.toString());

            Result result = recognizer.recognize(stroke);
            System.out.println("\nGot result: " + result.getName());
            System.out.println("\tScore=" + result.getScore());
            System.out.println("\tBounding Box =" + result.getBoundingBox());
            System.out.println(result.getName());

            int x = (int)stroke.get(0).getX();
            int y = (int)stroke.get(0).getY();

            if (!(result.getName().equals("No match"))) {
                switch (result.getName()) {
                    case "circle":
                        associatedStaff.addNote(new Note(x, y,
                                MusicConstants.WHOLE_NOTE, MusicConstants.SYMBOL_NOTE, associatedStaffIndex));
                        Main.statusLabel.setText("Added a whole note at (" + x + ", " + y + ").");
                        break;
                    case "half note":
                        associatedStaff.addNote(new Note(x, y,
                                MusicConstants.HALF_NOTE, MusicConstants.SYMBOL_NOTE, associatedStaffIndex));
                        Main.statusLabel.setText("Added a half note at (" + x + ", " + y + ").");
                        break;
                    case "quarter note":
                        associatedStaff.addNote(new Note(x, y,
                                MusicConstants.QUARTER_NOTE, MusicConstants.SYMBOL_NOTE, associatedStaffIndex));
                        Main.statusLabel.setText("Added a quarter note at (" + x + ", " + y + ").");
                        break;
                    case "eighth note":
                        associatedStaff.addNote(new Note(x, y,
                                MusicConstants.EIGHTH_NOTE, MusicConstants.SYMBOL_NOTE, associatedStaffIndex));
                        Main.statusLabel.setText("Added a eighth note at (" + x + ", " + y + ").");
                        break;
                    case "sixteenth note":
                        associatedStaff.addNote(new Note(x, y,
                                MusicConstants.SIXTEENTH_NOTE, MusicConstants.SYMBOL_NOTE, associatedStaffIndex));
                        Main.statusLabel.setText("Added a sixteenth note at (" + x + ", " + y + ").");
                        break;
                    case "rectangle":
                        associatedStaff.addNote(new Note(x, y,
                                MusicConstants.WHOLE_NOTE, MusicConstants.SYMBOL_REST, associatedStaffIndex));
                        Main.statusLabel.setText("Added a whole rest at (" + x + ", " + y + ").");
                        break;
                    case "half rest":
                        associatedStaff.addNote(new Note(x, y,
                                MusicConstants.HALF_NOTE, MusicConstants.SYMBOL_REST, associatedStaffIndex));
                        Main.statusLabel.setText("Added a half rest at (" + x + ", " + y + ").");
                        break;
                    case "right curly brace":
                        associatedStaff.addNote(new Note(x, y,
                                MusicConstants.QUARTER_NOTE, MusicConstants.SYMBOL_REST, associatedStaffIndex));
                        Main.statusLabel.setText("Added a quarter rest at (" + x + ", " + y + ").");
                        break;
                    case "eighth rest":
                        associatedStaff.addNote(new Note(x, y,
                                MusicConstants.EIGHTH_NOTE, MusicConstants.SYMBOL_REST, associatedStaffIndex));
                        Main.statusLabel.setText("Added a eighth rest at (" + x + ", " + y + ").");
                        break;
                    case "sixteenth rest":
                        associatedStaff.addNote(new Note(x, y,
                                MusicConstants.SIXTEENTH_NOTE, MusicConstants.SYMBOL_REST, associatedStaffIndex));
                        Main.statusLabel.setText("Added a sixteenth rest at (" + x + ", " + y + ").");
                        break;
                    case "flat":
                        boolean flatIsValid = associatedStaff.addAccidentalIfValid(new Accidental(x, y, MusicConstants.SYMBOL_FLAT));
                        if (flatIsValid) {
                            Main.statusLabel.setText("Added a flat at (" + x + ", " + y + ").");
                        } else {
                            Main.statusLabel.setText("Tried to add a flat at (" + x + ", " + y + "), but there was no note.");
                        }
                        break;
                    case "star":
                        boolean sharpIsValid = associatedStaff.addAccidentalIfValid(new Accidental(x, y, MusicConstants.SYMBOL_SHARP));
                        if (sharpIsValid) {
                            Main.statusLabel.setText("Added a sharp at (" + x + ", " + y + ").");
                        } else {
                            Main.statusLabel.setText("Tried to add a sharp at (" + x + ", " + y + "), but there was no note.");
                        }
                        break;
                }
            } else {
                Main.statusLabel.setText("No match for the candidate stroke :(");
            }

            stroke = new ArrayList<>();
            repaint();
        }
        // Drag mode
        else if (Main.selectOn) {
            if (selectedSymbol != null) {
                Note note = new Note(mouseX, mouseY, selectedSymbol.getDuration(), selectedSymbol.getType(), associatedStaffIndex);
                Main.statusLabel.setText("Pitch of this note is " + note.getPitch());
            }
        }
        // Insert mode
        else {
            // Note or rest
            if (isNoteOrRest()) {
                if (isNote()) {
                    mouseX = xSnapAdjust(mouseX, associatedStaff);
                }

                Note note = new Note(mouseX, mouseY, Main.currentDuration, Main.currentTool, associatedStaffIndex);
                associatedStaff.addNote(note);
                associatedStaff.setDraggedNoteExists(false);

                Main.statusLabel.setText("Pitch of this note is " + note.getPitch());
            }
            // Accidental (flat or sharp)
           else if (isAccidental()) {
               Accidental accidental = new Accidental(mouseX, mouseY, Main.currentTool);
               associatedStaff.addAccidentalIfValid(accidental);
               associatedStaff.setDraggedAccidentalExists(false);
            }
        }
        lastMouseX = mouseX;
        lastMouseY = mouseY;
        repaint();
    }

    private int xSnapAdjust(int mouseX, Staff associatedStaff) {
        for (int i = 0; i < associatedStaff.getNotes().size(); i++) {
            Note curr = associatedStaff.getNotes().get(i);
            if (curr.getType() == MusicConstants.SYMBOL_NOTE) {
                if (mouseX >= curr.getXPositionPoint() && mouseX <= curr.getXEnd()) {
                    return curr.getX();
                }
            }
        }
        return mouseX;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
            if (selectedSymbol != null) {
                selectedSymbol = null;
                staffArrayList.get(lastAssociatedStaffIndex).setSelectedNoteExists(false);
                repaint();
            }
            if (selectedAccidental != null) {
                selectedAccidental = null;
                selectedAccidentalNote.removeAccidental();
                staffArrayList.get(lastAssociatedStaffIndex).setSelectedAccidentalExists(false);
                repaint();
            }
        }
    }

    // Unused listeners
    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
//        System.out.println("Mouse entered MusicView");
    }

    @Override
    public void mouseExited(MouseEvent e) {
//        System.out.println("Mouse left MusicView");
    }

    @Override
    public void mouseMoved(MouseEvent e) {
//        System.out.println("Mouse is moving");
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    // Helpers
    private boolean isAccidental() {
        return Main.currentTool == MusicConstants.SYMBOL_FLAT || Main.currentTool == MusicConstants.SYMBOL_SHARP;
    }

    private boolean isNoteOrRest() {
        return Main.currentTool == MusicConstants.SYMBOL_NOTE || Main.currentTool == MusicConstants.SYMBOL_REST;
    }

    private boolean isNote() {
        return Main.currentTool == MusicConstants.SYMBOL_NOTE;
    }

    // Getters and setters
    public int getStaves() {
        return staffArrayList.size();
    }

    public int getIndexOfLastStaff() {
        return staffArrayList.size() - 1;
    }
}

