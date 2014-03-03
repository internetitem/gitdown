package com.internetitem.gitdown;

import org.eclipse.jgit.treewalk.TreeWalk;

public class FoundFile {

	private TreeWalk treeWalk;
	private String actualName;
	private String extension;
	private String handlerName;

	public FoundFile(TreeWalk treeWalk, String actualName, String extension, String handlerName) {
		this.treeWalk = treeWalk;
		this.actualName = actualName;
		this.extension = extension;
		this.handlerName = handlerName;
	}

	public TreeWalk getTreeWalk() {
		return treeWalk;
	}

	public String getActualName() {
		return actualName;
	}

	public String getExtension() {
		return extension;
	}

	public String getHandlerName() {
		return handlerName;
	}

}
