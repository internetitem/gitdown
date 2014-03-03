package com.internetitem.gitdown.config;

import javax.validation.constraints.NotNull;

import io.dropwizard.Configuration;

import org.hibernate.validator.constraints.NotEmpty;

public class GitDownConfiguration extends Configuration {

	@NotEmpty
	private String branch;

	@NotEmpty
	private String repoPath;

	@NotNull
	private GitDownSettings renderSettings;

	private String[] settingOrder;

	public String getRepoPath() {
		return repoPath;
	}

	public String getBranch() {
		return branch;
	}

	public GitDownSettings getRenderSettings() {
		return renderSettings;
	}

	public String[] getSettingOrder() {
		return settingOrder;
	}
}
