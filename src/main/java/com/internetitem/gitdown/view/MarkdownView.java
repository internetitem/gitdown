package com.internetitem.gitdown.view;

import io.dropwizard.views.View;

public class MarkdownView extends View {

	private String title;
	private String text;

	public MarkdownView(String title, String text) {
		super("markdown.ftl");
		this.title = title;
		this.text = text;
	}

	public String getTitle() {
		return title;
	}

	public String getText() {
		return text;
	}
}
