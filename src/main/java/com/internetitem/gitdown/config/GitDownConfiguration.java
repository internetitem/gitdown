package com.internetitem.gitdown.config;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.yammer.dropwizard.config.Configuration;

public class GitDownConfiguration extends Configuration {

	@NotEmpty
	private String branch;

	@NotEmpty
	private String repoPath;

	@NotNull
	private List<String> indexFiles;

	@NotEmpty
	private List<String> markdownExtensions;

	public String getRepoPath() {
		return repoPath;
	}

	public String getBranch() {
		return branch;
	}

	public List<String> getIndexFiles() {
		return indexFiles;
	}

	public List<String> getMarkdownExtensions() {
		return markdownExtensions;
	}
}
