import java.awt.*;

public class Note {
    private int associatedStaff;
    private int x;
    private int y;
    private int width;
    private int height;
    private int xPositionPoint;
    private int yPositionPoint;
    private int duration;
    private int type;
    private String pitch;
    private Accidental accidental;
    private boolean isSelected = false;

    public Note(int x, int y, int duration, int type, int associatedStaff) {
        this.x = x;
        this.y = y;
        this.duration = duration;
        this.type = type;
        this.associatedStaff = associatedStaff;

        this.width = Staff.getNoteWidth(this);
        this.height = Staff.getNoteHeight(this);

        switch (duration) {
            case 0:         // whole note
                this.xPositionPoint = x - 10;
                this.yPositionPoint = y - 6;
                break;
            case 1:         // half note
                this.xPositionPoint = x - 15;
                this.yPositionPoint = y - 34;
                break;
            case 2:         // quarter note
                this.xPositionPoint = x - 7;
                this.yPositionPoint = y - 35;
                break;
            case 3:         // eighth note
                this.xPositionPoint = x - 15;
                this.yPositionPoint = y - 36;
                break;
            case 4:         // sixteenth note
                this.xPositionPoint = x - 6;
                this.yPositionPoint = y - 35;
                break;
        }
        pitch = calculatePitch();
    }

    private String calculatePitch() {
        /*
        *          - 0 -                           C6
        *
        *                                          A6
        *                                          G5
        *           25 --------------------------- F5
        *                                          E5
        *           40 --------------------------- D5
        *                                          C5
        *           55 --------------------------- B5
        *                                          A5
        *           70 --------------------------- G4
        *                                          F4
        *           85 --------------------------- E4
        *                                          D4
        *                                          C4
        *
        *         -  -                             A4
        *
        *
         */
        int relativeY = getYPositionPoint() % 120 + 5;

        // Notes with stems are janky with Y positioning
        if (duration != 0) {
            relativeY += 30;
        }

        int margin = 4;
        String pitch = "";

        // Figure out which octave we're in
//        System.out.println("Relative y before octave adjustment is " + relativeY);
        if (relativeY > 71) {
            pitch = "4";
        }
        else if (relativeY <= 71 && relativeY > 19) {
            pitch = "5";
        }
        else if (relativeY <= 20) {
            pitch = "6";
        }

        // Figure out the letter
        if (relativeY <= 25 - margin) {
            relativeY += 50;
        }
        if (relativeY <= 75 - margin) {
            relativeY += 50;
        }

//        System.out.println("Relative y is " + relativeY);

        if (relativeY < 120 + margin && relativeY > 120 - margin) {
            return "A" + pitch;
        }
        else if (relativeY <= 112 + margin && relativeY > 112 - margin) {
            return "B" + pitch;
        }
        else if (relativeY < 105 + margin && relativeY > 105 - margin) {
            return "C" + pitch;
        }
        else if (relativeY <= 97 + margin && relativeY > 97 - margin) {
            return "D" + pitch;
        }
        else if (relativeY < 90 + margin && relativeY > 90 - margin) {
            return "E" + pitch;
        }
        else if (relativeY <= 82 + margin && relativeY > 82 - margin) {
            return "F" + pitch;
        }
        else if (relativeY < 75 + margin && relativeY > 75 - margin) {
            return "G" + pitch;
        }
    return null;
    }

    public void drawOutline(Graphics g) {
        g.setColor(Color.blue);
        g.drawRect(this.getXPositionPoint(), this.getYPositionPoint(), this.getWidth(), this.getHeight());
        g.setColor(Color.black);
    }



    // Getters
    public int getX() {
        return this.x;
    }
    public int getY() {
        return this.y;
    }
    public int getWidth() { return this.width; }
    public int getHeight() { return this.height; }
    public int getXEnd() { return this.xPositionPoint + this.width; }
    public int getYEnd() { return this.yPositionPoint + this.height; }
    public int getXPositionPoint() {
        return this.xPositionPoint;
    }
    public int getYPositionPoint() {
        return this.yPositionPoint;
    }

    public String getPitch() { return this.pitch; }
    public Accidental getAccidental() { return this.accidental; }
    public int getAssociatedStaff() { return this.associatedStaff; }
    public int getDuration() {
        return duration;
    }
    public int getType() {
        return type;
    }
    public boolean hasAccidental() {
        return accidental != null;
    }
    public boolean isSelected() {
        return this.isSelected;
    }


    // Setters
    public void setX(int x) {
        this.x = x;
        switch (duration) {
            case MusicConstants.WHOLE_NOTE:         // whole note
                this.xPositionPoint = x - 10;
                break;
            case MusicConstants.HALF_NOTE:         // half note
                this.xPositionPoint = x - 15;
                break;
            case MusicConstants.QUARTER_NOTE:         // quarter note
                this.xPositionPoint = x - 7;
                break;
            case MusicConstants.EIGHTH_NOTE:         // eighth note
                this.xPositionPoint = x - 15;
                break;
            case MusicConstants.SIXTEENTH_NOTE:         // sixteenth note
                this.xPositionPoint = x - 6;
                break;
        }
        // Update accidental's coordinates, if one exists
        if (this.hasAccidental()) {
            switch (duration) {
                case MusicConstants.WHOLE_NOTE:         // whole note
                    this.accidental.setX(this.getXPositionPoint() - 10);
                    break;
                case MusicConstants.HALF_NOTE:         // half note
                    this.accidental.setX(this.getXPositionPoint());
                    break;
                case MusicConstants.QUARTER_NOTE:         // quarter note
                    this.accidental.setX(this.getXPositionPoint() - 10);
                    break;
                case MusicConstants.EIGHTH_NOTE:         // eighth note
                    this.accidental.setX(this.getXPositionPoint());
                    break;
                case MusicConstants.SIXTEENTH_NOTE:         // sixteenth note
                    this.accidental.setX(this.getXPositionPoint() - 10);
                    break;
            }
        }
    }
    public void setY(int y) {
        this.y = y;
        switch (duration) {
            case MusicConstants.WHOLE_NOTE:         // whole note
                this.yPositionPoint = y - 6;
                break;
            case MusicConstants.HALF_NOTE:         // half note
                this.yPositionPoint = y - 34;
                break;
            case MusicConstants.QUARTER_NOTE:         // quarter note
                this.yPositionPoint = y - 35;
                break;
            case MusicConstants.EIGHTH_NOTE:         // eighth note
                this.yPositionPoint = y - 36;
                break;
            case MusicConstants.SIXTEENTH_NOTE:         // sixteenth note
                this.yPositionPoint = y - 35;
                break;
        }
        if (this.hasAccidental()) {
            switch (duration) {
                case MusicConstants.WHOLE_NOTE:         // whole note
                    this.accidental.setY(this.getYPositionPoint() - 8);
                    break;
                case MusicConstants.HALF_NOTE:         // half note
                    this.accidental.setY(this.getYPositionPoint() + 20);
                    break;
                case MusicConstants.QUARTER_NOTE:         // quarter note
                    this.accidental.setY(this.getYPositionPoint() + 20);
                    break;
                case MusicConstants.EIGHTH_NOTE:         // eighth note
                    this.accidental.setY(this.getYPositionPoint() + 20);
                    break;
                case MusicConstants.SIXTEENTH_NOTE:         // sixteenth note
                    this.accidental.setY(this.getYPositionPoint() + 20);
                    break;
            }
        }
    }
    public void setPos(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public void setPitch(String pitch) { this.pitch = pitch; }
    public void setAccidental(Accidental accidental) { this.accidental = accidental; }
    public void setAssociatedStaff(int associatedStaff) {
        this.associatedStaff = associatedStaff;
    }

    public void setSelected(boolean bool) {
        this.isSelected = bool;
    }


}
