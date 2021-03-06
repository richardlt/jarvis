package org.jarvis.core.resources;

import java.util.Arrays;

/**
 * resource
 */
public class ResourceData {
	/**
	 * @param value
	 */
	public ResourceData(String value) {
		this.setString(value);
	}
	/**
	 * @param value
	 */
	public ResourceData(byte value[]) {
		this.setByte(value);
	}
	/**
	 * @return String
	 */
	public String getString() {
		return string;
	}
	/**
	 * @return String
	 */
	public byte[] getByte() {
		return binary;
	}
	/**
	 * @param value
	 */
	public void setString(String value) {
		this.string = value;
	}
	/**
	 * @param value
	 */
	public void setByte(byte value[]) {
		this.binary = value;
	}
	/**
	 * @return String
	 */
	public String getContentType() {
		return contentType;
	}
	/**
	 * @param contentType
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	private byte binary[];
	private String string;
	private String contentType;
	
	/**
	 * @return boolean
	 */
	public boolean isOctetStream() {
		return binary != null;
	}

	@Override
	public String toString() {
		return "ResourceData [binary=" + Arrays.toString(binary) + ", string=" + string + ", contentType=" + contentType
				+ "]";
	}
}
