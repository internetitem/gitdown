package com.internetitem.gitdown;

import com.internetitem.gitdown.handler.FileHandler;

public class FileData {

	private byte[] data;
	private String extension;
	private FileHandler handler;
	private String requestName;
	private String actualName;
	private FileDataType fileDataType;

	public FileData(byte[] data, String extension, FileHandler handler, String requestName, String actualName, FileDataType fileDataType) {
		this.data = data;
		this.extension = extension;
		this.handler = handler;
		this.requestName = requestName;
		this.actualName = actualName;
		this.fileDataType = fileDataType;
	}

	public byte[] getData() {
		return data;
	}

	public String getExtension() {
		return extension;
	}

	public FileHandler getHandler() {
		return handler;
	}

	public String getRequestName() {
		return requestName;
	}

	public String getActualName() {
		return actualName;
	}

	public FileDataType getFileDataType() {
		return fileDataType;
	}
	
	public static FileData notFound(String requestName) {
		return new FileData(null, null, null, requestName, null, FileDataType.NotFound);
	}
	
	public static FileData redirect(String requestName, String actualName) {
		return new FileData(null, null, null, requestName, actualName, FileDataType.NotFound);
	}

	public static enum FileDataType {
		File,
		IndexFile,
		Redirect,
		NotFound
	}
}
