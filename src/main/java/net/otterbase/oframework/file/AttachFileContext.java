package net.otterbase.oframework.file;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.google.common.io.Files;
import com.mortennobel.imagescaling.AdvancedResizeOp;
import com.mortennobel.imagescaling.ResampleOp;

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

	public AttachFileVO createFileByURL(IAttachIndex index, URL imgURL) throws Exception {

		AttachFileVO result = new AttachFileVO();

		File dir = new File(OFContext.getProperty("webapp.file.path") + File.separator + index.getSavedPath());
		if (!dir.isDirectory()) dir.mkdirs();
		
		HttpURLConnection con = (HttpURLConnection) imgURL.openConnection();
		con.setDoInput(true);
		con.setRequestMethod("GET");
		con.setDoOutput(true);
		con.setReadTimeout(5000);
		con.setUseCaches(false);
		

		InputStream in;
		if (con.getResponseCode() >= 400) {
			in = con.getErrorStream();
		}
		else {
			in = con.getInputStream();
		}

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
		
		String filename = imgURL.getPath();
		if (filename.contains("/"))
			filename = filename.substring(filename.lastIndexOf("/") + 1);
		
		result.setFilename(filename);
		result.setFilesize(readBytes);
		result.setExtention(filename.contains(".") ? filename.substring(filename.lastIndexOf(".") + 1) : "unknown");
		if (result.getContentType() == null) {
			result.setContentType("unknown");
		}
		
		return result;

	}

	public AttachFileVO createFileByFile(IAttachIndex index, File file) throws Exception {

		AttachFileVO result = new AttachFileVO();

		File dir = new File(OFContext.getProperty("webapp.file.path") + File.separator + index.getSavedPath());
		if (!dir.isDirectory()) dir.mkdirs();
		
		File to = new File(dir.toString() + File.separator + "source");
		Files.copy(file, to);
		
		String filename = file.getName();
		result.setFilename(filename);
		result.setFilesize(file.length());
		result.setExtention(filename.contains(".") ? filename.substring(filename.lastIndexOf(".") + 1) : "unknown");
		if (result.getContentType() == null) {
			result.setContentType("unknown");
		}
		
		return result;
	}

	public File getFile(IAttachIndex index) {
		return new File(OFContext.getProperty("webapp.file.path") + "/" + index.getSavedPath() + "/source");
	}
	
	public File getFile(IAttachIndex index, int width, int height, String type) {

		File tFile = new File(OFContext.getProperty("file.savePath") + "/" + index.getSavedPath() + "/" + width + "x" + height + "_" + type + ".jpg");
		if (tFile.exists()) return tFile;

		File afile = new File(OFContext.getProperty("file.savePath") + "/" + index.getSavedPath() + "/source");

		try {
			if (OFContext.getProperty("exec.imageMagick") != null && !OFContext.getProperty("exec.imageMagick").isEmpty()) {
				if (System.getProperty("os.name").contains("indows")) {
		            String[] cmd = new String[] { "\"" + OFContext.getProperty("exec.imageMagick") + "\"", afile.toString(), "-resize", width + "x" + height + "^", "-quality", "100", "-gravity", "center", "-crop", width + "x" + height + "+0+0", "+repage", tFile.getPath() };
		            Process process = Runtime.getRuntime().exec(cmd);
		            process.waitFor();
				}
				else {
		            String[] cmd = new String[] { "/bin/sh", "-c", OFContext.getProperty("exec.imageMagick") + " " + afile.getPath() + " -resize " + width + "x" + height+ "^ -quality 100 -gravity center -crop " +  width + "x" + height + "+0+0 +repage" + " " + tFile.getPath() };
		            Process process = Runtime.getRuntime().exec(cmd);
		            process.waitFor();
				}
			}
			else {
				tFile = this.getFileForGD(index, width, height, type);
			}
		}
		catch(Exception ex) {
			ex.printStackTrace();
			tFile = this.getFileForGD(index, width, height, type);
		}

		return tFile;
	}
	
	private File getFileForGD(IAttachIndex index, int width, int height, String thumbnail_type) {

		File tFile = new File(OFContext.getProperty("file.savePath") + "/" + index.getSavedPath() + "/" + width + "x" + height + "_" + thumbnail_type + ".jpg");

		try {
			
			File file = new File(OFContext.getProperty("file.savePath") + "/" + index.getSavedPath() + "/source");
			BufferedImage img = ImageIO.read(file.toURI().toURL());

			double width_per;
			double height_per;
			double per = 0;
			double resize_width = width;
			double resize_height = height;

			if (resize_width > 0 && img.getWidth() >= resize_width)
				width_per = resize_width / img.getWidth();
			else
				width_per = 1;

			if (resize_height > 0 && img.getHeight() >= resize_height)
				height_per = resize_height / img.getHeight();
			else
				height_per = 1;

			if (thumbnail_type.equals("ratio")) {
				if (width_per > height_per)
					per = height_per;
				else
					per = width_per;
				resize_width = ((double) img.getWidth() * per);
				resize_height = ((double) img.getHeight() * per);
			} else {
				if (width_per < height_per)
					per = height_per;
				else
					per = width_per;
			}

			if (per == 0)
				per = 1;

			int _x = 0;
			int _y = 0;

			int new_width = (int) (img.getWidth() * per);
			int new_height = (int) (img.getHeight() * per);

			if (thumbnail_type.equals("crop")) {
				_x = (int) (resize_width / 2 - new_width / 2);
				_y = (int) (resize_height / 2 - new_height / 2);
			} else {
				_x = 0;
				_y = 0;
			}

			ResampleOp resampleOp = new ResampleOp(new_width, new_height);
			resampleOp.setUnsharpenMask(AdvancedResizeOp.UnsharpenMask.Normal);
			BufferedImage destImg = resampleOp.filter(img, null);

			int type = img.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : img.getType();
			BufferedImage targetImage = new BufferedImage((int) resize_width, (int) resize_height, type);
			Graphics2D graphics2D = targetImage.createGraphics();
			graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			graphics2D.setBackground(Color.WHITE);
			graphics2D.fillRect(0, 0, (int) resize_width, (int) resize_height);
			graphics2D.drawImage(destImg, _x - 2, _y - 2, (int) new_width + 4, (int) new_height + 4, null);
			
			Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("jpeg");
			ImageWriter writer = (ImageWriter) iter.next();
			ImageWriteParam iwp = writer.getDefaultWriteParam();
			iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			iwp.setCompressionQuality(0.9f);

			FileImageOutputStream output = new FileImageOutputStream(tFile);
			writer.setOutput(output);

			
			IIOImage image = new IIOImage(targetImage, null, null);
			writer.write(null, image, iwp);
			writer.dispose();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return tFile;
	}

}
