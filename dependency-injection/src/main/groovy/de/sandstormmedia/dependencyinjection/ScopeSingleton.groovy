package de.sandstormmedia.dependencyinjection

import org.codehaus.groovy.transform.GroovyASTTransformationClass
import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Scope Singleton annotation for the Dependency Injection Framework
 */
@Retention(RetentionPolicy.SOURCE)
@Target([ElementType.TYPE])
@GroovyASTTransformationClass(["de.sandstormmedia.dependencyinjection.ScopeSingletonTransformation"])
public @interface ScopeSingleton {
}
