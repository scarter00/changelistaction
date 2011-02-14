package net.intellij.plugins.changelistaction;

import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.apache.log4j.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.border.Border;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

public class CmdExecutor {

	private static final Logger LOG = Logger.getLogger(CmdExecutor.class);

	private static ToolWindow toolWindow;

	private static ConsoleView view;

	private static String id = "Changelist Action Console";

	/**
	 * Executes command and show it in the console.
	 */
	public static int execute(String command, Project project) {
		try {
			Runtime rt = Runtime.getRuntime();
			LOG.info("Invoking " + command);
			Process proc = rt.exec(command);

			if (project != null) {
				if (view == null) {
					TextConsoleBuilder builder =
							TextConsoleBuilderFactory.getInstance()
									.createBuilder(project);
					view = builder.getConsole();
				}

				OSProcessHandler handler = new OSProcessHandler(proc, command);
				view.attachToProcess(handler);
				handler.startNotify();

				ToolWindowManager manager =
						ToolWindowManager.getInstance(project);

				toolWindow = manager.getToolWindow(id);

				if (toolWindow == null) {
					toolWindow = createToolWindow(manager, command, project);
				}
			}

/*
            // any error message?
            StreamGobbler errorGobbler = new
                    StreamGobbler(proc.getErrorStream(), "ERROR");

            // any output?
            StreamGobbler outputGobbler = new
                    StreamGobbler(proc.getInputStream(), "OUTPUT");

            // kick them off
            errorGobbler.run();
            String out = outputGobbler.run();
*/

			// any error???
			int exitValue = proc.waitFor();
			LOG.debug("Exit value: " + exitValue);
			return exitValue;
		}
		catch (Exception ex) {
			LOG.debug("Error executing command.", ex);
		}
		return 0;
	}

	/**
	 * Creates tool window.
	 */
	private static ToolWindow createToolWindow(
		final ToolWindowManager toolWindowManager, final String command,
		final Project project) {

		DefaultActionGroup actionGroup = new DefaultActionGroup();
		actionGroup.add(new AnAction("Invoke changelist action",
			"Invoke preivous user action on VCS changelist.",
			IconLoader.getIcon
			("/net/intellij/plugins/changelistaction/icon16.png")) {
			@Override
			public void actionPerformed(AnActionEvent anActionEvent) {
				execute(command, project);
			}
		});
		actionGroup.add(new AnAction("Clear console",
			"Clear console window.",
			IconLoader.getIcon
			("/net/intellij/plugins/changelistaction/clear.png")) {
			@Override
			public void actionPerformed(AnActionEvent anActionEvent) {
				view.clear();
			}
		});
		actionGroup.add(new AnAction("Close",
			"Close Changelist Action window.",
			IconLoader.getIcon("/actions/cancel.png")) {
			@Override
			public void actionPerformed(AnActionEvent anActionEvent) {
				view.clear();
				toolWindowManager.unregisterToolWindow(id);
			}
		});
		JComponent toolbar = ActionManager.getInstance().createActionToolbar
			(ActionPlaces.UNKNOWN, actionGroup, false).getComponent();


		final JPanel panel = new JPanel(new BorderLayout());
		panel.add(toolbar, BorderLayout.WEST);

		ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
		Content content =
			contentFactory.createContent(view.getComponent(), "", false);

		panel.add(content.getComponent(), BorderLayout.CENTER);

		ToolWindow window = registerToolWindow(toolWindowManager, panel);

		window.show(
			new Runnable() {
				public void run() {
//					System.out.println("Do something here");
				}
			});

		return window;
	}

	private static ToolWindow registerToolWindow(
		final ToolWindowManager
			toolWindowManager, final JPanel panel) {

		final ToolWindow window = toolWindowManager.registerToolWindow(
			id,
			true, ToolWindowAnchor.BOTTOM);
		final ContentFactory contentFactory =
			ContentFactory.SERVICE.getInstance();

		final Content content = contentFactory.createContent(panel, "", false);

		content.setCloseable(false);

		window.getContentManager().addContent(content);

		window.setIcon(
			IconLoader.getIcon(
				"/net/intellij/plugins/changelistaction/icon16.png"));

		return window;
	}

}
