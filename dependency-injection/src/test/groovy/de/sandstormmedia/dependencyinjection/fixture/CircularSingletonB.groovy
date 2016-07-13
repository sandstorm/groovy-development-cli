package de.sandstormmedia.dependencyinjection.fixture

import de.sandstormmedia.dependencyinjection.Inject
import de.sandstormmedia.dependencyinjection.ScopeSingleton

@ScopeSingleton
class CircularSingletonB {

	@Inject
	CircularSingletonA a
}
