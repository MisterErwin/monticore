package de.monticore.codegen.cd2java._symboltable.symboltablecreator;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.cd4code._ast.CD4CodeMill;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.StringTransformations;

import java.util.*;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.CoreTemplates.VALUE;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.*;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.*;
import static de.monticore.codegen.cd2java.factories.CDModifier.*;

public class SymbolTableCreatorDecorator extends AbstractCreator<ASTCDCompilationUnit, Optional<ASTCDClass>> {

  protected final SymbolTableService symbolTableService;

  protected final VisitorService visitorService;

  protected final MethodDecorator methodDecorator;

  protected static final String TEMPLATE_PATH = "_symboltable.symboltablecreator.";

  public SymbolTableCreatorDecorator(final GlobalExtensionManagement glex,
                                     final SymbolTableService symbolTableService,
                                     final VisitorService visitorService,
                                     final MethodDecorator methodDecorator) {
    super(glex);
    this.visitorService = visitorService;
    this.symbolTableService = symbolTableService;
    this.methodDecorator = methodDecorator;
  }

  @Override
  public Optional<ASTCDClass> decorate(ASTCDCompilationUnit input) {
    Optional<String> startProd = symbolTableService.getStartProdASTFullName(input.getCDDefinition());
    if (startProd.isPresent()) {
      String astFullName = startProd.get();
      String symbolTableCreator = symbolTableService.getSymbolTableCreatorSimpleName();
      String visitorName = visitorService.getVisitorFullName();
      String scopeInterface = symbolTableService.getScopeInterfaceFullName();
      String dequeType = String.format(DEQUE_TYPE, scopeInterface);
      String dequeWildcardType = String.format(DEQUE_WILDCARD_TYPE, scopeInterface);

      String simpleName = symbolTableService.removeASTPrefix(Names.getSimpleName(startProd.get()));
      List<ASTCDType> symbolDefiningClasses = symbolTableService.getSymbolDefiningClasses(input.getCDDefinition().getCDClassList());
      Map<ASTCDClass, String> inheritedSymbolPropertyClasses = symbolTableService.getInheritedSymbolPropertyClasses(input.getCDDefinition().getCDClassList());
      List<ASTCDType> noSymbolDefiningClasses = symbolTableService.getNoSymbolAndScopeDefiningClasses(input.getCDDefinition().getCDClassList());
      List<ASTCDType> symbolDefiningProds = symbolTableService.getSymbolDefiningProds(input.getCDDefinition());
      List<ASTCDType> onlyScopeProds = symbolTableService.getOnlyScopeClasses(input.getCDDefinition());

      ASTCDAttribute realThisAttribute = createRealThisAttribute(visitorName);
      List<ASTCDMethod> realThisMethods = methodDecorator.decorate(realThisAttribute);

      ASTCDAttribute firstCreatedScopeAttribute = createFirstCreatedScopeAttribute(scopeInterface);
      List<ASTCDMethod> firstCreatedScopeMethod = methodDecorator.getAccessorDecorator().decorate(firstCreatedScopeAttribute);

      ASTCDClass symTabCreator = CD4CodeMill.cDClassBuilder()
          .setName(symbolTableCreator)
          .setModifier(PUBLIC.build())
          .addInterface(getCDTypeFacade().createQualifiedType(visitorName))
          .addCDConstructor(createSimpleConstructor(symbolTableCreator, scopeInterface))
          .addCDConstructor(createDequeConstructor(symbolTableCreator, dequeWildcardType, dequeType))
          .addCDAttribute(createScopeStackAttribute(dequeType))
          .addCDAttribute(realThisAttribute)
          .addAllCDMethods(realThisMethods)
          .addCDAttribute(firstCreatedScopeAttribute)
          .addAllCDMethods(firstCreatedScopeMethod)
          .addCDMethod(createCreateFromASTMethod(astFullName, symbolTableCreator))
          .addCDMethod(createPutOnStackMethod(scopeInterface))
          .addAllCDMethods(createCurrentScopeMethods(scopeInterface))
          .addCDMethod(createSetScopeStackMethod(dequeType, simpleName))
          .addCDMethod(createCreateScopeMethod(scopeInterface, input.getCDDefinition().getName()))
          .addAllCDMethods(createSymbolClassMethods(symbolDefiningClasses, scopeInterface))
          .addAllCDMethods(createSymbolClassMethods(inheritedSymbolPropertyClasses, scopeInterface))
          .addAllCDMethods(createVisitForNoSymbolMethods(noSymbolDefiningClasses))
          .addAllCDMethods(createAddToScopeMethods(symbolDefiningProds))
          .addAllCDMethods(createScopeClassMethods(onlyScopeProds, scopeInterface))
          .build();
      return Optional.ofNullable(symTabCreator);
    }
    return Optional.empty();
  }

  protected ASTCDConstructor createSimpleConstructor(String symTabCreator, String scopeInterface) {
    ASTCDParameter enclosingScope = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(scopeInterface), ENCLOSING_SCOPE_VAR);
    ASTCDConstructor constructor = getCDConstructorFacade().createConstructor(PUBLIC.build(), symTabCreator, enclosingScope);
    this.replaceTemplate(EMPTY_BODY, constructor, new StringHookPoint("putOnStack(Log.errorIfNull(" + ENCLOSING_SCOPE_VAR + "));"));
    return constructor;
  }

  protected ASTCDConstructor createDequeConstructor(String symTabCreator, String dequeWildcardType, String dequeType) {
    ASTCDParameter enclosingScope = getCDParameterFacade().createParameter(getCDTypeFacade().createTypeByDefinition(dequeWildcardType), SCOPE_STACK_VAR);
    ASTCDConstructor constructor = getCDConstructorFacade().createConstructor(PUBLIC.build(), symTabCreator, enclosingScope);
    this.replaceTemplate(EMPTY_BODY, constructor, new
        StringHookPoint("this." + SCOPE_STACK_VAR + " = Log.errorIfNull((" + dequeType + ")" + SCOPE_STACK_VAR + ");"));
    return constructor;
  }

  protected ASTCDAttribute createScopeStackAttribute(String dequeType) {
    ASTCDAttribute scopeStack = getCDAttributeFacade().createAttribute(PROTECTED, dequeType, SCOPE_STACK_VAR);
    this.replaceTemplate(VALUE, scopeStack, new StringHookPoint("= new java.util.ArrayDeque<>()"));
    return scopeStack;
  }

  protected ASTCDAttribute createFirstCreatedScopeAttribute(String scopeInterface) {
    return getCDAttributeFacade().createAttribute(PROTECTED, scopeInterface, "firstCreatedScope");
  }

  protected ASTCDAttribute createRealThisAttribute(String visitor) {
    ASTCDAttribute scopeStack = getCDAttributeFacade().createAttribute(PRIVATE, visitor, REAL_THIS);
    this.replaceTemplate(VALUE, scopeStack, new StringHookPoint("= this"));
    return scopeStack;
  }

  protected ASTCDMethod createCreateFromASTMethod(String astStartProd, String symbolTableCreator) {
    String artifactScopeFullName = symbolTableService.getArtifactScopeFullName();
    ASTCDParameter rootNodeParam = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(astStartProd), "rootNode");
    ASTCDMethod createFromAST = getCDMethodFacade().createMethod(PUBLIC,
        getCDTypeFacade().createQualifiedType(artifactScopeFullName), "createFromAST", rootNodeParam);
    this.replaceTemplate(EMPTY_BODY, createFromAST, new TemplateHookPoint(
        TEMPLATE_PATH + "CreateFromAST", artifactScopeFullName, symbolTableCreator));
    return createFromAST;
  }

  protected ASTCDMethod createPutOnStackMethod(String scopeInterface) {
    ASTCDParameter scopeParam = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(scopeInterface), SCOPE_VAR);
    ASTCDMethod createFromAST = getCDMethodFacade().createMethod(PUBLIC, "putOnStack", scopeParam);
    this.replaceTemplate(EMPTY_BODY, createFromAST, new TemplateHookPoint(
        TEMPLATE_PATH + "PutOnStack"));
    return createFromAST;
  }

  protected List<ASTCDMethod> createCurrentScopeMethods(String scopeInterface) {
    ASTCDMethod getCurrentScope = getCDMethodFacade().createMethod(PUBLIC_FINAL,
        getCDTypeFacade().createOptionalTypeOf(scopeInterface), "getCurrentScope");
    this.replaceTemplate(EMPTY_BODY, getCurrentScope, new StringHookPoint(
        "return Optional.ofNullable(" + SCOPE_STACK_VAR + ".peekLast());"));

    ASTCDMethod removeCurrentScope = getCDMethodFacade().createMethod(PUBLIC_FINAL,
        getCDTypeFacade().createOptionalTypeOf(scopeInterface), "removeCurrentScope");
    this.replaceTemplate(EMPTY_BODY, removeCurrentScope, new StringHookPoint(
        "return Optional.ofNullable(" + SCOPE_STACK_VAR + ".pollLast());"));
    return new ArrayList<>(Arrays.asList(getCurrentScope, removeCurrentScope));
  }

  protected ASTCDMethod createSetScopeStackMethod(String dequeType, String simpleName) {
    ASTCDParameter dequeParam = getCDParameterFacade().createParameter(getCDTypeFacade().createTypeByDefinition(dequeType), SCOPE_STACK_VAR);
    ASTCDMethod createFromAST = getCDMethodFacade().createMethod(PROTECTED,
        "set" + StringTransformations.capitalize(simpleName) + "ScopeStack", dequeParam);
    this.replaceTemplate(EMPTY_BODY, createFromAST, new StringHookPoint(
        "this." + SCOPE_STACK_VAR + " = " + SCOPE_STACK_VAR + ";"));
    return createFromAST;
  }

  protected ASTCDMethod createCreateScopeMethod(String scopeInterfaceName, String definitionName) {
    String symTabMill = symbolTableService.getSymTabMillFullName();
    ASTCDParameter boolParam = getCDParameterFacade().createParameter(getCDTypeFacade().createBooleanType(), SHADOWING_VAR);
    ASTCDMethod createFromAST = getCDMethodFacade().createMethod(PUBLIC, getCDTypeFacade().createQualifiedType(scopeInterfaceName),
        "createScope", boolParam);
    this.replaceTemplate(EMPTY_BODY, createFromAST, new TemplateHookPoint(
        TEMPLATE_PATH + "CreateScope", scopeInterfaceName, symTabMill, definitionName));
    return createFromAST;
  }

  protected List<ASTCDMethod> createSymbolClassMethods(List<ASTCDType> symbolClasses, String scopeInterface) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    for (ASTCDType symbolClass : symbolClasses) {
      methodList.addAll(createSymbolClassMethods(symbolClass, symbolTableService.getSymbolFullName(symbolClass), scopeInterface));
    }
    return methodList;
  }

  protected List<ASTCDMethod> createSymbolClassMethods(Map<ASTCDClass, String> symbolClassMap, String scopeInterface) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    for (ASTCDType symbolClass : symbolClassMap.keySet()) {
      methodList.addAll(createSymbolClassMethods(symbolClass, symbolClassMap.get(symbolClass), scopeInterface));
    }
    return methodList;
  }

  protected List<ASTCDMethod> createSymbolClassMethods(ASTCDType symbolClass, String symbolFullName, String scopeInterface) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    String astFullName = symbolTableService.getASTPackage() + "." + symbolClass.getName();
    String simpleName = symbolTableService.removeASTPrefix(symbolClass);

    // visit method
    methodList.add(createSymbolVisitMethod(astFullName, symbolFullName, simpleName));

    // endVisit method
    methodList.add(createSymbolEndVisitMethod(astFullName, symbolClass));

    ASTCDParameter astParam = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(astFullName), "ast");
    // create_$ symbol method
    methodList.add(createSymbolCreate_Method(symbolFullName, simpleName, astParam));

    ASTCDParameter symbolParam = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(symbolFullName), SYMBOL_VAR);
    // initialize_$ method
    methodList.add(createSymbolInitialize_Method(simpleName, astParam, symbolParam));
    if (symbolClass.getModifierOpt().isPresent()) {
      String simpleSymbolName = symbolTableService.removeASTPrefix(Names.getSimpleName(symbolFullName));
      boolean isScopeSpanningSymbol = symbolTableService.hasScopeStereotype(symbolClass.getModifierOpt().get()) ||
          symbolTableService.hasInheritedScopeStereotype(symbolClass.getModifierOpt().get());
      // addToScopeAndLinkWithNode method
      methodList.add(createSymbolAddToScopeAndLinkWithNodeMethod(scopeInterface, astParam, symbolParam, isScopeSpanningSymbol));

      // setLinkBetweenSymbolAndNode method
      methodList.add(createSymbolSetLinkBetweenSymbolAndNodeMethod(simpleSymbolName, astParam, symbolParam, isScopeSpanningSymbol));
      if (isScopeSpanningSymbol) {
        // setLinkBetweenSpannedScopeAndNode method
        String scopeClassFullName = symbolTableService.getScopeClassFullName();
        methodList.add(createSymbolSetLinkBetweenSpannedScopeAndNodeMethod(scopeInterface, scopeClassFullName, astParam));
      }
    }
    return methodList;
  }


  protected ASTCDMethod createSymbolVisitMethod(String astFullName, String symbolFullName, String simpleName) {
    ASTCDMethod visitMethod = visitorService.getVisitorMethod(VISIT, getCDTypeFacade().createQualifiedType(astFullName));
    this.replaceTemplate(EMPTY_BODY, visitMethod, new TemplateHookPoint(
        TEMPLATE_PATH + "Visit", symbolFullName, simpleName));
    return visitMethod;
  }

  protected ASTCDMethod createSymbolEndVisitMethod(String astFullName, ASTCDType symbolClass) {
    ASTCDMethod endVisitMethod = visitorService.getVisitorMethod(END_VISIT, getCDTypeFacade().createQualifiedType(astFullName));
    if (symbolClass.getModifierOpt().isPresent() && (symbolTableService.hasScopeStereotype(symbolClass.getModifierOpt().get())
        || symbolTableService.hasInheritedScopeStereotype(symbolClass.getModifierOpt().get()))) {
      this.replaceTemplate(EMPTY_BODY, endVisitMethod, new StringHookPoint("removeCurrentScope();"));
    }
    return endVisitMethod;
  }

  protected ASTCDMethod createSymbolCreate_Method(String symbolFullName, String simpleName, ASTCDParameter astParam) {
    ASTCDMethod createSymbolMethod = getCDMethodFacade().createMethod(PROTECTED, getCDTypeFacade().createQualifiedType(symbolFullName),
        "create_" + simpleName, astParam);
    this.replaceTemplate(EMPTY_BODY, createSymbolMethod, new StringHookPoint("return new " + symbolFullName + "(ast.getName());"));
    return createSymbolMethod;
  }

  protected ASTCDMethod createSymbolSetLinkBetweenSpannedScopeAndNodeMethod(String scopeInterface, String scopeClassFullName, ASTCDParameter astParam) {
    ASTCDParameter scopeParam = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(scopeInterface), SCOPE_VAR);
    // setLinkBetweenSpannedScopeAndNode method
    ASTCDMethod setLinkBetweenSpannedScopeAndNode = getCDMethodFacade().createMethod(PUBLIC, "setLinkBetweenSpannedScopeAndNode", scopeParam, astParam);
    this.replaceTemplate(EMPTY_BODY, setLinkBetweenSpannedScopeAndNode, new TemplateHookPoint(
        TEMPLATE_PATH + "SetLinkBetweenSpannedScopeAndNode", scopeClassFullName));
    return setLinkBetweenSpannedScopeAndNode;
  }

  protected ASTCDMethod createSymbolAddToScopeAndLinkWithNodeMethod(String scopeInterface, ASTCDParameter astParam, ASTCDParameter symbolParam, boolean isScopeSpanningSymbol) {
    ASTCDMethod addToScopeAnLinkWithNode = getCDMethodFacade().createMethod(PUBLIC, "addToScopeAndLinkWithNode", symbolParam, astParam);
    this.replaceTemplate(EMPTY_BODY, addToScopeAnLinkWithNode, new TemplateHookPoint(
        TEMPLATE_PATH + "AddToScopeAndLinkWithNode", scopeInterface, isScopeSpanningSymbol));
    return addToScopeAnLinkWithNode;
  }

  protected ASTCDMethod createSymbolSetLinkBetweenSymbolAndNodeMethod(String simpleSymbolName, ASTCDParameter astParam, ASTCDParameter symbolParam, boolean isScopeSpanningSymbol) {
    ASTCDMethod setLinkBetweenSymbolAndNode = getCDMethodFacade().createMethod(PUBLIC, "setLinkBetweenSymbolAndNode", symbolParam, astParam);
    this.replaceTemplate(EMPTY_BODY, setLinkBetweenSymbolAndNode, new TemplateHookPoint(
        TEMPLATE_PATH + "SetLinkBetweenSymbolAndNode", simpleSymbolName, isScopeSpanningSymbol));
    return setLinkBetweenSymbolAndNode;
  }

  protected ASTCDMethod createSymbolInitialize_Method(String simpleName, ASTCDParameter astParam, ASTCDParameter symbolParam) {
    return getCDMethodFacade().createMethod(PROTECTED, "initialize_" + simpleName, symbolParam, astParam);
  }

  protected List<ASTCDMethod> createScopeClassMethods(List<ASTCDType> scopeClasses, String scopeInterface) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    for (ASTCDType scopeClass : scopeClasses) {
      String astFullName = symbolTableService.getASTPackage() + "." + scopeClass.getName();
      String simpleName = symbolTableService.removeASTPrefix(scopeClass);
      // visit method
      methodList.add(createScopeVisitMethod(astFullName, scopeInterface, simpleName));

      ASTCDParameter astParam = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(astFullName), "ast");
      // create_$ method
      methodList.add(createScopeCreate_Method(scopeInterface, simpleName, astParam));

      // initialize_$ method
      ASTCDParameter scopeParam = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(scopeInterface), SCOPE_VAR);
      methodList.add(createScopeInitialize_Method(simpleName, scopeClass, astParam, scopeParam));

      // setLinkBetweenSpannedScopeAndNode method
      methodList.add(createScopeSetLinkBetweenSpannedScopeAndNodeMethod(scopeInterface, astParam, scopeParam));
    }
    return methodList;
  }

  protected ASTCDMethod createScopeVisitMethod(String astFullName, String scopeInterface, String simpleName) {
    ASTCDMethod visitMethod = visitorService.getVisitorMethod(VISIT, getCDTypeFacade().createQualifiedType(astFullName));
    this.replaceTemplate(EMPTY_BODY, visitMethod, new TemplateHookPoint(
        TEMPLATE_PATH + "VisitScope", scopeInterface, simpleName));
    return visitMethod;
  }

  protected ASTCDMethod createScopeCreate_Method(String scopeInterface, String simpleName, ASTCDParameter astParam) {
    ASTCDMethod createSymbolMethod = getCDMethodFacade().createMethod(PROTECTED, getCDTypeFacade().createQualifiedType(scopeInterface),
        "create_" + simpleName, astParam);
    this.replaceTemplate(EMPTY_BODY, createSymbolMethod, new StringHookPoint("return createScope(false);"));
    return createSymbolMethod;
  }

  protected ASTCDMethod createScopeInitialize_Method(String simpleName, ASTCDType scopeClass,
                                                     ASTCDParameter astParam, ASTCDParameter scopeParam) {
    boolean hasNameAttribute = scopeClass.getCDAttributeList().stream().anyMatch(a -> a.getName().equals(NAME_VAR));
    ASTCDMethod initializeMethod = getCDMethodFacade().createMethod(PROTECTED, "initialize_" + simpleName, scopeParam, astParam);
    if (hasNameAttribute) {
      this.replaceTemplate(EMPTY_BODY, initializeMethod, new StringHookPoint(SCOPE_VAR + ".setName(ast.getName());"));
    }
    return initializeMethod;
  }

  protected ASTCDMethod createScopeSetLinkBetweenSpannedScopeAndNodeMethod(String scopeInterface, ASTCDParameter astParam,
                                                                           ASTCDParameter scopeParam) {
    ASTCDMethod setLinkBetweenSpannedScopeAndNode = getCDMethodFacade().createMethod(PUBLIC,
        "setLinkBetweenSpannedScopeAndNode", scopeParam, astParam);
    this.replaceTemplate(EMPTY_BODY, setLinkBetweenSpannedScopeAndNode, new TemplateHookPoint(
        TEMPLATE_PATH + "SetLinkBetweenSpannedScopeAndNodeScope", scopeInterface));
    return setLinkBetweenSpannedScopeAndNode;
  }

  protected List<ASTCDMethod> createVisitForNoSymbolMethods(List<ASTCDType> astcdClasses) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    for (ASTCDType astcdClass : astcdClasses) {
      String astFullName = symbolTableService.getASTPackage() + "." + astcdClass.getName();
      ASTCDMethod visitorMethod = visitorService.getVisitorMethod(VISIT, getCDTypeFacade().createQualifiedType(astFullName));
      this.replaceTemplate(EMPTY_BODY, visitorMethod, new TemplateHookPoint(TEMPLATE_PATH + "VisitNoSymbol"));
      methodList.add(visitorMethod);
    }
    return methodList;
  }

  protected List<ASTCDMethod> createAddToScopeMethods(List<ASTCDType> astcdClasses) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    for (ASTCDType astcdClass : astcdClasses) {
      String symbolFullName = symbolTableService.getSymbolFullName(astcdClass);
      ASTCDParameter symbolParam = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(symbolFullName), SYMBOL_VAR);
      ASTCDMethod addToScopeMethod = getCDMethodFacade().createMethod(PUBLIC, "addToScope", symbolParam);
      this.replaceTemplate(EMPTY_BODY, addToScopeMethod, new TemplateHookPoint(TEMPLATE_PATH + "AddToScope"));
      methodList.add(addToScopeMethod);
    }
    return methodList;
  }
}
