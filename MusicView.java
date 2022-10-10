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

        if (Main.selectOn) {        // Drag mode

            // Case 1 - note is selected, user clicks outside border region
            if (selectedSymbol != null) {
                int x = selectedSymbol.getXPositionPoint();
                int y = selectedSymbol.getYPositionPoint();
                boolean inXBounds = (mouseX >= x && mouseX <= x + selectedSymbol.getWidth());
                boolean inYBounds = (mouseY >= y && mouseY <= y + selectedSymbol.getHeight());

                if (!(inXBounds && inYBounds)) {
                    System.out.println("Out of bounds - removing selection");
                    staffArrayList.get(lastAssociatedStaffIndex).addNote(selectedSymbol);
                    staffArrayList.get(lastAssociatedStaffIndex).setSelectedNoteExists(false);
                    selectedSymbol = null;
                    associatedStaff.setSelectedNote(null);
                    associatedStaff.setSelectedNoteIndex(-1);
                    associatedStaff.setSelectedNoteExists(false);
                    lastAssociatedStaffIndex = -1;
                }
            }

            // Case 2 - note is selected, user clicks inside border region
            if (selectedSymbol != null) {

            } else {
                // Case 3 - no note selected, user clicks inside region with note
                for (int i = 0; i < associatedStaff.getNotes().size(); i++) {
                    Note note = associatedStaff.getNotes().get(i);
                    int x = note.getXPositionPoint();
                    int y = note.getYPositionPoint();
//                System.out.println("Mouse (x,y) :" + mouseX + "," + mouseY);
//                System.out.println("Note bounds: (" + x_upperLeft + "," + y_upperLeft + ") | (" + x_lowerRight + "," + y_lowerRight + ")");
                    boolean inXBounds = (mouseX >= x && mouseX <= x + note.getWidth());
                    boolean inYBounds = (mouseY >= y && mouseY <= y + note.getHeight());
//                System.out.println("Cursor position is " + mouseX + "," + mouseY);
//                System.out.println("Note bounds are " + x + "," + y + " to "
//                        + (x + note.getWidth()) + "," + (y + note.getHeight()));
                    if (inXBounds && inYBounds) {
                        selectedSymbol = note;

                        // Turn the selected symbol into the SelectedNote type in the Staff
                        associatedStaff.setSelectedNote(note);
                        associatedStaff.setSelectedNoteIndex(i);
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


        } else {                    // Insert mode
            Note note = new Note(mouseX, mouseY, Main.currentDuration, Main.currentTool, associatedStaffIndex);
            associatedStaff.setDraggedNote(note);
            System.out.println("Added new note at coords: (" + note.getXPositionPoint() + "," + note.getYPositionPoint() + ")");
        }

        repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
//        System.out.println("Mouse is being dragged");
        int mouseX = e.getX();
        int mouseY = e.getY();
        int associatedStaffIndex = mouseY / 120;
        Staff associatedStaff = staffArrayList.get(associatedStaffIndex);

        if (Main.selectOn) {
            if (selectedSymbol != null) {
                Note note = new Note(mouseX, mouseY, selectedSymbol.getDuration(), selectedSymbol.getType(), associatedStaffIndex);
                selectedSymbol.setX(mouseX);
                selectedSymbol.setY(mouseY);
                lastAssociatedStaffIndex = associatedStaffIndex;

                Main.statusLabel.setText("Pitch of this note is " + note.getPitch());
                associatedStaff.setSelectedNote(note);
            }
        } else {
            Note note = new Note(mouseX, mouseY, Main.currentDuration, Main.currentTool, associatedStaffIndex);

            Main.statusLabel.setText("Pitch of this note is " + note.getPitch());
            associatedStaff.setDraggedNote(note);
        }
        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
//        System.out.println("Mouse has been released");
        int mouseX = e.getX();
        int mouseY = e.getY();

        int associatedStaffIndex = mouseY / 120;
        Staff associatedStaff = staffArrayList.get(associatedStaffIndex);

        if (Main.selectOn) {
            if (selectedSymbol != null) {
                Note note = new Note(mouseX, mouseY, selectedSymbol.getDuration(), selectedSymbol.getType(), associatedStaffIndex);
                Main.statusLabel.setText("Pitch of this note is " + note.getPitch());
            }
        } else {
            Note note = new Note(mouseX, mouseY, Main.currentDuration, Main.currentTool, associatedStaffIndex);
            associatedStaff.addNote(note);
            associatedStaff.setDraggedNoteExists(false);

            Main.statusLabel.setText("Pitch of this note is " + note.getPitch());
        }
        lastMouseX = mouseX;
        lastMouseY = mouseY;
        repaint();
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
            System.out.println("KeyEvent.VK_DELETE in keyPressed");
            System.out.println("Selected symbol is " + selectedSymbol);
            System.out.println("Last associated staff is " + lastAssociatedStaffIndex);
            if (selectedSymbol != null) {
                {
                    selectedSymbol = null;
                    staffArrayList.get(lastAssociatedStaffIndex).setSelectedNoteExists(false);
                    repaint();
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }


    // Getters and setters
    public int getStaves() {
        return staffArrayList.size();
    }

    public int getIndexOfLastStaff() {
        return staffArrayList.size() - 1;
    }
}

