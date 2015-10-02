package rpax.massis.app;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import javax.swing.Timer;

import rpax.massis.displays.MASSISIcon;
import rpax.massis.model.building.Building.BuildingProgressMonitor;

@SuppressWarnings("serial")
public class LauncherProgressMonitor extends JWindow implements
        BuildingProgressMonitor {

    private final JProgressBar progressBar = new JProgressBar();
    private final JLabel label = new JLabel("Loading building");
    private final Timer timer;
    private String text = "Loading building";
    private int percentage = 0;

    public LauncherProgressMonitor()
    {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run()
            {

                Container container = getContentPane();
                container.setLayout(null);
                JPanel panel = new JPanel();
                panel.setBorder(new javax.swing.border.EtchedBorder());
                panel.setBackground(new Color(255, 255, 255));
                panel.setBounds(10, 10, 348, 150);
                panel.setLayout(null);
                container.add(panel);
                MASSISIcon icon = new MASSISIcon();
                icon.setDimension(new Dimension(75, 75));
                label.setIcon(icon);
                label.setFont(new Font("Verdana", Font.BOLD, 10));
                label.setBounds(0, 0, 340, 150);
                panel.add(label);

                progressBar.setMaximum(100);
                progressBar.setBounds(55, 180, 250, 15);
                container.add(progressBar);
                setSize(370, 215);
                setLocationRelativeTo(null);
                setVisible(true);

            }
        });

        timer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                {
                    progressBar.setValue(percentage);
                    label.setText(text);
                }

            }
        });
        timer.start();
    }

    public static void main(String[] args)
    {
    }

    @Override
    public void onFinished()
    {

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run()
            {
                timer.stop();
                setVisible(false);
                dispose();
            }
        });
    }

    @Override
    public void onUpdate(final double progress, final String msg)
    {

        percentage = (int) progress;
        text = msg;

    }
}