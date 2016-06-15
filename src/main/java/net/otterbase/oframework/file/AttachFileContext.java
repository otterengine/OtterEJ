package net.otterbase.oframework.file;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

import net.otterbase.oframework.OFContext;
import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicMatch;

public class AttachFileContext {

	public AttachFileVO createFileByMultipart(IAttachIndex index, CommonsMultipartFile filedata) throws Exception {
		
		AttachFileVO result = new AttachFileVO();

		File dir = new File(OFContext.getProperty("webapp.file.path") + File.separator + index.getSavedPath());
		if (!dir.isDirectory()) dir.mkdirs();

		InputStream in = filedata.getInputStream();
		OutputStream out = new BufferedOutputStream(new FileOutputStream(dir.toString() + File.separator + "source"));

		byte[] buffer = new byte[1024];
		int numRead;
		long readBytes = 0;
		while ((numRead = in.read(buffer)) != -1) {
			out.write(buffer, 0, numRead);
			if (readBytes == 0) {
				MagicMatch match = Magic.getMagicMatch(buffer);
				result.setContentType(match.getMimeType());
			}
			readBytes += numRead;
		}
		
		if (in != null) in.close();
		if (out != null) out.close();
		
		String filename = filedata.getFileItem().getName();
		
		result.setFilename(filename);
		result.setFilesize(readBytes);
		result.setExtention(filename.contains(".") ? filename.substring(filename.lastIndexOf(".") + 1) : "unknown");
		if (result.getContentType() == null) {
			result.setContentType("unknown");
		}
		
		return result;
	}

	public AttachFileVO createFileByURL(IAttachIndex result, URL imgURL) {

		/*

		URLConnection imgConnection = null;
		
		InputStream in = null;
		OutputStream out = null;

		try {
			
			Date now = new Date();
			URL imgURL = new URL(imageUrl);			
			
			String suffix = "jpeg";
			try {
				if (imgURL.getPath().substring(imgURL.getPath().lastIndexOf(".") + 1).toLowerCase() == "jpg") {
					suffix = "jpeg";
				}
				else if (imgURL.getPath().substring(imgURL.getPath().lastIndexOf(".") + 1).toLowerCase() == "png") {
					suffix = "png";
				}
				else if (imgURL.getPath().substring(imgURL.getPath().lastIndexOf(".") + 1).toLowerCase() == "gif") {
					suffix = "gif";
				}
			}
			catch(Exception ex) {}
			
			result = new AttachFile();

			result.setFilename(imgURL.getPath().substring(imgURL.getPath().lastIndexOf("/") + 1));
			result.setExtension("image");
			result.setByteLength(0);
			result.setServPath(new SimpleDateFormat("yyyy/MM").format(now));
			result.setContentType("image/" + suffix);
			result.setCreatedAt(now);

			if (result.getFilename().length() > 255)
				result.setFilename(result.getFilename().substring(0, 250));

			session.persist(result);

			File dir = new File(OFContext.getProperty("file.savePath") + "/" + result.getServPath() + "/" + result.getId());
			if (!dir.isDirectory()) dir.mkdirs();

			imgConnection = imgURL.openConnection();
			in = imgConnection.getInputStream(); 
			out = new BufferedOutputStream(new FileOutputStream(dir.toString() + "/source"));

			byte[] buffer = new byte[1024];
			int numRead;
			long readBytes = 0;
			while ((numRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, numRead);
				readBytes += numRead;
			}
			
			if (readBytes <= 0) throw new Exception();
			result.setByteLength(readBytes);
			
			if (in != null) in.close();
			if (out != null) out.close();

			session.getTransaction().commit();
		} catch (Exception ex) {
			session.getTransaction().rollback();
			ex.printStackTrace();
			result = null;
		} finally {
			try {
				if (in != null) in.close();
				if (out != null) out.close();
			} catch (IOException ioe) {
			}
		}
		*/
		// TODO Auto-generated method stub
		return null;
	}

}
