/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.templateclassgenerator.it;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.BeforeClass;
import org.junit.Test;

import setup.GeneratorConfig;
import types.Attribute;
import types.Helper;
import _templates.templates.b.Constructor;
import _templates.templates.b.JavaClass;
import _templates.templates.b.Template;
import de.monticore.ast.ASTNode;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.ExtendedGeneratorEngine;
import de.monticore.java.javadsl._ast.ASTConstructorDeclaration;
import de.monticore.java.javadsl._parser.JavaDSLParser;
import de.monticore.java.symboltable.JavaTypeSymbol;
import de.monticore.symboltable.Scope;
import de.monticore.templateclassgenerator.EmptyNode;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class UsageTest extends AbstractSymtabTest {
  private static Path outputDirectory = Paths.get("target/generated-sources/templateClasses/");
  
  private static Scope symTab = null;
  
  @BeforeClass
  public static void setup() {
    symTab = createJavaSymTab(outputDirectory);
  }
  
  @Test
  public void testJavaClassTemplateClass() {
    final GeneratorSetup setup = new GeneratorSetup(outputDirectory.toFile());
//    GlobalExtensionManagement g = new GlobalExtensionManagement();
//    g.defineGlobalValue("a.b.TemplateTemplate", new TemplateTemplate());
//    g.defineGlobalValue("bubu", "einString");
//    setup.setGlex(g);
    GeneratorConfig.init(setup);
    String classname = "Test1";
    List<Attribute> attributes = new ArrayList<>();
    attributes.add(new Attribute("Integer", "i"));
    attributes.add(new Attribute("String", "s"));
    Path filePath = Paths.get("test/" + classname + ".java");
    JavaClass.generate(filePath, new EmptyNode(), "test", classname,
        attributes);
    JavaTypeSymbol testClass = symTab.<JavaTypeSymbol> resolve("test.Test1", JavaTypeSymbol.KIND)
        .orElse(null);
    assertNotNull(testClass);
    ASTNode node = new EmptyNode();
  }
  
  @Test
  public void testReturnMethod() throws RecognitionException, IOException {
    final GeneratorSetup setup = new GeneratorSetup(outputDirectory.toFile());
    ExtendedGeneratorEngine generator = new ExtendedGeneratorEngine(setup);
    List<Attribute> attributes = new ArrayList<>();
    attributes.add(new Attribute("Integer", "i"));
    attributes.add(new Attribute("String", "s"));
    Function<String, ASTConstructorDeclaration> function = (String s) -> parseToASTConstructorDecl(s);
    ASTConstructorDeclaration meth = Constructor.generate("Test2", attributes, new Helper(), function);
    
    
    assertNotNull(meth);
  }
  
  @Test
  public void testToStringMethod() {
    final GeneratorSetup setup = new GeneratorSetup(outputDirectory.toFile());
    ExtendedGeneratorEngine generator = new ExtendedGeneratorEngine(setup);
    String classname = "Test1";
    List<Attribute> attributes = new ArrayList<>();
    attributes.add(new Attribute("Integer", "i"));
    attributes.add(new Attribute("String", "s"));
    String s = JavaClass.generate("test", classname, attributes);
    assertNotNull(s);
  }
  
  
  @Test
  public void testGenerateToStringInTemplate(){
    String s = Template.generate();
    assertNotNull(s);
  }
  
  private ASTConstructorDeclaration parseToASTConstructorDecl(String s) {
    JavaDSLParser parser = new JavaDSLParser();
    try {
      return parser.parseString_ConstructorDeclaration(s).get();
    }
    catch (RecognitionException | IOException e) {
      e.printStackTrace();
    }
    return null;
    
  }
}
