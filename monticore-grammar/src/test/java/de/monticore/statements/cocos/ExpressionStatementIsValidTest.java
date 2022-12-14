/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.cocos;

import de.monticore.statements.mccommonstatements.cocos.ExpressionStatementIsValid;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCBlockStatement;
import de.monticore.statements.testmccommonstatements.TestMCCommonStatementsMill;
import de.monticore.statements.testmccommonstatements._cocos.TestMCCommonStatementsCoCoChecker;
import de.monticore.statements.testmccommonstatements._parser.TestMCCommonStatementsParser;
import de.monticore.statements.testmccommonstatements._visitor.TestMCCommonStatementsTraverser;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.types.check.FlatExpressionScopeSetter;
import de.monticore.types.check.FullDeriveFromCombineExpressionsWithLiterals;
import de.monticore.types.check.FullSynthesizeFromCombineExpressionsWithLiterals;
import de.monticore.types.check.IDerive;
import de.monticore.types.check.ISynthesize;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.TypeCalculator;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ExpressionStatementIsValidTest {

  protected TestMCCommonStatementsCoCoChecker checker;

  @BeforeClass
  public static void init() {
    TestMCCommonStatementsMill.reset();
    TestMCCommonStatementsMill.init();
    TestMCCommonStatementsMill.globalScope().clear();
    BasicSymbolsMill.initializePrimitives();
    Log.enableFailQuick(false);
    initSymbols();
  }

  @Before
  public void setUp() {
    Log.clearFindings();
    ISynthesize synthesize = new FullSynthesizeFromCombineExpressionsWithLiterals();
    IDerive derive = new FullDeriveFromCombineExpressionsWithLiterals();
    TypeCalculator typeCalc = new TypeCalculator(synthesize, derive);
    this.checker = new TestMCCommonStatementsCoCoChecker();
    this.checker.addCoCo(new ExpressionStatementIsValid(typeCalc));
  }

  protected static void initSymbols() {
    FieldSymbol anInt = TestMCCommonStatementsMill.fieldSymbolBuilder()
      .setName("anInt")
      .setType(SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.INT))
      .setEnclosingScope(TestMCCommonStatementsMill.globalScope())
      .setAstNodeAbsent()
      .build();
    TestMCCommonStatementsMill.globalScope().add(anInt);
  }

  public void checkValid(String expressionString) throws IOException {
    TestMCCommonStatementsParser parser = new TestMCCommonStatementsParser();
    Optional<ASTMCBlockStatement> ast = parser.parse_StringMCBlockStatement(expressionString);

    TestMCCommonStatementsTraverser traverser = TestMCCommonStatementsMill.inheritanceTraverser();
    FlatExpressionScopeSetter scopeSetter = new FlatExpressionScopeSetter(TestMCCommonStatementsMill.globalScope());
    traverser.add4ExpressionsBasis(scopeSetter);
    ast.orElseThrow().accept(traverser);

    checker.checkAll(ast.orElseThrow());
    assertTrue(Log.getFindings().toString(), Log.getFindings().isEmpty());
  }

  public void checkInvalid(String expressionString) throws IOException {
    TestMCCommonStatementsParser parser = new TestMCCommonStatementsParser();
    Optional<ASTMCBlockStatement> ast = parser.parse_StringMCBlockStatement(expressionString);

    TestMCCommonStatementsTraverser traverser = TestMCCommonStatementsMill.inheritanceTraverser();
    FlatExpressionScopeSetter scopeSetter = new FlatExpressionScopeSetter(TestMCCommonStatementsMill.globalScope());
    traverser.add4ExpressionsBasis(scopeSetter);
    ast.orElseThrow().accept(traverser);

    checker.checkAll(ast.orElseThrow());
    assertFalse(Log.getFindings().toString(), Log.getFindings().isEmpty());
  }

  @Test
  public void testIsValid() throws IOException {
    checkValid("anInt = 5;");
  }

  @Test
  public void testIsInvalid() throws IOException {
    checkInvalid("anInt = true;");
  }
}
