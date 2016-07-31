package com.bonocomms.xdefine.common.interceptor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.nio.charset.Charset;
import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.bonocomms.xdefine.XFContext;
import com.bonocomms.xdefine.common.UAgentInfo;
import com.bonocomms.xdefine.common.wrapper.ServletContextHolder;
import com.bonocomms.xdefine.http.HttpClient;
import com.bonocomms.xdefine.vo.ParamData;

public class RequestInterceptor extends HandlerInterceptorAdapter {
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		
		ServletContextHolder.instance().sync(request, response);
		ParamData.sync(request);
		
		try {
			String url = request.getRequestURL().toString();
			url = url.substring(url.indexOf("//") + 2);
			url = url.substring(0, url.indexOf("/"));
			
			String domain = "";
			File file = new File(XFContext.getProperty("webapp.file.path") + "/.xdefine");
			if (file.exists()) {
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.MONTH, -1);
				if (file.lastModified() > cal.getTime().getTime()) throw new Exception();
				
				FileInputStream fis = new FileInputStream(file);
				domain = IOUtils.toString(fis, Charset.defaultCharset());
			}
			else {
				file.createNewFile();
			}
			
			if (!url.equals(domain)) {
				final String fixurl = url;
				Thread thread = new Thread() {
					@Override
					public void run() {
						super.run();
						HttpClient.get("http://xdefine.bonocomms.com/site?url=" + fixurl);
					}
				};
				thread.start();
				
				BufferedWriter writer = new BufferedWriter(new FileWriter(file));
	            writer.write(url);
				writer.close();
			}
		}
		catch(Exception ex) {
		}
		
//		request.setAttribute("http_url", pm.get("webapp.site_web_url"));
//		request.setAttribute("https_url", pm.get("webapp.site_ssl_url"));
		
		UAgentInfo uaInfo = new UAgentInfo(request.getHeader("user-agent"), null);
		
		request.setAttribute("_xs", uaInfo.isMobilePhone);
		request.setAttribute("_sm", uaInfo.isMobilePhone || uaInfo.isTierTablet);
		request.setAttribute("_cpx", request.getContextPath());
				
		request.setCharacterEncoding("UTF-8");
		return super.preHandle(request, response, handler);
		
	}
}
