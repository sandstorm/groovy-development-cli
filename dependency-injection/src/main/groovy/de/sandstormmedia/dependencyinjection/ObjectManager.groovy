package de.sandstormmedia.dependencyinjection

/**
 * The Dependency Injection Object Manager.
 *
 * Field which are annotated with @Inject are post-compiled and
 * access this ObjectManager to get the desired instance.
 *
 * Classes annotated with @ScopeSingleton register themselves as Singletons
 * in the same manner.
 *
 * see InjectTransformation and ScopeSingletonTransformation
 */
class ObjectManager {
    private List<String> objectNamesRegisteredAsSingleton = Collections.synchronizedList([])
    private Map<String, Object> singletonInstances = Collections.synchronizedMap([:])

    /**
     * class loader to create instances which later are injected
     */
    public ClassLoader classLoader

    /**
     * constructor of this singleton
     */
    private ObjectManager() {
        this.classLoader = this.class.classLoader
    }

    /**
     * singleton instance
     */
    private static ObjectManager instance = new ObjectManager()

    /**
     * singleton instance
     *
     * @return
     */
    public static ObjectManager getInstance() {
        return instance
    }

    /**
     * reset this manager to its initial state
     */
    public void reset() {
        objectNamesRegisteredAsSingleton = []
        singletonInstances = [:]
    }

    /**
     * called by ScopeSingletonTransformation
     *
     * @param objectName
     */
    public void registerSingleton(String objectName) {
        objectNamesRegisteredAsSingleton << objectName
    }

    /**
     * called by InjectTransformation
     *
     * @param objectName
     * @param classLoader
     * @return
     */
    public Object get(String objectName, ClassLoader classLoader) {
        // if the object is a known singleton, we directly return it
        if (singletonInstances[objectName]) {
            return singletonInstances[objectName]
        }

        // we need to INITIALIZE the class when we load it (if it has not been loaded before)
        // -> that's the second parameter which is "true".
        //
        // This initialization triggers the *static initializer code*, which in turn registers
        // the class as singleton if it has not been encountered before.
        def clazz = classLoader.loadClass(objectName)

        def instance = clazz.newInstance()

        // Thus, only at this point after we know the class was already loaded, we KNOW
        // whether the class is singleton or not. THIS CHECK MUST BE BELOW THE CLASS-LOADING
        // FROM ABOVE!
        if (objectNamesRegisteredAsSingleton.contains(objectName)) {
            singletonInstances[objectName] = instance
        }

        return instance
    }
}
