package com.murerz.repoz.web.fs;

public interface FileSystem {

	RepozFile read(String path);

	void save(RepozFile file);

	void delete(String path);

	void deleteAll();

}
