// RunBuddy.java
/***
 * @author: Megan Frenkel
 */

class RunBuddy
{
    private String status;

    public RunBuddy()
    {
        status = "Welcome";
    }

    public static void main (String [] args) throws InterruptedException
    {
        WelcomeFrame welcomeFrame = new WelcomeFrame("Welcome to Run Buddy!");
        UserProfile userProfile = new UserProfile();

        welcomeFrame.setVisible(true);
        welcomeFrame.initializeUserProfile(userProfile);

        // do nothing until the profile is created; profile is created either by
        // user pressing submit or by user loading
        while (userProfile.isProfileSet() == false) {};

        // if the user profile is set, then close the welcome frame and open the trainingFrame
        welcomeFrame.setVisible(false);

        // use user profile to generate a traning plan file
        TrainingPlanGenerator planGenerator = new TrainingPlanGenerator(userProfile);
        planGenerator.createTrainingPlan();

        // open up training frame
        TrainingFrame trainingFrame = new TrainingFrame("Log of Runs", userProfile, planGenerator);
        trainingFrame.setVisible(true);
    }
}