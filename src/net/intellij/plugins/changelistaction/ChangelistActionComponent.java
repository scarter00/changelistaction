package net.intellij.plugins.changelistaction;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StorageScheme;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;
import javax.swing.JComponent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;

@State(
		name = ChangelistActionComponent.COMPONENT_NAME,
		storages = {
				@Storage(id = "changelist-action-default", file = "$PROJECT_FILE$"),
				@Storage(id = "changelist-action-dir", file = "$PROJECT_CONFIG_DIR$/changelist-action.xml", scheme = StorageScheme.DIRECTORY_BASED)})

public class ChangelistActionComponent implements Configurable, PersistentStateComponent<ChangelistActionComponent.State> {

	private static final Logger LOG = Logger.getLogger(ChangelistActionComponent.class);

	private static final String MARKER_FILE = "%F";
	private static final String MARKER_PRJ_ROOT = "%P";
	private static final String MARKER_CHANGELIST_NAME = "%C";

	public static final String COMPONENT_NAME = "VCS Changelist Action";

	public void invokeAction(
			Project project,
			String changelistName,
			List<VirtualFile> changes) {

		String prjBaseDir = project.getBaseDir().getPath();

		final ProjectFileIndex fileIndex = ProjectRootManager.getInstance(project).getFileIndex();

		LinkedHashSet<String> allFiles =
				ChangelistUtil.createFilenames(changes, fileIndex, state.absolutePath);

		// prepare list
		String lineSeparator = System.getProperty("line.separator");
		StringBuilder sb = new StringBuilder();
		for (String affectedFile : allFiles) {
			sb.append(affectedFile).append(lineSeparator);
		}

		// write file list to temp file
		File temp;
		try {
			temp = File.createTempFile("idea-", "-changes");
			temp.deleteOnExit();
			BufferedWriter out = new BufferedWriter(new FileWriter(temp));
			out.write(sb.toString());
			out.close();
		} catch (IOException ioex) {
			LOG.error("Error creating temp file.", ioex);
			return;
		}

		// invoke command
		String command = state.command.trim();
		if (command.length() == 0) {
			LOG.error("command is empty, do nothing");
			return;
		}

		command = replaceMarkerWithValue(command, MARKER_PRJ_ROOT, prjBaseDir);
		command = replaceMarkerWithValue(command, MARKER_FILE, temp.getAbsolutePath());
		command = replaceMarkerWithValue(
				command, MARKER_CHANGELIST_NAME, ChangelistUtil.createFilenameFromChangelistName(changelistName));

		if (state.consoleOutput) {
			CmdExecutor.execute(command, project);
		} else {
			try {
				Runtime runtime = Runtime.getRuntime();
				runtime.exec(command);
			} catch (IOException ioex) {
				LOG.error("Error invoking command.", ioex);
			}
		}
	}

	/**
	 * Returns command with marker replaced by value.
	 */
	private String replaceMarkerWithValue(String command, String marker, String value) {
		int ndx = command.indexOf(marker);
		if (ndx != -1) {
			command = command.substring(0, ndx) + value +
					command.substring(ndx + MARKER_PRJ_ROOT.length());
		}
		return command;
	}


	// ---------------------------------------------------------------- configurable

	private ChangelistActionConfiguration configurationComponent;
	private Icon pluginIcon;

	/**
	 * Returns display name.
	 */
	@Nls
	public String getDisplayName() {
		return COMPONENT_NAME;
	}

	/**
	 * Returns plugin icon.
	 */
	@Nullable
	public Icon getIcon() {
		if (pluginIcon == null) {
			pluginIcon = IconLoader.getIcon("/net/intellij/plugins/changelistaction/icon32.png");
		}
		return pluginIcon;
	}

	/**
	 * No help is available.
	 */
	@Nullable
	@NonNls
	public String getHelpTopic() {
		return null;
	}

	/**
	 * Returns the user interface component for editing the configuration.
	 */
	public JComponent createComponent() {
		if (configurationComponent == null) {
			configurationComponent = new ChangelistActionConfiguration();
			configurationComponent.load(state);
		}
		return configurationComponent.getPanel();
	}

	/**
	 * Checks if the settings in the configuration panel were modified by the user and
	 * need to be saved.
	 */
	public boolean isModified() {
		return configurationComponent.isModified();
	}

	/**
	 * Store the settings from configurable to other components.
	 * Repaints all editors.
	 */
	public void apply() throws ConfigurationException {
		configurationComponent.save();
	}

	/**
	 * Load settings from other components to configurable.
	 */
	public void reset() {
		configurationComponent.load(state);
	}

	/**
	 * Disposes the Swing components used for displaying the configuration.
	 */
	public void disposeUIResources() {
		configurationComponent = null;
	}

	// ------------------------------------------------------ state

	/**
	 * Configuration state.
	 */
	public static final class State {
		public String command = "";
		public boolean absolutePath;
		public boolean consoleOutput;
	}

	private final State state = new State();

	/**
	 * Returns plugin state.
	 */
	public State getState() {
		return state;
	}

	/**
	 * Loads state from configuration file.
	 */
	public void loadState(State state) {
		XmlSerializerUtil.copyBean(state, this.state);
	}

}
