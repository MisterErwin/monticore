/* (c) https://github.com/MontiCore/monticore */

package de.monticore.prettyprint;

import de.monticore.cardinality._ast.ASTCardinality;
import de.monticore.testcardinality._parser.TestCardinalityParser;
import de.monticore.cardinality._prettyprint.CardinalityFullPrettyPrinter;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CardinalityPrettyPrinterTest {
  
  @Before
  public void init() {
    // replace log by a sideffect free variant
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Before
  public void setUp() {
    Log.getFindings().clear();
  }
  
  @Test
  public void testCardinality1() throws IOException {
    TestCardinalityParser parser = new TestCardinalityParser();
    Optional<ASTCardinality> result = parser.parseCardinality(new StringReader("[*]"));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTCardinality cardinality = result.get();
    
    CardinalityFullPrettyPrinter prettyPrinter = new CardinalityFullPrettyPrinter(new IndentPrinter());
    String output = prettyPrinter.prettyprint(cardinality);
    
    result = parser.parseCardinality(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(cardinality.deepEquals(result.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testCardinality2() throws IOException {
    TestCardinalityParser parser = new TestCardinalityParser();
    Optional<ASTCardinality> result = parser.parseCardinality(new StringReader("[5..9]"));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTCardinality cardinality = result.get();
    
    CardinalityFullPrettyPrinter prettyPrinter = new CardinalityFullPrettyPrinter(new IndentPrinter());
    String output = prettyPrinter.prettyprint(cardinality);
    
    result = parser.parseCardinality(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(cardinality.deepEquals(result.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testCardinality3() throws IOException {
    TestCardinalityParser parser = new TestCardinalityParser();
    Optional<ASTCardinality> result = parser.parseCardinality(new StringReader("[6..*]"));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTCardinality cardinality = result.get();
    
    CardinalityFullPrettyPrinter prettyPrinter = new CardinalityFullPrettyPrinter(new IndentPrinter());
    String output = prettyPrinter.prettyprint(cardinality);
    
    result = parser.parseCardinality(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(cardinality.deepEquals(result.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
