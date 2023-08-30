/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.ast_new.reference;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._ast.ast_class.reference.ASTReferenceDecorator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getAttributeBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ASTReferenceDecoratorTest extends DecoratorTestCase {


  private ASTCDClass astMandClass;

  private ASTCDClass astOptClass;

  private ASTCDClass astListClass;

  private ASTReferenceDecorator<ASTCDClass> referenceDecorator;

  @Before
  public void setup() {
    ASTCDCompilationUnit ast = this.parse("de", "monticore", "codegen", "ast", "ReferencedSymbol");
    this.referenceDecorator = new ASTReferenceDecorator(this.glex, new SymbolTableService(ast));
    ASTCDClass mandclazz = getClassBy("ASTBarMand", ast);
    ASTCDClass changedClass = CD4AnalysisMill.cDClassBuilder().setName(mandclazz.getName())
        .setModifier(mandclazz.getModifier())
        .build();
    this.astMandClass = referenceDecorator.decorate(mandclazz, changedClass);
    ASTCDClass optclazz = getClassBy("ASTBarOpt", ast);
    ASTCDClass changedOptClass = CD4AnalysisMill.cDClassBuilder().setName(optclazz.getName())
        .setModifier(changedClass.getModifier())
        .build();
    this.astOptClass = referenceDecorator.decorate(optclazz, changedOptClass);
    ASTCDClass listclazz = getClassBy("ASTBarList", ast);
    ASTCDClass changedListClass = CD4AnalysisMill.cDClassBuilder().setName(listclazz.getName())
        .setModifier(listclazz.getModifier())
        .build();
    this.astListClass = referenceDecorator.decorate(listclazz, changedListClass);
  }

  @Test
  public void testMandatoryAttributeSize() {
    assertFalse(astMandClass.getCDAttributeList().isEmpty());
    assertEquals(1, astMandClass.getCDAttributeList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMandatorySymbolAttribute() {
    ASTCDAttribute nameSymbol = getAttributeBy("nameSymbol", astMandClass);
    assertTrue(nameSymbol.getModifier().isProtected());
    assertDeepEquals("de.monticore.codegen.ast.referencedsymbol._symboltable.FooSymbol", nameSymbol.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testOptionalAttributeSize() {
    assertFalse(astOptClass.getCDAttributeList().isEmpty());
    assertEquals(1, astOptClass.getCDAttributeList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testOptionalSymbolAttribute() {
    ASTCDAttribute nameSymbol = getAttributeBy("nameSymbol", astOptClass);
    assertTrue(nameSymbol.getModifier().isProtected());
    assertDeepEquals("de.monticore.codegen.ast.referencedsymbol._symboltable.FooSymbol", nameSymbol.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testListAttributeSize() {
    assertFalse(astListClass.getCDAttributeList().isEmpty());
    assertEquals(1, astListClass.getCDAttributeList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testListSymbolAttribute() {
    ASTCDAttribute nameSymbol = getAttributeBy("nameSymbol", astListClass);
    assertTrue(nameSymbol.getModifier().isProtected());
    assertDeepEquals("Map<String,de.monticore.codegen.ast.referencedsymbol._symboltable.FooSymbol>", nameSymbol.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMandatoryMethods() {
    assertEquals(5, astMandClass.getCDMethodList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void testOptionalMethods() {
    assertEquals(5, astOptClass.getCDMethodList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void testListMethods() {
    assertEquals(39, astListClass.getCDMethodList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

}
