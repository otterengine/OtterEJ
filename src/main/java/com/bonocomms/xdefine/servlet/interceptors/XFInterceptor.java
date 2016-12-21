package com.bonocomms.xdefine.servlet.interceptors;

import org.hibernate.SessionFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public abstract class XFInterceptor extends HandlerInterceptorAdapter {

	protected SessionFactory sessionFactory;

	public XFInterceptor() {

	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

}
