/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types3;

import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.combineexpressionswithliterals._ast.ASTFoo;
import de.monticore.expressions.combineexpressionswithliterals._parser.CombineExpressionsWithLiteralsParser;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsArtifactScope;
import de.monticore.expressions.combineexpressionswithliterals._visitor.CombineExpressionsWithLiteralsTraverser;
import de.monticore.expressions.commonexpressions.types3.util.CommonExpressionsLValueRelations;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.lambdaexpressions._ast.ASTLambdaExpression;
import de.monticore.expressions.lambdaexpressions._symboltable.LambdaExpressionsSTCompleteTypes2;
import de.monticore.ocl.oclexpressions.symboltable.OCLExpressionsSymbolTableCompleter;
import de.monticore.ocl.setexpressions.symboltable.SetExpressionsSymbolTableCompleter;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.types.check.IDerive;
import de.monticore.types.check.ISynthesize;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.types3wrapper.TypeCheck3AsIDerive;
import de.monticore.types.check.types3wrapper.TypeCheck3AsISynthesize;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types3.util.CombineExpressionsWithLiteralsTypeTraverserFactory;
import de.monticore.types3.util.DefsTypesForTests;
import de.monticore.types3.util.DefsVariablesForTests;
import de.monticore.visitor.ITraverser;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.monticore.types3.util.DefsTypesForTests.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * used to provide facilities to test type derivers.
 * main extensions point are the methods
 * setup, setupValues, parseString*, generateScopes, calculateTypes
 */
public class AbstractTypeVisitorTest extends AbstractTypeTest {

  // Parser, etc. used for convenience:
  // (may be any other Parser that understands CommonExpressions)
  protected CombineExpressionsWithLiteralsParser parser;

  // we can use our own type4Ast instance to try to find occurrences of
  // Type Visitors using the map from the mill instead of the provided one
  protected Type4Ast type4Ast;

  protected ITraverser typeMapTraverser;

  @Deprecated
  protected ITraverser scopeGenitor;

  protected ITraverser symbolTableCompleter;

  /**
   * @deprecated this is not the genitor, but the completer
   */
  @Deprecated
  protected ITraverser getScopeGenitor() {
    return scopeGenitor;
  }

  protected ITraverser getSymbolTableCompleter() {
    return symbolTableCompleter;
  }

  protected ITraverser getTypeMapTraverser() {
    return typeMapTraverser;
  }

  @Before
  public void setupDefaultMill() {
    CombineExpressionsWithLiteralsMill.reset();
    CombineExpressionsWithLiteralsMill.init();
    BasicSymbolsMill.initializePrimitives();
    DefsTypesForTests.setup();
    parser = CombineExpressionsWithLiteralsMill.parser();
    type4Ast = new Type4Ast();
    typeMapTraverser = new CombineExpressionsWithLiteralsTypeTraverserFactory()
        .createTraverser(type4Ast);
    setupSymbolTableCompleter(typeMapTraverser, type4Ast);
  }

  protected void setupSymbolTableCompleter(
      ITraverser typeMapTraverser, Type4Ast type4Ast) {
    CombineExpressionsWithLiteralsTraverser combinedScopesCompleter =
        CombineExpressionsWithLiteralsMill.traverser();
    IDerive deriver = new TypeCheck3AsIDerive(
        typeMapTraverser, type4Ast, new CommonExpressionsLValueRelations()
    );
    ISynthesize synthesizer = new TypeCheck3AsISynthesize(
        typeMapTraverser, type4Ast
    );
    combinedScopesCompleter.add4LambdaExpressions(
        new LambdaExpressionsSTCompleteTypes2(
            typeMapTraverser,
            getType4Ast()
        )
    );
    OCLExpressionsSymbolTableCompleter oclExprCompleter =
        new OCLExpressionsSymbolTableCompleter();
    oclExprCompleter.setDeriver(deriver);
    oclExprCompleter.setSynthesizer(synthesizer);
    combinedScopesCompleter.add4OCLExpressions(oclExprCompleter);
    combinedScopesCompleter.setOCLExpressionsHandler(oclExprCompleter);
    SetExpressionsSymbolTableCompleter setExprCompleter =
        new SetExpressionsSymbolTableCompleter();
    setExprCompleter.setDeriver(deriver);
    setExprCompleter.setSynthesizer(synthesizer);
    combinedScopesCompleter.add4SetExpressions(setExprCompleter);
    combinedScopesCompleter.setSetExpressionsHandler(setExprCompleter);
    symbolTableCompleter = combinedScopesCompleter;
    scopeGenitor = combinedScopesCompleter;
  }

  /**
   * adds a set of variables, functions, etc. to the global scope,
   * useful for most non-specific tests
   */
  protected void setupValues() {
    IBasicSymbolsScope gs =
        BasicSymbolsMill.globalScope();
    DefsVariablesForTests.setup(gs);
    // deprecated
    inScope(gs, variable("person1", _personSymType));
    inScope(gs, variable("person2", _personSymType));
    inScope(gs, variable("student1", _studentSymType));
    inScope(gs, variable("student2", _studentSymType));
    inScope(gs, variable("csStudent1", _csStudentSymType));
    inScope(gs, variable("intList",
        SymTypeExpressionFactory.createGenerics(
            _boxedListSymType.getTypeInfo(), _intSymType))
    );
    inScope(gs, variable("intLinkedList",
        SymTypeExpressionFactory.createGenerics(
            _linkedListSymType.getTypeInfo(), _intSymType))
    );
    // non-member functions
    inScope(gs, function("runnable", _voidSymType));
    inScope(gs, function("intProvider", _intSymType));
    FunctionSymbol intConsumer = function("intConsumer", _voidSymType, _intSymType);
    inScope(gs, intConsumer);
    inScope(gs, function("intConsumerProvider", intConsumer.getFunctionType()));
    inScope(gs, function("intEllipticConsumer", _voidSymType,
        List.of(_intSymType), true));
    inScope(gs, function("int2int", _intSymType, _intSymType));
    TypeVarSymbol genericConsumerVar = typeVariable("T");
    FunctionSymbol genericConsumer
        = inScope(gs, function("genericConsumer", _voidSymType,
        SymTypeExpressionFactory.createTypeVariable(genericConsumerVar))
    );
    genericConsumer.getSpannedScope().add(genericConsumerVar);
    inScope(gs, function("overloadedFunc1", _booleanSymType, _intSymType));
    inScope(gs, function("overloadedFunc1", _intSymType, _booleanSymType));
  }

  // Parse a String expression of the according language
  protected Optional<ASTExpression> parseStringExpr(String exprStr)
      throws IOException {
    return parser.parse_StringExpression(exprStr);
  }

  // Parse a String type identifier of the according language
  protected Optional<ASTMCType> parseStringMCType(String mcTypeStr)
      throws IOException {
    return parser.parse_StringMCType(mcTypeStr);
  }

  protected void generateScopes(ASTExpression expr) {
    // create a root
    ASTFoo rootNode = CombineExpressionsWithLiteralsMill.fooBuilder()
        .setExpression(expr)
        .build();
    ICombineExpressionsWithLiteralsArtifactScope rootScope =
        CombineExpressionsWithLiteralsMill.scopesGenitorDelegator()
            .createFromAST(rootNode);
    rootScope.setName("fooRoot");
    // complete the symbol table
    expr.accept(getSymbolTableCompleter());
  }

  protected void generateScopes(ASTMCType mcType) {
    // create an expression to contain the type
    // currently (MC 7.5) lambda expressions are the only expressions
    // which can directly contain MCTypes
    ASTLambdaExpression lambda = CombineExpressionsWithLiteralsMill
        .lambdaExpressionBuilder()
        .setLambdaParameters(
            CombineExpressionsWithLiteralsMill.lambdaParametersBuilder()
                .setLambdaParametersList(List.of(
                    CombineExpressionsWithLiteralsMill.lambdaParameterBuilder()
                        .setName("parameter")
                        .setMCType(mcType)
                        .build()
                ))
                .build()
        )
        .setLambdaBody(
            CombineExpressionsWithLiteralsMill.lambdaExpressionBodyBuilder()
                .setExpression(
                    CombineExpressionsWithLiteralsMill.literalExpressionBuilder()
                        .setLiteral(
                            CombineExpressionsWithLiteralsMill
                                .natLiteralBuilder()
                                .setDigits("8243721")
                                .build()
                        )
                        .build()
                )
                .setType(SymTypeExpressionFactory.createPrimitive("int"))
                .build()
        )
        .build();
    // create a root
    ASTFoo rootNode = CombineExpressionsWithLiteralsMill.fooBuilder()
        .setExpression(lambda)
        .build();
    ICombineExpressionsWithLiteralsArtifactScope rootScope =
        CombineExpressionsWithLiteralsMill.scopesGenitorDelegator()
            .createFromAST(rootNode);
    rootScope.setName("fooRoot");
  }

  protected void calculateTypes(ASTExpression expr) {
    expr.accept(typeMapTraverser);
  }

  protected void calculateTypes(ASTMCType mcType) {
    mcType.accept(typeMapTraverser);
  }

  protected ASTExpression parseExpr(String exprStr) throws IOException {
    Optional<ASTExpression> astExpression = parseStringExpr(exprStr);
    assertTrue(getAllFindingsAsString(), astExpression.isPresent());
    return astExpression.get();
  }

  protected ASTMCType parseMCType(String typeStr) throws IOException {
    Optional<ASTMCType> mcType = parseStringMCType(typeStr);
    assertTrue(getAllFindingsAsString(), mcType.isPresent());
    return mcType.get();
  }

  protected void checkExpr(String exprStr, String expectedType)
      throws IOException {
    checkExpr(exprStr, expectedType, true);
  }

  protected void checkExpr(
      String exprStr, String expectedType, boolean allowNormalization
  ) throws IOException {
    ASTExpression astexpr = parseExpr(exprStr);
    generateScopes(astexpr);
    calculateTypes(astexpr);
    assertNoFindings();
    assertTrue("No type calculated for expression " + exprStr,
        getType4Ast().hasTypeOfExpression(astexpr));
    SymTypeExpression type = getType4Ast().getTypeOfExpression(astexpr);
    assertNoFindings();
    // usually, type normalization is expected and (basically) always allowed
    // for specific tests, however, it may be required to disable this
    boolean equalsNormalized =
        expectedType.equals(SymTypeRelations.normalize(type).printFullName());
    if (!allowNormalization || !equalsNormalized) {
      assertEquals("Wrong type for expression " + exprStr,
          expectedType,
          type.printFullName()
      );
    }
  }

  protected void checkType(String typeStr, String expectedType)
      throws IOException {
    ASTMCType astType = parseMCType(typeStr);
    generateScopes(astType);
    calculateTypes(astType);
    SymTypeExpression type = getType4Ast().getTypeOfTypeIdentifier(astType);
    assertNoFindings();
    assertEquals("Wrong type for type identifier " + typeStr,
        expectedType,
        type.printFullName()
    );
  }

  /**
   * roundtrip test: parse, calculate type, print, compare
   */
  protected void checkTypeRoundTrip(String typeStr) throws IOException {
    checkType(typeStr, typeStr);
  }

  protected void checkErrorExpr(String exprStr, String expectedError)
      throws IOException {
    ASTExpression astExpr = parseExpr(exprStr);
    generateScopes(astExpr);
    assertNoFindings();
    calculateTypes(astExpr);
    SymTypeExpression type = getType4Ast().getTypeOfExpression(astExpr);
    assertTrue("expected Obscure for expression \"" + exprStr +
        "\" but got " + type.printFullName(), type.isObscureType());
    assertHasErrorCode(expectedError);
    Log.getFindings().clear();
  }

  protected void checkErrorMCType(String typeStr, String expectedError)
      throws IOException {
    ASTMCType astType = parseMCType(typeStr);
    generateScopes(astType);
    assertNoFindings();
    Log.getFindings().clear();
    calculateTypes(astType);
    SymTypeExpression type = getType4Ast().getPartialTypeOfTypeId(astType);
    assertTrue("expected Obscure for expression \"" + typeStr +
        "\" but got " + type.printFullName(), type.isObscureType());
    assertHasErrorCode(expectedError);
  }

  protected List<String> getFirstErrorCodes(long n) {
    List<String> errorsInLog = Log.getFindings().stream()
        .filter(Finding::isError)
        .map(err -> err.getMsg().split(" ")[0])
        .limit(n)
        .collect(Collectors.toList());
    List<String> errorsToReturn;

    if (errorsInLog.size() < n) {
      errorsToReturn = errorsInLog;
      for (int i = 0; i < n - errorsInLog.size(); i++) {
        errorsToReturn.add("");
      }
    }
    else {
      errorsToReturn = errorsInLog.subList(0, (int) n);
    }
    return errorsToReturn;
  }

  protected List<String> getAllErrorCodes() {
    return getFirstErrorCodes(Log.getErrorCount());
  }

  protected boolean hasErrorCode(String code) {
    return getAllErrorCodes().stream().anyMatch(code::equals);
  }

  protected void assertHasErrorCode(String code) {
    assertTrue(
        "Error \"" + code + "\" expected, "
            + "but instead the errors are:"
            + System.lineSeparator()
            + Log.getFindings().stream()
            .map(Finding::buildMsg)
            .collect(Collectors.joining(System.lineSeparator()))
            + System.lineSeparator(),
        hasErrorCode(code)
    );
  }

  protected Type4Ast getType4Ast() {
    return type4Ast;
  }
}
