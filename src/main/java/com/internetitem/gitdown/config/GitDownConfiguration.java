package com.internetitem.gitdown.config;

import org.hibernate.validator.constraints.NotEmpty;

import com.yammer.dropwizard.config.Configuration;

public class GitDownConfiguration extends Configuration {

	@NotEmpty
	private String repoPath;

	public String getRepoPath() {
		return repoPath;
	}
}
