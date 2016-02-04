/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.emf;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Optional;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.emf.util.AST2ModelFiles;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar_withconcepts._ast.Grammar_WithConceptsPackage;
import de.monticore.grammar.grammar_withconcepts._parser.Grammar_WithConceptsParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.Slf4jLog;

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 *
 */
public class TestGrammarEcore {
  
  @BeforeClass
  public static void setup() {
    Slf4jLog.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testMCGrammar() {
     try {
       
       AST2ModelFiles res = AST2ModelFiles.get();
      res.serializeAST(Grammar_WithConceptsPackage.eINSTANCE);
      
      
      String path1 = "de/monticore/emf/Automaton.mc4";
      Optional<ASTMCGrammar> transB = new Grammar_WithConceptsParser().parse("src/test/resources/" + path1);
      assertTrue(transB.isPresent());
      AST2ModelFiles.get().serializeASTInstance(transB.get(), "Automaton");
    }
    catch (RecognitionException | IOException e) {
      fail(e.getMessage());
    }
  }
}
