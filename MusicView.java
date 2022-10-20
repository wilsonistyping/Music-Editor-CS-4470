import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class MusicView extends JComponent implements MouseListener, MouseMotionListener, KeyListener {
    private final static int INITIAL_STAVES = 4;
    private static int preferredWidth = 1060;
    private static int preferredHeight = 480;
    private static int lastMouseX, lastMouseY;

    public static Note selectedSymbol = null;
    public static Accidental selectedAccidental = null;
    public static int lastAssociatedStaffIndex;

    private ArrayList<Staff> staffArrayList = new ArrayList<>();
    private int indexOfCurrentStaff;

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
        for (int i = 0; i < staffArrayList.size(); i++) {
            Staff currStaff = staffArrayList.get(i);
            currStaff.paint(g);
        }
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
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        requestFocusInWindow();
        int mouseX = e.getX();
        int mouseY = e.getY();
        int associatedStaffIndex = mouseY / 120;
        Staff associatedStaff = staffArrayList.get(associatedStaffIndex);

        // Drag mode
        if (Main.selectOn) {

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

            // Case 2 - note is selected, user clicks inside border
            if (selectedSymbol != null) {
                // nothing happens lol
            }
            // Case 3 - no note selected, user clicks inside note region
            else {
                for (int i = 0; i < associatedStaff.getNotes().size(); i++) {
                    Note note = associatedStaff.getNotes().get(i);
                    int x = note.getXPositionPoint();
                    int y = note.getYPositionPoint();
                    boolean inXBounds = (mouseX >= x && mouseX <= x + note.getWidth());
                    boolean inYBounds = (mouseY >= y && mouseY <= y + note.getHeight());

//                    // Mouse is within the accidental bounds
//                    if (note.hasAccidental()) {
//                        Accidental acc = note.getAccidental();
//                        boolean inXAccBounds = (mouseX >= acc.getX() && mouseX <= acc.getX() + acc.getWidth());
//                        boolean inYAccBounds = (mouseY >= acc.getY() && mouseY <= acc.getY() + acc.getHeight());
//
//                        if (inXAccBounds && inYAccBounds) {
//                            selectedAccidental = acc;
//                            associatedStaff.setSelectedAccidental(acc, note);
//                            associatedStaff.setSelectedAccidentalExists(true);
//                            lastAssociatedStaffIndex = associatedStaffIndex;
//                        }
//                    }

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
        // Insert mode
        else {
            // Flag for a dragged note if the symbol is note (0 or rest (1)
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

        // Drag mode (Select on)
        if (Main.selectOn) {
            // If there is a symbol selected, then change its position according to the mouse position
            if (selectedSymbol != null) {
                Note note = new Note(mouseX, mouseY, selectedSymbol.getDuration(), selectedSymbol.getType(), associatedStaffIndex);
                note.setAccidental(selectedSymbol.getAccidental());
                selectedSymbol.setX(mouseX);
                selectedSymbol.setY(mouseY);
                lastAssociatedStaffIndex = associatedStaffIndex;
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

        // Drag mode (Select on)
        if (Main.selectOn) {
            if (selectedSymbol != null) {
                Note note = new Note(mouseX, mouseY, selectedSymbol.getDuration(), selectedSymbol.getType(), associatedStaffIndex);
                Main.statusLabel.setText("Pitch of this note is " + note.getPitch());
            }
        }
        // Insert mode (Select off)
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
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
//            System.out.println("KeyEvent.VK_DELETE in keyPressed");
            System.out.println("Selected symbol is " + selectedSymbol);
            System.out.println("Selected accidental is " + selectedAccidental);
            System.out.println("Last associated staff is " + lastAssociatedStaffIndex);
            if (selectedSymbol != null) {
                selectedSymbol = null;
                staffArrayList.get(lastAssociatedStaffIndex).setSelectedNoteExists(false);
                repaint();
            }
            if (selectedAccidental != null) {
                selectedAccidental = null;
                staffArrayList.get(lastAssociatedStaffIndex).setSelectedAccidentalExists(false);
                staffArrayList.get(lastAssociatedStaffIndex).setSelectedAccidentalExists(false);
                repaint();
            }
        }
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

