package de.monticore.types;


import de.monticore.types.mcbasicgenericstypestest._parser.MCBasicGenericsTypesTestParser;
import de.monticore.types.mcbasictypes._ast.ASTReferenceType;
import de.monticore.types.mcbasictypes._ast.ASTType;
import de.monticore.types.mcgenerictypes._ast.ASTArrayType;
import de.monticore.types.mcgenerictypes._ast.ASTComplexReferenceType;
import de.monticore.types.mcgenerictypestest._parser.MCGenericTypesTestParser;
import de.se_rwth.commons.logging.Log;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class MCGenericsTypesTest {

  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
  }

  @Test
  public void testBasicGenericsTypes() throws IOException {
    Class foo = boolean.class;
    String[] types = new String[]{"Foo<String>.Bar<List<Integer>>","List<? extends Person>","List<P<String>>","Optional<String>","Set<String>","Map<String,String>","List<socnet.Person>"};

    for (String testType : types) {
      MCGenericTypesTestParser mcBasicTypesParser = new MCGenericTypesTestParser();
      // .parseType(primitive);

      Optional<ASTType> type = mcBasicTypesParser.parse_String(testType);

      assertNotNull(type);
      assertTrue(type.isPresent());
      assertTrue(type.get() instanceof ASTReferenceType);
      System.out.println(type.get().getClass());
      ASTReferenceType t = (ASTReferenceType) type.get();
    }
  }

  @Test
  public void testArrayTypes() throws IOException {
    Class foo = boolean.class;
    String[] types = new String[]{"String[][]"};

    for (String testType : types) {
      MCGenericTypesTestParser mcBasicTypesParser = new MCGenericTypesTestParser();
      // .parseType(primitive);

      Optional<ASTType> type = mcBasicTypesParser.parse_String(testType);

      assertNotNull(type);
      assertTrue(type.isPresent());
      assertTrue(type.get() instanceof ASTArrayType);
      System.out.println(type.get().getClass());
      ASTArrayType t = (ASTArrayType) type.get();
      assertEquals(2,t.getDimensions());
    }
  }

}
