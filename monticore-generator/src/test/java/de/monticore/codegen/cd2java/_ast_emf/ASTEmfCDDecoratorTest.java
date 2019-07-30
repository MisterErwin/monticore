package de.monticore.codegen.cd2java._ast_emf;

import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._ast.ast_class.ASTScopeDecorator;
import de.monticore.codegen.cd2java._ast.ast_class.ASTService;
import de.monticore.codegen.cd2java._ast.ast_class.ASTSymbolDecorator;
import de.monticore.codegen.cd2java._ast.ast_class.reference.ASTReferenceDecorator;
import de.monticore.codegen.cd2java._ast.ast_interface.ASTInterfaceDecorator;
import de.monticore.codegen.cd2java._ast.ast_interface.ASTLanguageInterfaceDecorator;
import de.monticore.codegen.cd2java._ast.ast_interface.FullASTInterfaceDecorator;
import de.monticore.codegen.cd2java._ast.builder.ASTBuilderDecorator;
import de.monticore.codegen.cd2java._ast.builder.BuilderDecorator;
import de.monticore.codegen.cd2java._ast.constants.ASTConstantsDecorator;
import de.monticore.codegen.cd2java._ast.factory.NodeFactoryService;
import de.monticore.codegen.cd2java._ast.mill.MillDecorator;
import de.monticore.codegen.cd2java._ast_emf.ast_class.ASTEmfDecorator;
import de.monticore.codegen.cd2java._ast_emf.ast_class.ASTFullEmfDecorator;
import de.monticore.codegen.cd2java._ast_emf.ast_class.DataEmfDecorator;
import de.monticore.codegen.cd2java._ast_emf.ast_class.emfMutatorMethodDecorator.EmfMutatorDecorator;
import de.monticore.codegen.cd2java._ast_emf.emf_package.PackageImplDecorator;
import de.monticore.codegen.cd2java._ast_emf.emf_package.PackageInterfaceDecorator;
import de.monticore.codegen.cd2java._ast_emf.enums.EmfEnumDecorator;
import de.monticore.codegen.cd2java._ast_emf.factory.EmfNodeFactoryDecorator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.data.DataDecoratorUtil;
import de.monticore.codegen.cd2java.data.InterfaceDecorator;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.codegen.cd2java.methods.AccessorDecorator;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.codegen.cd2java.methods.accessor.MandatoryAccessorDecorator;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.umlcd4a.CD4AnalysisLanguage;
import de.monticore.umlcd4a.CD4AnalysisModelLoader;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.symboltable.CD4AnalysisSymbolTableCreator;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ASTEmfCDDecoratorTest extends DecoratorTestCase {

  private GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private ASTCDCompilationUnit decoratedCompilationUnit;

  private ASTCDCompilationUnit originalCompilationUnit;

  @Before
  public void setup() {
    this.glex.setGlobalValue("astHelper", new DecorationHelper());
    this.decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "ast", "AST");
    this.originalCompilationUnit = decoratedCompilationUnit.deepClone();
    this.glex.setGlobalValue("service", new EmfService(decoratedCompilationUnit));

    ModelPath modelPath = new ModelPath(Paths.get("src/test/resources/de/monticore/codegen"));
    CD4AnalysisLanguage cd4aLanguage = new CD4AnalysisLanguage();
    ResolvingConfiguration resolvingConfiguration = new ResolvingConfiguration();
    resolvingConfiguration.addDefaultFilters(cd4aLanguage.getResolvingFilters());

    GlobalScope symbolTable = new GlobalScope(modelPath, cd4aLanguage, resolvingConfiguration);
    CD4AnalysisModelLoader cd4aModelLoader = new CD4AnalysisModelLoader(cd4aLanguage);

    CD4AnalysisSymbolTableCreator symbolTableCreator = cd4aModelLoader.getModelingLanguage().getSymbolTableCreator(resolvingConfiguration, symbolTable).get();


    ASTService astService = new ASTService(decoratedCompilationUnit);
    SymbolTableService symbolTableService = new SymbolTableService(decoratedCompilationUnit);
    VisitorService visitorService = new VisitorService(decoratedCompilationUnit);
    EmfService emfService = new EmfService(decoratedCompilationUnit);
    NodeFactoryService nodeFactoryService = new NodeFactoryService(decoratedCompilationUnit);
    MethodDecorator methodDecorator = new MethodDecorator(glex);
    EmfMutatorDecorator emfMutatorDecorator = new EmfMutatorDecorator(glex, astService);
    DataEmfDecorator dataEmfDecorator = new DataEmfDecorator(glex, methodDecorator, astService, new DataDecoratorUtil(), emfMutatorDecorator);
    ASTSymbolDecorator astSymbolDecorator = new ASTSymbolDecorator(glex, symbolTableService);
    ASTScopeDecorator astScopeDecorator = new ASTScopeDecorator(glex, symbolTableService);
    ASTEmfDecorator astEmfDecorator = new ASTEmfDecorator(glex, astService, visitorService, nodeFactoryService,
        astSymbolDecorator, astScopeDecorator, methodDecorator, symbolTableService, emfService);
    ASTReferenceDecorator astReferencedSymbolDecorator = new ASTReferenceDecorator(glex, symbolTableService);
    ASTFullEmfDecorator fullEmfDecorator = new ASTFullEmfDecorator(dataEmfDecorator, astEmfDecorator, astReferencedSymbolDecorator);

    ASTLanguageInterfaceDecorator astLanguageInterfaceDecorator = new ASTLanguageInterfaceDecorator(astService, visitorService);

    BuilderDecorator builderDecorator = new BuilderDecorator(glex, new AccessorDecorator(glex), astService);
    ASTBuilderDecorator astBuilderDecorator = new ASTBuilderDecorator(glex, builderDecorator);

    EmfNodeFactoryDecorator nodeFactoryDecorator = new EmfNodeFactoryDecorator(glex, nodeFactoryService);

    MillDecorator millDecorator = new MillDecorator(glex, astService);

    ASTConstantsDecorator astConstantsDecorator = new ASTConstantsDecorator(glex, astService);

    EmfEnumDecorator emfEnumDecorator = new EmfEnumDecorator(glex, new AccessorDecorator(glex), astService);

    ASTInterfaceDecorator astInterfaceDecorator = new ASTInterfaceDecorator(glex, astService, visitorService,
        astSymbolDecorator, astScopeDecorator, methodDecorator);
    InterfaceDecorator dataInterfaceDecorator = new InterfaceDecorator(glex, new DataDecoratorUtil(), methodDecorator, astService);
    FullASTInterfaceDecorator fullASTInterfaceDecorator = new FullASTInterfaceDecorator(dataInterfaceDecorator, astInterfaceDecorator);

    PackageImplDecorator packageImplDecorator = new PackageImplDecorator(glex, new MandatoryAccessorDecorator(glex), emfService);
    PackageInterfaceDecorator packageInterfaceDecorator = new PackageInterfaceDecorator(glex, emfService);

    ASTEmfCDDecorator astcdDecorator = new ASTEmfCDDecorator(glex, symbolTableCreator, fullEmfDecorator, astLanguageInterfaceDecorator, astBuilderDecorator, nodeFactoryDecorator,
        millDecorator, astConstantsDecorator, emfEnumDecorator, fullASTInterfaceDecorator, packageImplDecorator, packageInterfaceDecorator);
    this.decoratedCompilationUnit = astcdDecorator.decorate(decoratedCompilationUnit);
  }

  @Test
  public void testCompilationCopy() {
    assertNotEquals(originalCompilationUnit, decoratedCompilationUnit);
  }

  @Test
  public void testPackageChanged() {
    String packageName = decoratedCompilationUnit.getPackageList().stream().reduce((a, b) -> a + "." + b).get();
    assertEquals("de.monticore.codegen.ast.ast._ast", packageName);
  }


  @Test
  public void testPackage() {
    List<String> expectedPackage = Arrays.asList("de", "monticore", "codegen", "ast", "ast", "_ast");
    assertEquals(expectedPackage, decoratedCompilationUnit.getPackageList());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    for (ASTCDClass clazz : decoratedCompilationUnit.getCDDefinition().getCDClassList()) {
      System.out.printf("==================== %s ====================\n", clazz.getName());
      StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, clazz, clazz);
      System.out.println(sb.toString());
    }
  }
}