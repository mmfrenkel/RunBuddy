//TrainingFrame.java
/**
 * The TrainingFrame() class represents the basic template for the
 * UI displayed to a RunBuddy user after they have submitted their userProfile
 * and want to interact with the generated running plan in UI form. A user is able
 * to "check off" the elements of the training plan as they complete them, as the
 * TrainingFrame() will update it's display accordingly to keep track of
 * their progress. It will also congratulate them when the training plan is completed!
 *
 * @author: Megan Frenkel
 */

import javax.swing.BorderFactory;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.EtchedBorder;
import javax.swing.JScrollPane;
import java.awt.event.ItemListener;


class TrainingFrame extends JFrame
{
    // instance variables for UI
    private String userName;                                // User's name from UserProfile
    private ProgressPanel progressPanel;
    private JMenuItem exit;
    private JPanel mainPanel;
    private JPanel infoPanel;
    private JTextField statusField;

    // instance variables that represent key data points
    private double [][][] userTrainingPlan;                 // array version of trainingPlan
    private double totalMiles;                              // total miles in training plan
    private UserProfile userProfile;
    private TrainingPlanGenerator trainingPlanGenerator;
    private double milesCompleted;
    private int runsCompleted;

    // final variables
    final int MILEAGE = 0;
    final int PACE_INDEX = 1;
    final int COMPLETED_INDEX = 2;
    final int WIDTH = 500, HEIGHT = 750, WIDTH_LABEL = 450, HEIGHT_LABEL = 50;
    final String FONT_TYPE = "Helvetica";
    final Color BACKGROUND_COLOR = new Color(237, 237, 237);
    final Color UNFINISHED_COLOR = new Color(146, 174, 250);
    final Color FINISHED_COLOR = new Color(193, 207, 246);

    // constructor
    public TrainingFrame(String title, UserProfile userProfile, TrainingPlanGenerator planGenerator)
    {
        super(title);

        // initialize instance variables for class
        this.userName = userProfile.getName();
        this.userProfile = userProfile;
        this.trainingPlanGenerator = planGenerator;
        this.userTrainingPlan = trainingPlanGenerator.getUserTrainingPlan();
        this.totalMiles = getTotalMiles();
        this.milesCompleted = getMilesCompleted();
        this.runsCompleted = getRunsCompleted();

        // create UI
        layoutComponents();
        addListeners();
    }

    /**
     * This represents the main method called by the constructor to
     * generate the UI components for the TrainingFrame. This sets the
     * size and location of the Frame and calls on the other central methods
     * to generate all the components.
     */
    private void layoutComponents()
    {
        // format the frame object (this)
        setSize(new Dimension(WIDTH, HEIGHT));
        setMinimumSize(new Dimension(WIDTH, HEIGHT));
        setMaximumSize(new Dimension(WIDTH, HEIGHT));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // create and add elements to the TrainingFrame
        createScrollBar();
        createInfoPanel();
        createMenu();
        addWelcome();
        addHeaderElements();
        addMostRecentStatus();
        addTrainingPlanElements();
        addEndPlan();
    }

    /**
     * Adds a listener for the exit button to exit program.
     */
    private void addListeners()
    {
        exit.addActionListener(
                new ActionListener()
                {
                    public void actionPerformed(ActionEvent ae) { System.exit(0); }
                }
        );
    }

    /**
     * Creates a scrollPane which the mainPanel sits in; this allows
     * the user to scroll through their trainingPlan, as it won't fit in
     * one window.
     */
    private void createScrollBar()
    {
        // initializes the mainPanel, which sits in the JScrollPane
        mainPanel = new JPanel(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(mainPanel);

        // sets configurations
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane);
    }

    /**
     * Define and add the mainPanel to the WelcomeFrame; this mainPanel
     * will hold all components.
     */
    private void createInfoPanel()
    {
        // format an info panel
        infoPanel = new JPanel(new FlowLayout());
        infoPanel.setBackground(BACKGROUND_COLOR);
        infoPanel.setPreferredSize(new Dimension(WIDTH_LABEL, HEIGHT_LABEL * (userTrainingPlan.length) * 15));

        // add infoPanel to mainPanel
        mainPanel.add(infoPanel);
    }

    /**
     * Creates the menu, that will allow another UI option for quitting
     * the program.
     */
    private void createMenu()
    {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");
        exit = new JMenuItem("Exit");      // only user option

        // add menu elements, etc, with formatting back to the main frame
        menu.add(exit);
        menuBar.add(menu);
        Font menuFont = new Font(FONT_TYPE, Font.BOLD, 13);
        exit.setFont(menuFont);
        menu.setFont(menuFont);

        this.setJMenuBar(menuBar);
    }

    /**
     * Called on when TrainingFrame is initially created, this
     * uses the name of the user to generate a "welcome" message.
     */
    private void addWelcome()
    {
        String welcome = "Welcome, " + this.userName + "!";
        Font welcomeFont = new Font(FONT_TYPE, Font.BOLD, 25);
        JLabel welcomeLabel = new JLabel(welcome);
        welcomeLabel.setFont(welcomeFont);
        welcomeLabel.setBackground(BACKGROUND_COLOR);
        infoPanel.add(welcomeLabel);
    }

    /**
     * Alters the user that they have reached the end of the trainingPlan and
     * that their next run is their half marathon!
     */
    private void addEndPlan()
    {
        String endPlan = "********  End of plan! Next run: " +
                "Half Marathon, 13.2 Miles! ******** ";
        Font endPlanFont = new Font(FONT_TYPE, Font.BOLD, 15);
        JLabel endPlanLabel = new JLabel(endPlan);
        endPlanLabel.setFont(endPlanFont);
        endPlanLabel.setBackground(BACKGROUND_COLOR);
        infoPanel.add(endPlanLabel);
    }

    /**
     * This method gets called on when the TrainingFrame is initially created
     * to add the components of the Frame that do not involve the specific
     * TrainingPlan items.
     */
    private void addHeaderElements()
    {
        JPanel headerPanel = new JPanel(new GridLayout(1, 2));

        // String to explain purpose to user; JTextArea allows for text wrapping
        String headerString = "\n\n\nHere is your training\n      schedule...";
        Font welcomeFont = new Font(FONT_TYPE, Font.PLAIN, 20);
        JTextArea headerArea = new JTextArea(headerString);
        headerArea.setFont(welcomeFont);
        headerArea.setBackground(BACKGROUND_COLOR);
        headerArea.setEditable(false);
        headerArea.setWrapStyleWord(true);
        headerArea.setLineWrap(true);

        // progress bar; will update over time
        progressPanel = new ProgressPanel(percentCompleted());

        // add back to mainPanels
        headerPanel.add(headerArea);
        headerPanel.add(progressPanel);
        infoPanel.add(headerPanel);
    }

    /**
     * This method gets called on when the TrainingFrame is initially created
     * in order to setup the "statusField" object that will show the user's
     * progress in miles run and runs completed over time.
     */
    private void addMostRecentStatus()
    {
        Font statusFont = new Font(FONT_TYPE, Font.BOLD, 15);
        String status = "     Total Miles Run: " + ((int) milesCompleted) + " " +
                        "     Total Runs Completed: " + runsCompleted;
        statusField = new JTextField(status);
        statusField.setEditable(false);
        statusField.setBorder(BorderFactory.createEmptyBorder());
        statusField.setFont(statusFont);
        statusField.setBackground(BACKGROUND_COLOR);
        infoPanel.add(statusField);
    }

    /**
     * For each day/run of the training plan, creates a new ActivityPanel object
     * for that run and adds it back to the infoPanel Panel. This method gets
     * called on when the TrainingFrame is created.
     */
    private void addTrainingPlanElements()
    {
        for (int week = 0; week < userTrainingPlan.length; week++ )
        {
            for (int day = 0; day < userTrainingPlan[week].length; day++ )
            {
                // create ActivityPanel for each run to add to panel
                ActivityPanel ap = new ActivityPanel(day, week, userTrainingPlan[week][day]);
                infoPanel.add(ap);
            }
        }
    }

    /**
     * This method is called on by the ItemListener when the checkBox
     * on every ActivityPanel object is clicked on. This method calls
     * on the trainingPlanGenerator instance variable to re-save the user
     * profile with the updates, updates the runsCompleted and milesCompleted
     * instance variables, adjusts the ProgressBar to reflect the new
     * percent completed and updates the status string.
     */
    private void updateAll()
    {
        // save a new version of the training plan
        trainingPlanGenerator.saveUserProfile();

        // update instance variables
        runsCompleted = getRunsCompleted();
        milesCompleted = getMilesCompleted();

        // update UI to show adjusted progress bar, status string
        progressPanel.adjustProgressBar(percentCompleted());
        updateStatus();
    }

    /**
     * From the loaded training plan for the user, find the number
     * of miles that were "prescribed" in the plan.
     *
     * @return double
     */
    private double getTotalMiles()
    {
        double totalMiles = 0;

        // add up all miles in the training plan, for every week, day...
        for (int week = 0; week < userTrainingPlan.length; week++ )
        {
            for (int day = 0; day < userTrainingPlan[week].length; day++ )
            {
                totalMiles += userTrainingPlan[week][day][MILEAGE];
            }
        }
        return totalMiles;
    }

    /**
     * From the loaded training plan for the user, find the number of
     * completed miles.
     *
     * @return double representing the number of miles completed
     */
    private double getMilesCompleted()
    {
        double completedMiles = 0;

        // for all completed runs in the training plan (completed = 1), add
        for (int week = 0; week < userTrainingPlan.length; week++ )
        {
            for (int day = 0; day < userTrainingPlan[week].length; day++ )
            {
                // if "is completed" is true...
                if (userTrainingPlan[week][day][COMPLETED_INDEX] > 0)
                {
                    completedMiles += userTrainingPlan[week][day][0];
                }
            }
        }
        return completedMiles;
    }

    /**
     * Gets the number of runs that have been completed by the user.
     *
     * @return    int representing number of completed runs
     */
    private int getRunsCompleted()
    {
        int totalRuns = 0;

        // for all runs in the training plan, see if completed = 1 (true)
        for (int week = 0; week < userTrainingPlan.length; week++ )
        {
            for (int day = 0; day < userTrainingPlan[week].length; day++ )
            {
                if (userTrainingPlan[week][day][COMPLETED_INDEX] > 0)
                {
                    totalRuns += 1;   // if "is completed" is true, add on a run
                }
            }
        }
        return totalRuns;
    }

    /**
     * Gets the percent of the total mileage that is completed.
     *
     * @return  int representing the percent of total mileage completed
     */
    private int percentCompleted()
    {
        double percent = getMilesCompleted() / getTotalMiles() * 100;
        int percentRounded = (int) percent;
        return percentRounded;
    }

    /**
     * Tests if the training plan is fully completed by user; this
     * means "all boxes referring to training days are selected".
     *
     * @return   boolean; true or false that trainingPlan is complete
     */
    private boolean isCompleted()
    {
        return getRunsCompleted() == userTrainingPlan.length * 7;  // 7 = number of days in week
    }

    /**
     * This method gets called by the updateAll() method in order to adjust
     * the top string, representing the total miles and days run. Additionally,
     * if the user has completed the entirety of the training plan, it will add
     * a new message accordingly
     */
    private void updateStatus()
    {
        String status;

        // format miles so that no crazy decimals
        String miles = Double.toString(milesCompleted);
        if (miles.length() > 5) miles = miles.substring(0, 4);

        // check if user has completed training plan
        if (isCompleted())
        {
            status = "    You finished! (" + miles + " miles, " + runsCompleted + " days)";
            statusField.setText(status);
            congrats();
        }
        else
        {
            status = "     Total Miles Run: " + milesCompleted + " " +
                     "     Total Runs Completed: " + runsCompleted;
            statusField.setText(status);
        }
        // update on UI based on text generated
    }

    /**
     * If the user has completed their workout, this method pops open
     * a box to congratulate them.
     */
    private void congrats()
    {
         ImageIcon paneIcon = new ImageIcon("resources/run-icon.png");
         JOptionPane.showMessageDialog(null,
                 "Congratulations on completing the training plan! You're ready to run! :)",
                 "You did it!",
                 JOptionPane.INFORMATION_MESSAGE,
                 paneIcon);
    }

    /**
     * Created a JLabel that contains a checkbox inside of it; an instance
     * of this inner class represents a single workout.
     */
    class ActivityPanel extends JPanel
    {
        // instance variables for inner class
        int dayIndex;
        int weekIndex;
        double [] values;
        JCheckBox checkBox;
        String description;

        public ActivityPanel(double day, double week, double [] values)
        {
            super(new GridBagLayout());
            this.dayIndex = (int) day;
            this.weekIndex = (int) week;
            this.values = values;

            // set prefered size and settings
            this.setPreferredSize(new Dimension(WIDTH_LABEL, HEIGHT_LABEL));
            this.setBackground(UNFINISHED_COLOR);

            // generate the description for checkbox and add it back to the panel
            this.description = generateDescription();
            createCheckBox(description);
            this.addCheckListener();
        }

        /**
         * Generates a description of the workout to add to the workout panel,
         * including the week, day, pace and mileage for that day.
         *
         * @return   String describing a single workout
         */
        private String generateDescription()
        {
            return String.format("Week %2s, Day %2s ---  PACE:  %5s miles/hour   MILEAGE: %4.1f miles",
                    weekIndex + 1, dayIndex + 1, paceString(), values[MILEAGE]);
        }

        /**
         * Get a formatted String version of the pace for a given workout.
         * (i.e., "9:45" for 9 min 45 seconds)
         *
         * @return   String representing the pace
         */
        private String paceString()
        {
            int intPart = (int) this.values[PACE_INDEX];
            double doublePart = this.values[PACE_INDEX] - intPart;

            // find the amount of time (for the pace) in seconds
            int secondsPace = (int) (doublePart * 60);

            // return a string version of the pace ("00:00")
            // deal with 0 scenario
            if (secondsPace == 0) return "" + intPart + ":00";
            else return "" + intPart + ":" + secondsPace;
        }

        /**
         * Create the JCheckBox object and format it within this JPanel to
         * create the single line of the "workout" that will display in the UI.
         *
         * @param description   describes what the JCheckBox should display
         */
        private void createCheckBox(String description)
        {
            checkBox = new JCheckBox(description);

            // set font
            Font boxFont = new Font(FONT_TYPE, Font.BOLD, 12);
            checkBox.setFont(boxFont);

            // set color
            checkBox.setOpaque(true);
            checkBox.setBackground(UNFINISHED_COLOR);

            // add back
            this.add(checkBox);
        }

        /**
         * Adds a new listener to the JCheckBox, awaiting for when a user
         * clicks that they have completed a run. This kicks-off saving a
         * new training plan file and updating the UI accordingly.
         */
        private void addCheckListener()
        {
            checkBox.addItemListener(
                    new ItemListener()
                    {
                        public void itemStateChanged(ItemEvent itemEvent)
                        {
                            // whenever the checkbox gets clicked...
                            JCheckBox clicked = (JCheckBox) itemEvent.getSource();
                            int state = itemEvent.getStateChange();

                            if (state == ItemEvent.SELECTED)
                            {
                                // ...change the status of that workout to "completed" and update color
                                userTrainingPlan[weekIndex][dayIndex][COMPLETED_INDEX] = 1;
                                checkBox.setBackground(FINISHED_COLOR);
                                setBackground(FINISHED_COLOR);
                                updateAll();
                            }
                            else
                            {
                                // ...change the status of that workout to "incomplete" and update color
                                userTrainingPlan[weekIndex][dayIndex][COMPLETED_INDEX] = 0;
                                checkBox.setBackground(UNFINISHED_COLOR);
                                setBackground(UNFINISHED_COLOR);
                                updateAll();
                            }
                        }
                    }
            );
        }
    }
}
