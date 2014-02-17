package com.internetitem.gitdown;

import io.dropwizard.Application;
import io.dropwizard.servlets.assets.AssetServlet;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import java.nio.charset.Charset;

import com.internetitem.gitdown.config.GitDownConfiguration;
import com.internetitem.gitdown.service.GitService;

public class GitDownService extends Application<GitDownConfiguration> {

	@Override
	public void initialize(Bootstrap<GitDownConfiguration> bootstrap) {
	}

	@Override
	public void run(GitDownConfiguration configuration, Environment environment) throws Exception {
		GitHelper gitHelper = new GitHelper(configuration);
		environment.lifecycle().manage(gitHelper);
		MarkdownHelper markdownHelper = new MarkdownHelper(configuration);
		environment.jersey().register(new GitService(gitHelper, markdownHelper));
		environment.servlets().addServlet("asset-servlet", new AssetServlet("/assets/", "/assets/", "index.html", Charset.forName("UTF-8")));
	}

}
