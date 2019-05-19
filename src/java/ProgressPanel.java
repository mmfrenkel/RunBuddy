//CircularProgressBar.java
/**
 * The ProgressPanel() class is a JPanel that contains only a
 * central circular progress bar element that is added to the TrainingFrame() object
 * displayed to a RunBuddy user. This class initially creates a progressBar based on the
 * percentCompleted provided to the constructor, but can be updated using the
 * adjustProgressBar() method.
 *
 * Credit: Inspiration to override the updateUI() method with a new UI class
 * came from pubically available previous work by username aterai found on github:
 * https://github.com/aterai/java-swing-tips/blob/master/ProgressCircle/src/java/example/MainPanel.java
 */

import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import javax.swing.*;
import javax.swing.plaf.basic.BasicProgressBarUI;


class ProgressPanel extends JPanel
{
    // main instance variables
    private JProgressBar progressBar;
    private final int MIN = 0;          // main percentage for progress bar
    private final int MAX = 100;        // max percentage for progress bar

    // constructor for JPanel
    public ProgressPanel(int percentCompleted)
    {
        super(new BorderLayout());
        JPanel p = new JPanel(new GridLayout(1, 1));

        // create a ProgressBar to add to JPanel
        createProgressBar();
        adjustProgressBar(percentCompleted);

        p.add(progressBar);
        add(p);
        setPreferredSize(new Dimension(200, 200));
    }

    /**
     * Called on by constructor to create the main UI component of this
     * class, a circular progress bar that will represent user progress
     * in their training plan.
     */
    private void createProgressBar()
    {
        progressBar = new JProgressBar()
        {
            // override to make the progress bar a circular object
            @Override public void updateUI()
            {
                super.updateUI();
                setUI(new ProgressCircleUI());
                setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
            }
        };
        // format settings
        progressBar.setStringPainted(true);
        progressBar.setForeground(new Color(146, 174, 250));
        progressBar.setMinimum(MIN);
        progressBar.setMaximum(MAX);
    }

    /**
     * This public method can be called upon to update the
     * "percent completed" or the extent to which the progress
     * bar is covered, over time.
     *
     * @param percentCompleted   percent of the training plan that is complete
     */
    public void adjustProgressBar(int percentCompleted)
    {
        progressBar.setValue(percentCompleted);
        progressBar.updateUI();
    }

    /**
     * Inner class that defines the UI for the ProgressBar() object
     * and allows it to be circular rather than linear. Ideas for the
     * stucture and elements of this method were derived from
     * Github user aterai (see github link above).
     */
    class ProgressCircleUI extends BasicProgressBarUI
    {
        @Override
        public void paint(Graphics g, JComponent c)
        {
            // adjust size with frame
            Insets b = progressBar.getInsets();
            int barRectWidth = progressBar.getWidth() - b.right - b.left;
            int barRectHeight = progressBar.getHeight() - b.top - b.bottom;
            if (barRectWidth <= 0 || barRectHeight <= 0) { return; } // too small

            // cast the Graphics object to a new 2D
            Graphics2D g2 = (Graphics2D) g.create();

            // smooth out the circle so that not poor resolution
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // values used to calculated shape of ellipse
            double degree = 360 * progressBar.getPercentComplete();
            double sz = Math.min(barRectWidth, barRectHeight);
            double cx = b.left + barRectWidth * .5;
            double cy = b.top + barRectHeight * .5;
            double or = sz * .5;
            double ir = or * .5;

            // create outermost circle and the sector (the "finished part")
            Shape outer = new Ellipse2D.Double(cx - or, cy - or, sz, sz);
            Shape sector = new Arc2D.Double(cx - or, cy - or, sz, sz, 90 - degree, degree, Arc2D.PIE);

            // create the foreground and background layers
            Area foreground = new Area(sector);
            Area background = new Area(outer);

            // create the "hole" in the middle of the figure
            Shape inner = new Ellipse2D.Double(cx - ir, cy - ir, ir * 2, ir * 2);
            Area hole = new Area(inner);
            foreground.subtract(hole);
            background.subtract(hole);

            // sets color of "unfinished part"
            g2.setPaint(new Color(200, 200, 200));
            g2.fill(background);

            // sets color of "finished part"
            g2.setPaint(progressBar.getForeground());
            g2.fill(foreground);

            // add the percentage in the middle
            if (progressBar.isStringPainted()) { paintString(g, b.left, b.top, barRectWidth, barRectHeight, 150, b);}
        }
    }
}

