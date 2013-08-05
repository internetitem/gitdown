package com.internetitem.gitdown.config;

import java.util.List;

import org.hibernate.validator.constraints.NotEmpty;

import com.yammer.dropwizard.config.Configuration;

public class GitDownConfiguration extends Configuration {

	@NotEmpty
	private String branch;

	@NotEmpty
	private String repoPath;

	private List<String> indexFiles;

	public String getRepoPath() {
		return repoPath;
	}

	public String getBranch() {
		return branch;
	}

	public List<String> getIndexFiles() {
		return indexFiles;
	}
}
