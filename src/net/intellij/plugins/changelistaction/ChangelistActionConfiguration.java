package net.intellij.plugins.changelistaction;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ChangelistActionConfiguration {

	private JTextField command;
	private JPanel panel;
	private JCheckBox absolutePath;
	private JCheckBox consoleOutput;

	public JPanel getPanel() {
		return panel;
	}

	// ---------------------------------------------------------------- LSRM

	private ChangelistActionComponent.State state = new ChangelistActionComponent.State();

	public void load(ChangelistActionComponent.State state) {
		this.state = state;
		command.setText(state.command);
		absolutePath.setSelected(state.absolutePath);
		consoleOutput.setSelected(state.consoleOutput);
	}

	public ChangelistActionComponent.State save() {
		state.command = command.getText();
		state.absolutePath = absolutePath.isSelected();
		state.consoleOutput = consoleOutput.isSelected();
		return state;
	}

	public boolean isModified() {
		return !state.command.equals(command.getText()) ||
			state.absolutePath != absolutePath.isSelected() ||
			state.consoleOutput != consoleOutput.isSelected();
	}


}
