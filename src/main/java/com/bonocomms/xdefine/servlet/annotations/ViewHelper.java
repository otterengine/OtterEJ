package com.bonocomms.xdefine.servlet.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ViewHelper {
	
	public String name();

}
