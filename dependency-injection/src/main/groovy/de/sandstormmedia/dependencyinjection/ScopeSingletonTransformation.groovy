package de.sandstormmedia.dependencyinjection

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.ast.stmt.Statement
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation

/**
 * The ScopeSingleton Transformation; which adds static initializer code to the class
 * registering this class as singleton with the ObjectManager:
 *
 * ObjectManager.instance.registerSingleton(this.name)
 */
@GroovyASTTransformation(phase=CompilePhase.SEMANTIC_ANALYSIS)
public class ScopeSingletonTransformation implements ASTTransformation {

	public void visit(ASTNode[] nodes, SourceUnit sourceUnit) {
		AnnotationNode annotationNode = nodes[0]
		ClassNode classNode = nodes[1]


		Statement initializer = new AstBuilder().buildFromSpec {
			expression {
				methodCall {
					property {
						classExpression ObjectManager
						constant "instance"
					}
					constant "registerSingleton"
					argumentList {
						property {
							variable "this"
							constant "name"
						}
					}
				}
			}
		}.head()
		classNode.addStaticInitializerStatements([initializer], false)
	}
}