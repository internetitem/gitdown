package com.internetitem.gitdown.error;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;

import com.internetitem.gitdown.Constants;
import com.internetitem.gitdown.view.BasicView;

public class NotFoundExceptionMapper implements ExceptionMapper<FileNotFoundException> {

	@Context
	private ServletContext servletContext;

	@Override
	public Response toResponse(FileNotFoundException exception) {
		return Response.status(Status.NOT_FOUND).type(Constants.CONTENT_TYPE_HTML).entity(new BasicView(Constants.TEMPLATE_NOT_FOUND, servletContext.getContextPath(), "Not Found")).build();
	}

}
