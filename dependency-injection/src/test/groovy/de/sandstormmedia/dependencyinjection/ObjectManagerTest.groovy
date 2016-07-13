package de.sandstormmedia.dependencyinjection

import de.sandstormmedia.dependencyinjection.fixture.CircularSingletonA
import de.sandstormmedia.dependencyinjection.fixture.CircularSingletonB
import de.sandstormmedia.dependencyinjection.fixture.ClassA
import de.sandstormmedia.dependencyinjection.fixture.ClassB
import de.sandstormmedia.dependencyinjection.fixture.SingletonC
import org.junit.Test
import static org.junit.Assert.*

/**
 * Testcases for the basic Dependency Injection
 */
class ObjectManagerTest {

	@Test
	void basicInjectionWorks() {
		def a = new ClassA()
		def a2 = new ClassA()
		assertTrue(a.otherClass instanceof ClassB)
		assertNull(a.otherClassFoo)
		assertNotSame(a.otherClass, a2.otherClass)
	}

	@Test
	void singletonInjectionWorks() {
		def a = new ClassA()
		def b = new ClassB()
		assertTrue(a.singletonC instanceof SingletonC)
		assertSame(a.singletonC, b.singletonC)
	}

	@Test
	void circularSingletonInjectionCurrentlyNotSupported() {
		try {
			CircularSingletonA a = ObjectManager.instance.get('de.sandstormmedia.dependencyinjection.fixture.CircularSingletonA', this.class.classLoader)
			CircularSingletonB b = ObjectManager.instance.get('de.sandstormmedia.dependencyinjection.fixture.CircularSingletonB', this.class.classLoader)

			fail("circular dependencies currently not supported")
		} catch (StackOverflowError e) {
			// Expected
		}
	}
}
