package com.internetitem.gitdown.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import com.internetitem.gitdown.GitHelper;

@Path("/")
public class GitService {

	private GitHelper gitHelper;

	public GitService(GitHelper gitHelper) {
		this.gitHelper = gitHelper;
	}

	@GET
	@Path("")
	public byte[] serveFile() throws Exception {
		return serveFile("");
	}

	@GET
	@Path("{page: .+}")
	public byte[] serveFile(@PathParam("page") String path) throws Exception {
		return gitHelper.getData("refs/heads/master", "" + path);
	}
}
