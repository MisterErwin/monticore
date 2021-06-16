/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd;

import de.monticore.MontiCoreScript;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.codegen.mc2cd.scopeTransl.MC2CDScopeTranslation;
import de.monticore.codegen.mc2cd.symbolTransl.MC2CDSymbolTranslation;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammarfamily.GrammarFamilyMill;
import de.monticore.grammar.grammarfamily._symboltable.GrammarFamilyPhasedSTC;
import de.monticore.grammar.grammarfamily._symboltable.IGrammarFamilyGlobalScope;
import de.monticore.io.paths.MCPath;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.monticore.types.mcfullgenerictypes.MCFullGenericTypesMill;
import parser.MCGrammarParser;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class TestHelper {

  /**
   * Convenience bundling of parsing and transformation
   *
   * @param model the .mc4 file that is to be parsed and transformed
   * @return the root node of the resulting CD AST
   */

  public static Optional<ASTCDCompilationUnit> parseAndTransformForSymbol(Path model) {
    Optional<ASTMCGrammar> grammar = MCGrammarParser.parse(model);
    if (!grammar.isPresent()) {
      return Optional.empty();
    }
    IGrammarFamilyGlobalScope symbolTable = createGlobalScope(new MCPath(Paths.get("src/test/resources")));
    GrammarFamilyPhasedSTC stc = new GrammarFamilyPhasedSTC();
    stc.createFromAST(grammar.get());
    ASTCDCompilationUnit cdCompilationUnit = new MC2CDSymbolTranslation().apply(grammar.get());
    return Optional.of(cdCompilationUnit);
  }

  public static Optional<ASTCDCompilationUnit> parseAndTransformForScope(Path model) {
    Optional<ASTMCGrammar> grammar = MCGrammarParser.parse(model);
    if (!grammar.isPresent()) {
      return Optional.empty();
    }
    MontiCoreScript mc = new MontiCoreScript();
    IGrammarFamilyGlobalScope symbolTable = createGlobalScope(new MCPath(Paths.get("src/test/resources")));
    mc.createSymbolsFromAST(symbolTable, grammar.get());
    ASTCDCompilationUnit cdCompilationUnit = new MC2CDScopeTranslation().apply(grammar.get());
    return Optional.of(cdCompilationUnit);
  }

  public static Optional<ASTCDCompilationUnit> parseAndTransform(Path model) {
    Optional<ASTMCGrammar> grammar = MCGrammarParser.parse(model);
    if (!grammar.isPresent()) {
      return Optional.empty();
    }
    MontiCoreScript mc = new MontiCoreScript();
    IGrammarFamilyGlobalScope symbolTable = createGlobalScope(new MCPath(Paths.get("src/test/resources")));
    mc.createSymbolsFromAST(symbolTable, grammar.get());
    ASTCDCompilationUnit cdCompilationUnit = new MC2CDTransformation(
        new GlobalExtensionManagement()).apply(grammar.get());
    return Optional.of(cdCompilationUnit);
  }
  
  public static IGrammarFamilyGlobalScope createGlobalScope(MCPath symbolPath) {
    IGrammarFamilyGlobalScope scope = GrammarFamilyMill.globalScope();
    // reset global scope
    scope.clear();
    BasicSymbolsMill.initializePrimitives();

    // Set ModelPath
    scope.setSymbolPath(symbolPath);
    return scope;
  }

  public static Optional<ASTCDClass> getCDClass(ASTCDCompilationUnit cdCompilationUnit, String cdClassName) {
    return cdCompilationUnit.getCDDefinition().getCDClassesList().stream()
        .filter(cdClass -> cdClass.getName().equals(cdClassName))
        .findAny();
  }

  public static Optional<ASTCDInterface> getCDInterface(ASTCDCompilationUnit cdCompilationUnit, String cdInterfaceName) {
    return cdCompilationUnit.getCDDefinition().getCDInterfacesList().stream()
        .filter(cdClass -> cdClass.getName().equals(cdInterfaceName))
        .findAny();
  }

  public static boolean isListOfType(ASTMCType typeRef, String typeArg) {
    if (!TransformationHelper.typeToString(typeRef).equals("java.util.List")) {
      return false;
    }
    if (!(typeRef instanceof ASTMCGenericType)) {
      return false;
    }
    ASTMCGenericType type = (ASTMCGenericType) typeRef;
    if (type.getMCTypeArgumentList().size() != 1) {
      return false;
    }
    if (!type.getMCTypeArgumentList().get(0).getMCTypeOpt().get()
            .printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter()).equals(typeArg)) {
      return false;
    }
    return true;
  }

}
