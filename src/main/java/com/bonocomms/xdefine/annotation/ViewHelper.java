package com.bonocomms.xdefine.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ViewHelper {
	
	public String name();

}
