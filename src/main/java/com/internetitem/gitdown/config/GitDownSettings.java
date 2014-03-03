package com.internetitem.gitdown.config;

import java.util.List;

public class GitDownSettings {

	private boolean caseSensitive;

	private boolean replace;

	private List<String> indexFiles;

	private List<GitDownFileType> fileTypes;

	public boolean isCaseSensitive() {
		return caseSensitive;
	}

	public boolean isReplace() {
		return replace;
	}

	public List<String> getIndexFiles() {
		return indexFiles;
	}

	public List<GitDownFileType> getFileTypes() {
		return fileTypes;
	}

}
