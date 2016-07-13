package de.sandstormmedia.dependencyinjection.fixture

import de.sandstormmedia.dependencyinjection.Inject
import de.sandstormmedia.dependencyinjection.ScopeSingleton

@ScopeSingleton
class CircularSingletonA {

	@Inject
	CircularSingletonB b
}
