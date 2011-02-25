package net.intellij.plugins.changelistaction;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class ChangelistActionConfiguration {

	private JTextField command;
	private JPanel panel;
	private JCheckBox consoleOutput;
	private JRadioButton absolutePaths;
	private JRadioButton relativePathContent;
	private JRadioButton relativePathProject;
	private JCheckBox executeInBackground;

	public JPanel getPanel() {
		return panel;
	}

	private ChangelistActionComponent.State state = new ChangelistActionComponent.State();

	public void load(ChangelistActionComponent.State state) {
		this.state = state;
		command.setText(state.command);
		setPathType(state.pathType);
		consoleOutput.setSelected(state.consoleOutput);
		executeInBackground.setSelected(state.executeInBackground);
	}

	public ChangelistActionComponent.State save() {
		state.command = command.getText();
		state.pathType = getPathType();
		state.consoleOutput = consoleOutput.isSelected();
		state.executeInBackground = executeInBackground.isSelected();
		return state;
	}

	public boolean isModified() {
		return !state.command.equals(command.getText()) ||
			state.pathType != getPathType() ||
			state.consoleOutput != consoleOutput.isSelected() ||
			state.executeInBackground != executeInBackground.isSelected()
		;
	}

	// ---------------------------------------------------------------- utils

	private void setPathType(int pathType) {
		switch (pathType) {
			case 0: absolutePaths.setSelected(true); break;
			case 1: relativePathContent.setSelected(true); break;
			case 2: relativePathProject.setSelected(true); break;
		}
	}

	private int getPathType() {
		int pathType = 0;
		if (absolutePaths.isSelected()) {
			pathType = 0;
		} else if (relativePathContent.isSelected()) {
			pathType = 1;
		} else if (relativePathProject.isSelected()) {
			pathType = 2;
		}
		return pathType;
	}

}
