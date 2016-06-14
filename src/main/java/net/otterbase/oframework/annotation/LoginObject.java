package net.otterbase.oframework.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface LoginObject {

	String nameField();
	String[] trueFields();
	
}
