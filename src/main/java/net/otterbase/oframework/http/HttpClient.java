package net.otterbase.oframework.http;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpClient {
	public static HttpResult get(String url) {

		HttpResult result = new HttpResult();
		
		try {
			URL fbURL = new URL(url);
			HttpURLConnection con = (HttpURLConnection) fbURL.openConnection();
			con.setDoInput(true);
			con.setRequestMethod("GET");
			con.setDoOutput(true);
			con.setReadTimeout(5000);
			con.setUseCaches(false);

			InputStream in;
			if (con.getResponseCode() >= 400) {
				in = con.getErrorStream();
				result.setSuccess(false);
			}
			else {
				in = con.getInputStream();
				result.setSuccess(true);
			}

			StringBuffer buffer = new StringBuffer();
			if (url.contains(".json") || url.contains(".ajax")) {
				BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));

				String line = "";
				while ((line = br.readLine()) != null) {
					buffer.append(line + "\n");
				}
			}
			else {
				byte[] b = new byte[4096];
				for (int n; (n = in.read(b)) != -1;) buffer.append(new String(b, 0, n));
			}
			result.setStatus(con.getResponseCode());
			result.setData(buffer.toString());

		}
		catch(Exception ex) {
			ex.printStackTrace();
			result.setSuccess(false);
		}
		
		return result;
		
	}

	public static HttpResult post(String url, String param) {

		HttpResult result = new HttpResult();
		
		try {
			URL fbURL = new URL(url);
			HttpURLConnection con = (HttpURLConnection) fbURL.openConnection();
			con.setDoInput(true);
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			con.setRequestMethod("POST");
			con.setDoOutput(true);
			con.setReadTimeout(1000);
			con.setUseCaches(false);

			OutputStream out = con.getOutputStream();
			out.write( param.getBytes("UTF-8") );
			out.flush();
		    out.close();

			InputStream in;
			if (con.getResponseCode() >= 400) {
				in = con.getErrorStream();
				result.setSuccess(false);
			}
			else {
				in = con.getInputStream();
				result.setSuccess(true);
			}

			StringBuffer buffer = new StringBuffer();
			byte[] b = new byte[4096];
			for (int n; (n = in.read(b)) != -1;) buffer.append(new String(b, 0, n));

			result.setStatus(con.getResponseCode());
			result.setData(buffer.toString());
		}
		catch(Exception ex) {
			ex.printStackTrace();
			result.setSuccess(false);
		}
		
		return result;
		
	}

	public static HttpResult postWithAuthorize(String url, String param, String header) {

		HttpResult result = new HttpResult();
		
		try {
			URL fbURL = new URL(url);
			HttpURLConnection con = (HttpURLConnection) fbURL.openConnection();
			con.setDoInput(true);
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			con.setRequestProperty("Authorization", header);
			con.setRequestMethod("POST");
			con.setDoOutput(true);
			con.setReadTimeout(1000);
			con.setUseCaches(false);

			OutputStream out = con.getOutputStream();
			out.write( param.getBytes("UTF-8") );
			out.flush();
		    out.close();

			InputStream in;
			if (con.getResponseCode() >= 400) {
				in = con.getErrorStream();
				result.setSuccess(false);
			}
			else {
				in = con.getInputStream();
				result.setSuccess(true);
			}

			StringBuffer buffer = new StringBuffer();
			byte[] b = new byte[4096];
			for (int n; (n = in.read(b)) != -1;) buffer.append(new String(b, 0, n));

			result.setStatus(con.getResponseCode());
			result.setData(buffer.toString());
		}
		catch(Exception ex) {
			ex.printStackTrace();
			result.setSuccess(false);
		}
		
		return result;
		
	}
}
