package com.internetitem.gitdown.handler;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Response;

import com.internetitem.gitdown.FileData;

public interface FileHandler {

	Response handleFile(ServletContext servletContext, FileData data) throws IOException;

}
