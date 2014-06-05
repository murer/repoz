package com.murerz.repoz.web.fs;

import java.io.BufferedInputStream;

public interface FileSystem {

	RepozFile read(String path);

	void save(RepozFile file);

}
