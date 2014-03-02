package com.internetitem.gitdown.view;

import io.dropwizard.views.View;

public class BasicView extends View {

	private String root;
	private String title;

	public BasicView(String filename, String root, String title) {
		super(filename);
		this.root = root;
		this.title = title;
	}

	public String getRoot() {
		return root;
	}

	public String getTitle() {
		return title;
	}

}
