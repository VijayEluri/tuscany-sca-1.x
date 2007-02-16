package org.osoa.sca.annotations;

import static java.lang.annotation.ElementType.METHOD;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

/**
 * Annotation that can be applied to an attribute of an @Intent annotation to indicate the
 * attribute provides qualifiers for the intent.
 * 
 * @version $Rev$ $Date$
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface Qualifier {
}
