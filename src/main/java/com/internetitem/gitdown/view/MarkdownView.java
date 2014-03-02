package com.internetitem.gitdown.view;

public class MarkdownView extends BasicView {

	private String text;

	public MarkdownView(String root, String title, String text) {
		super("markdown.ftl", root, title);
		this.text = text;
	}

	public String getText() {
		return text;
	}
}
