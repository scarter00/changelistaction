package net.intellij.plugins.changelistaction;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.changes.ChangeListManager;
import com.intellij.openapi.vfs.VirtualFile;

import java.util.List;

/**
 * @author Igor Spasic
 */
public class ChangelistToolbarAction extends AnAction {

	@Override
	public void actionPerformed(AnActionEvent anActionEvent) {
		DataContext dataContext = anActionEvent.getDataContext();
		Project project = DataKeys.PROJECT.getData(dataContext);
		ChangelistActionComponent component =
			project.getComponent(ChangelistActionComponent.class);
		ChangeListManager changeListManager =
			ChangeListManager.getInstance(project);


//		Application application = ApplicationManager.getApplication();
//		Module module = DataKeys.MODULE.getData(dataContext);

		List<VirtualFile> changes = changeListManager.getAffectedFiles();
		component.invokeAction(project, changes);
	}
}
