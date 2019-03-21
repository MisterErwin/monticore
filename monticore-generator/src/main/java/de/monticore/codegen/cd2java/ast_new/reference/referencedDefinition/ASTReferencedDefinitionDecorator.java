package de.monticore.codegen.cd2java.ast_new.reference.referencedDefinition;

import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java.ast_new.reference.ReferencedSymbolUtil;
import de.monticore.codegen.cd2java.ast_new.reference.referencedDefinition.referencedDefinitionMethodDecorator.ReferencedDefinitionAccessorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.codegen.cd2java.factories.CDModifier.PRIVATE;

public class ASTReferencedDefinitionDecorator extends AbstractDecorator<ASTCDClass, ASTCDClass> {

  private static final String DEFINITION = "Definition";

  private final ReferencedDefinitionAccessorDecorator accessorDecorator;

  public ASTReferencedDefinitionDecorator(final GlobalExtensionManagement glex, final ReferencedDefinitionAccessorDecorator accessorDecorator) {
    super(glex);
    this.accessorDecorator = accessorDecorator;
  }

  @Override
  public ASTCDClass decorate(ASTCDClass input) {
    List<ASTCDAttribute> attributeList = new ArrayList<>();
    List<ASTCDMethod> methodList = new ArrayList<>();
    for (ASTCDAttribute astcdAttribute : input.getCDAttributeList()) {
      if (ReferencedSymbolUtil.isReferencedSymbolAttribute(astcdAttribute)) {
        String referencedSymbolType = ReferencedSymbolUtil.getReferencedSymbolTypeName(astcdAttribute);
        //create referenced symbol attribute and methods
        methodList.addAll(getRefDefinitionMethods(astcdAttribute, referencedSymbolType));
      }
    }
    input.addAllCDMethods(methodList);
    input.addAllCDAttributes(attributeList);
    return input;
  }

  protected List<ASTCDMethod> getRefDefinitionMethods(ASTCDAttribute astcdAttribute, String referencedSymbol) {
    ASTType symbolType;
    String referencedNode = referencedSymbol.substring(0, referencedSymbol.lastIndexOf("_symboltable")) + GeneratorHelper.AST_PACKAGE_SUFFIX_DOT + GeneratorHelper.AST_PREFIX + ReferencedSymbolUtil.getSimpleSymbolName(referencedSymbol);
    if (GeneratorHelper.isListType(astcdAttribute.printType())) {
      //if the attribute is a list
      symbolType = getCDTypeFactory().createListTypeOf(referencedNode);
    } else {
      //if the attribute is mandatory or optional
      symbolType = getCDTypeFactory().createOptionalTypeOf(referencedNode);
    }
    ASTCDAttribute refSymbolAttribute = getCDAttributeFactory().createAttribute(PRIVATE, symbolType, astcdAttribute.getName() + DEFINITION);
    refSymbolAttribute.getModifier().setStereotype(astcdAttribute.getModifier().getStereotype().deepClone());
    return accessorDecorator.decorate(refSymbolAttribute);
  }
}
