# Groovy Development CLI

TODO


# Dependency Injection

This is a very light-weight dependency injection framework based in annotation.
Note that cyclic dependencies are not supported yet.

## Usage

Make sure that your classes to inject have an parameterless constructor.

```groovy
class ClassA {
    @Inject SingletonC singletonC
    @Inject ClassB otherClass
    ClassA otherClassFoo
}

@ScopeSingleton
class SingletonC {
}
```