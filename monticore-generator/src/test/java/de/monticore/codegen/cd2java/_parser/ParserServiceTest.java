/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._parser;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.types.MCTypeFacade;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ParserServiceTest extends DecoratorTestCase {

  private ParserService parserService;

  private MCTypeFacade mcTypeFacade;

  private ASTCDCompilationUnit astcdCompilationUnit;

  private ASTCDClass astAutomaton;

  @Before
  public void setup() {
    this.mcTypeFacade = MCTypeFacade.getInstance();

    astcdCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable", "Automaton");
    astAutomaton = astcdCompilationUnit.getCDDefinition().getCDClassesList().get(0);

    parserService = new ParserService(astcdCompilationUnit);
  }

  @Test
  public void testCDSymbolPresent(){
    assertTrue(parserService.getCDSymbol().isPresentAstNode());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testCreateParserService() {
    ParserService createdParserService = ParserService.createParserService(astcdCompilationUnit.getCDDefinition().getSymbol());
    assertTrue(createdParserService.getCDSymbol().isPresentAstNode());
    assertDeepEquals(parserService.getCDSymbol().getAstNode(), createdParserService.getCDSymbol().getAstNode());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSubPackage() {
    assertEquals("_parser", parserService.getSubPackage());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetParserClassNameMethods(){
    assertEquals("AutomatonParser", parserService.getParserClassSimpleName());
    assertEquals("AutomatonParser", parserService.getParserClassSimpleName(parserService.getCDSymbol()));
    assertEquals("de.monticore.codegen.symboltable.automaton._parser.AutomatonParser", parserService.getParserClassFullName());
    assertEquals("de.monticore.codegen.symboltable.automaton._parser.AutomatonParser", parserService.getParserClassFullName(parserService.getCDSymbol()));
    
    assertTrue(Log.getFindings().isEmpty());
 }

  @Test
  public void testGetAntlrParserNameMethods(){
    assertEquals("AutomatonAntlrParser", parserService.getAntlrParserSimpleName());
    assertEquals("AutomatonAntlrParser", parserService.getAntlrParserSimpleName(parserService.getCDSymbol()));
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testRemoveASTPrefix(){
    String automatonClassName = astAutomaton.getName();
    assertEquals("ASTAutomaton", automatonClassName);
    assertEquals("Automaton", parserService.removeASTPrefix(automatonClassName));
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetParseRuleNameJavaCompatible(){
    assertEquals("automaton", parserService.getParseRuleNameJavaCompatible(astAutomaton));
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetStartProd(){
    assertTrue(parserService.getStartProd().isPresent());
    assertEquals(parserService.getStartProd(), parserService.getStartProd((ASTCDDefinition) parserService.getCDSymbol().getAstNode()));
    assertEquals("de.monticore.codegen.symboltable.Automaton.ASTAutomaton", parserService.getStartProd().get());
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
