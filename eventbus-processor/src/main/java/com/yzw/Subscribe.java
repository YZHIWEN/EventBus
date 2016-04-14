package com.yzw;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target(value = ElementType.METHOD)
public @interface Subscribe {
	String tag() default "";

	ThreadMode threadmode() default ThreadMode.UI;

	int priority() default 0;

}
