import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
    private static final int INITIAL_STAFF_COUNT = 4;
    private static final ArrayList<MusicView> musicBook = new ArrayList<>();

    /*
    * 0 - note
    * 1 - rest
    * 2 - flat
    * 3 - sharp
     */
    public static int currentTool;
    /*
    * 0 - whole
    * 1 - half
    * 2 - quarter
    * 3 - eighth
    * 4 - sixteenth
    * */
    public static int currentDuration;
    private static int currentPage = 0;
    public static boolean selectOn = false;
    private static boolean penOn = false;

    private static JButton deleteStaffButton, deletePageButton, prevPageButton, nextPageButton;
    private static JMenuItem editMenu_deleteStaff, pageMenu_deletePage, pageMenu_nextPage, pageMenu_prevPage;
    public static JLabel statusLabel;
    private static MusicView musicPage;
    private static JFrame frame;
    private static JScrollPane scroller;
    
    private static void createAndShowGUI() throws IOException {
        JFrame.setDefaultLookAndFeelDecorated(true);
        frame = new JFrame("Wilson's CS 4470 Music Editor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());

        JPanel statusPanel = new JPanel();
        statusLabel = new JLabel("Status label");
        statusPanel.add(statusLabel);

        /* ----- Content panel (MusicView) controls ----- */
        musicBook.add(new MusicView());
        updateContentPanel();

        /* --- Control panel controls --- */
        JPanel controlPanel = new JPanel();

        frame.add(controlPanel, BorderLayout.WEST);
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));

        /* -------- Music Controls -------- */
        JPanel musicIntroPanel = new JPanel();
        musicIntroPanel.add(new JLabel("Music Controls"));
        musicIntroPanel.setLayout(new BoxLayout(musicIntroPanel, BoxLayout.X_AXIS));
        controlPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        controlPanel.add(musicIntroPanel);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 8)));

        /* Select & Pen buttons */
        JPanel selectPenButtonsPanel = new JPanel();
        JButton selectButton = new JButton("Select");
        JButton penButton = new JButton("Pen");
        selectPenButtonsPanel.setLayout(new BoxLayout(selectPenButtonsPanel, BoxLayout.X_AXIS));
        selectPenButtonsPanel.add(selectButton);
        selectPenButtonsPanel.add(penButton);
        controlPanel.add(selectPenButtonsPanel);
        // Select & Pen button logic
        selectButton.addActionListener(e -> {
            selectOn = !selectOn;
            statusLabel.setText("selectButton is " + selectOn);
        });
        penButton.addActionListener(e -> statusLabel.setText("penButton pressed! Doesn't do anything right now."));

        /* New Staff & Delete Staff buttons */
        controlPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        JPanel staffButtonsPanel = new JPanel();
        JButton newStaffButton = new JButton("New Staff");
        deleteStaffButton = new JButton("Delete Staff");
        staffButtonsPanel.setLayout(new BoxLayout(staffButtonsPanel, BoxLayout.X_AXIS));
        staffButtonsPanel.add(newStaffButton);
        staffButtonsPanel.add(deleteStaffButton);
        controlPanel.add(staffButtonsPanel);
        // New Staff & Delete Staff button logic
        newStaffButton.addActionListener(e ->  {
            statusLabel.setText("newStaffButton pressed");
            addStaff();
        });
        deleteStaffButton.addActionListener(e -> {
            statusLabel.setText("deleteStaffButton pressed");
            subtractStaff();
        });

        /* Play & Stop buttons */
        controlPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        JPanel playStopButtonsPanel = new JPanel();
        JButton playButton = new JButton("Play");
        JButton stopButton = new JButton("Stop");
        playStopButtonsPanel.setLayout(new BoxLayout(playStopButtonsPanel, BoxLayout.X_AXIS));
        playStopButtonsPanel.add(playButton);
        playStopButtonsPanel.add(stopButton);
        controlPanel.add(playStopButtonsPanel);
        // Play & Stop button logic
        playButton.addActionListener(e -> statusLabel.setText("playButton pressed"));
        stopButton.addActionListener(e -> statusLabel.setText("stopButton pressed"));
        controlPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        /* ---- Note Type and Note Duration grouping ----- */
        JPanel noteTypeAndDurationPanel = new JPanel();
        noteTypeAndDurationPanel.setLayout(new GridLayout(0, 2));

        /* Note type radio buttons */
        JRadioButton noteRadioButton = new JRadioButton("Note");
        noteRadioButton.setSelected(true);                  // Select Note by default
        JRadioButton restRadioButton = new JRadioButton("Rest");
        JRadioButton flatRadioButton = new JRadioButton("Flat");
        JRadioButton sharpRadioButton = new JRadioButton("Sharp");
        // Note type radio button logic
        noteRadioButton.addActionListener(e -> {
            statusLabel.setText("noteRadioButton pressed");
            currentTool = 0;

            System.out.println("Current tool is " + currentTool + "/ Current duration is " + currentDuration);
        });
        restRadioButton.addActionListener(e -> {
            statusLabel.setText("restRadioButton pressed");
            currentTool = 1;
        });
        flatRadioButton.addActionListener(e -> {
            statusLabel.setText("flatRadioButton pressed");
            currentTool = 2;
        });
        sharpRadioButton.addActionListener(e -> {
            statusLabel.setText("sharpRadioButton pressed");
            currentTool = 3;
        });
        // Group the radio buttons together
        ButtonGroup noteRadioGroup = new ButtonGroup();
        noteRadioGroup.add(noteRadioButton);
        noteRadioGroup.add(restRadioButton);
        noteRadioGroup.add(flatRadioButton);
        noteRadioGroup.add(sharpRadioButton);
        // Add buttons to the panel
        JPanel noteRadioPanel = new JPanel();
        noteRadioPanel.setLayout(new BoxLayout(noteRadioPanel, BoxLayout.Y_AXIS));
        noteRadioPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        noteRadioPanel.add(noteRadioButton);
        noteRadioPanel.add(restRadioButton);
        noteRadioPanel.add(flatRadioButton);
        noteRadioPanel.add(sharpRadioButton);
        noteTypeAndDurationPanel.add(noteRadioPanel);

        /* Note duration slider */
        NoteSlider noteSlider = new NoteSlider(statusLabel, currentDuration);
        noteTypeAndDurationPanel.add(noteSlider);
        controlPanel.add(noteTypeAndDurationPanel);


        /* ------- Page buttons ------- */
        JPanel pageIntroPanel = new JPanel();
        pageIntroPanel.add(new JLabel("Page Controls"));
        pageIntroPanel.setLayout(new BoxLayout(pageIntroPanel, BoxLayout.X_AXIS));
        controlPanel.add(pageIntroPanel);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        /* Page creation (add/delete) */
        JPanel pageCreationPanel = new JPanel();
        pageCreationPanel.setLayout(new BoxLayout(pageCreationPanel, BoxLayout.X_AXIS));
        JButton newPageButton = new JButton("New Page");
        deletePageButton = new JButton("Delete Page");
        deletePageButton.setEnabled(false);
        pageCreationPanel.add(newPageButton);
        pageCreationPanel.add(deletePageButton);
        controlPanel.add(pageCreationPanel);
        // Page creation button logic
        newPageButton.addActionListener(e -> {
            statusLabel.setText("newPageButton pressed");
            addPage();
        });
        deletePageButton.addActionListener(e -> {
            statusLabel.setText("deletePageButton pressed");
            deletePage();
        });

        /* Page navigation (prev/next) */
        JPanel pageNavigationPanel = new JPanel();
        pageNavigationPanel.setLayout(new BoxLayout(pageNavigationPanel, BoxLayout.X_AXIS));
        prevPageButton = new JButton("Previous");
        nextPageButton = new JButton("Next");
        prevPageButton.setEnabled(false);
        nextPageButton.setEnabled(false);
        pageNavigationPanel.add(prevPageButton);
        pageNavigationPanel.add(nextPageButton);
        controlPanel.add(pageNavigationPanel);
        // Page navigation button logic
        prevPageButton.addActionListener(e -> {
            statusLabel.setText("prevPageButton pressed");
            prevPage();
        });
        nextPageButton.addActionListener(e -> {
            statusLabel.setText("nextPageButton pressed");
            nextPage();
        });

        // Finishing touches
        insertMenu(frame, statusLabel);
        frame.add(statusPanel, BorderLayout.SOUTH);
        frame.setMinimumSize(new Dimension(600, 600));
        frame.pack();
        frame.setVisible(true);
    }

    /* Below are a lot of helper methods yeah */
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

    /* Helper methods related to staff functionality */
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

    /* Helper methods related to page functionality */
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
