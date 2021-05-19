/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java;

import de.monticore.cd4analysis._symboltable.CD4AnalysisSymbolTableCompleter;
import de.monticore.cd4analysis._symboltable.ICD4AnalysisGlobalScope;
import de.monticore.cd4analysis._symboltable.ICD4AnalysisScope;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._parser.CD4CodeParser;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cd4code._symboltable.ICD4CodeGlobalScope;
import de.monticore.cd4code.trafo.CD4CodeAfterParseTrafo;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.io.paths.ModelPath;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.fail;

public abstract class DecoratorTestCase {

  private static final String MODEL_PATH = "src/test/resources/";

  @Before
  public void setUpDecoratorTestCase() {
    Log.init();
    Log.enableFailQuick(false);
    CD4CodeMill.reset();
    CD4CodeMill.init();
    BasicSymbolsMill.initializePrimitives();
    ICD4CodeGlobalScope globalScope = CD4CodeMill.globalScope();
    BasicSymbolsMill.initializePrimitives();
    globalScope.setFileExt("cd");
    globalScope.setModelPath(new ModelPath(Paths.get(MODEL_PATH)));
  }

  public ASTCDCompilationUnit parse(String... names) {
    String qualifiedName = String.join("/", names);

    CD4CodeParser parser = CD4CodeMill.parser();
    Optional<ASTCDCompilationUnit> ast = null;
    try {
      ast = parser.parse(MODEL_PATH + qualifiedName + ".cd");
    } catch (IOException e) {
      fail(String.format("Failed to load model '%s'", qualifiedName));
    }
    if (!ast.isPresent()) {
      fail(String.format("Failed to load model '%s'", qualifiedName));
    }

    ASTCDCompilationUnit comp = ast.get();
    new CD4CodeAfterParseTrafo().transform(ast.get());

    CD4CodeMill.scopesGenitorDelegator().createFromAST(comp);
    comp.getEnclosingScope().setAstNode(comp);

    return comp;
  }

  /**
   * Collects all compilation units and calls the cd type completer for
   * completing the symbol table of each cd.
   *
   * @param gs The given global scope to extract all compilation units
   */
  public void completeCDTypes(ICD4AnalysisGlobalScope gs) {
    for (ICD4AnalysisScope scope : gs.getSubScopes()) {
      if (!scope.getDiagramSymbols().isEmpty()) {
        // artifact scopes with a diagram symbol always yield to the AST node of a compilation unit
        ASTCDCompilationUnit comp = (ASTCDCompilationUnit) scope.getAstNode();

        // complete types for CD
        CD4CodeSymbolTableCompleter v = new CD4CodeSymbolTableCompleter(comp);
        comp.accept(v.getTraverser());
      }
    }
  }

  /**
   * Imports all super CDs based on a given compilation unit.
   *
   * @param comp Input compiluation unit
   */
  public void importCDs(ASTCDCompilationUnit comp) {
    for (ASTMCImportStatement imp : comp.getMCImportStatementList()) {
      String impName = String.join("/", imp.getMCQualifiedName().getPartsList());
      ASTCDCompilationUnit sub = parse(impName);
      importCDs(sub);
    }
  }
}
