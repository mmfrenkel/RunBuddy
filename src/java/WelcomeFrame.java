//WelcomeFrame.java
/**
 * The WelcomeFrame() class represents the main UI that a RunBuddy user interacts
 * with when they first start the RunBuddy program. An instance of the WelcomeFrame()
 * class will prompt the user to submit information in order to generate a unique
 * running plan, chiefly their age, training time available until race, running level,
 * and name. The WelcomeFrame() will not let a user submit their information until values
 * for all fields are provided. Additionally, the program warns a user that 5 weeks is
 * not enough time to train properly for a half marathon, if that button is selected.
 *
 * @author: Megan Frenkel
 */

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.Color;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Color;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;


class WelcomeFrame extends JFrame
{
    // key widgets, needed for design/actions
    private JPanel mainPanel;                                                            // holds all components
    private JTextField nameField;                                                        // user provides name
    private JButton youngestAge, youngAge, middleAge, olderAge;                          // age options
    private JButton beginner, intermediate, advanced;                                    // ability options
    private JButton timeFrame1, timeFrame2, timeFrame3, timeFrame4, timeFrame5;          // timeframe options
    private JButton submit;                                                              // click to submit data
    private JLabel headerPanel;                                                          // title and logo
    private JLabel insertName, selectAge, selectAbility, selectTime, selectSubmit;       // instruction labels
    private JMenu menu;                                                                  // main menu...
    private JMenuBar menuBar;                                                            // ... and bar
    private JMenuItem startOver, loadExisting, exit;                                     // ... and options

    // will be userful later for expediting/optimizing formatting/listeners
    private JButton [] timeButtons;
    private JButton [] ageButtons;
    private JButton [] levelButtons;

    // important class component
    private UserProfile userProfile;

    // constants
    final int WIDTH = 300, HEIGHT = 750;                                                // how big window should be
    final String FONT_TYPE = "Helvetica";                                               // font for window
    final String HEADER_PATH = "src/resources/header.png";
    final String PATH_RUN_ICON = "src/resources/run-icon.png";

    // constructor
    public WelcomeFrame(String title)
    {
        super(title);

        // create the UI
        layoutComponents();
        addListeners();
    }

    /**
     * Create a UserProfile object associated with this class.
     */
    public void initializeUserProfile(UserProfile profile)
    {
        this.userProfile = profile;
    }

    /**
     * Called on when WelcomeFrame is created as central method
     * for formatting JFrame and adding components to mainPanel,
     * which holds all components and is inserted directly to main JFrame.
     */
    private void layoutComponents()
    {
        // format the frame object (this)
        setSize(new Dimension(WIDTH, HEIGHT));
        setMinimumSize(new Dimension(WIDTH, HEIGHT));
        setMaximumSize(new Dimension(WIDTH, HEIGHT));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // create and add elements to main panel
        createMainPanel();
        addMenuBar();
        addHeader();
        addInstructions();
        setQuestionaireLabels();
        formatQuestionaireLabels();
        setOptionButtons();
        formatOptionButtons();
        createChoicePanel();
    }

    /**
     * Called on by constructor to add listeners to all relevent
     * buttons to accept user actions.
     */
    private void addListeners()
    {
        // add listeners for age
        youngestAge.addActionListener(new OptionListener("age", 1));
        youngAge.addActionListener(new OptionListener("age", 2));
        middleAge.addActionListener(new OptionListener("age", 3));
        olderAge.addActionListener(new OptionListener("age", 4));

        // add listeners for level
        beginner.addActionListener(new OptionListener("level", 1));
        intermediate.addActionListener(new OptionListener("level", 2));
        advanced.addActionListener(new OptionListener("level", 3));

        // add listeners for time
        timeFrame1.addActionListener(      // alert user that their training time frame is too short
                new ActionListener()
                {
                    public void actionPerformed(ActionEvent ae)
                    {
                        ImageIcon paneIcon = new ImageIcon(PATH_RUN_ICON);
                        JOptionPane.showMessageDialog(null,
                                "Woah, woah, woah. That's not enough " +
                                "time to train for a half marathon! " +
                                "Please select another option.",
                                "Watch out!",
                                JOptionPane.INFORMATION_MESSAGE,
                                paneIcon
                        );
                    }
                }
        );
        timeFrame2.addActionListener(new OptionListener("time", 2));
        timeFrame3.addActionListener(new OptionListener("time", 3));
        timeFrame4.addActionListener(new OptionListener("time", 4));
        timeFrame5.addActionListener(new OptionListener("time", 5));

        // add listeners for name, to update the nameField
        nameField.addFocusListener(
                new FocusListener()
                {
                    public void focusGained(FocusEvent fe) { }
                    public void focusLost(FocusEvent fe) { userProfile.setName(nameField.getText()); }
                }
        );

        // submit button
        submit.addActionListener(
                new ActionListener()
                {
                    public void actionPerformed(ActionEvent ae)
                    {
                        // test if the user has submitted all relevant data points
                        boolean ready = userProfile.readyForExport();

                        if (ready) userProfile.createUserProfile();
                        else
                        {
                            // not ready yet! wait...
                            ImageIcon paneIcon = new ImageIcon(PATH_RUN_ICON);
                            JOptionPane.showMessageDialog(null,
                                    "Please select one option for each category.",
                                    "Watch out!",
                                    JOptionPane.INFORMATION_MESSAGE,
                                    paneIcon
                            );
                        }
                    }
                }
        );

        // menu options...

        // ... reset everything and start over
        startOver.addActionListener(
                new ActionListener()
                {
                    public void actionPerformed(ActionEvent ae) { userProfile.resetProfile(); }
                }
        );

        // ...call on the userProfile to load a file containing existing info
        loadExisting.addActionListener(
                new ActionListener()
                {
                    public void actionPerformed(ActionEvent ae)
                    {
                        try { userProfile.loadProfile(); }
                        catch (FileNotFoundException e)
                        {
                            ImageIcon paneIcon = new ImageIcon(PATH_RUN_ICON);
                            JOptionPane.showMessageDialog(null,
                                    "Whoops, couldn't find file, try again.",
                                    "Watch out!",
                                    JOptionPane.INFORMATION_MESSAGE,
                                    paneIcon
                            );
                        };
                    }
                }
        );

        // ...exit program entirely if clicked
        exit.addActionListener(
                new ActionListener()
                {
                    public void actionPerformed(ActionEvent ae) { System.exit(0); }
                }
        );

    }

    /**
     * Define and add the mainPanel to the WelcomeFrame; this mainPanel
     * will hold all components.
     */
    private void createMainPanel()
    {
        // format a mainPanel
        Color mainPanelColor = new Color(255, 255, 255);
        mainPanel = new JPanel(new FlowLayout());
        mainPanel.setBackground(mainPanelColor);

        // add mainPanel to JFrame
        this.add(mainPanel);
    }

    /**
     * Create a menu bar that allows users to clear submitted answers,
     * exit the program and load an existing profile.
     */
    private void addMenuBar()
    {
        menuBar = new JMenuBar();
        menu = new JMenu("Menu");
        startOver = new JMenuItem("Clear");
        exit = new JMenuItem("Exit");
        loadExisting = new JMenuItem("Load Existing Profile");

        menu.add(loadExisting);
        menu.add(startOver);
        menu.add(exit);
        menuBar.add(menu);

        Font menuFont = new Font(FONT_TYPE, Font.BOLD, 13);
        startOver.setFont(menuFont);
        exit.setFont(menuFont);
        loadExisting.setFont(menuFont);
        menu.setFont(menuFont);

        this.setJMenuBar(menuBar);
    }

    /**
     * Adds a "header" to the window that gives the name and logo of the program.
     */
    private void addHeader()
    {
        try
        {
            BufferedImage logo = ImageIO.read(new File(HEADER_PATH));
            headerPanel = new JLabel(new ImageIcon(logo));
            mainPanel.add(headerPanel);
        }
        catch (IOException e)
        {
            System.out.println("WARNING: Could not JLabel with logo due to " + e);
            headerPanel = new JLabel("Run Buddy");
        }
    }

    /**
     * Adds a label containing main instructions for the user.
     */
    private void addInstructions()
    {
        // set font types
        Font instructionFontB = new Font(FONT_TYPE, Font.BOLD, 13);
        Font instructionFontP = new Font(FONT_TYPE, Font.PLAIN, 13);

        // define labels and set fonts
        JLabel instructions1 = new JLabel("          Welcome!         ");
        JLabel instructions2 = new JLabel("To get started, please submit ");
        JLabel instructions3 = new JLabel("your information below.");
        JLabel instructions4 = new JLabel("****************************************");

        instructions1.setFont(instructionFontB);
        instructions2.setFont(instructionFontP);
        instructions3.setFont(instructionFontP);
        instructions4.setFont(instructionFontP);

        // add back to main
        mainPanel.add(instructions1);
        mainPanel.add(instructions2);
        mainPanel.add(instructions3);
        mainPanel.add(instructions4);
    }

    /**
     * Defines the content of the questionaire labels.
     */
    private void setQuestionaireLabels()
    {
        // set labels that describe user questionaire options
        insertName = new JLabel("1. Your name:", SwingConstants.CENTER);
        nameField = new JTextField("");
        nameField.setHorizontalAlignment(JTextField.CENTER);
        selectAge = new JLabel("2. Select your age group:", SwingConstants.CENTER);
        selectAbility = new JLabel("3. Select your ability level:", SwingConstants.CENTER);
        selectTime = new JLabel("4. Select the time until your race:", SwingConstants.CENTER);
        selectSubmit = new JLabel("***", SwingConstants.CENTER);
    }

    /**
     * Formats the questionaire labels such that they are all the the same font.
     */
    private void formatQuestionaireLabels()
    {
        // create array of buttons to expedite formatting
        JLabel [] labels = { insertName, selectAge, selectAbility, selectTime, selectSubmit };

        // give each label the same font and color
        Color labelColor = new Color (240, 240, 255);
        Font labelFont = new Font(FONT_TYPE, Font.BOLD, 13);
        for (JLabel l : labels)
        {
            l.setFont(labelFont);
            l.setForeground(labelColor);
        }
    }

    /**
     * Defines the option buttons (i.e., what their contents should be).
     */
    private void setOptionButtons()
    {
        // buttons for age selection
        youngestAge = new JButton("<18 years old");
        youngAge = new JButton("18 - 35 years old");
        middleAge = new JButton("36 - 55 years old");
        olderAge = new JButton("> 56 years old");
        ageButtons = new JButton [] { youngestAge, youngAge, middleAge, olderAge };

        // buttons for ability selection
        beginner = new JButton("Beginner\n(0-5 miles per week)");
        intermediate = new JButton("Intermediate\n(6-15 miles per week)");
        advanced = new JButton("Advanced\n(>15 miles per week)");
        levelButtons = new JButton [] { beginner, intermediate, advanced };

        // buttons for time selection
        timeFrame1 = new JButton("<5 weeks");
        timeFrame2 = new JButton("5-7 weeks");
        timeFrame3 = new JButton("7-9 weeks");
        timeFrame4 = new JButton("9-11 weeks");
        timeFrame5 = new JButton(">11 weeks");
        timeButtons = new JButton [] { timeFrame1, timeFrame2, timeFrame3, timeFrame4, timeFrame5 };

        // submit button
        submit = new JButton("SUBMIT");
    }

    /**
     * Adds formatting (font, margin) to each of the OptionButtons.
     */
    private void formatOptionButtons()
    {
        // create array of buttons to expedite formatting
        JButton [] buttons = {
                youngestAge, youngAge, middleAge, olderAge,
                beginner, intermediate, advanced,
                timeFrame1, timeFrame2, timeFrame3, timeFrame4, timeFrame5
        };

        // all buttons should have same font and margin
        Font buttonFont = new Font(FONT_TYPE, Font.PLAIN, 12);
        for (JButton j : buttons)
        {
            j.setFont(buttonFont);
            j.setMargin(new Insets(1,1,1,1));
        }

        // Submit button is unique
        Font submitFont = new Font(FONT_TYPE, Font.BOLD, 14);
        submit.setFont(submitFont);
    }

    /**
     * Main method that inserts all the JPanel and JButtons relevant to
     * the user questionaire to the mainPanel.
     */
    private void createChoicePanel()
    {
        JPanel insertPanel = new JPanel(new GridLayout(19, 1));
        insertPanel.add(insertName);
        insertPanel.add(nameField);

        // age section
        insertPanel.add(selectAge);
        for (JButton j : ageButtons) insertPanel.add(j);

        // ability section
        insertPanel.add(selectAbility);
        for (JButton j : levelButtons) insertPanel.add(j);

        // timeframe section
        insertPanel.add(selectTime);
        for (JButton j : timeButtons) insertPanel.add(j);

        // submit info
        insertPanel.add(selectSubmit);
        insertPanel.add(submit);

        // last formats
        insertPanel.setBackground(new Color(50, 50, 50));
        mainPanel.add(insertPanel);
    }

    /**
     * OptionListeners are main class that waits for user to submit information.
     * When user clicks a button, it updates the userProfile to reflect the
     * user's input and updates the formatting on the UI
     */
    class OptionListener implements ActionListener
    {
        String type;
        int value;

        // constructor
        public OptionListener(String type, int value)
        {
            this.type = type;
            this.value = value;
        }

        /**
         * Updates the userProfile instance variable when a user clicks
         * on a profile option in the UI.
         *
         * @param ae  The ActionEvent (a click)
         */
        public void actionPerformed(ActionEvent ae)
        {

            if (type.equals("age")) { userProfile.setAge(value); }
            else if (type.equals("level")) { userProfile.setAbility(value); }
            else if (type.equals("time")) { userProfile.setTime(value); }

            // highlight component in yellow to reflect selection
            JButton clickedButton = (JButton) ae.getSource();
            setFormatting(clickedButton);
        }

        /**
         * Allows for only one of the options (for age, level and time)
         * to be highlighed at a time (i.e., the selected option only).
         *
         * @param jButton
         */
        public void setFormatting(JButton jButton)
        {
            // reset all JButton of a given type...
            if (type.equals("age"))
            {
                for (JButton j : ageButtons) { resetFormating(j); }
            }
            else if (type.equals("level"))
            {
                for (JButton j : levelButtons) { resetFormating(j); }
            }
            else if (type.equals("time"))
            {
                for (JButton j : timeButtons) { resetFormating(j); }
            }

            //...then update JUST the button pressed to be colored
            Color color = new Color (30, 144, 255);
            jButton.setOpaque(true);
            jButton.setBackground(color);
        }

        /**
         * Resets the formatting of a given jButton to the default.
         *
         * @param jButton that needs to be reformatted
         */
        public void resetFormating(JButton jButton)
        {
            // initial settings
            Color black = new Color (0, 0, 0);
            jButton.setBackground(black);
            jButton.setOpaque(false);
        }
    }
}
