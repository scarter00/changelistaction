package net.intellij.plugins.changelistaction;

import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.VirtualFile;

import java.util.LinkedHashSet;
import java.util.List;

public class ChangelistUtil {

	/**
	 * Creates a unique list of filenames from the given changelist files.
	 *
	 * @param absolutePath if this is false then the paths returned will
	 * be relative from their respective content root (as determined by the
	 * <tt>fileIndex</tt>)
	 */
	public static LinkedHashSet<String> createFilenames(
			List<VirtualFile> changedFiles,
			ProjectFileIndex fileIndex,
			boolean absolutePath) {

		LinkedHashSet<String> allFiles =
				new LinkedHashSet<String>(changedFiles.size());

		String vfsFileSepartor = "/";

		for (VirtualFile changeFile : changedFiles) {
			VirtualFile contentRootForFile =
					fileIndex.getContentRootForFile(changeFile);

			String path = changeFile.getPath();

			if (!absolutePath) {
				if (changeFile.getPath().startsWith(contentRootForFile.getPath())) {
					path = path.substring(contentRootForFile.getPath().length());
				}

				if (path.startsWith(vfsFileSepartor)) {
					path = path.substring(vfsFileSepartor.length());
				}

			}

			if (allFiles.contains(path) == false) {
				allFiles.add(path);
			}
		}

		return allFiles;
	}

	public static String createFilenameFromChangelistName(String changelistName) {
		String name = changelistName.replace(" ", "_");
		name = name.replace(",", "");
		name = name.replace("/", "_");
		name = name.replace("\\", "_");

		// I'm always writing stuff like "PTR - 1234",
		// which turns in to "PTR_-_1234", blech
		name = name.replace("_-_", "-");

		return name;
	}
}
