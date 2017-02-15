package com.massisframework.massis.displays;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.eteks.sweethome3d.io.FileUserPreferences;
import com.eteks.sweethome3d.model.Home;
import com.eteks.sweethome3d.model.Level;
import com.eteks.sweethome3d.swing.FileContentManager;
import com.eteks.sweethome3d.swing.PlanComponent;
import com.eteks.sweethome3d.swing.SwingViewFactory;
import com.eteks.sweethome3d.viewcontroller.PlanController;

/**
 * Displays the home in Sweet home 3D. It is not very useful, but it doesn't
 * hurt either.
 *
 * @author rpax
 *
 */
public class HomeDisplay extends JFrame {

	private static final long serialVersionUID = -6696779235522417183L;
	private PlanComponent planComponent;
	private PlanController planController;
	private Home home;

	public HomeDisplay(Home home)
	{
		this.home = home;
		setTitle("HomeDisplay2D");
	}

	@Override
	public void dispose()
	{
		super.dispose();
		unregisterListeners();
	}

	private void init()
	{
		FileUserPreferences fileUserPreferences = new FileUserPreferences();
		planController = new PlanController(home, fileUserPreferences,
				new SwingViewFactory(), new FileContentManager(
						fileUserPreferences),
				null);

		planComponent = new PlanComponent(home, fileUserPreferences,
				planController);

		JScrollPane scrolledPlanComponent = new JScrollPane(planComponent);

		JPanel controlPanel = new JPanel(new GridLayout(2, 2));

		JPanel scalePanel = new JPanel(new GridLayout(1, 2));
		scalePanel.setBorder(BorderFactory.createTitledBorder("Zoom"));
		JButton decreaseScaleButton = new JButton("-");
		decreaseScaleButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				planComponent.setScale(planComponent.getScale() / 2.0f);
			}
		});
		JButton increaseScaleButton = new JButton("+");
		increaseScaleButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				planComponent.setScale(planComponent.getScale() * 2.0f);
			}
		});
		JPanel lvlPanel = new JPanel(new GridLayout(1, 2));
		String[] levels = new String[home.getLevels().size()];
		for (int i = 0; i < home.getLevels().size(); i++)
		{
			levels[i] = home.getLevels().get(i).getName();
		}
		final JComboBox<String> levelComboBox = new JComboBox<String>(levels);

		levelComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				Level currentLevel = home.getLevels().get(
						levelComboBox.getSelectedIndex());
				home.setSelectedLevel(currentLevel);
			}
		});
		lvlPanel.add(levelComboBox);
		scalePanel.add(decreaseScaleButton);
		scalePanel.add(increaseScaleButton);
		controlPanel.add(scalePanel);
		controlPanel.add(lvlPanel);
		getContentPane().add("South", controlPanel);
		getContentPane().add("Center", scrolledPlanComponent);

		pack();
		setSize(600, 500);

		registerListeners();
	}

	private void unregisterListeners()
	{
	}

	private void registerListeners()
	{
	}

	@Override
	public void setVisible(boolean visible)
	{
		if (visible)
		{
			if (planComponent == null)
			{
				init();
			} else
			{
				registerListeners();
			}
		} else
		{
			unregisterListeners();
		}
		super.setVisible(visible);
	}
}