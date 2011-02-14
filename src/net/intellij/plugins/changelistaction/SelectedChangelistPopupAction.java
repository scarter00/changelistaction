package net.intellij.plugins.changelistaction;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ChangeList;
import com.intellij.openapi.vfs.VirtualFile;

import java.util.ArrayList;
import java.util.List;

public class SelectedChangelistPopupAction extends AnAction {

	@Override
	public void actionPerformed(AnActionEvent e) {
		DataContext dataContext = e.getDataContext();

		Project project = DataKeys.PROJECT.getData(dataContext);
		ChangelistActionComponent clActionComponennt =
				project.getComponent(ChangelistActionComponent.class);

		for (ChangeList iChangeList : getSelectedChangelists(dataContext)) {
			String changelistName = iChangeList.getName();
			List<Change> changes = new ArrayList<Change>(iChangeList.getChanges());

			List<VirtualFile> changeFiles = new ArrayList<VirtualFile>(changes.size());
			for (Change change : changes) {
				changeFiles.add(change.getVirtualFile());
			}
			clActionComponennt.invokeAction(project, changelistName, changeFiles);
			// TODO: force opening the console (depending on flag)?
		}
	}

	private ChangeList[] getSelectedChangelists(DataContext dataContext) {
		// TODO:STO deprecated, but what's the new mechanism?
		return DataKeys.CHANGE_LISTS.getData(dataContext);
	}
}
