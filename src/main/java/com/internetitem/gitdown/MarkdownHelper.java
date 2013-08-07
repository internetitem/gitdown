package com.internetitem.gitdown;

import java.io.UnsupportedEncodingException;

import org.pegdown.Extensions;
import org.pegdown.LinkRenderer;
import org.pegdown.PegDownProcessor;

import com.internetitem.gitdown.config.GitDownConfiguration;

public class MarkdownHelper {

	public static final LinkRenderer LINK_RENDERER = new GitdownLinkRenderer();

	private GitDownConfiguration configuration;

	private ThreadLocal<PegDownProcessor> tlProcessor;

	public MarkdownHelper(GitDownConfiguration configuration) {
		this.configuration = configuration;
		this.tlProcessor = new ThreadLocal<>();
	}

	public boolean isMarkdown(String name) {
		for (String extension : configuration.getMarkdownExtensions()) {
			if (name.endsWith(extension)) {
				return true;
			}
		}
		return false;
	}

	public byte[] convertMarkdown(byte[] data) {
		try {
			PegDownProcessor pdp = getPegDownProcessor();
			String source = new String(data, "UTF-8");
			String result = pdp.markdownToHtml(source, LINK_RENDERER);
			return result.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	private PegDownProcessor getPegDownProcessor() {
		PegDownProcessor processor = tlProcessor.get();
		if (processor == null) {
			processor = buildPegDownProcessor();
			tlProcessor.set(processor);
		}
		return processor;
	}

	private PegDownProcessor buildPegDownProcessor() {
		int options = Extensions.WIKILINKS | Extensions.TABLES | Extensions.AUTOLINKS;
		return new PegDownProcessor(options);
	}
}
