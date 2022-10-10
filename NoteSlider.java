import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.io.IOException;
import java.util.Hashtable;

public class NoteSlider extends JPanel
                        implements ChangeListener {
    private JLabel outputLabel;
    private JSlider noteDurationSlider;
    private int currentDuration;

    public NoteSlider(JLabel outputLabel, int currentDuration) throws IOException {
        this.currentDuration = currentDuration;

        ImageIcon sixteenthNoteImage = createImageIcon("images/sixteenthNote.png", "Sixteenth Note");
        ImageIcon eighthNoteImage = createImageIcon("/images/eighthNote.png", "Eigth Note");
        ImageIcon quarterNoteImage = createImageIcon("/images/quarterNote.png", "Quarter Note");
        ImageIcon halfNoteImage = createImageIcon("/images/halfNote.png", "Half Note");
        ImageIcon wholeNoteImage = createImageIcon("/images/wholeNote.png", "Whole Note");

        this.outputLabel = outputLabel;

        noteDurationSlider = new JSlider(JSlider.VERTICAL, 0, 4, 0);
        noteDurationSlider.addChangeListener(this);
        noteDurationSlider.setMajorTickSpacing(1);
        noteDurationSlider.setPaintTicks(true);
        noteDurationSlider.setPreferredSize(new Dimension(100, 300));

        Hashtable durationTable = new Hashtable();
        durationTable.put(0, new JLabel(wholeNoteImage, JLabel.LEFT));
        durationTable.put(1, new JLabel(halfNoteImage, JLabel.LEFT));
        durationTable.put(2, new JLabel(quarterNoteImage, JLabel.LEFT));
        durationTable.put(3, new JLabel(eighthNoteImage, JLabel.LEFT));
        durationTable.put(4, new JLabel(sixteenthNoteImage, JLabel.LEFT));

        noteDurationSlider.setLabelTable(durationTable);
        noteDurationSlider.setPaintLabels(true);

        add(noteDurationSlider, BoxLayout.X_AXIS);
    }


    @Override
    public void stateChanged(ChangeEvent e) {
        outputLabel.setText("Note slider state has changed");
        currentDuration = noteDurationSlider.getValue();
        Main.currentDuration = this.currentDuration;
    }

    public int getCurrentDuration() {
        return currentDuration;
    }

    // ** This code was a snippet retrieved from the guide linked here to assist with image processing: https://docs.oracle.com/javase/tutorial/uiswing/components/icon.html
    protected ImageIcon createImageIcon(String path,
                                        String description) {
        java.net.URL imgURL = getClass().getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL, description);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
}


