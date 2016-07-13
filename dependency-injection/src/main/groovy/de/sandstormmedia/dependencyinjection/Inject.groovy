package de.sandstormmedia.dependencyinjection

import org.codehaus.groovy.transform.GroovyASTTransformationClass
import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Inject annotation for the Dependency Injection Framework
 */
@Retention(RetentionPolicy.SOURCE)
@Target([ElementType.FIELD])
@GroovyASTTransformationClass(["de.sandstormmedia.dependencyinjection.InjectTransformation"])
public @interface Inject {
}
