package org.jf.recordwither;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target({ElementType.RECORD_COMPONENT, ElementType.TYPE})
public @interface WitherIgnore {
}
