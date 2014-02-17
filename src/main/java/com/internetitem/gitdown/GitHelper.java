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

public class GitHelper implements Managed {

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

		TreeWalk foundFile;
		FileDataType type;
		if (filename.equals("") || filename.endsWith("/")) {
			foundFile = findIndexFile(tree, filename);
			if (foundFile == null) {
				return new FileData(null, filename, null, FileDataType.NotFound);
			} else {
				String matchedFile = foundFile.getPathString();
				String matchedPart = matchedFile.substring(0, filename.length());
				if (!matchedPart.equals(filename)) {
					return new FileData(null, filename, matchedPart, FileDataType.Redirect);
				} else {
					type = FileDataType.IndexFile;
				}
			}
		} else {
			foundFile = findFile(tree, filename);
			if (foundFile != null) {
				String matchedFile = foundFile.getPathString();
				// This is how we check if something is a directory
				if (matchedFile.length() > filename.length() && matchedFile.charAt(filename.length()) == '/') {
					return new FileData(null, filename, filename + "/", FileDataType.Redirect);
				} else if (!matchedFile.equals(filename)) {
					return new FileData(null, filename, matchedFile, FileDataType.Redirect);
				} else {
					type = FileDataType.File;
				}
			} else {
				for (String extension : configuration.getMarkdownExtensions()) {
					String newName = filename + extension;
					foundFile = findFile(tree, newName);
					if (foundFile != null) {
						String actualName = foundFile.getPathString();
						if (!actualName.equals(newName)) {
							String redirectTo = actualName.substring(0, actualName.length() - extension.length());
							return new FileData(null, filename, redirectTo, FileDataType.Redirect);
						}
						break;
					}
				}

				if (foundFile != null) {
					type = FileDataType.File;
				} else {
					String newName = filename + "/";
					foundFile = findIndexFile(tree, newName);
					if (foundFile == null) {
						return new FileData(null, filename, newName, FileDataType.NotFound);
					} else {
						return new FileData(null, filename, newName, FileDataType.Redirect);
					}
				}
			}
		}

		String actualName = foundFile.getPathString();
		ObjectId objectId = foundFile.getObjectId(0);

		ObjectLoader loader = repository.open(objectId);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		loader.copyTo(bos);
		byte[] data = bos.toByteArray();
		return new FileData(data, filename, actualName, type);
	}

	private TreeWalk findFile(RevTree tree, String filename) throws Exception {
		TreeWalk treeWalk = new TreeWalk(repository);
		treeWalk.addTree(tree);
		treeWalk.setRecursive(true);
		treeWalk.setFilter(new GitFileFilter(configuration.isCaseSensitive(), filename));
		if (!treeWalk.next()) {
			return null;
		}
		return treeWalk;
	}

	private TreeWalk findIndexFile(RevTree tree, String filename) throws Exception {
		for (String indexName : configuration.getIndexFiles()) {
			String newName = filename + indexName;
			TreeWalk walk = findFile(tree, newName);
			if (walk != null) {
				return walk;
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
