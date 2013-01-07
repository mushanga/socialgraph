package com.amazonbird.servlet;

import com.amazonbird.config.PropsConfigMgrImpl;

public class FileUtil {
	PropsConfigMgrImpl configMgr = PropsConfigMgrImpl.getInstance();

	private static FileUtil instance = new FileUtil();

	private FileUtil() {

	}

	public static FileUtil getInstance() {

		return instance;
	}

	public String getFilePath() {
		String imagesPath = configMgr.getImagesPath();
		if (imagesPath == null || imagesPath.length() == 0) {
			throw new RuntimeException(
					"Pease add 'images' property to tcommer.properties file. Example images=C:\\\\tcommer\\\\");
		} else {
			return imagesPath;
		}

	}

	public String getFilePathLogical() {

		return (configMgr.isDev()) ? "http://localhost:8080/gujum/file/"
				: "http://www.gujum.com/tcommerce/file/";

	}
}
