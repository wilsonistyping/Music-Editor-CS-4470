import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
    private static final ArrayList<MusicView> musicBook = new ArrayList<>();
    public static int currentTool;
    public static int currentDuration;
    private static int currentPage = 0;
    public static boolean selectOn = false;
    private static boolean penOn = false;

    private static JButton newStaffButton, deleteStaffButton, deletePageButton, prevPageButton, nextPageButton;
    private static JMenuItem editMenu_deleteStaff, pageMenu_deletePage, pageMenu_nextPage, pageMenu_prevPage;
    public static JLabel statusLabel;
    private static MusicView musicPage;
    private static JFrame frame;
    private static JScrollPane scroller;
    private static JPanel statusPanel, controlPanel, musicControlsHeaderPane, selectPenButtonsPanel;

    /**
     * createAndShowGUI() is the primary method that controls the load of everything else.
     * @throws IOException
     */
    private static void createAndShowGUI() throws IOException {
        initializeGUI();

        insertMenu(frame, statusLabel);
        frame.add(statusPanel, BorderLayout.SOUTH);
        frame.setMinimumSize(new Dimension(600, 600));
        frame.pack();
        frame.setVisible(true);
    }
    private static void initializeGUI() throws IOException {
        initializeFrame();
        initializeStatusPane();
        initializeContentPane();
        initializeControlPane();
    }

    // Logic for all of the panels in the box layout
    private static void initializeFrame() {
        JFrame.setDefaultLookAndFeelDecorated(true);
        frame = new JFrame("Wilson's CS 4470 Music Editor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());
    }
    private static void initializeStatusPane() {
        statusPanel = new JPanel();
        statusLabel = new JLabel("Status label");
        statusPanel.add(statusLabel);
    }
    private static void initializeContentPane() {
        musicBook.add(new MusicView());
        updateContentPanel();
    }
    private static void initializeControlPane() throws IOException {

        /* --- Control panel initialization --- */
        controlPanel = new JPanel();
        frame.add(controlPanel, BorderLayout.WEST);
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));

        /* -------- Music Controls Header initialization -------- */
        musicControlsHeaderPane = new JPanel();
        musicControlsHeaderPane.add(new JLabel("<html><strong>Music Controls</strong></html>"));
        controlPanel.add(Box.createRigidArea(new Dimension(0, 8))); // rigidAreas are for padding
        controlPanel.add(musicControlsHeaderPane);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 8)));


        /* -------- Select & Pen buttons GROUP -------- */
        selectPenButtonsPanel = new JPanel();
        selectPenButtonsPanel.setLayout(new BoxLayout(selectPenButtonsPanel, BoxLayout.X_AXIS));

        JButton selectButton = new JButton("Select");
        JButton penButton = new JButton("Pen");
        
        selectPenButtonsPanel.add(selectButton);
        selectPenButtonsPanel.add(penButton);

        selectButtonLogic(selectButton);
        penButtonLogic(penButton);

        controlPanel.add(selectPenButtonsPanel);


        /* -------- New Staff & Delete Staff buttons -------- */
        controlPanel.add(Box.createRigidArea(new Dimension(0, 10)));    // upper margin of 10
        JPanel staffButtonsPanel = new JPanel();
        staffButtonsPanel.setLayout(new BoxLayout(staffButtonsPanel, BoxLayout.X_AXIS));

        newStaffButton = new JButton("New Staff");
        deleteStaffButton = new JButton("Delete Staff");

        staffButtonsPanel.add(newStaffButton);
        staffButtonsPanel.add(deleteStaffButton);

        newStaffButtonLogic(newStaffButton);
        deleteStaffButtonLogic(deleteStaffButton);

        controlPanel.add(staffButtonsPanel);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        
        /* -------- Play & Stop buttons GROUP -------- */
        JPanel playStopButtonsPanel = new JPanel();
        playStopButtonsPanel.setLayout(new BoxLayout(playStopButtonsPanel, BoxLayout.X_AXIS));

        JButton playButton = new JButton("Play");
        JButton stopButton = new JButton("Stop");

        playStopButtonsPanel.add(playButton);
        playStopButtonsPanel.add(stopButton);

        playButton.addActionListener(e -> statusLabel.setText("playButton pressed"));
        stopButton.addActionListener(e -> statusLabel.setText("stopButton pressed"));

        controlPanel.add(playStopButtonsPanel);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        
        /* -------- Note Type and Note Duration GROUP -------- */
        JPanel noteTypeAndDurationPanel = new JPanel();
        noteTypeAndDurationPanel.setLayout(new GridLayout(0, 2));

        /* Note (Symbol) Type */
        JPanel noteRadioPanel = new JPanel();
        noteRadioPanel.setLayout(new BoxLayout(noteRadioPanel, BoxLayout.Y_AXIS));
        noteRadioPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JRadioButton noteRadioButton = new JRadioButton("Note");
        JRadioButton restRadioButton = new JRadioButton("Rest");
        JRadioButton flatRadioButton = new JRadioButton("Flat");
        JRadioButton sharpRadioButton = new JRadioButton("Sharp");
        noteRadioButton.setSelected(true);                  // Select Note by default

        symbolTypeLogic(noteRadioButton, restRadioButton, flatRadioButton, sharpRadioButton);

        noteRadioPanel.add(noteRadioButton);
        noteRadioPanel.add(restRadioButton);
        noteRadioPanel.add(flatRadioButton);
        noteRadioPanel.add(sharpRadioButton);
        noteTypeAndDurationPanel.add(noteRadioPanel);

        /* Note duration slider */
        NoteSlider noteSlider = new NoteSlider(statusLabel, currentDuration);
        noteTypeAndDurationPanel.add(noteSlider);

        controlPanel.add(noteTypeAndDurationPanel);
        
        /* ------- Page buttons GROUP ------- */
        JPanel pageHeaderPane = new JPanel();
        pageHeaderPane.add(new JLabel("Page Controls"));
        pageHeaderPane.setLayout(new BoxLayout(pageHeaderPane, BoxLayout.X_AXIS));
        controlPanel.add(pageHeaderPane);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        /* ------- Page Creation GROUP (add/delete) ------- */
        JPanel pageCreationPanel = new JPanel();
        pageCreationPanel.setLayout(new BoxLayout(pageCreationPanel, BoxLayout.X_AXIS));
        
        JButton newPageButton = new JButton("New Page");
        deletePageButton = new JButton("Delete Page");
        deletePageButton.setEnabled(false);

        newPageLogic(newPageButton);
        deletePageLogic(deletePageButton);

        pageCreationPanel.add(newPageButton);
        pageCreationPanel.add(deletePageButton);

        controlPanel.add(pageCreationPanel);
        

        /* ------- Page navigation GROUP (prev/next) ------- */
        JPanel pageNavigationPanel = new JPanel();
        pageNavigationPanel.setLayout(new BoxLayout(pageNavigationPanel, BoxLayout.X_AXIS));
        
        prevPageButton = new JButton("Previous");
        nextPageButton = new JButton("Next");
        
        prevPageButton.setEnabled(false);
        nextPageButton.setEnabled(false);
       
        pageNavigationPanel.add(prevPageButton);
        pageNavigationPanel.add(nextPageButton);

        prevPageLogic(prevPageButton);
        nextPageLogic(nextPageButton);

        controlPanel.add(pageNavigationPanel);
    }

    // All of the logic for the buttons in the control pane below

    // select/pen row
    private static void selectButtonLogic(JButton selectButton) {
        // Select button logic, pen button logic
        selectButton.addActionListener(e -> {
            selectOn = !selectOn;
            statusLabel.setText("selectButton is " + selectOn);
        });
    }
    private static void penButtonLogic(JButton penButton) {
        penButton.addActionListener(e -> statusLabel.setText("penButton pressed! Doesn't do anything right now."));
    }
    // new/delete staff row
    private static void newStaffButtonLogic(JButton newStaffButton) {
        newStaffButton.addActionListener(e ->  {
            statusLabel.setText("newStaffButton pressed");
            addStaff();
        });
    }
    private static void deleteStaffButtonLogic(JButton deleteStaffButton) {
        deleteStaffButton.addActionListener(e -> {
            statusLabel.setText("deleteStaffButton pressed");
            subtractStaff();
        });
    }

    // Radio buttons for symbol types
    private static void symbolTypeLogic(JRadioButton note, JRadioButton rest, JRadioButton flat, JRadioButton sharp) {
        // Symbol radio button logic
        note.addActionListener(e -> {
            statusLabel.setText("noteRadioButton pressed");
            currentTool = 0;
        });
        rest.addActionListener(e -> {
            statusLabel.setText("restRadioButton pressed");
            currentTool = 1;
        });
        
        // Accidental logic
        flat.addActionListener(e -> {
            statusLabel.setText("flatRadioButton pressed");
            currentTool = 2;
        });
        sharp.addActionListener(e -> {
            statusLabel.setText("sharpRadioButton pressed");
            currentTool = 3;
        });

        // Group the radio buttons together
        ButtonGroup noteRadioGroup = new ButtonGroup();
        noteRadioGroup.add(note);
        noteRadioGroup.add(rest);
        noteRadioGroup.add(flat);
        noteRadioGroup.add(sharp);
    }

    // new/delete page row
    private static void newPageLogic(JButton newPageButton) {
        newPageButton.addActionListener(e -> {
            statusLabel.setText("newPageButton pressed");
            addPage();
        });
    }
    private static void deletePageLogic(JButton deletePageButton) {
        deletePageButton.addActionListener(e -> {
            statusLabel.setText("deletePageButton pressed");
            deletePage();
        });
    }

    // previous/next page (navigation) row
    private static void prevPageLogic(JButton prevPageButton) {
        prevPageButton.addActionListener(e -> {
            statusLabel.setText("prevPageButton pressed");
            prevPage();
        });
    }
    private static void nextPageLogic(JButton nextPageButton) {
        nextPageButton.addActionListener(e -> {
            statusLabel.setText("nextPageButton pressed");
            nextPage();
        });
    }

    /** Below are a lot of helper methods yeah */
    private static void insertMenu(JFrame frame, JLabel statusLabel) {
        JMenuBar menuBar = new JMenuBar();

        // File menu stuff
        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);
        JMenuItem fileMenu_exit = new JMenuItem("Exit");
        fileMenu.add(fileMenu_exit);
        fileMenu_exit.addActionListener(e -> System.exit(0));

        // Edit menu stuff
        JMenu editMenu = new JMenu("Edit");
        menuBar.add(editMenu);
        JMenuItem editMenu_newStaff = new JMenuItem("New Staff");
        editMenu_deleteStaff = new JMenuItem("Delete Staff");
        editMenu.add(editMenu_newStaff);
        editMenu.add(editMenu_deleteStaff);

        // Edit menu button logic
        editMenu_newStaff.addActionListener(e -> {
            statusLabel.setText("editMenu_newStaff pressed");
            addStaff();
        });
        editMenu_deleteStaff.addActionListener(e -> {
            statusLabel.setText("editMenu_deleteStaff pressed");
            subtractStaff();
        });

        // Page Menu
        JMenu pageMenu = new JMenu("Pages");
        menuBar.add(pageMenu);
        // Initialize menu items
        JMenuItem pageMenu_newPage = new JMenuItem("New Page");
        pageMenu_deletePage = new JMenuItem("Delete Page");
        pageMenu_deletePage.setEnabled(false);
        pageMenu_prevPage = new JMenuItem("Previous Page");
        pageMenu_prevPage.setEnabled(false);
        pageMenu_nextPage = new JMenuItem("Next Page");
        pageMenu_nextPage.setEnabled(false);

        // Add action listeners
        pageMenu_newPage.addActionListener(e -> {
            statusLabel.setText("pageMenu_newPage pressed");
            addPage();
        });
        pageMenu_deletePage.addActionListener(e -> {
            statusLabel.setText("pageMenu_deletePage pressed");
            deletePage();
        });
        pageMenu_prevPage.addActionListener(e -> {
            statusLabel.setText("pageMenu_prevPage pressed");
            prevPage();
        });
        pageMenu_nextPage.addActionListener(e -> {
            statusLabel.setText("pageMenu_nextPage pressed");
            nextPage();
        });
        // Add menu items to menu
        pageMenu.add(pageMenu_newPage);
        pageMenu.add(pageMenu_deletePage);
        pageMenu.add(pageMenu_prevPage);
        pageMenu.add(pageMenu_nextPage);

        frame.setJMenuBar(menuBar);
    }

    // Staff functionality
    private static int getStaves() {
        return musicPage.getStaves();

    }

    private static void addStaff() {
        int numStaves = getStaves();
        musicPage.newStaff();
        updateStaffButtons();
    }
    private static void subtractStaff() {
        int numStaves = getStaves();
        musicPage.deleteStaff();
        if (numStaves > 1) {
            numStaves--;
            updateStaffButtons();
        }
    }

    private static void updateStaffButtons() {
        deleteStaffButton.setEnabled(true);
        editMenu_deleteStaff.setEnabled(true);

        int numStaves = getStaves();
        if (numStaves <= 1) {
            deleteStaffButton.setEnabled(false);
            editMenu_deleteStaff.setEnabled(false);
        }
    }

    // Page functionality
    private static void addPage() {
        currentPage++;
        System.out.println("Current page is " + currentPage);

        frame.remove(scroller);
        musicBook.add(new MusicView());
        updateContentPanel();

        updateStaffButtons();
        updatePageButtons();
    }
    private static void deletePage() {
        if (musicBook.size() - 1 != 0) {
            musicBook.remove(currentPage);
            if (currentPage != 0) {         // Catches condition where the first page is the page removed
                currentPage--;
            }
            System.out.println("Current page is " + currentPage);

            frame.remove(scroller);
            updateContentPanel();

            updateStaffButtons();
            updatePageButtons();
        }
    }
    private static void prevPage() {
        if (currentPage != 0) {
            currentPage--;
            musicPage = musicBook.get(currentPage);
            System.out.println("Page number: " + currentPage + "; Staff count: " + musicPage.getStaves());

            frame.remove(scroller);
            updateContentPanel();

            updateStaffButtons();
            updatePageButtons();
        }
    }
    private static void nextPage() {
        int lastPage = musicBook.size() - 1;
        if (currentPage < lastPage) {
            currentPage++;
            musicPage = musicBook.get(currentPage);
            System.out.println("Page number: " + currentPage + "; Staff count: " + musicPage.getStaves());

            frame.remove(scroller);
            updateContentPanel();

            updateStaffButtons();
            updatePageButtons();
        }
    }

    private static void updateContentPanel() {
        musicPage = musicBook.get(currentPage);
        scroller = new JScrollPane(musicPage);
        scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        frame.add(scroller, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
    }

    private static void updatePageButtons() {
        prevPageButton.setEnabled(true);
        nextPageButton.setEnabled(true);
        deletePageButton.setEnabled(true);
        pageMenu_prevPage.setEnabled(true);
        pageMenu_nextPage.setEnabled(true);
        pageMenu_deletePage.setEnabled(true);

        if (currentPage == 0) {
            prevPageButton.setEnabled(false);
            pageMenu_prevPage.setEnabled(false);
        }
        if (currentPage == musicBook.size() - 1) {
            nextPageButton.setEnabled(false);
            pageMenu_nextPage.setEnabled(false);
        }
        if (musicBook.size() - 1 == 0) {
            deletePageButton.setEnabled(false);
            pageMenu_deletePage.setEnabled(false);
        }
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    createAndShowGUI();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
