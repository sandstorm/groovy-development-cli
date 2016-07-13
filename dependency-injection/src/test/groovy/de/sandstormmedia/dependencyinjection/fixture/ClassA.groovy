package de.sandstormmedia.dependencyinjection.fixture

import de.sandstormmedia.dependencyinjection.Inject

class ClassA {
    @Inject SingletonC singletonC
    @Inject ClassB otherClass
    ClassA otherClassFoo
}
