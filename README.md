# Groovy Development CLI

Use the *groovy-runner* and/or the *dependency-injection* via maven dependency.

```
repositories {
    maven {
        url "https://dl.bintray.com/sandstorm/maven"
    }
}
dependencies {
    compile "de.sandstormmedia.groovy-development-cli:dependency-injection:1.0.0"
    compile "de.sandstormmedia.groovy-development-cli:groovy-runner:1.0.0"
}
```

Our versions are [Semantic Versions](http://semver.org/).

## groovy-runner

The *groovy-runner* is the library you can build your own CLI tool with. It is not executable: it is a library.

### Usage

We launched an example project to show how to build a custom CLI.

* [github.com/sandstorm/groovy-development-cli-hello-world](https://github.com/sandstorm/groovy-development-cli-hello-world)


## Dependency Injection

This is a very light-weight dependency injection framework based in annotation.
Note that cyclic dependencies are not supported yet.

### Usage

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

We use [bintray.com](http://bintray.com) to publish the maven artifacts.
If you are interested, it is done via their [github.com/bintray/gradle-bintray-plugin](https://github.com/bintray/gradle-bintray-plugin).

Since publishing in a multi publishing environment is hardly covered by the documentation,
  we peeked at [github.com/budjb/http-requests](https://github.com/budjb/http-requests).
