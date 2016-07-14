# Groovy Development CLI

The *groovyrunner* is the library you can build your own CLI tool with. It is not executable: it is a library.

## Hello World

We launched an example project to show how to build a custom CLI: [github.com/sandstorm/groovy-development-cli-hello-world]()


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

# External Documentation

## Artifact Publication via Bintray

bintray.com/
github.com/bintray/gradle-bintray-plugin
github.com/budjb/http-requests
