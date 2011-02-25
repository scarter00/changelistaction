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

	public JPanel getPanel() {
		return panel;
	}

	private ChangelistActionComponent.State state = new ChangelistActionComponent.State();

	public void load(ChangelistActionComponent.State state) {
		this.state = state;
		command.setText(state.command);
		switch (state.pathType) {
			case 0: absolutePaths.setSelected(true); break;
			case 1: relativePathContent.setSelected(true); break;
			case 2: relativePathProject.setSelected(true); break;
		}
		consoleOutput.setSelected(state.consoleOutput);
	}

	public ChangelistActionComponent.State save() {
		state.command = command.getText();
		state.pathType = getPathType();
		state.consoleOutput = consoleOutput.isSelected();
		return state;
	}

	public boolean isModified() {
		return !state.command.equals(command.getText()) ||
			state.pathType != getPathType() ||
			state.consoleOutput != consoleOutput.isSelected();
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
