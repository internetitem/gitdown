package com.internetitem.gitdown.handler;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Response;

import com.internetitem.gitdown.FileData;

public class StaticFileHandler implements FileHandler {
	public StaticFileHandler() {
	}

	@Override
	public Response handleFile(ServletContext servletContext, FileData data) throws IOException {
		String contentType = servletContext.getMimeType(data.getActualName());
		return Response.ok(data.getData(), contentType).build();
	}
}
