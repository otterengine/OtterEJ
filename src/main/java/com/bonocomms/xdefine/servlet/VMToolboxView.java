package com.bonocomms.xdefine.servlet;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.context.Context;
import org.apache.velocity.tools.Scope;
import org.apache.velocity.tools.ToolboxFactory;
import org.apache.velocity.tools.config.XmlFactoryConfiguration;
import org.apache.velocity.tools.view.ViewToolContext;
import org.springframework.web.context.support.ServletContextResource;

/**
 * Velocity Tools 2.0부터 적용된 새로운 형태의 XML 형식을 Spring 3에서 제대로 지원하지 않는점을 수정
 * (Configures Velocity Toolbox View to use new XML Configuration in Spring)
 *
 * @author Gregor "hrax" Magdolen
 * @see http://www.elepha.info/elog/archives/305-configuring-velocity-tools-20-in-spring.html
 */
public class VMToolboxView extends org.springframework.web.servlet.view.velocity.VelocityLayoutView {

	@Override
	protected Context createVelocityContext(Map<String, Object> model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// Create a ViewToolContext instance since ChainedContext is deprecated
		// in Velocity Tools 2.0.
		ViewToolContext velocityContext = new ViewToolContext(getVelocityEngine(), request, response, getServletContext());
		velocityContext.putAll(model);

		// Load a Configuration and publish toolboxes to the context when
		// necessary
		if (getToolboxConfigLocation() != null) {
			XmlFactoryConfiguration cfg = new XmlFactoryConfiguration();
			cfg.read(new ServletContextResource(getServletContext(), getToolboxConfigLocation()).getURL());
			ToolboxFactory factory = cfg.createFactory();

			velocityContext.addToolbox(factory.createToolbox(Scope.APPLICATION));
			velocityContext.addToolbox(factory.createToolbox(Scope.REQUEST));
			velocityContext.addToolbox(factory.createToolbox(Scope.SESSION));
		}
		return velocityContext;
	}
}