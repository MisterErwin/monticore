/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.serialization;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.cd.cd4code.CD4CodeMill;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.StringTransformations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static de.monticore.cd.facade.CDModifier.*;
import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.JSON_PRINTER;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.END_VISIT;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.VISIT;

/**
 * creates a Symbols2Json class from a grammar
 */
public class Symbols2JsonDecorator extends AbstractDecorator {

  protected final SymbolTableService symbolTableService;

  protected final VisitorService visitorService;

  protected final MethodDecorator methodDecorator;

  protected final AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> accessorDecorator;

  protected final AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> mutatorDecorator;

  protected static final String TEMPLATE_PATH = "_symboltable.serialization.";

  protected static final String PRINTER_END_OBJECT = "printer.endObject();";

  protected static final String PRINTER_END_ARRAY = "printer.endArray();";

  public Symbols2JsonDecorator(final GlobalExtensionManagement glex,
                               final SymbolTableService symbolTableService,
                               final VisitorService visitorService,
                               final MethodDecorator methodDecorator) {
    super(glex);
    this.symbolTableService = symbolTableService;
    this.visitorService = visitorService;
    this.methodDecorator = methodDecorator;
    this.accessorDecorator = methodDecorator.getAccessorDecorator();
    this.mutatorDecorator = methodDecorator.getMutatorDecorator();

  }

  public ASTCDClass decorate(ASTCDCompilationUnit scopeCD, ASTCDCompilationUnit symbolCD) {
    String symbols2JsonName = symbolTableService.getSymbols2JsonSimpleName();
    String scopeInterfaceFullName = symbolTableService.getScopeInterfaceFullName();
    String artifactScopeInterfaceFullName = symbolTableService.getArtifactScopeInterfaceFullName();
    String scopeClassFullName = symbolTableService.getScopeClassFullName();
    String deSerFullName = symbolTableService.getScopeDeSerFullName();
    List<ASTCDType> symbolDefiningProds = symbolTableService.getSymbolDefiningProds(symbolCD.getCDDefinition());
    String visitorFullName = visitorService.getVisitor2FullName();
    String traverserFullName = visitorService.getTraverserInterfaceFullName();
    String millName = visitorService.getMillFullName();
    List<CDDefinitionSymbol> superGrammars = symbolTableService.getSuperCDsTransitive();

    List<ASTCDClass> symbolTypes = symbolCD.getCDDefinition().getCDClassList();
    List<ASTCDClass> scopeTypes = scopeCD.getCDDefinition().getCDClassList();

    ASTCDAttribute traverserAttribute = createTraverserAttribute(traverserFullName);

    ASTCDClass symbols2JsonClass = CD4CodeMill.cDClassBuilder()
            .setName(symbols2JsonName)
            .setModifier(PUBLIC.build())
            .addInterface(getMCTypeFacade().createQualifiedType(visitorFullName))
            .addAllCDAttributes(createDeSerAttrs(symbolDefiningProds))
            .addCDAttribute(getCDAttributeFacade().createAttribute(PROTECTED, JSON_PRINTER, "printer"))
            .addCDMethod(createGetJsonPrinterMethod())
            .addCDMethod(createSetJsonPrinterMethod())
            .addCDAttribute(traverserAttribute)
            .addAllCDMethods(accessorDecorator.decorate(traverserAttribute))
            .addAllCDMethods(mutatorDecorator.decorate(traverserAttribute))
            .addAllCDConstructors(createConstructors(millName, traverserFullName, symbols2JsonName))
            .addCDMethod(createInitMethod(deSerFullName, scopeClassFullName, symbolDefiningProds, superGrammars))
            .addCDMethod(createGetSerializedStringMethod())
            .addAllCDMethods(createLoadMethods(artifactScopeInterfaceFullName, deSerFullName))
            .addCDMethod(createStoreMethod(artifactScopeInterfaceFullName))
            .addAllCDMethods(createScopeVisitorMethods(scopeInterfaceFullName))
            .addAllCDMethods(createSymbolVisitorMethods(symbolDefiningProds))
             .addAllCDMethods(createArtifactScopeVisitorMethods(artifactScopeInterfaceFullName))
            .build();
    return symbols2JsonClass;
  }

  protected ASTCDAttribute createTraverserAttribute(String traverserFullName) {
    return getCDAttributeFacade()
            .createAttribute(PRIVATE, traverserFullName, "traverser");
  }

  protected List<ASTCDConstructor> createConstructors(String millName, String traverserFullName, String symbolTablePrinterName) {
    List<ASTCDConstructor> constructors = new ArrayList<>();

    ASTCDConstructor constructor = getCDConstructorFacade().createConstructor(PUBLIC, symbolTablePrinterName);
    StringBuilder sb = new StringBuilder("this(" + millName + ".traverser(), new " + JSON_PRINTER + "());\n");
    this.replaceTemplate(EMPTY_BODY, constructor, new StringHookPoint(sb.toString()));
    constructors.add(constructor);

    List<ASTCDParameter> constructorParameters = new ArrayList<>();
    String traverserParam = "traverser";
    constructorParameters.add(getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(traverserFullName), traverserParam));
    String printerParam = "printer";
    constructorParameters.add(getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(JSON_PRINTER), printerParam));
    ASTCDConstructor constructorB = getCDConstructorFacade().createConstructor(PUBLIC, symbolTablePrinterName, constructorParameters);
    StringBuilder sb2 = new StringBuilder("this.printer = " + printerParam + ";\n");
    sb2.append("this.traverser = " + traverserParam + ";\n");
    sb2.append("init();\n");
    this.replaceTemplate(EMPTY_BODY, constructorB, new StringHookPoint(sb2.toString()));
    constructors.add(constructorB);
    return constructors;
  }

  protected ASTCDMethod createInitMethod(String deSerFullName, String scopeFullName, List<ASTCDType> prods, List<CDDefinitionSymbol> superGrammars) {
    ASTCDMethod initMethod = getCDMethodFacade().createMethod(PUBLIC, "init");
    String globalScope = symbolTableService.getGlobalScopeInterfaceFullName();
    String millName = symbolTableService.getMillFullName();

    Map<String, String> deSerMap = Maps.newHashMap();
    for (ASTCDType prod : prods) {
      deSerMap.put(symbolTableService.getSymbolDeSerSimpleName(prod), symbolTableService.getSymbolFullName(prod));
    }
    Map<String, String> printerMap = Maps.newHashMap();
    for (CDDefinitionSymbol cdSymbol: superGrammars) {
      printerMap.put(cdSymbol.getName(), symbolTableService.getSymbols2JsonFullName(cdSymbol));
    }
    this.replaceTemplate(EMPTY_BODY, initMethod,
            new TemplateHookPoint(TEMPLATE_PATH + "symbols2Json.Init",
                    globalScope, deSerFullName, scopeFullName, millName,
                    deSerMap, symbolTableService.getCDName(), printerMap));
    return initMethod;
  }

  protected List<ASTCDAttribute> createDeSerAttrs(List<ASTCDType> prods) {
    List<ASTCDAttribute> attrList = Lists.newArrayList();
    attrList.add(getCDAttributeFacade().createAttribute(PROTECTED, symbolTableService.getScopeDeSerFullName(), "scopeDeSer"));
    for (ASTCDType prod : prods) {
      String name = StringTransformations.uncapitalize(symbolTableService.getSymbolDeSerSimpleName(prod));
      attrList.add(getCDAttributeFacade().createAttribute(PROTECTED, symbolTableService.getSymbolDeSerFullName(prod), name));
    }
    return attrList;
  }

  protected ASTCDMethod createGetJsonPrinterMethod() {
    ASTMCType type = getMCTypeFacade().createQualifiedType(JSON_PRINTER);
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, type, "getJsonPrinter");
    this.replaceTemplate(EMPTY_BODY, method, new StringHookPoint("return this.printer;"));
    return method;
  }

  protected ASTCDMethod createSetJsonPrinterMethod() {
    ASTMCType type = getMCTypeFacade().createQualifiedType(JSON_PRINTER);
    ASTCDParameter parameter = getCDParameterFacade().createParameter(type, "printer");
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, "setJsonPrinter", parameter);
    this.replaceTemplate(EMPTY_BODY, method, new StringHookPoint("this.printer=printer;"));
    return method;
  }

  protected ASTCDMethod createGetSerializedStringMethod() {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, getMCTypeFacade().createStringType(), "getSerializedString");
    this.replaceTemplate(EMPTY_BODY, method, new StringHookPoint("return this.printer.getContent();"));
    return method;
  }

  protected List<ASTCDMethod> createScopeVisitorMethods(String scopeInterfaceName) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();

    ASTCDMethod visitMethod = visitorService.getVisitorMethod(VISIT, getMCTypeFacade().createQualifiedType(scopeInterfaceName));
    this.replaceTemplate(EMPTY_BODY, visitMethod, new TemplateHookPoint(TEMPLATE_PATH
            + "symbols2Json.VisitScope4STP"));
    visitorMethods.add(visitMethod);

    ASTCDMethod endVisitMethod = visitorService.getVisitorMethod(END_VISIT, getMCTypeFacade().createQualifiedType(scopeInterfaceName));
    this.replaceTemplate(EMPTY_BODY, endVisitMethod, new StringHookPoint(PRINTER_END_ARRAY + "\n" + PRINTER_END_OBJECT));
    visitorMethods.add(endVisitMethod);

    return visitorMethods;
  }

  protected List<ASTCDMethod> createArtifactScopeVisitorMethods(String artifactScopeInterfaceName) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    ASTCDMethod visitMethod = visitorService.getVisitorMethod(VISIT, getMCTypeFacade().createQualifiedType(artifactScopeInterfaceName));
    this.replaceTemplate(EMPTY_BODY, visitMethod, new TemplateHookPoint(TEMPLATE_PATH
            + "symbols2Json.VisitArtifactScope"));
    visitorMethods.add(visitMethod);

    ASTCDMethod endVisitMethod = visitorService
            .getVisitorMethod(END_VISIT, getMCTypeFacade().createQualifiedType(artifactScopeInterfaceName));
    this.replaceTemplate(EMPTY_BODY, endVisitMethod, new StringHookPoint(PRINTER_END_ARRAY + "\n" + PRINTER_END_OBJECT));
    visitorMethods.add(endVisitMethod);

    return visitorMethods;
  }

  protected List<ASTCDMethod> createSymbolVisitorMethods(List<ASTCDType> symbolProds) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();

    for (ASTCDType symbolProd : symbolProds) {
      String symbolFullName = symbolTableService.getSymbolFullName(symbolProd);
      String kind = symbolTableService.getSymbolFullName(symbolProd);
      ASTCDMethod visitMethod = visitorService.getVisitorMethod(VISIT, getMCTypeFacade().createQualifiedType(symbolFullName));
      this.replaceTemplate(EMPTY_BODY, visitMethod,
              new TemplateHookPoint(TEMPLATE_PATH + "symbols2Json.VisitSymbol", symbolProd.getName()));
      visitorMethods.add(visitMethod);
    }
    return visitorMethods;
  }

  protected ASTCDMethod createLoadMethod(ASTCDParameter parameter, String parameterInvocation,
                                         ASTMCQualifiedType returnType, String deSerFullName) {
    ASTCDMethod loadMethod = getCDMethodFacade()
            .createMethod(PUBLIC, returnType, "load", parameter);
    this.replaceTemplate(EMPTY_BODY, loadMethod,
            new TemplateHookPoint(TEMPLATE_PATH + "symbols2Json.Load",
                    parameterInvocation, deSerFullName));
    return loadMethod;
  }

  protected ASTCDMethod createStoreMethod(String artifactScopeName) {
    ASTCDParameter artifactScopeParam = getCDParameterFacade()
            .createParameter(getMCTypeFacade().createQualifiedType(artifactScopeName), "scope");
    ASTCDParameter fileNameParam = getCDParameterFacade()
            .createParameter(getMCTypeFacade().createStringType(), "fileName");
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, getMCTypeFacade().createStringType(), "store", artifactScopeParam, fileNameParam);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(TEMPLATE_PATH + "symbols2Json.Store"));
    return method;
  }

  protected List<ASTCDMethod> createLoadMethods(String artifactScopeName, String deSerFullName) {
    ASTMCQualifiedType returnType = getMCTypeFacade().createQualifiedType(artifactScopeName);

    ASTCDParameter urlParam = getCDParameterFacade()
            .createParameter(getMCTypeFacade().createQualifiedType("java.net.URL"), "url");
    ASTCDMethod loadURLMethod = createLoadMethod(urlParam, "url", returnType, deSerFullName);

    ASTCDParameter readerParam = getCDParameterFacade()
            .createParameter(getMCTypeFacade().createQualifiedType("java.io.Reader"), "reader");
    ASTCDMethod loadReaderMethod = createLoadMethod(readerParam, "reader", returnType, deSerFullName);

    ASTCDParameter stringParam = getCDParameterFacade()
            .createParameter(getMCTypeFacade().createStringType(), "model");
    ASTCDMethod loadStringMethod = createLoadMethod(stringParam, "java.nio.file.Paths.get(model)",
            returnType, deSerFullName);

    return Lists.newArrayList(loadURLMethod, loadReaderMethod, loadStringMethod);
  }

}
