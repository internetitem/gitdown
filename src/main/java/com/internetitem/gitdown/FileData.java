package com.internetitem.gitdown;

public class FileData {

	private byte[] data;
	private String requestName;
	private String actualName;
	private FileDataType fileDataType;

	public FileData(byte[] data, String requestName, String actualName, FileDataType fileDataType) {
		this.data = data;
		this.requestName = requestName;
		this.actualName = actualName;
		this.fileDataType = fileDataType;
	}

	public byte[] getData() {
		return data;
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

	public static enum FileDataType {
		File,
		IndexFile,
		Redirect,
		NotFound
	}
}
