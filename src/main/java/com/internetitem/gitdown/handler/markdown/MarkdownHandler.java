package com.internetitem.gitdown.handler.markdown;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Response;

import org.pegdown.Extensions;
import org.pegdown.LinkRenderer;
import org.pegdown.PegDownProcessor;
import org.pegdown.plugins.PegDownPlugins;

import com.internetitem.gitdown.Constants;
import com.internetitem.gitdown.FileData;
import com.internetitem.gitdown.GitdownLinkRenderer;
import com.internetitem.gitdown.GitdownUnlabeledImageRule;
import com.internetitem.gitdown.handler.FileHandler;
import com.internetitem.gitdown.view.MarkdownView;

public class MarkdownHandler implements FileHandler {

	public static final LinkRenderer LINK_RENDERER = new GitdownLinkRenderer();

	private ThreadLocal<PegDownProcessor> tlProcessor;

	public MarkdownHandler() {
		this.tlProcessor = new ThreadLocal<PegDownProcessor>() {
			protected PegDownProcessor initialValue() {
				return buildPegDownProcessor();
			};
		};
	}

	private PegDownProcessor buildPegDownProcessor() {
		int options = Extensions.ALL;
		PegDownPlugins plugins = PegDownPlugins.builder().withPlugin(GitdownUnlabeledImageRule.class).build();
		return new PegDownProcessor(options, plugins);
	}

	@Override
	public Response handleFile(ServletContext servletContext, FileData data) throws IOException {
		try {
			PegDownProcessor pdp = tlProcessor.get();
			String source = new String(data.getData(), "UTF-8");
			String result = pdp.markdownToHtml(source, LINK_RENDERER);
			String title = getTitle(data);
			return Response.ok(new MarkdownView(servletContext.getContextPath(), title, result), Constants.CONTENT_TYPE_HTML).build();
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	private String getTitle(FileData data) {
		String filename = data.getActualName();
		String extension = data.getExtension();
		if (extension != null) {
			filename = filename.substring(0, filename.length() - extension.length());
		}
		filename = filename.replaceAll("-", " ");
		return filename;
	}

}
