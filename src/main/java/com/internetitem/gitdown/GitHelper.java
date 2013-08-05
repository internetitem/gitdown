package com.internetitem.gitdown;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;

import com.internetitem.gitdown.FileData.FileDataType;
import com.internetitem.gitdown.config.GitDownConfiguration;
import com.yammer.dropwizard.lifecycle.Managed;

public class GitHelper implements Managed {

	private GitDownConfiguration configuration;
	private Set<String> indexFiles;

	private Repository repository;

	public GitHelper(GitDownConfiguration configuration) throws Exception {
		this.configuration = configuration;
		this.indexFiles = setupIndexFiles();
		this.repository = getRepository();
	}

	private Set<String> setupIndexFiles() {
		Set<String> indexFiles = new HashSet<>();

		if (configuration.getIndexFiles() != null && !configuration.getIndexFiles().isEmpty()) {
			indexFiles.addAll(configuration.getIndexFiles());
		} else {
			indexFiles.add("index.md");
		}

		return indexFiles;
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
			}
			type = FileDataType.IndexFile;
		} else {
			foundFile = findFile(tree, filename);
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
		treeWalk.setFilter(PathFilter.create(filename));
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
