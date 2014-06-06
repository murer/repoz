package com.murerz.repoz.web.fs;

public class FileSystemFactory {

	public static FileSystem create() {
		return new RawFileSystem();
	}


}
