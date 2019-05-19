//UserProfile.java
/**
 * The UserProfile() class represents a given user's stored profile,
 * updating as a user submits more information through the WelcomeFrame().
 * Once a user interacting with a WelcomeFrame() object selects 'submit',
 * an instance of the UserProfile() will save the user profile in a file
 * named "<USERNAME>.txt" so that the profile can be opened and used again
 * at a later date.
 *
 * Note that this class makes use of a ReadWriteLock object and a method called
 * setProfileCreateWithLocking(); because a single UserProfile object is used
 * by multiple classes in the RunBuddy program, this approach prevents (the unlikely
 * though possible) occurrance of read/write conflicts.
 */

import java.util.concurrent.locks.*;
import javax.swing.JFileChooser;
import java.io.*;
import java.util.*;
import javax.swing.*;


class UserProfile
{
    // instance variables store user-provided information;
    // Integer instead of int because Integercan be null
    private String userName;
    private Integer userAgeGroup;
    private Integer userAbility;
    private Integer userTimeGroup;

    // determines status of user profile
    private boolean hasName;
    private boolean hasAgeGroup;
    private boolean hasAbility;
    private boolean hasTimeGroup;
    private boolean profileCreated;

    // the file that the profile will go into
    private File profileFile;

    // important component to be sure that the program doesn't read/write at the same time
    private ReadWriteLock profileCreatedLock;

    // constructor -- instantiate all instance values to values if none provided
    public UserProfile()
    {
        this.userName = null;
        this.userAgeGroup = null;
        this.userAbility = null;
        this.userTimeGroup = null;
        this.profileFile = null;

        this.hasName = false;
        this.hasAgeGroup = false;
        this.hasTimeGroup = false;
        this.hasAbility = false;

        this.profileCreatedLock = new ReentrantReadWriteLock();
        this.profileCreated = false;
    };

    /**
     * Sets all the userProfile instance variables, given a name, age, ability level
     * and time group passed in. This is called on by the loadProfile() method if
     * the user seeks to load in an already existing profile.
     *
     * @param name          name of user
     * @param ageGroup      ability group as integer (1 youngest, 4 oldest)
     * @param abilityLevel  ability group as integer (1 beginner, 3 advanced)
     * @param timeGroup     amount of training time as integer (2 as little, 5 as many weeks)
     */
    public void setAll(String name, int ageGroup, int abilityLevel, int timeGroup)
    {
        setName(name);
        setAge(ageGroup);
        setAbility(abilityLevel);
        setTime(timeGroup);
        setProfileCreateWithLocking(true);
    };

    /**
     * Setter for userName instance variable
     * @param name   name of user
     */
    public void setName(String name)
    {
        this.userName = name;
        this.hasName = true;
    }

    /**
     * Setter for ageGroup instance variable
     * @param ageGroup   int representing age group
     */
    public void setAge(int ageGroup)
    {
        this.userAgeGroup = ageGroup;
        this.hasAgeGroup = true;
    }

    /**
     * Setter for setAbility instance variable
     * @param abilityLevel   int representing ability level
     */
    public void setAbility(int abilityLevel)
    {
        this.userAbility = abilityLevel;
        this.hasAbility = true;
    }

    /**
     * Setter for timeGroup instance variable
     * @param timeGroup     int representing time group
     */
    public void setTime(int timeGroup)
    {
        this.userTimeGroup = timeGroup;
        this.hasTimeGroup = true;
    }

    /**
     * Getter for name
     * @return  String representing name
     */
    public String getName()
    {
        return this.userName;
    }

    /**
     * Getter for age.
     * @return   int representing age group
     */
    public Integer getAge()
    {
        return this.userAgeGroup;
    }

    /**
     * Getter for ability
     * @return   int representing ability
     */
    public Integer getAbility()
    {
        return this.userAbility;
    }

    /**
     * Getter for timeframe
     * @return   int representing the amount of training time available
     */
    public Integer getTimeFrame()
    {
        return this.userTimeGroup;
    }

    /**
     * Determines if the profile is created; uses a lock
     * to sure that no read-write conflict/corruption occurs.
     * @return
     */
    public boolean isProfileSet()
    {
        profileCreatedLock.readLock().lock();
        boolean created = profileCreated;
        profileCreatedLock.readLock().unlock();

        return created;
    }

    /**
     * Determines if user has filled out all required files.
     * @return  boolean; is the profile ready?
     */
    public boolean readyForExport()
    {
        if (hasName && hasAgeGroup && hasAbility && hasTimeGroup) return true;
        else return false;
    }

    /**
     * Resets the userProfile, if called on my the 'clear' option
     * in WelcomeFrame to start a profile over.
     */
    public void resetProfile()
    {
        this.userName = null;
        this.userAgeGroup = null;
        this.userAbility = null;
        this.userTimeGroup = null;

        this.hasName = false;
        this.hasAgeGroup = false;
        this.hasTimeGroup = false;
        this.hasAbility = false;

        setProfileCreateWithLocking(false);
    }

    /**
     * Loads in an existing userprofile from a file that a user selects.
     * @throws FileNotFoundException
     */
    public void loadProfile() throws FileNotFoundException
    {
        JFileChooser chooser = new JFileChooser();

        // only allow user to choose a file from browser
        File file;
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int returnValue = chooser.showOpenDialog(new JFrame("Select a file"));
        {
            if (returnValue == JFileChooser.APPROVE_OPTION) { file = chooser.getSelectedFile(); }
            else
            {
                ImageIcon paneIcon = new ImageIcon("resources/run-icon.png");
                JOptionPane.showMessageDialog(null,
                        "Could not load any file.",
                        "Watch out!",
                        JOptionPane.INFORMATION_MESSAGE,
                        paneIcon
                );
                return;
            }
        }

        // load in values in userProfile file
        Scanner inFile = new Scanner(file);
        ArrayList<String> values = new ArrayList<String>();

        // this should read in name, age, ability, time (length ArrayList = 4)
        // and update this userProfile object
        while (inFile.hasNextLine()) { values.add(inFile.nextLine()); }
        setAll( values.get(0),
                Integer.parseInt(values.get(1)),
                Integer.parseInt(values.get(2)),
                Integer.parseInt(values.get(3))
        );
    }

    /**
     * Creates a userProfile as a file that can be saved and stored for
     * later use.
     */
    public void createUserProfile()
    {
        try
        {
            // name the file after the user's name provided
            profileFile = new File(userName + "_profile.txt");

            // create a single string to add to the file based on userProfile
            String data = userName + "," + userAgeGroup + "," + userAbility + "," + userTimeGroup;

            // write to file and close when done
            FileWriter fWriter = new FileWriter(profileFile);
            PrintWriter pWriter = new PrintWriter (fWriter);
            pWriter.println(data);
            pWriter.close();

            setProfileCreateWithLocking(true);
        }
        catch (IOException e)  // warn user about issue with file
        {
            ImageIcon paneIcon = new ImageIcon("resources/run-icon.png");
            JOptionPane.showMessageDialog(null,
                    "Warning, I could not save the profile due to " + e,
                    "Watch out!",
                    JOptionPane.INFORMATION_MESSAGE,
                    paneIcon
            );
        }
    }

    /**
     * This method is designed to make sure that there are no
     * read-write conflicts between what is being ask of from
     * a main method and other classes that potential reference
     * a single UserProfile object at one time.
     * (See RunBuddy.java while-loop)
     *
     * @param bool   has the profile been created?
     */
    private void setProfileCreateWithLocking(boolean bool)
    {
        profileCreatedLock.writeLock().lock();
        profileCreated = bool;
        profileCreatedLock.writeLock().unlock();
    }

}