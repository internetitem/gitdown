package com.internetitem.gitdown;

import com.internetitem.gitdown.config.GitDownConfiguration;
import com.internetitem.gitdown.service.TestService;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.assets.AssetsBundle;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;

public class GitDownService extends Service<GitDownConfiguration> {

	@Override
	public void initialize(Bootstrap<GitDownConfiguration> bootstrap) {
		bootstrap.addBundle(new AssetsBundle("/assets/", "/", "index.html"));
	}

	@Override
	public void run(GitDownConfiguration configuration, Environment environment) throws Exception {
		environment.addResource(new TestService());
	}

}
