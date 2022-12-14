/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.builder.inheritedmethods;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.codegen.cd2java._ast.builder.buildermethods.BuilderMandatoryMutatorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import org.apache.commons.lang3.StringUtils;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;

/**
 * changes return type of builder setters for mandatory attributes
 */
public class InheritedBuilderMandatoryMutatorDecorator extends BuilderMandatoryMutatorDecorator {

  public InheritedBuilderMandatoryMutatorDecorator(final GlobalExtensionManagement glex,
                                                   final ASTMCType builderType) {
    super(glex, builderType);
  }

  @Override
  protected ASTCDMethod createSetter(final ASTCDAttribute ast) {
    String name = String.format(SET, StringUtils.capitalize(getDecorationHelper().getNativeAttributeName(ast.getName())));
    ASTCDMethod method = this.getCDMethodFacade().createMethod(PUBLIC.build(), builderType, name, this.getCDParameterFacade().createParameters(ast));
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_ast.builder.SetInherited", ast, name));
    return method;
  }
}
