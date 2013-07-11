package com.internetitem.gitdown.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/")
public class TestService {

	@GET
	@Path("/test")
	public String sayHello() {
		return "Hello";
	}
}
