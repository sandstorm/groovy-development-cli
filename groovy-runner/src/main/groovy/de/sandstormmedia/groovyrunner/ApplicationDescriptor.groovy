package de.sandstormmedia.groovyrunner

import groovy.io.FileType
import org.junit.runner.Description
import org.junit.runner.JUnitCore
import org.junit.runner.notification.Failure
import org.junit.runner.notification.RunListener

/**
 * Base class to be used for the "Main" class of an Executable runnable through GroovyRunner.
 */
abstract class ApplicationDescriptor extends Script {
    private String[] sourceDirectories

    @Override
    public Object run() {
        return this
    }

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    /****************************************
     * SECTION: GENERIC DISPATCHER
     ****************************************/

    /**
     * Generic dispatcher which starts commands.
     *
     * @param command
     */
    public void startCommand(String command, String[] sourceDirectories) {
        this.sourceDirectories = sourceDirectories
        def commandParts = command.split(' ')
        command = commandParts[0]
        def arguments = []
        def argumentTypes = []
        if (commandParts.length > 1) {
            arguments = commandParts[1..commandParts.length - 1]
            argumentTypes = arguments.collect { String.class }
        }
        def methodExists = this.metaClass.respondsTo(this, "${command}StartCommand", argumentTypes.toArray()).size() > 0
        println ""
        if (methodExists) {
            this."${command}StartCommand"(*arguments)
        } else {
            println "Method '${command}StartCommand' with ${argumentTypes.size()} String arguments not found. Press 'help' for a list of all commands"
        }

        print '> '
    }

    /**
     * Generic dispatcher which stops commands.
     *
     * @param command
     */
    public void stopCommand(String command) {
        def commandParts = command.split(' ')
        command = commandParts[0]
        def arguments = []
        def argumentTypes = []
        if (commandParts.length > 1) {
            arguments = commandParts[1..commandParts.length - 1]
            argumentTypes = arguments.collect { String.class }
        }
        def methodExists = this.metaClass.respondsTo(this, "${command}StopCommand", argumentTypes.toArray()).size() > 0
        if (methodExists) {
            this."${command}StopCommand"(*arguments)
        } else {
            println "info: no stop method '${command}StopCommand' found."
        }
    }

    /****************************************
     * SECTION: Help
     ****************************************/

    public void helpStartCommand() {
        help()
        println ""
        println "All Commands found:"
        this.metaClass.methods.findAll { method ->
            return method.name ==~ /^.*StartCommand/
        }.each { method ->
            def shortName = method.name.substring(0, method.name.length() - 12)
            println "- $shortName"
        }
        println ""
    }

    public void help() {
        println "TERMINAL BEHAVIOR"
        println "================="
        println "If no command is entered the previous command is executed again."
        println "If a command is executed it terminates the previous command."
        println ""
        println "> help          shows help"
        println ""
    }

    public void helpStopCommand() {}


    /****************************************
     * SECTION: Test Command
     ****************************************/

    /**
     * Run unit tests for the given classes.
     *
     * @param testClassNames
     */
    void testStartCommand(String classRegex) {
        // collect all test class names
        List<String> testClassNames = []
        def testBaseDirectories = sourceDirectories.collect({ sourceDirectory ->
            return new File(sourceDirectory + '/src/test/groovy/')
        }).findAll { testBaseDirectory ->
            def exists = testBaseDirectory.exists()
            if (!exists) {
                println ANSI_YELLOW + "Missing $testBaseDirectory.absolutePath. Continuing..." + ANSI_RESET
            }
            return exists
        }
        testBaseDirectories.each { testBaseDirectory ->
            testBaseDirectory.eachFileRecurse(FileType.FILES) { file ->
                if (file.name.endsWith('Test.groovy')) {
                    // might be a base class for other tests
                    if (file.name.startsWith('Abstract')) {
                        if (file.text.contains('abstract class')) {
                            return
                        }
                    }

                    def relativeFileName = file.absolutePath.substring(testBaseDirectory.absolutePath.length() + 1, file.absolutePath.length() - 7)
                    testClassNames << relativeFileName.replace('/', '.')
                }
            }
        }

        testClassNames = testClassNames.findAll { testClassName ->
            testClassName ==~ '.*' + classRegex + '.*'
        }
        List<Class> testClasses = testClassNames.collect { testClassName ->
            return Class.forName(testClassName, false, this.class.classLoader)
        }
        def jUnit = new JUnitCore()
        def runListener = new RunListener() {
            public void testFinished(Description description) {
                println description.className + '::' + description.methodName
            }
        }
        jUnit.addListener(runListener)
        def result = jUnit.run(*testClasses)
        println ""

        if (result.failureCount > 0) {
            println "$ANSI_RED${result.failureCount} FAILURES $ANSI_RESET (${result.runCount} in total)"

            def groupedByTestClass = (Map<Class, List<Failure>>) [:]
            result.getFailures().each { failure ->
                def testClass = failure.description.testClass
                def bucket = groupedByTestClass[testClass]
                if (bucket == null) {
                    bucket = groupedByTestClass[testClass] = []
                }
                bucket << failure
            }
            def failedTests = groupedByTestClass.keySet().sort { it?.simpleName }
            failedTests.each { testClass ->
                if (testClass == null) {
                    println "${ANSI_RED}Unknown test class${ANSI_RESET}"
                } else {
                    println "$ANSI_RED$testClass.simpleName$ANSI_RESET ($testClass.canonicalName)"
                }
                groupedByTestClass[testClass].each {
                    println "\t$it.description.methodName"
                    if (result.failureCount < 5 || groupedByTestClass.size() == 1) {
                        println '\t\t' + it.trace.split('\n').join('\n\t\t')
                    }
                }
            }
        } else {
            println "$ANSI_GREEN SUCCESS $ANSI_RESET (${result.runCount} executed)"
        }

        println ""
    }

    void testStartCommand() {
        testStartCommand('')
    }

    void testStopCommand() {}

    void testStopCommand(String regex) {}

}
