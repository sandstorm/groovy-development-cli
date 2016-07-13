package de.sandstormmedia.dependencyinjection

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.control.messages.SyntaxErrorMessage
import org.codehaus.groovy.syntax.SyntaxException
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation

/**
 * The Inject Transformation, which replaces the code:
 *
 * @Inject
 * MyClassName foo
 *
 * with:
 * MyClassName foo = ObjectManager.instance.get("fully-qualified.MyClassName", this.class.classLoader)
 */
@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
public class InjectTransformation implements ASTTransformation {

    public void visit(ASTNode[] nodes, SourceUnit sourceUnit) {
        sourceUnit.getAST().getClasses().each { classNode ->
            classNode.getProperties()
            classNode.getFields().each { fieldNode ->
                if (fieldNode.getAnnotations(new ClassNode(Inject))) {
                    if (fieldNode.dynamicTyped == true) {

                        // Error Message
                        SyntaxException syntaxException = new SyntaxException('@Inject annotation not allowed on dynamically typed field.\n', fieldNode.lineNumber, fieldNode.columnNumber)
                        SyntaxErrorMessage syntaxErrorMessage = new SyntaxErrorMessage(syntaxException, sourceUnit)
                        sourceUnit.errorCollector.addErrorAndContinue(syntaxErrorMessage)
                    } else {

                        // Build up "[myField] = ObjectManager.instance.get("<fully-qualified-class-name-here>", this.class.classLoader)
                        fieldNode.initialValueExpression = new AstBuilder().buildFromSpec {
                            methodCall {
                                property {
                                    classExpression ObjectManager
                                    constant "instance"
                                }
                                constant "get"
                                argumentList {
                                    constant fieldNode.type.name
                                    property {
                                        property {
                                            variable "this"
                                            constant "class"
                                        }
                                        constant "classLoader"
                                    }
                                }
                            }
                        }.head()
                    }
                }
            }
        }

    }
}
