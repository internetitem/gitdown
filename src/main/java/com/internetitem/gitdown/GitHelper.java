package com.internetitem.gitdown;

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
import org.eclipse.jgit.treewalk.filter.PathFilter;

import com.yammer.dropwizard.lifecycle.Managed;

public class GitHelper implements Managed {

	private Repository repository;

	public GitHelper(String path) throws Exception {
		this.repository = getRepository(path);
	}

	@Override
	public void start() throws Exception {
	}

	private Repository getRepository(String path) throws Exception {
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
		System.err.println(builder.getGitDir().getAbsolutePath());
		return builder.build();
	}

	@Override
	public void stop() throws Exception {
		repository.close();
	}

	public byte[] getData(String branch, String filename) throws Exception {
		ObjectId branchObjectId = repository.resolve(branch);
		if (branchObjectId == null) {
			throw new Exception("branch " + branch + " could not be resolved");
		}
		RevWalk revWalk = new RevWalk(repository);
		RevCommit commit = revWalk.parseCommit(branchObjectId);
		RevTree tree = commit.getTree();
		TreeWalk treeWalk = new TreeWalk(repository);
		treeWalk.addTree(tree);
		treeWalk.setRecursive(true);
		treeWalk.setFilter(PathFilter.create(filename));
		if (!treeWalk.next()) {
			throw new Exception("Not found");
		}
		ObjectId objectId = treeWalk.getObjectId(0);
		ObjectLoader loader = repository.open(objectId);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		loader.copyTo(bos);
		return bos.toByteArray();
	}

}
