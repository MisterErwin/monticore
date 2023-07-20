/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.ast_new;

import de.monticore.cd.facade.CDModifier;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._ast.ast_class.ASTService;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ASTServiceTest extends DecoratorTestCase {

  private static String AST_AUT_PACKAGE = "de.monticore.codegen.ast.automaton._ast.";

  private ASTService astService;

  private ASTCDCompilationUnit astcdCompilationUnit;

  private ASTCDClass astAutomaton;

  @Before
  public void setup() {
    astcdCompilationUnit = this.parse("de", "monticore", "codegen", "ast", "Automaton");
    astAutomaton = astcdCompilationUnit.getCDDefinition().getCDClassesList().get(0);

    astService = new ASTService(astcdCompilationUnit);
  }

  @Test
  public void testCDSymbolPresent() {
    assertTrue(astService.getCDSymbol().isPresentAstNode());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testConstructorsCreateEqualService() {
    ASTService astServiceFromDefinitionSymbol = new ASTService(astcdCompilationUnit.getCDDefinition().getSymbol());
    assertTrue(astServiceFromDefinitionSymbol.getCDSymbol().isPresentAstNode());
    assertDeepEquals(astService.getCDSymbol().getAstNode(), astServiceFromDefinitionSymbol.getCDSymbol().getAstNode());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testCreateASTService() {
    ASTService createdASTService = ASTService.createASTService(astcdCompilationUnit.getCDDefinition().getSymbol());
    assertTrue(createdASTService.getCDSymbol().isPresentAstNode());
    assertDeepEquals(astService.getCDSymbol().getAstNode(), createdASTService.getCDSymbol().getAstNode());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSubPackage() {
    assertEquals("_ast", astService.getSubPackage());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetASTBaseInterfaceSimpleName() {
    assertEquals("ASTAutomatonNode", astService.getASTBaseInterfaceSimpleName());
    assertEquals("ASTAutomatonNode", astService.getASTBaseInterfaceSimpleName(astcdCompilationUnit.getCDDefinition().getSymbol()));
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetASTBaseInterfaceFullName() {
    assertEquals(AST_AUT_PACKAGE + "ASTAutomatonNode", astService.getASTBaseInterfaceFullName());
    assertEquals(AST_AUT_PACKAGE + "ASTAutomatonNode", astService.getASTBaseInterfaceFullName(astcdCompilationUnit.getCDDefinition().getSymbol()));
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetASTBaseInterfaceName() {
    assertDeepEquals(AST_AUT_PACKAGE + "ASTAutomatonNode", astService.getASTBaseInterface());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetASTConstantClassSimpleName() {
    assertEquals("ASTConstantsAutomaton", astService.getASTConstantClassSimpleName());
    assertEquals("ASTConstantsAutomaton", astService.getASTConstantClassSimpleName(astcdCompilationUnit.getCDDefinition().getSymbol()));
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetASTConstantClassFullName() {
    assertEquals(AST_AUT_PACKAGE + "ASTConstantsAutomaton", astService.getASTConstantClassFullName());
    assertEquals(AST_AUT_PACKAGE + "ASTConstantsAutomaton", astService.getASTConstantClassFullName(astcdCompilationUnit.getCDDefinition().getSymbol()));
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetASTSimpleName() {
    assertEquals("ASTAutomaton", astService.getASTSimpleName(astAutomaton));
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetASTFullName() {
    assertEquals(AST_AUT_PACKAGE + "ASTAutomaton", astService.getASTFullName(astAutomaton));
    assertEquals(AST_AUT_PACKAGE + "ASTAutomaton", astService.getASTFullName(astAutomaton, astcdCompilationUnit.getCDDefinition().getSymbol()));
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testIsSymbolWithoutName() {
    // test if has a name
    assertFalse(astService.isSymbolWithoutName(astAutomaton));
    // remove name attribute
    List<ASTCDAttribute> attributeList = astAutomaton.deepClone().getCDAttributeList().stream()
        .filter(a -> !"name".equals(a.getName()))
        .collect(Collectors.toList());
    ASTCDClass astAutomatonWithoutName = astAutomaton.deepClone();
    astAutomatonWithoutName.setCDAttributeList(attributeList);
    assertTrue(astService.isSymbolWithoutName(astAutomatonWithoutName));
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testCreateGetNameMethod() {
    ASTCDMethod getNameMethod = astService.createGetNameMethod();
    assertEquals("getName", getNameMethod.getName());
    assertDeepEquals(CDModifier.PUBLIC_ABSTRACT, getNameMethod.getModifier());
    assertTrue(getNameMethod.getMCReturnType().isPresentMCType());
    assertDeepEquals(String.class, getNameMethod.getMCReturnType().getMCType());
    assertTrue(getNameMethod.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
