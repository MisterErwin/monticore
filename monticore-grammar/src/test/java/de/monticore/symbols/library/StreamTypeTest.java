// (c) https://github.com/MontiCore/monticore
package de.monticore.symbols.library;

import de.monticore.io.FileReaderWriter;
import de.monticore.io.paths.MCPath;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.*;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfGenerics;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class StreamTypeTest {

  @Before
  public void init() throws URISyntaxException {
    LogStub.init();
    Log.enableFailQuick(false);

    OOSymbolsMill.reset();
    OOSymbolsMill.init();
    BasicSymbolsMill.initializePrimitives();
    // workaround to get library path working in emf
    Path symbolsPath = new File(FileReaderWriter.getResource(getClass().getClassLoader(), "de/monticore/symbols/library/Stream.sym").get().toURI()).toPath().getParent();
    OOSymbolsMill.globalScope().setSymbolPath(new MCPath(symbolsPath));
  }

  @Test
  public void resolveStreamType() {
    Optional<TypeSymbol> streamOpt = OOSymbolsMill.globalScope()
        .resolveType("Stream");
    assertTrue(streamOpt.isPresent());
    TypeSymbol stream = streamOpt.get();
    assertNotNull(stream.getSpannedScope());
    assertEquals(1, stream.getSpannedScope().getTypeVarSymbols().size());
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void resolveStaticEmpty() {
    MethodSymbol method = resolveMethod("Stream.emptyStream");
    assertTrue(method.getParameterList().isEmpty());
    assertIsStreamWithTypeVar(method.getType());
    assertTrue(method.isIsStatic());
    List<TypeVarSymbol> typeVars = method.getSpannedScope().getLocalTypeVarSymbols();
    assertEquals(1, typeVars.size());
    assertNotEquals(typeVars.get(0).getName(), "T");
  }

  @Test
  public void resolveStreamMethodAppendFirst() {
    MethodSymbol method = resolveMethod("Stream.appendFirst");
    assertTrue(method.isIsStatic());
    assertEquals(2, method.getParameterList().size());
    assertTrue(method.getParameterList().get(0).getType().isTypeVariable());
    assertIsStreamWithTypeVar(method.getParameterList().get(1).getType());
    assertIsStreamWithTypeVar(method.getType());
  }

  @Test
  public void resolveStreamMethodLen() {
    MethodSymbol method = resolveMethod("Stream.len");
    assertEquals(0, method.getParameterList().size());
    assertTrue(method.getEnclosingScope().getSpanningSymbol() instanceof TypeSymbol);
    assertEquals("Stream", method.getEnclosingScope().getSpanningSymbol().getName());
    assertEquals(BasicSymbolsMill.LONG, method.getType().getTypeInfo().getName());
  }

  @Test
  public void resolveStreamFunctions() {
    resolveCommonMemberMethods("Stream");
    resolveCommonMemberMethods("EventStream");
    resolveCommonMemberMethods("SyncStream");
    resolveCommonMemberMethods("ToptStream");
  }

  protected void resolveStaticMethods(String streamType) {
    resolveMethod(streamType + ".emptyStream");
    resolveMethod(streamType + ".appendFirst");
    resolveMethod(streamType + ".conc");
    resolveMethod(streamType + ".repeat");
    resolveMethod(streamType + ".iterate");
  }

  protected void resolveCommonMemberMethods(String streamType) {
    resolveMethod(streamType + ".len");
    resolveMethod(streamType + ".first");
    resolveMethod(streamType + ".dropFirst");
    resolveMethod(streamType + ".nth");
    resolveMethod(streamType + ".take");
    resolveMethod(streamType + ".drop");
    resolveMethod(streamType + ".times");
    resolveMethod(streamType + ".map");
    resolveMethod(streamType + ".filter");
    resolveMethod(streamType + ".takeWhile");
    resolveMethod(streamType + ".dropWhile");
    resolveMethod(streamType + ".rcDups");
  }

  @Test
  public void resolveTimedStreamMethods() {
    resolveMethod("EventStream.delay");
    resolveMethod("EventStream.mapSlice");
    resolveMethod("EventStream.rougherTime");
    resolveMethod("EventStream.finerTime");
    resolveMethod("EventStream.sync");
    resolveMethod("EventStream.topt");
    resolveMethod("EventStream.untimed");
    resolveMethod("ToptStream.sizeEps");
    resolveMethod("SyncStream.event");
  }

  @Test
  public void isTypeVarResolvableFromReturn() {

  }

  protected void assertIsStreamWithTypeVar(SymTypeExpression type) {
    assertNotNull(type);
    assertTrue(type.isGenericType());
    assertEquals("Stream", type.getTypeInfo().getName());
    assertEquals(1, ((SymTypeOfGenerics) type).getArgumentList().size());
    assertTrue(((SymTypeOfGenerics) type).getArgument(0).isTypeVariable());
  }

  protected MethodSymbol resolveMethod(String name) {
    Optional<MethodSymbol> emptyOpt = OOSymbolsMill.globalScope().resolveMethod(name);
    assertTrue(emptyOpt.isPresent());
    assertTrue(emptyOpt.get().isIsMethod());
    return emptyOpt.get();
  }
}
