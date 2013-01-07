package com.amazonbird.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amazonbird.config.PropsConfigMgrImpl;

/**
 * This is a File Upload Servlet that is used with AJAX to monitor the progress
 * of the uploaded file. It will return an XML object containing the meta
 * information as well as the percent complete.
 */
public class FileServlet extends HttpServlet implements Servlet {
	private static final long serialVersionUID = 2740693677625051632L;

	PropsConfigMgrImpl configMgr = PropsConfigMgrImpl.getInstance();

	public FileServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
		String url = request.getRequestURL().toString();
		String fileName = "";
		int slashIndex = url.lastIndexOf('/');

		fileName = url.substring(slashIndex + 1);

		String physicalFileName = FileUtil.getInstance().getFilePath() + fileName;

		// Get the absolute path of the image
		ServletContext sc = getServletContext();

		// Get the MIME type of the image
		String mimeType = sc.getMimeType(physicalFileName);
		if (mimeType == null) {
			sc.log("Could not get MIME type of " + physicalFileName);
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}

		// Set content type
		resp.setContentType(mimeType);

		// Set content size
		File file = new File(physicalFileName);
		resp.setContentLength((int) file.length());

		// Open the file and output streams
		FileInputStream in = new FileInputStream(file);
		OutputStream out = resp.getOutputStream();

		// Copy the contents of the file to the output stream
		byte[] buf = new byte[1024];
		int count = 0;
		while ((count = in.read(buf)) >= 0) {
			out.write(buf, 0, count);
		}
		in.close();
		out.close();
	}

}
