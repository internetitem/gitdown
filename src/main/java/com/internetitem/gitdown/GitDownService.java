package com.internetitem.gitdown;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;

import com.internetitem.gitdown.config.GitDownConfiguration;
import com.internetitem.gitdown.service.GitService;

public class GitDownService extends Application<GitDownConfiguration> {

	@Override
	public void initialize(Bootstrap<GitDownConfiguration> bootstrap) {
		bootstrap.addBundle(new ViewBundle());
		bootstrap.addBundle(new AssetsBundle());
	}

	@Override
	public void run(GitDownConfiguration configuration, Environment environment) throws Exception {
		GitHelper gitHelper = new GitHelper(configuration);
		environment.lifecycle().manage(gitHelper);
		MarkdownHelper markdownHelper = new MarkdownHelper(configuration);
		environment.jersey().register(new GitService(gitHelper, markdownHelper));
	}

}
