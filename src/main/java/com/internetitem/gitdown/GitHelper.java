package com.internetitem.gitdown;

import io.dropwizard.lifecycle.Managed;

import java.io.ByteArrayOutputStream;
import java.io.File;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;

import com.internetitem.gitdown.FileData.FileDataType;
import com.internetitem.gitdown.config.GitDownConfiguration;
import com.internetitem.gitdown.config.GitDownFileType;
import com.internetitem.gitdown.error.FileNotFoundException;
import com.internetitem.gitdown.handler.FileHandler;
import com.internetitem.gitdown.handler.StaticFileHandler;
import com.internetitem.gitdown.handler.markdown.MarkdownHandler;

public class GitHelper implements Managed {

	private static final FileHandler STATIC_HANDLER = new StaticFileHandler();

	private static final FileHandler MARKDOWN_HANDLER = new MarkdownHandler();

	private GitDownConfiguration configuration;

	private Repository repository;

	public GitHelper(GitDownConfiguration configuration) throws Exception {
		this.configuration = configuration;
		this.repository = getRepository();
	}

	@Override
	public void start() throws Exception {
	}

	private Repository getRepository() throws Exception {
		String path = configuration.getRepoPath();
		File file = new File(path);
		if (!file.isDirectory()) {
			throw new Exception("Path " + path + " does not exist");
		}

		File gitFile = new File(file, ".git");
		if (gitFile.isDirectory()) {
			file = gitFile;
		}

		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		builder.setGitDir(file);
		builder.readEnvironment();
		builder.findGitDir();
		if (builder.getGitDir() == null) {
			throw new Exception("Path " + path + " is not a git repository");
		}
		return builder.build();
	}

	@Override
	public void stop() throws Exception {
		repository.close();
	}

	public FileData getData(String filename) throws Exception {
		RevTree tree = getTree();

		FoundFile foundFile;
		FileDataType type;
		if (filename.equals("") || filename.endsWith("/")) {
			foundFile = findIndexFile(tree, filename);
			if (foundFile == null) {
				return FileData.notFound(filename);
			} else {
				String matchedFile = foundFile.getActualName();
				String matchedPart = matchedFile.substring(0, filename.length());
				if (!matchedPart.equals(filename)) {
					return FileData.redirect(filename, matchedPart);
				} else {
					type = FileDataType.IndexFile;
				}
			}
		} else {
			foundFile = findFile(tree, filename, null, null);
			if (foundFile != null) {
				String matchedFile = foundFile.getActualName();
				// This is how we check if something is a directory
				if (matchedFile.length() > filename.length() && matchedFile.charAt(filename.length()) == '/') {
					return FileData.redirect(filename, filename + "/");
				} else if (!matchedFile.equals(filename)) {
					return FileData.redirect(filename, matchedFile);
				} else {
					type = FileDataType.File;
				}
			} else {
				for (GitDownFileType fileType : configuration.getRenderSettings().getFileTypes()) {
					for (String extension : fileType.getExtensions()) {
						String newName = filename + extension;
						foundFile = findFile(tree, newName, fileType, extension);
						if (foundFile != null) {
							String actualName = foundFile.getActualName();
							if (!actualName.equals(newName)) {
								String redirectTo = actualName.substring(0, actualName.length() - extension.length());
								return FileData.redirect(filename, redirectTo);
							}
							break;
						}
					}
				}

				if (foundFile != null) {
					type = FileDataType.File;
				} else {
					String newName = filename + "/";
					foundFile = findIndexFile(tree, newName);
					if (foundFile == null) {
						return FileData.notFound(filename);
					} else {
						return FileData.redirect(filename, newName);
					}
				}
			}
		}

		String actualName = foundFile.getActualName();
		ObjectId objectId = foundFile.getTreeWalk().getObjectId(0);

		ObjectLoader loader = repository.open(objectId);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		loader.copyTo(bos);
		byte[] data = bos.toByteArray();
		return new FileData(data, foundFile.getExtension(), getHandler(foundFile.getHandlerName()), filename, actualName, type);
	}

	private FileHandler getHandler(String handlerName) throws FileNotFoundException {
		if (handlerName.equals(Constants.HANDLER_DEFAULT)) {
			return STATIC_HANDLER;
		} else if (handlerName.equals(Constants.HANDLER_MARKDOWN)) {
			return MARKDOWN_HANDLER;
		} else {
			throw new FileNotFoundException();
		}
	}

	private FoundFile findFile(RevTree tree, String filename, GitDownFileType fileType, String extension) throws Exception {
		TreeWalk treeWalk = findFileInGit(tree, filename);
		if (treeWalk == null) {
			return null;
		}
		if (fileType == null) {
			OUTER: for (GitDownFileType checkFileType : configuration.getRenderSettings().getFileTypes()) {
				for (String checkExtension : checkFileType.getExtensions()) {
					if (extensionMatches(filename, checkExtension)) {
						fileType = checkFileType;
						extension = checkExtension;
						break OUTER;
					}
				}
			}
		}

		if (fileType == null) {
			return new FoundFile(treeWalk, filename, null, Constants.HANDLER_DEFAULT);
		} else {
			return new FoundFile(treeWalk, filename, extension, fileType.getHandler());
		}
	}

	private boolean extensionMatches(String filename, String extension) {
		if (configuration.getRenderSettings().isCaseSensitive()) {
			if (filename.toLowerCase().endsWith(extension.toLowerCase())) {
				return true;
			}
		} else {
			if (filename.endsWith(extension)) {
				return true;
			}
		}
		return false;
	}

	private TreeWalk findFileInGit(RevTree tree, String filename) throws Exception {
		TreeWalk treeWalk = new TreeWalk(repository);
		treeWalk.addTree(tree);
		treeWalk.setRecursive(true);
		treeWalk.setFilter(new GitFileFilter(configuration.getRenderSettings().isCaseSensitive(), filename));
		if (!treeWalk.next()) {
			return null;
		}
		return treeWalk;
	}

	private FoundFile findIndexFile(RevTree tree, String filename) throws Exception {
		for (String indexName : configuration.getRenderSettings().getIndexFiles()) {
			String newName = filename + indexName;
			FoundFile foundFile = findFile(tree, newName, null, null);
			if (foundFile != null) {
				return foundFile;
			}
		}
		return null;
	}

	private RevTree getTree() throws Exception {
		String branch = configuration.getBranch();
		ObjectId branchObjectId = repository.resolve(branch);
		if (branchObjectId == null) {
			throw new Exception("branch " + branch + " could not be resolved");
		}
		RevWalk revWalk = new RevWalk(repository);
		RevCommit commit = revWalk.parseCommit(branchObjectId);
		RevTree tree = commit.getTree();
		return tree;
	}

}
