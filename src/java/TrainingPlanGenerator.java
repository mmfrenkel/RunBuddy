//TrainingPlanGenerator.java
/**
 * This TrainingPlanGenerator() class takes the information provided
 * as an instance of the UserProfile() class and generates a custom training
 * plan for the user based on their submitted age, skill level, and available
 * training time. This custom plan is based off of a "base" training plan that
 * is saved in the project's directory that is derived from CoolRunning.com:
 *
 * See link to reference: http://www.coolrunning.com/engine/2/2_4/144.shtml
 *
 * The custom training plan is saved locally in a text file with the
 * convention "<USERNAME>.txt" in order to save the file for a later time.
 *
 * @author: Megan Frenkel
 */

import java.io.*;
import javax.swing.*;
import java.util.*;


class TrainingPlanGenerator
{
    //important instance variables used to generate plan
    private double [][][] baseTrainingPlan;
    private double [][][] userTrainingPlan;
    private String userName;
    private Integer ageGroup;
    private Integer abilityGroup;
    private Integer timeGroup;

    // final variables, representing values known about the base plan
    final String PLAN_FILE = "basetrainingplan.txt";
    final int BASE_PLAN_WEEKS = 8;                      // base plan comes with 8 weeks
    final int DAYS_IN_WEEK = 7;                         // 7 days in a week
    final int VALUE_IN_DAY = 3;                         // values are distance, pace, and isCompleted

    // constructor
    public TrainingPlanGenerator(UserProfile userProfile)
    {
        // instantiate values from userProfile
        this.userName = userProfile.getName();
        this.ageGroup = userProfile.getAge();
        this.abilityGroup = userProfile.getAbility();
        this.timeGroup = userProfile.getTimeFrame();
    }

    /**
     * Central method that generates the user's unique training plan
     */
    public void createTrainingPlan()
    {
        loadBaseTrainingPlan();     // initialize baseTrainingPlan
        customizeForTimeGroup();    // take timeFrame into account
        customizeForAge();          // take age into account
        customizeForAbility();      // take ability into account
        saveUserProfile();          // save the user's training plan to a file
    }

    /**
     * Loads the "base" training plan as a 3D-array containing elements for each
     * [week][day][value] in the "basetrainingplan.txt" file. The week and day
     * are loaded in implicitly by the array index of the values added.
     */
    private void loadBaseTrainingPlan()
    {
        // initialize the 3D array
        baseTrainingPlan = new double [BASE_PLAN_WEEKS][DAYS_IN_WEEK][VALUE_IN_DAY];

        try // open the file and add elements
        {
            Scanner inFile = new Scanner(new File(PLAN_FILE));

            // this while loop will load one line of the base training plan at a time,
            // generating the 3D array
            int weekNumber = 0;
            while (inFile.hasNextLine())
            {
                String newLine = inFile.nextLine();
                String[] day = newLine.split(", ");

                int currentWeek = Integer.parseInt(day[0]);
                int currentDay = Integer.parseInt(day[1]);
                double dayMilage = Double.parseDouble(day[2]);
                double dayPace = Double.parseDouble(day[3]);
                double completed = Double.parseDouble(day[4]);

                if (currentWeek != weekNumber) { weekNumber += 1; }  // go to the next week

                // weekNumber - 1 because of array indices
                baseTrainingPlan[weekNumber - 1][currentDay - 1][0] = dayMilage;
                baseTrainingPlan[weekNumber - 1][currentDay - 1][1] = dayPace;
                baseTrainingPlan[weekNumber - 1][currentDay - 1][2] = completed;
            }
        }
        catch (FileNotFoundException e)  // if the base file is missing...
        {
            JOptionPane.showMessageDialog(null, "Error! Could not load base training plan; " +
                    "please check that file exists. See more: " + e);
            System.exit(1);
        }
    }

    /**
     * Customizes the training plan for the timeline the user has provided by initializing
     * the userTrainingPlan 3D array with the number of weeks adjusted. Note that no option
     * is provided for timeGroup1 because it is considered impossible.
     */
    private void customizeForTimeGroup()
    {
        // note that timeGroup 1 is considered impossible! So it doesn't let the user run with that...
        int userWeek = 0;

        if (timeGroup == 2)  // short plan, remove two weeks relative to base plan...
        {
            userTrainingPlan = new double [BASE_PLAN_WEEKS - 2][DAYS_IN_WEEK][VALUE_IN_DAY];

            // copy over basePlan to userTrainingPlan
            for (int week = 0; week < BASE_PLAN_WEEKS; week++)
            {
                if (week == 1 || week == 4)  { } // do nothing, skip weeks 2 and 5
                else
                {
                    for (int day = 0; day < DAYS_IN_WEEK; day++)
                    {
                        for (int value = 0; value < VALUE_IN_DAY; value++)
                        {
                            // add in baseTrainingPlan values
                            userTrainingPlan[userWeek][day][value] = baseTrainingPlan[week][day][value];
                        }
                    }
                    userWeek += 1;
                }
            }
        }
        else if (timeGroup == 3)  // just copy over base plan array to userTrainingPlan array
        {
            userTrainingPlan = new double [BASE_PLAN_WEEKS][DAYS_IN_WEEK][VALUE_IN_DAY];

            // copy over basePlan to userTrainingPlan
            for (int week = 0; week < BASE_PLAN_WEEKS; week++)
            {
                for (int day = 0; day < DAYS_IN_WEEK; day++)
                {
                    for (int value = 0; value < VALUE_IN_DAY; value++)
                    {
                        // add in baseTrainingPlan values
                        userTrainingPlan[week][day][value] = baseTrainingPlan[week][day][value];
                    }
                }
            }
        }
        else if (timeGroup == 4)  // add on two weeks of training to make plan longer by duplicating weeks
        {
            userTrainingPlan = new double [BASE_PLAN_WEEKS + 2][DAYS_IN_WEEK][VALUE_IN_DAY];

            // copy over basePlan to userTrainingPlan
            for (int week = 0; week < BASE_PLAN_WEEKS; week++)
            {
                if (week == 2 || week == 5)  // duplicate weeks 3 and 6
                {
                     for (int day = 0; day < DAYS_IN_WEEK; day++)
                     {
                         for (int value = 0; value < VALUE_IN_DAY; value++)
                         {
                             // add in baseTrainingPlan values twice, two weeks in a row
                             userTrainingPlan[userWeek][day][value] = baseTrainingPlan[week][day][value];
                             userTrainingPlan[userWeek+1][day][value] = baseTrainingPlan[week][day][value];
                         }
                     }
                    userWeek += 2;
                }
                else   // just copy over the week, these weeks are not to be duplicated
                {
                    for (int day = 0; day < DAYS_IN_WEEK; day++)
                    {
                        for (int value = 0; value < VALUE_IN_DAY; value++)
                        {
                            userTrainingPlan[userWeek][day][value] = baseTrainingPlan[week][day][value];
                        }
                    }
                    userWeek += 1;
                }
            }
        }
        else if (timeGroup == 5)  //  add on four weeks of training to make the plan MUCH longer
        {
            userTrainingPlan = new double [BASE_PLAN_WEEKS + 4][DAYS_IN_WEEK][VALUE_IN_DAY];

            // copy over basePlan to userTrainingPlan
            for (int week = 0; week < BASE_PLAN_WEEKS; week++)
            {
                if (week == 0 || week == 2 || week == 4 || week == 6)  // duplicate weeks 1, 3, 5, 7
                {
                    for (int day = 0; day < DAYS_IN_WEEK; day++)
                    {
                        for (int value = 0; value < VALUE_IN_DAY; value++)
                        {
                            // add in baseTrainingPlan values twice, two weeks in a row
                            userTrainingPlan[userWeek][day][value] = baseTrainingPlan[week][day][value];
                            userTrainingPlan[userWeek+1][day][value] = baseTrainingPlan[week][day][value];
                        }
                    }
                    userWeek += 2;
                }
                else   // just copy over the week, no duplication
                {
                    for (int day = 0; day < DAYS_IN_WEEK; day++)
                    {
                        for (int value = 0; value < VALUE_IN_DAY; value++)
                        {
                            // add in baseTrainingPlan values once
                            userTrainingPlan[userWeek][day][value] = baseTrainingPlan[week][day][value];
                        }
                    }
                    userWeek += 1;
                }
            }
        }
    }

    /**
     * Customizes the userTrainingPlan for age. The youngest group runs the longest plan
     * and at a decreased pace; the oldest runs the shortest plan at the slowest pace.
     */
    private void customizeForAge()
    {
        // NOTE the userTrainingPlan has already been initialized
        if (ageGroup == 1)                                                  // youngesters! run longer!
        {
            for (int week = 0; week < userTrainingPlan.length; week++ )
            {
                for (int day = 0; day < userTrainingPlan[week].length; day++ )
                {
                    double [] givenDay = userTrainingPlan[week][day];
                    givenDay[0] *= 1.1;                                     // increase mileage by 10%
                    givenDay[1] *= 0.9;                                     // decrease pace by 10%
                }
            }
        }
        else if (ageGroup == 2) {}                                          // same as default, do nothing
        else if (ageGroup == 3)                                             // older! run shorter + slower!
        {
            for (int week = 0; week < userTrainingPlan.length; week++ )
            {
                for (int day = 0; day < userTrainingPlan[week].length; day++ )
                {
                    double [] givenDay = userTrainingPlan[week][day];
                    givenDay[0] *= 0.9;                                     // decrease each mileage by 10%
                    givenDay[1] *= 1.1;                                     // increase each pace by 10%
                }
            }
        }
        else if (ageGroup == 4)                                             // oldest! run much shorter, much slower
        {
            for (int week = 0; week < userTrainingPlan.length; week++ )
            {
                for (int day = 0; day < userTrainingPlan[week].length; day++ )
                {
                    double [] givenDay = userTrainingPlan[week][day];
                    givenDay[0] *= 0.8;                                     // decrease each mileage by 20%
                    givenDay[1] *= 1.2;                                     // increase each pace by 20%
                }
            }
        }
    }

    /**
     * Customize the userTrainingPlan for ability level; adjusts such that
     * beginner runners run slower and short; advanced runners run faster
     * and longer.
     */
    private void customizeForAbility()
    {
        // NOTE the userTrainingPlan has already been initialized
        if (abilityGroup == 1)                                              // beginner...
        {
            for (int week = 0; week < userTrainingPlan.length; week++ )
            {
                for (int day = 0; day < userTrainingPlan[week].length; day++ )
                {
                    double [] givenDay = userTrainingPlan[week][day];
                    givenDay[0] *= 0.9;                                     // ...decrease each mileage by 10%
                    givenDay[1] *= 1.2;                                     // ...increase each pace by 20%
                }
            }
        }
        else if (abilityGroup == 2)  {}                                     // intermediate...same as default

        else if (abilityGroup == 3)                                         // advanced...
        {
            for (int week = 0; week < userTrainingPlan.length; week++ )
            {
                for (int day = 0; day < userTrainingPlan[week].length; day++ )
                {
                    double [] givenDay = userTrainingPlan[week][day];
                    givenDay[0] *= 1.2;                                     // ... increase each mileage by 20%
                    givenDay[1] *= 0.8;                                     // ... decrease each pace by 20%
                }
            }
        }
    }

    /**
     * Saves the generated userTrainingPlan to a local file with convention
     * "<USERNAME>.txt", where each line represents a single running workout
     * formatted as: week, day, distance, pace, isFinished. Note that
     * isFinished (did the user complete this run?) will default to 0. A value
     * of 1 indicates the run is complete.
     */
    public void saveUserProfile()
    {
        try
        {
            // create the file
            File trainingPlan = new File(userName + "_training_plan.txt");
            FileWriter fWriter = new FileWriter(trainingPlan);
            PrintWriter pWriter = new PrintWriter (fWriter);

            // add elements of training plan to the a profile for the user
            for (int week = 0; week < userTrainingPlan.length; week++ )
            {
                for (int day = 0; day < userTrainingPlan[week].length; day++ )
                {
                    double mileage = userTrainingPlan[week][day][0];
                    double pace = userTrainingPlan[week][day][1];
                    double completed = userTrainingPlan[week][day][2]; // will start out as 0 for 'not completed'

                    // create a string line to put into the file and add it
                    int adjustedWeek = week + 1;
                    int adjustedDay = day + 1;
                    String userPlanLine = adjustedWeek + ", " + adjustedDay + ", " +
                            String.format("%.3f", mileage) + ", " + String.format("%.3f", pace) + ", " + completed;
                    pWriter.println(userPlanLine);
                }
            }
            pWriter.close();
        }
        catch (IOException e)
        {
            ImageIcon paneIcon = new ImageIcon("resources/run-icon.png");
            JOptionPane.showMessageDialog(null,
                    "Warning! I could not save the user training plan due to " + e,
                    "Watch out!",
                    JOptionPane.INFORMATION_MESSAGE,
                    paneIcon
            );
        }
    }

    /**
     * Getter for the userTrainingPlan array
     * @return  3D array representing the current userTrainingPlan
     */
    public double [][][] getUserTrainingPlan()
    {
        return userTrainingPlan;
    }
}