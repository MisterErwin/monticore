/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static de.monticore.grammar.cocos.OverridingNTs.ERROR_CODE;
import static de.se_rwth.commons.logging.LogStub.enableFailQuick;
import static java.lang.String.format;
import de.se_rwth.commons.logging.Log;

public class OverridingNTsTest extends CocoTest {

  private final String MESSAGE = " The production for the nonterminal QualifiedName must not be overridden " +
          "by a production for an %s nonterminal.";
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4009.A4009";
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @BeforeClass
  public static void disableFailQuick() {
    checker.addCoCo(new OverridingNTs());
  }

  @Test
  public void testInvalidA() {
    testInvalidGrammar(grammar + "a", ERROR_CODE,
            format(MESSAGE, "interface"), checker);
  }

  @Test
  public void testInvalidB() {
    testInvalidGrammar(grammar + "b", ERROR_CODE, format(MESSAGE, "enum"),
            checker);
  }

  @Test
  public void testInvalidC() {
    testInvalidGrammar(grammar + "c", ERROR_CODE, format(MESSAGE, "lexical"),
            checker);
  }

  @Test
  public void testInvalidD() {
    testInvalidGrammar(grammar + "d", ERROR_CODE, format(MESSAGE, "external"),
            checker);
  }

  @Test
  public void testInvalidE() {
    testInvalidGrammar(grammar + "e", ERROR_CODE, format(MESSAGE, "abstract"),
            checker);
  }

  @Test
  public void testCorrect() {
    testValidGrammar("de.monticore.grammar.cocos.valid.Overriding", checker);
  }

}
