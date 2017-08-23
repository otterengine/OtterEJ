package net.xdefine.tools;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import net.xdefine.tools.vo.HttpResult;

public class HttpClient {
	
	private static final int DEFUALT_TIMEOUT = 1000;
	private static String userAgent = "";
	private static String encode = "UTF-8";
	
	public static void setEncode(String enc) {
		encode = enc;
	}

	public static void setUserAgent(String agent) {
		userAgent = agent;
	}

	public static HttpResult get(String url) {
		return getWithHeaders(url, null, DEFUALT_TIMEOUT);
	}
	
	public static HttpResult get(String url, int timeout) {
		return getWithHeaders(url, null, timeout);
	}

	public static HttpResult getWithAuthorize(String url, String header) {
		return getWithAuthorize(url, header, DEFUALT_TIMEOUT);
	}
	
	public static HttpResult getWithAuthorize(String url, String header, int timeout) {
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Authorization", header);
		return getWithHeaders(url, headers, timeout);
	}

	public static HttpResult getWithHeaders(String url, Map<String, String> headers) {
		return getWithHeaders(url, headers, DEFUALT_TIMEOUT);
	}
	
	public static HttpResult getWithHeaders(String url, Map<String, String> headers, int timeout) {

		if (headers == null) headers = new HashMap<String, String>();
		if (!headers.containsKey("User-Agent") && userAgent != null && !userAgent.isEmpty()) {
			headers.put("User-Agent", userAgent);
		}

		HttpResult result = new HttpResult();
		
		try {
			HttpURLConnection.setFollowRedirects(true);
			HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
			con.setDoInput(true);

			for (String key : headers.keySet()) {
				con.setRequestProperty(key.trim(), headers.get(key));
			}

			con.setRequestMethod("GET");
			if (timeout > 0) con.setReadTimeout(timeout);
			con.setDoOutput(true);
			con.setUseCaches(false);

			result = getResult(con, (url.contains(".json") || url.contains(".ajax")));
			con.disconnect();
		}
		catch(Exception ex) {
			result = new HttpResult();
			result.setSuccess(false);
		}
		
		return result;
	}

	public static HttpResult post(String url, String param) {
		return postWithHeaders(url, param, null, DEFUALT_TIMEOUT);
	}
	
	public static HttpResult post(String url, String param, int timeout) {
		return postWithHeaders(url, param, null, timeout);
	}

	public static HttpResult postWithAuthorize(String url, String param, String header) {
		return postWithAuthorize(url, param, header, DEFUALT_TIMEOUT);
	}
	
	public static HttpResult postWithAuthorize(String url, String param, String header, int timeout) {
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Authorization", header);
		return postWithHeaders(url, param, headers, timeout);
	}

	public static HttpResult postWithHeaders(String url, String param, Map<String, String> headers) {
		return postWithHeaders(url, param, headers, 1000);
	}
	
	public static HttpResult postWithHeaders(String url, String param, Map<String, String> headers, int timeout) {

		if (headers == null) headers = new HashMap<String, String>();
		if (!headers.containsKey("User-Agent") && userAgent != null && !userAgent.isEmpty()) {
			headers.put("User-Agent", userAgent);
		}
		headers.put("Content-Type", "application/x-www-form-urlencoded");
		
		HttpResult result = new HttpResult();
		
		try {
			URL fbURL = new URL(url);
			HttpURLConnection con = (HttpURLConnection) fbURL.openConnection();
			con.setDoInput(true);

			for (String key : headers.keySet()) {
				con.setRequestProperty(key.trim(), headers.get(key));
			}
			
			con.setRequestMethod("POST");
			con.setDoOutput(true);
			if (timeout > 0) con.setReadTimeout(timeout);
			con.setUseCaches(false);

			OutputStream out = con.getOutputStream();
			out.write( param.getBytes(encode) );
			out.flush();
		    out.close();

			result = getResult(con, (url.contains(".json") || url.contains(".ajax")));
		}
		catch(Exception ex) {
			result.setSuccess(false);
		}
		
		return result;
		
	}
	
	private static HttpResult getResult(HttpURLConnection con, boolean perLine) throws Exception {
		
		HttpResult result = new HttpResult();
		
		boolean isText = con.getContentType() != null && (con.getContentType().contains("text/") || con.getContentType().contains("application/"));

		InputStream in;
		if (con.getResponseCode() >= 400) {
			in = con.getErrorStream();
			result.setSuccess(false);
		}
		else {
			in = con.getInputStream();
			result.setSuccess(true);
		}
		
		String enc = encode;
		if (con.getContentType().contains("charset")) {
			enc = con.getContentType().substring(con.getContentType().indexOf("charset=") + 8).trim();
		}

		StringBuffer buffer = new StringBuffer();
		if (perLine || isText) {
			BufferedReader br = new BufferedReader(new InputStreamReader(in, enc));

			String line = "";
			while ((line = br.readLine()) != null) buffer.append(line + "\n");
			
			br.close();
		}
		else {
			byte[] b = new byte[4096];
			for (int n; (n = in.read(b)) != -1;) buffer.append(new String(b, 0, n));
		}
		
		result.setStatus(con.getResponseCode());
		result.setData(buffer.toString());
		
		return result;
	}
}
