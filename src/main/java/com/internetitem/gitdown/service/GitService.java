package com.internetitem.gitdown.service;

import java.net.URI;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.internetitem.gitdown.FileData;
import com.internetitem.gitdown.GitHelper;
import com.internetitem.gitdown.error.FileNotFoundException;
import com.internetitem.gitdown.handler.FileHandler;

@Path("/")
public class GitService {

	@Context
	private ServletContext servletContext;

	private GitHelper gitHelper;

	public GitService(GitHelper gitHelper) {
		this.gitHelper = gitHelper;
	}

	@GET
	public Response serveFile() throws Exception {
		return serveFile("");
	}

	@GET
	@Path("/{page: .+}")
	public Response serveFile(@PathParam("page") String path) throws Exception {
		FileData data = gitHelper.getData(path);
		switch (data.getFileDataType()) {
		case NotFound:
			throw new FileNotFoundException();
		case Redirect:
			return Response.status(Status.MOVED_PERMANENTLY).location(new URI(data.getActualName())).build();
		case File:
		case IndexFile:
		default:
			FileHandler handler = data.getHandler();
			return handler.handleFile(servletContext, data);
		}
	}

}
