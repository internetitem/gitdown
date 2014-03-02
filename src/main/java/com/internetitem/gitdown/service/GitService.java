package com.internetitem.gitdown.service;

import java.net.URI;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.internetitem.gitdown.Constants;
import com.internetitem.gitdown.FileData;
import com.internetitem.gitdown.GitHelper;
import com.internetitem.gitdown.MarkdownHelper;
import com.internetitem.gitdown.error.FileNotFoundException;
import com.internetitem.gitdown.view.MarkdownView;

@Path("/")
public class GitService {

	@Context
	private ServletContext servletContext;

	private GitHelper gitHelper;

	private MarkdownHelper markdownHelper;

	public GitService(GitHelper gitHelper, MarkdownHelper markdownHelper) {
		this.gitHelper = gitHelper;
		this.markdownHelper = markdownHelper;
	}

	@GET
	@Path("")
	public Response serveFile() throws Exception {
		return serveFile("");
	}

	@GET
	@Path("{page: .+}")
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
			String actualName = data.getActualName();
			Object returnObject;
			String contentType;
			if (markdownHelper.isMarkdown(actualName)) {
				String title = getTitle(data);
				returnObject = new MarkdownView(servletContext.getContextPath(), title, markdownHelper.convertMarkdown(data.getData()));
				contentType = Constants.CONTENT_TYPE_HTML;
			} else {
				returnObject = data.getData();
				contentType = getContentType(actualName);
			}
			return Response.ok(returnObject, contentType).build();
		}
	}

	private String getTitle(FileData data) {
		String filename = data.getActualName();
		String extension = markdownHelper.getExtension(filename);
		if (extension != null) {
			filename = filename.substring(0, filename.length() - extension.length());
		}
		filename = filename.replaceAll("-", " ");
		return filename;
	}

	private String getContentType(String actualName) {
		if (actualName.endsWith(".md")) {
			return "text/html";
		} else {
			return servletContext.getMimeType(actualName);
		}
	}

}
