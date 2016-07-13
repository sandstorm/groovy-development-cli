package de.sandstormmedia.groovyrunner

import de.sandstormmedia.dependencyinjection.ObjectManager

/**
 * Main class of the groovy runner.
 */
public class Main {
	private static final Set<String> exitCommands = new HashSet<String>(['exit', 'x'])
	private static final stopCommand = 'stop'
	private static CommandRunner runner

	public static void main(String[] args) {
		String sourceDirectories = System.getProperty('sandstormmedia.groovyrunner.source')
		if (sourceDirectories == null) {
			throw new RuntimeException('Groovy-Runner was started without the -Dsandstormmedia.groovyrunner.source argument, but this is needed.')
		}

		println "Interactive Groovy Runner started."
		Binding binding = new Binding()
		binding.setVariable("arguments", args)
		runner = new CommandRunner(binding, sourceDirectories.split(','))

		// execute commands from argument stack
		if (args.length > 0) {
			println "Executing commands ${args.join(', ')}"
		}
		for (int i = 0; i < args.length; i++) {
			if (!executeCommand(args[i])) {
				System.exit(0)
				return // terminate on exitCommand
			}
		}

		// switch to interactive mode
		println "Run 'help' for a list of available commands"
		println "Run '${stopCommand}' to stop without exiting the console"
		println "Run '${exitCommands.first()}' to terminate"
		println "Running in interactive mode."
		print '> '
		def command = readLine('help')
		while (executeCommand(command)) {
			command = readLine(command)
		}
		System.exit(0)
	}

	private static String readLine(String defaultLine) {
		def input = System.console().readLine().trim()
		if (input == "") {
			return defaultLine
		}
		return input
	}

	private static boolean executeCommand(String command) {
		runner.terminate()
		if (exitCommands.contains(command)) {
			return false
		}
		if (command == stopCommand) {
			print '> '
		} else {
			runner.start(command)
		}
		return true
	}

	private static class CommandRunner {
		private final Binding binding
		private boolean isRunning = false
		private Thread worker = null
		private String[] scripts
		private String[] sourceDirectories

		/**
		 * constructor
		 *
		 * @param binding - binding to use
		 */
		CommandRunner(Binding binding, String[] sourceDirectories) {
			this.binding = binding

			this.sourceDirectories = sourceDirectories

			this.scripts = new String[sourceDirectories.length * 4];
			for (int i = 0; i < sourceDirectories.length; i++) {
				this.scripts[i * 4 + 0] = sourceDirectories[i] + '/src/main/groovy/'
				this.scripts[i * 4 + 1] = sourceDirectories[i] + '/src/main/resources/'
				this.scripts[i * 4 + 2] = sourceDirectories[i] + '/src/test/groovy/'
				this.scripts[i * 4 + 3] = sourceDirectories[i] + '/src/main/scriptGroovy/'
			}

		}

		/**
		 * start the given command
		 *
		 * @param command command to start, e.g. 'run' or 'run noImport'
		 * @exception IllegalStateException this method has already been called an the terminate methods has not
		 */
		public void start(String command) {
			if (worker && isRunning) {
				throw new IllegalStateException("Cannot start a command if another is already running. Please terminate running command first.")
			}

			isRunning = true

			worker = Thread.start {
				// we build up a completely *FRESH* GroovyScriptEngine to be sure that it gets
				// a new classloader; and thus does not "see" the old classes.
				GroovyScriptEngine groovyScriptEngine = new GroovyScriptEngine(scripts)

				// Every thread can have a ContextClassLoader; and when a thread starts a new thread, the context class loader
				// is by default taken from the starting thread.
				// The following line is especially important for the Spark Framework, where the Executor is running
				// in a pretty-much-isolated Class Loader by default; thus without this line, the Executor would not
				// be able to pick up the Spark Job classes.
				// With this line, the Executor's ClassLoader is the GroovyScriptEngine one; so all scripts are properly found.
				// One (negative) side-effect of this is that Spark must be restarted at every run (because the classloader) changes;
				// otherwise changes to the classes are not picked up.
				Thread.currentThread().setContextClassLoader(groovyScriptEngine.getGroovyClassLoader())

				ObjectManager.instance.reset()
				ObjectManager.instance.classLoader = groovyScriptEngine.getGroovyClassLoader()

				ApplicationDescriptor application = groovyScriptEngine.run("ScriptMain.groovy", binding)
				application.startCommand(command, sourceDirectories)
				while (isRunning) {
					Thread.sleep(100)
				}
				application.stopCommand(command)
			}
		}

		/**
		 * terminates the command currently running if a command is running
		 * does nothing otherwise
		 */
		public void terminate() {
			if (worker && isRunning) {
				isRunning = false
				worker.join()
			}
		}

	}
}