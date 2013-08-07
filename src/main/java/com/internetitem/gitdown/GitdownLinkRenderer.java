package com.internetitem.gitdown;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.pegdown.LinkRenderer;
import org.pegdown.ast.WikiLinkNode;

public class GitdownLinkRenderer extends LinkRenderer {

	@Override
	public Rendering render(WikiLinkNode node) {
		try {
			String url = "./" + URLEncoder.encode(node.getText().replace(' ', '-'), "UTF-8");
			return new Rendering(url, node.getText());
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException();
		}
	}
}
