package de.sandstormmedia.dependencyinjection.fixture

import de.sandstormmedia.dependencyinjection.Inject

/**
 * Created by sebastian on 09.01.14.
 */
class ClassA {

	@Inject
	ClassB otherClass

	ClassA otherClassFoo

	@Inject
	SingletonC singletonC
}