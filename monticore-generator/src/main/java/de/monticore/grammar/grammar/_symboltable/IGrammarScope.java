/* generated by template symboltable.ScopeInterface*/

package de.monticore.grammar.grammar._symboltable;

import com.google.common.collect.LinkedListMultimap;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.resolving.ResolvedSeveralEntriesForSymbolException;
import de.monticore.utils.Names;
import de.se_rwth.commons.logging.Log;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public interface IGrammarScope extends IGrammarScopeTOP  {

  // all resolve Methods for MCGrammarSymbol
  default public Optional<MCGrammarSymbol> resolveMCGrammar(String name) {
    return getResolvedOrThrowException(resolveMCGrammarMany(name));
  }

  default public Optional<MCGrammarSymbol> resolveMCGrammar(String name, AccessModifier modifier) {
    return getResolvedOrThrowException(resolveMCGrammarMany(name, modifier));
  }

  default public Optional<MCGrammarSymbol> resolveMCGrammar(String name, AccessModifier modifier, Predicate<MCGrammarSymbol> predicate){
    return getResolvedOrThrowException(resolveMCGrammarMany(name, modifier, predicate));
  }

  default public Optional<MCGrammarSymbol> resolveMCGrammar(boolean foundSymbols, String name, AccessModifier modifier) {
    return getResolvedOrThrowException(resolveMCGrammarMany(foundSymbols, name, modifier));
  }

  // all resolveDown Methods for MCGrammarSymbol
  default public Optional<MCGrammarSymbol> resolveMCGrammarDown(String name) {
    return getResolvedOrThrowException(this.resolveMCGrammarDownMany(name));
  }

  default public Optional<MCGrammarSymbol> resolveMCGrammarDown(String name, AccessModifier modifier) {
    return getResolvedOrThrowException(resolveMCGrammarDownMany(name, modifier));
  }

  default public Optional<MCGrammarSymbol> resolveMCGrammarDown(String name, AccessModifier modifier, Predicate<MCGrammarSymbol> predicate) {
    return getResolvedOrThrowException(resolveMCGrammarDownMany(name, modifier, predicate));
  }

  // all resolveDownMany Methods for MCGrammarSymbol
  default public Collection<MCGrammarSymbol> resolveMCGrammarDownMany(String name) {
    return this.resolveMCGrammarDownMany(false, name, AccessModifier.ALL_INCLUSION, x -> true);
  }

  default public Collection<MCGrammarSymbol> resolveMCGrammarDownMany(String name, AccessModifier modifier) {
    return resolveMCGrammarDownMany(false, name, modifier, x -> true);
  }

  default public Collection<MCGrammarSymbol> resolveMCGrammarDownMany(String name, AccessModifier modifier, Predicate<MCGrammarSymbol> predicate) {
    return resolveMCGrammarDownMany(false, name, modifier, predicate);
  }

  default public Collection<MCGrammarSymbol> resolveMCGrammarDownMany(boolean foundSymbols, String name, AccessModifier modifier, Predicate<MCGrammarSymbol> predicate) {
      // 1. Conduct search locally in the current scope
    final Set<MCGrammarSymbol> resolved = this.resolveMCGrammarLocallyMany(foundSymbols, name,
        modifier, predicate);

    foundSymbols = foundSymbols | resolved.size() > 0;

    final String resolveCall = "resolveDownMany(\"" + name + "\", \"" + "MCGrammarSymbol"
        + "\") in scope \"" + getName() + "\"";
    Log.trace("START " + resolveCall + ". Found #" + resolved.size() + " (local)", "");
    // If no matching symbols have been found...
    if (resolved.isEmpty()) {
      // 2. Continue search in sub scopes and ...
      for (IGrammarScope subScope : getSubScopes()) {
        final Collection<MCGrammarSymbol> resolvedFromSub = subScope
            .continueAsMCGrammarSubScope(foundSymbols, name, modifier, predicate);
        foundSymbols = foundSymbols | resolved.size() > 0;
        // 3. unify results
        resolved.addAll(resolvedFromSub);
      }
    }
    Log.trace("END " + resolveCall + ". Found #" + resolved.size(), "");

    return resolved;
  }

  // resolveLocally Method for MCGrammarSymbol
  default public Optional<MCGrammarSymbol> resolveMCGrammarLocally(String name) {
    return getResolvedOrThrowException(
        this.resolveMCGrammarLocallyMany(false, name,  AccessModifier.ALL_INCLUSION, x -> true));
  }

  // all resolveImported Methods for MCGrammarSymbol
  default public Optional<MCGrammarSymbol> resolveMCGrammarImported(String name, AccessModifier modifier) {
    return this.resolveMCGrammarLocally(name);
  }

  // all resolveMany Methods for MCGrammarSymbol
  default public Collection<MCGrammarSymbol> resolveMCGrammarMany(String name) {
    return resolveMCGrammarMany(name, AccessModifier.ALL_INCLUSION);
  }

  default public Collection<MCGrammarSymbol> resolveMCGrammarMany(String name, AccessModifier modifier) {
    return resolveMCGrammarMany(name, modifier, x -> true);
  }

  default public Collection<MCGrammarSymbol> resolveMCGrammarMany(String name, AccessModifier modifier, Predicate<MCGrammarSymbol> predicate)  {
    return resolveMCGrammarMany(false, name, modifier, predicate);
  }

  default public Collection<MCGrammarSymbol> resolveMCGrammarMany(String name, Predicate<MCGrammarSymbol> predicate)  {
    return resolveMCGrammarMany(false, name, AccessModifier.ALL_INCLUSION, predicate);
  }

  default public Collection<MCGrammarSymbol> resolveMCGrammarMany(boolean foundSymbols, String name, AccessModifier modifier) {
    return resolveMCGrammarMany(foundSymbols, name, modifier, x -> true);
  }

  default public Collection<MCGrammarSymbol> resolveMCGrammarMany(boolean foundSymbols, String name, AccessModifier modifier, Predicate<MCGrammarSymbol> predicate)  {
    final Set<MCGrammarSymbol> resolvedSymbols = this.resolveMCGrammarLocallyMany(foundSymbols, name, modifier, predicate);
    final Collection<MCGrammarSymbol> resolvedFromEnclosing = continueMCGrammarWithEnclosingScope((foundSymbols | resolvedSymbols.size() > 0), name, modifier, predicate);
    resolvedSymbols.addAll(resolvedFromEnclosing);
    return resolvedSymbols;
  }

  default Set<MCGrammarSymbol> resolveMCGrammarLocallyMany(boolean foundSymbols, String name, AccessModifier modifier,
                                                           Predicate<MCGrammarSymbol> predicate) {

    final Set<MCGrammarSymbol> resolvedSymbols = new LinkedHashSet<>();

    try {
      // TODO remove filter?
      Optional<MCGrammarSymbol> resolvedSymbol = filterMCGrammar(name,
          getMCGrammarSymbols());
      if (resolvedSymbol.isPresent()) {
        resolvedSymbols.add(resolvedSymbol.get());
      }
    }
    catch (ResolvedSeveralEntriesForSymbolException e) {
      resolvedSymbols.addAll(e.getSymbols());
    }

    // filter out symbols that are not included within the access modifier
    Set<MCGrammarSymbol> filteredSymbols = filterSymbolsByAccessModifier(modifier, resolvedSymbols);
    filteredSymbols = new LinkedHashSet<>(
        filteredSymbols.stream().filter(predicate).collect(Collectors.toSet()));

    return filteredSymbols;
  }

  /**
   * @deprecated use the method resolveMCGrammarLocallyMany instead
   *             this one will be deleted soon
   */

  @Deprecated
  default Set<MCGrammarSymbol> resolveMCGrammarManyLocally(boolean foundSymbols, String name, AccessModifier modifier,
                                                           Predicate<MCGrammarSymbol> predicate) {
    return resolveMCGrammarLocallyMany(foundSymbols, name, modifier, predicate);
  }

  default Optional<MCGrammarSymbol> filterMCGrammar(String name, LinkedListMultimap<String, MCGrammarSymbol> symbols) {
    final Set<MCGrammarSymbol> resolvedSymbols = new LinkedHashSet<>();

    final String simpleName = Names.getSimpleName(name);

    if (symbols.containsKey(simpleName)) {
      for (MCGrammarSymbol symbol : symbols.get(simpleName)) {
        if (symbol.getName().equals(name) || symbol.getFullName().equals(name)) {
          resolvedSymbols.add(symbol);
        }
      }
    }

    return getResolvedOrThrowException(resolvedSymbols);
  }


  default Collection<MCGrammarSymbol> continueMCGrammarWithEnclosingScope(boolean foundSymbols, String name, AccessModifier modifier,
                                                                          Predicate<MCGrammarSymbol> predicate) {
    if (checkIfContinueWithEnclosingScope(foundSymbols) && (getEnclosingScope().isPresent())) {
      return getEnclosingScope().get().resolveMCGrammarMany(foundSymbols, name, modifier, predicate);
    }
    return Collections.emptySet();
  }

  default Collection<MCGrammarSymbol> continueAsMCGrammarSubScope(boolean foundSymbols, String name, AccessModifier modifier, Predicate<MCGrammarSymbol> predicate){
    if (checkIfContinueAsSubScope(name)) {
      final String remainingSymbolName = getRemainingNameForResolveDown(name);
      return this.resolveMCGrammarDownMany(foundSymbols, remainingSymbolName, modifier, predicate);
    }
    return Collections.emptySet();
  }

  /**
   * Adds the symbol to this scope. Also, this scope is set as the symbol's enclosing scope.
   */
  void add(MCGrammarSymbol symbol);

  /**
   * removes the given symbol from this scope and unsets the enclosing scope relation.
   *
   * @param symbol the symbol to be removed
   */
  void remove(MCGrammarSymbol symbol);

  default public List<MCGrammarSymbol> getLocalMCGrammarSymbols() {
    return getMCGrammarSymbols().values();
  }

  LinkedListMultimap<String, MCGrammarSymbol> getMCGrammarSymbols();

  // all resolve Methods for MCProdSymbol
  default public Optional<MCProdSymbol> resolveProd(String name) {
    return getResolvedOrThrowException(resolveProdMany(name));
  }

  default public Optional<MCProdSymbol> resolveProd(String name, AccessModifier modifier) {
    return getResolvedOrThrowException(resolveProdMany(name, modifier));
  }

  default public Optional<MCProdSymbol> resolveProd(String name, AccessModifier modifier, Predicate<MCProdSymbol> predicate){
    return getResolvedOrThrowException(resolveProdMany(name, modifier, predicate));
  }

  default public Optional<MCProdSymbol> resolveProd(boolean foundSymbols, String name, AccessModifier modifier) {
    return getResolvedOrThrowException(resolveProdMany(foundSymbols, name, modifier));
  }

  // all resolveDown Methods for MCProdSymbol
  default public Optional<MCProdSymbol> resolveProdDown(String name) {
    return getResolvedOrThrowException(this.resolveProdDownMany(name));
  }

  default public Optional<MCProdSymbol> resolveProdDown(String name, AccessModifier modifier) {
    return getResolvedOrThrowException(resolveProdDownMany(name, modifier));
  }

  default public Optional<MCProdSymbol> resolveProdDown(String name, AccessModifier modifier, Predicate<MCProdSymbol> predicate) {
    return getResolvedOrThrowException(resolveProdDownMany(name, modifier, predicate));
  }

  // all resolveDownMany Methods for MCProdSymbol
  default public Collection<MCProdSymbol> resolveProdDownMany(String name) {
    return this.resolveProdDownMany(false, name, AccessModifier.ALL_INCLUSION, x -> true);
  }

  default public Collection<MCProdSymbol> resolveProdDownMany(String name, AccessModifier modifier) {
    return resolveProdDownMany(false, name, modifier, x -> true);
  }

  default public Collection<MCProdSymbol> resolveProdDownMany(String name, AccessModifier modifier, Predicate<MCProdSymbol> predicate) {
    return resolveProdDownMany(false, name, modifier, predicate);
  }

  default public Collection<MCProdSymbol> resolveProdDownMany(boolean foundSymbols, String name, AccessModifier modifier, Predicate<MCProdSymbol> predicate) {
      // 1. Conduct search locally in the current scope
    final Set<MCProdSymbol> resolved = this.resolveProdLocallyMany(foundSymbols, name,
        modifier, predicate);

    foundSymbols = foundSymbols | resolved.size() > 0;

    final String resolveCall = "resolveDownMany(\"" + name + "\", \"" + "MCProdSymbol"
        + "\") in scope \"" + getName() + "\"";
    Log.trace("START " + resolveCall + ". Found #" + resolved.size() + " (local)", "");
    // If no matching symbols have been found...
    if (resolved.isEmpty()) {
      // 2. Continue search in sub scopes and ...
      for (IGrammarScope subScope : getSubScopes()) {
        final Collection<MCProdSymbol> resolvedFromSub = subScope
            .continueAsProdSubScope(foundSymbols, name, modifier, predicate);
        foundSymbols = foundSymbols | resolved.size() > 0;
        // 3. unify results
        resolved.addAll(resolvedFromSub);
      }
    }
    Log.trace("END " + resolveCall + ". Found #" + resolved.size(), "");

    return resolved;
  }

  // resolveLocally Method for MCProdSymbol
  default public Optional<MCProdSymbol> resolveProdLocally(String name) {
    return getResolvedOrThrowException(
        this.resolveProdLocallyMany(false, name,  AccessModifier.ALL_INCLUSION, x -> true));
  }

  // all resolveImported Methods for MCProdSymbol
  default public Optional<MCProdSymbol> resolveProdImported(String name, AccessModifier modifier) {
    return this.resolveProdLocally(name);
  }

  // all resolveMany Methods for MCProdSymbol
  default public Collection<MCProdSymbol> resolveProdMany(String name) {
    return resolveProdMany(name, AccessModifier.ALL_INCLUSION);
  }

  default public Collection<MCProdSymbol> resolveProdMany(String name, AccessModifier modifier) {
    return resolveProdMany(name, modifier, x -> true);
  }

  default public Collection<MCProdSymbol> resolveProdMany(String name, AccessModifier modifier, Predicate<MCProdSymbol> predicate)  {
    return resolveProdMany(false, name, modifier, predicate);
  }

  default public Collection<MCProdSymbol> resolveProdMany(String name, Predicate<MCProdSymbol> predicate)  {
    return resolveProdMany(false, name, AccessModifier.ALL_INCLUSION, predicate);
  }

  default public Collection<MCProdSymbol> resolveProdMany(boolean foundSymbols, String name, AccessModifier modifier) {
    return resolveProdMany(foundSymbols, name, modifier, x -> true);
  }

  default public Collection<MCProdSymbol> resolveProdMany(boolean foundSymbols, String name, AccessModifier modifier, Predicate<MCProdSymbol> predicate)  {
    final Set<MCProdSymbol> resolvedSymbols = this.resolveProdLocallyMany(foundSymbols, name, modifier, predicate);
    final Collection<MCProdSymbol> resolvedFromEnclosing = continueProdWithEnclosingScope((foundSymbols | resolvedSymbols.size() > 0), name, modifier, predicate);
    resolvedSymbols.addAll(resolvedFromEnclosing);
    return resolvedSymbols;
  }

  default Set<MCProdSymbol> resolveProdLocallyMany(boolean foundSymbols, String name, AccessModifier modifier,
                                                 Predicate<MCProdSymbol> predicate) {

    final Set<MCProdSymbol> resolvedSymbols = new LinkedHashSet<>();

    try {
      // TODO remove filter?
      Optional<MCProdSymbol> resolvedSymbol = filterProd(name,
          getMCProdSymbols());
      if (resolvedSymbol.isPresent()) {
        resolvedSymbols.add(resolvedSymbol.get());
      }
    }
    catch (ResolvedSeveralEntriesForSymbolException e) {
      resolvedSymbols.addAll(e.getSymbols());
    }

    // filter out symbols that are not included within the access modifier
    Set<MCProdSymbol> filteredSymbols = filterSymbolsByAccessModifier(modifier, resolvedSymbols);
    filteredSymbols = new LinkedHashSet<>(
        filteredSymbols.stream().filter(predicate).collect(Collectors.toSet()));

    return filteredSymbols;
  }

  /**
   * @deprecated use the method resolveProdLocallyMany instead
   *             this one will be deleted soon
   */

  @Deprecated
  default Set<MCProdSymbol> resolveProdManyLocally(boolean foundSymbols, String name, AccessModifier modifier,
                                                 Predicate<MCProdSymbol> predicate) {
    return resolveProdLocallyMany(foundSymbols, name, modifier, predicate);
  }

  default Optional<MCProdSymbol> filterProd(String name, LinkedListMultimap<String, MCProdSymbol> symbols) {
    final Set<MCProdSymbol> resolvedSymbols = new LinkedHashSet<>();

    final String simpleName = Names.getSimpleName(name);

    if (symbols.containsKey(simpleName)) {
      for (MCProdSymbol symbol : symbols.get(simpleName)) {
        if (symbol.getName().equals(name) || symbol.getFullName().equals(name)) {
          resolvedSymbols.add(symbol);
        }
      }
    }

    return getResolvedOrThrowException(resolvedSymbols);
  }


  default Collection<MCProdSymbol> continueProdWithEnclosingScope(boolean foundSymbols, String name, AccessModifier modifier,
                                                                Predicate<MCProdSymbol> predicate) {
    if (checkIfContinueWithEnclosingScope(foundSymbols) && (getEnclosingScope().isPresent())) {
      return getEnclosingScope().get().resolveProdMany(foundSymbols, name, modifier, predicate);
    }
    return Collections.emptySet();
  }

  default Collection<MCProdSymbol> continueAsProdSubScope(boolean foundSymbols, String name, AccessModifier modifier, Predicate<MCProdSymbol> predicate){
    if (checkIfContinueAsSubScope(name)) {
      final String remainingSymbolName = getRemainingNameForResolveDown(name);
      return this.resolveProdDownMany(foundSymbols, remainingSymbolName, modifier, predicate);
    }
    return Collections.emptySet();
  }

  /**
   * Adds the symbol to this scope. Also, this scope is set as the symbol's enclosing scope.
   */
  void add(MCProdSymbol symbol);

  /**
   * removes the given symbol from this scope and unsets the enclosing scope relation.
   *
   * @param symbol the symbol to be removed
   */
  void remove(MCProdSymbol symbol);

  default public List<MCProdSymbol> getLocalMCProdSymbols() {
    return getMCProdSymbols().values();
  }

  LinkedListMultimap<String, MCProdSymbol> getMCProdSymbols();

  // all resolve Methods for MCProdAttributeSymbol
  default public Optional<MCProdAttributeSymbol> resolveAdditionalAttribute(String name) {
    return getResolvedOrThrowException(resolveAdditionalAttributeMany(name));
  }

  default public Optional<MCProdAttributeSymbol> resolveAdditionalAttribute(String name, AccessModifier modifier) {
    return getResolvedOrThrowException(resolveAdditionalAttributeMany(name, modifier));
  }

  default public Optional<MCProdAttributeSymbol> resolveAdditionalAttribute(String name, AccessModifier modifier, Predicate<MCProdAttributeSymbol> predicate){
    return getResolvedOrThrowException(resolveAdditionalAttributeMany(name, modifier, predicate));
  }

  default public Optional<MCProdAttributeSymbol> resolveAdditionalAttribute(boolean foundSymbols, String name, AccessModifier modifier) {
    return getResolvedOrThrowException(resolveAdditionalAttributeMany(foundSymbols, name, modifier));
  }

  // all resolveDown Methods for MCProdAttributeSymbol
  default public Optional<MCProdAttributeSymbol> resolveAdditionalAttributeDown(String name) {
    return getResolvedOrThrowException(this.resolveAdditionalAttributeDownMany(name));
  }

  default public Optional<MCProdAttributeSymbol> resolveAdditionalAttributeDown(String name, AccessModifier modifier) {
    return getResolvedOrThrowException(resolveAdditionalAttributeDownMany(name, modifier));
  }

  default public Optional<MCProdAttributeSymbol> resolveAdditionalAttributeDown(String name, AccessModifier modifier, Predicate<MCProdAttributeSymbol> predicate) {
    return getResolvedOrThrowException(resolveAdditionalAttributeDownMany(name, modifier, predicate));
  }

  // all resolveDownMany Methods for MCProdAttributeSymbol
  default public Collection<MCProdAttributeSymbol> resolveAdditionalAttributeDownMany(String name) {
    return this.resolveAdditionalAttributeDownMany(false, name, AccessModifier.ALL_INCLUSION, x -> true);
  }

  default public Collection<MCProdAttributeSymbol> resolveAdditionalAttributeDownMany(String name, AccessModifier modifier) {
    return resolveAdditionalAttributeDownMany(false, name, modifier, x -> true);
  }

  default public Collection<MCProdAttributeSymbol> resolveAdditionalAttributeDownMany(String name, AccessModifier modifier, Predicate<MCProdAttributeSymbol> predicate) {
    return resolveAdditionalAttributeDownMany(false, name, modifier, predicate);
  }

  default public Collection<MCProdAttributeSymbol> resolveAdditionalAttributeDownMany(boolean foundSymbols, String name, AccessModifier modifier, Predicate<MCProdAttributeSymbol> predicate) {
      // 1. Conduct search locally in the current scope
    final Set<MCProdAttributeSymbol> resolved = this.resolveAdditionalAttributeLocallyMany(foundSymbols, name,
        modifier, predicate);

    foundSymbols = foundSymbols | resolved.size() > 0;

    final String resolveCall = "resolveDownMany(\"" + name + "\", \"" + "MCProdAttributeSymbol"
        + "\") in scope \"" + getName() + "\"";
    Log.trace("START " + resolveCall + ". Found #" + resolved.size() + " (local)", "");
    // If no matching symbols have been found...
    if (resolved.isEmpty()) {
      // 2. Continue search in sub scopes and ...
      for (IGrammarScope subScope : getSubScopes()) {
        final Collection<MCProdAttributeSymbol> resolvedFromSub = subScope
            .continueAsAdditionalAttributeSubScope(foundSymbols, name, modifier, predicate);
        foundSymbols = foundSymbols | resolved.size() > 0;
        // 3. unify results
        resolved.addAll(resolvedFromSub);
      }
    }
    Log.trace("END " + resolveCall + ". Found #" + resolved.size(), "");

    return resolved;
  }

  // resolveLocally Method for MCProdAttributeSymbol
  default public Optional<MCProdAttributeSymbol> resolveAdditionalAttributeLocally(String name) {
    return getResolvedOrThrowException(
        this.resolveAdditionalAttributeLocallyMany(false, name,  AccessModifier.ALL_INCLUSION, x -> true));
  }

  // all resolveImported Methods for MCProdAttributeSymbol
  default public Optional<MCProdAttributeSymbol> resolveAdditionalAttributeImported(String name, AccessModifier modifier) {
    return this.resolveAdditionalAttributeLocally(name);
  }

  // all resolveMany Methods for MCProdAttributeSymbol
  default public Collection<MCProdAttributeSymbol> resolveAdditionalAttributeMany(String name) {
    return resolveAdditionalAttributeMany(name, AccessModifier.ALL_INCLUSION);
  }

  default public Collection<MCProdAttributeSymbol> resolveAdditionalAttributeMany(String name, AccessModifier modifier) {
    return resolveAdditionalAttributeMany(name, modifier, x -> true);
  }

  default public Collection<MCProdAttributeSymbol> resolveAdditionalAttributeMany(String name, AccessModifier modifier, Predicate<MCProdAttributeSymbol> predicate)  {
    return resolveAdditionalAttributeMany(false, name, modifier, predicate);
  }

  default public Collection<MCProdAttributeSymbol> resolveAdditionalAttributeMany(String name, Predicate<MCProdAttributeSymbol> predicate)  {
    return resolveAdditionalAttributeMany(false, name, AccessModifier.ALL_INCLUSION, predicate);
  }

  default public Collection<MCProdAttributeSymbol> resolveAdditionalAttributeMany(boolean foundSymbols, String name, AccessModifier modifier) {
    return resolveAdditionalAttributeMany(foundSymbols, name, modifier, x -> true);
  }

  default public Collection<MCProdAttributeSymbol> resolveAdditionalAttributeMany(boolean foundSymbols, String name, AccessModifier modifier, Predicate<MCProdAttributeSymbol> predicate)  {
    final Set<MCProdAttributeSymbol> resolvedSymbols = this.resolveAdditionalAttributeLocallyMany(foundSymbols, name, modifier, predicate);
    final Collection<MCProdAttributeSymbol> resolvedFromEnclosing = continueAdditionalAttributeWithEnclosingScope((foundSymbols | resolvedSymbols.size() > 0), name, modifier, predicate);
    resolvedSymbols.addAll(resolvedFromEnclosing);
    return resolvedSymbols;
  }

  default Set<MCProdAttributeSymbol> resolveAdditionalAttributeLocallyMany(boolean foundSymbols, String name, AccessModifier modifier,
                                                                               Predicate<MCProdAttributeSymbol> predicate) {

    final Set<MCProdAttributeSymbol> resolvedSymbols = new LinkedHashSet<>();

    try {
      // TODO remove filter?
      Optional<MCProdAttributeSymbol> resolvedSymbol = filterAdditionalAttribute(name,
              getMCProdAttributeSymbols());
      if (resolvedSymbol.isPresent()) {
        resolvedSymbols.add(resolvedSymbol.get());
      }
    }
    catch (ResolvedSeveralEntriesForSymbolException e) {
      resolvedSymbols.addAll(e.getSymbols());
    }

    // filter out symbols that are not included within the access modifier
    Set<MCProdAttributeSymbol> filteredSymbols = filterSymbolsByAccessModifier(modifier, resolvedSymbols);
    filteredSymbols = new LinkedHashSet<>(
            filteredSymbols.stream().filter(predicate).collect(Collectors.toSet()));

    return filteredSymbols;
  }

  /**
   * @deprecated use the method resolveAdditionalAttributeLocallyMany instead
   *             this one will be deleted soon
   */

  @Deprecated
  default Set<MCProdAttributeSymbol> resolveAdditionalAttributeManyLocally(boolean foundSymbols, String name, AccessModifier modifier,
                                                                               Predicate<MCProdAttributeSymbol> predicate) {
    return resolveAdditionalAttributeLocallyMany(foundSymbols, name, modifier, predicate);
  }

  default Optional<MCProdAttributeSymbol> filterAdditionalAttribute(String name, LinkedListMultimap<String, MCProdAttributeSymbol> symbols) {
    final Set<MCProdAttributeSymbol> resolvedSymbols = new LinkedHashSet<>();

    final String simpleName = Names.getSimpleName(name);

    if (symbols.containsKey(simpleName)) {
      for (MCProdAttributeSymbol symbol : symbols.get(simpleName)) {
        if (symbol.getName().equals(name) || symbol.getFullName().equals(name)) {
          resolvedSymbols.add(symbol);
        }
      }
    }

    return getResolvedOrThrowException(resolvedSymbols);
  }


  default Collection<MCProdAttributeSymbol> continueAdditionalAttributeWithEnclosingScope(boolean foundSymbols, String name, AccessModifier modifier,
                                                                                              Predicate<MCProdAttributeSymbol> predicate) {
    if (checkIfContinueWithEnclosingScope(foundSymbols) && (getEnclosingScope().isPresent())) {
      return getEnclosingScope().get().resolveAdditionalAttributeMany(foundSymbols, name, modifier, predicate);
    }
    return Collections.emptySet();
  }

  default Collection<MCProdAttributeSymbol> continueAsAdditionalAttributeSubScope(boolean foundSymbols, String name, AccessModifier modifier, Predicate<MCProdAttributeSymbol> predicate){
    if (checkIfContinueAsSubScope(name)) {
      final String remainingSymbolName = getRemainingNameForResolveDown(name);
      return this.resolveAdditionalAttributeDownMany(foundSymbols, remainingSymbolName, modifier, predicate);
    }
    return Collections.emptySet();
  }

  /**
   * Adds the symbol to this scope. Also, this scope is set as the symbol's enclosing scope.
   */
  void add(MCProdAttributeSymbol symbol);

  /**
   * removes the given symbol from this scope and unsets the enclosing scope relation.
   *
   * @param symbol the symbol to be removed
   */
  void remove(MCProdAttributeSymbol symbol);

  default public List<MCProdAttributeSymbol> getLocalMCProdAttributeSymbols() {
    return getMCProdAttributeSymbols().values();
  }

  LinkedListMultimap<String, MCProdAttributeSymbol> getMCProdAttributeSymbols();

  /**
   * Adds the symbol to this scope. Also, this scope is set as the symbol's enclosing scope.
   */
  void add(MCProdComponentSymbol symbol);

  /**
   * removes the given symbol from this scope and unsets the enclosing scope relation.
   *
   * @param symbol the symbol to be removed
   */
  void remove(MCProdComponentSymbol symbol);

  default public List<MCProdComponentSymbol> getLocalMCProdComponentSymbols() {
    return getMCProdComponentSymbols().values();
  }

  LinkedListMultimap<String, MCProdComponentSymbol> getMCProdComponentSymbols();

  // all resolve Methods for MCProdSymbol
  default public Optional<MCProdComponentSymbol> resolveMCProdComponent(String name) {
    return getResolvedOrThrowException(resolveMCProdComponentMany(name));
  }

  default public Optional<MCProdComponentSymbol> resolveMCProdComponent(String name, AccessModifier modifier) {
    return getResolvedOrThrowException(resolveMCProdComponentMany(name, modifier));
  }

  default public Optional<MCProdComponentSymbol> resolveMCProdComponent(String name, AccessModifier modifier, Predicate<MCProdComponentSymbol> predicate){
    return getResolvedOrThrowException(resolveMCProdComponentMany(name, modifier, predicate));
  }

  default public Optional<MCProdComponentSymbol> resolveMCProdComponent(boolean foundSymbols, String name, AccessModifier modifier) {
    return getResolvedOrThrowException(resolveMCProdComponentMany(foundSymbols, name, modifier));
  }

  // all resolveDown Methods for MCMCProdComponentSymbol
  default public Optional<MCProdComponentSymbol> resolveMCProdComponentDown(String name) {
    return getResolvedOrThrowException(this.resolveMCProdComponentDownMany(name));
  }

  default public Optional<MCProdComponentSymbol> resolveMCProdComponentDown(String name, AccessModifier modifier) {
    return getResolvedOrThrowException(resolveMCProdComponentDownMany(name, modifier));
  }

  default public Optional<MCProdComponentSymbol> resolveMCProdComponentDown(String name, AccessModifier modifier, Predicate<MCProdComponentSymbol> predicate) {
    return getResolvedOrThrowException(resolveMCProdComponentDownMany(name, modifier, predicate));
  }

  // all resolveDownMany Methods for MCMCProdComponentSymbol
  default public Collection<MCProdComponentSymbol> resolveMCProdComponentDownMany(String name) {
    return this.resolveMCProdComponentDownMany(false, name, AccessModifier.ALL_INCLUSION, x -> true);
  }

  default public Collection<MCProdComponentSymbol> resolveMCProdComponentDownMany(String name, AccessModifier modifier) {
    return resolveMCProdComponentDownMany(false, name, modifier, x -> true);
  }

  default public Collection<MCProdComponentSymbol> resolveMCProdComponentDownMany(String name, AccessModifier modifier, Predicate<MCProdComponentSymbol> predicate) {
    return resolveMCProdComponentDownMany(false, name, modifier, predicate);
  }

  default public Collection<MCProdComponentSymbol> resolveMCProdComponentDownMany(boolean foundSymbols, String name, AccessModifier modifier, Predicate<MCProdComponentSymbol> predicate) {
    // 1. Conduct search locally in the current scope
    final Set<MCProdComponentSymbol> resolved = this.resolveMCProdComponentLocallyMany(foundSymbols, name,
            modifier, predicate);

    foundSymbols = foundSymbols | resolved.size() > 0;

    final String resolveCall = "resolveDownMany(\"" + name + "\", \"" + "MCMCProdComponentSymbol"
            + "\") in scope \"" + getName() + "\"";
    Log.trace("START " + resolveCall + ". Found #" + resolved.size() + " (local)", "");
    // If no matching symbols have been found...
    if (resolved.isEmpty()) {
      // 2. Continue search in sub scopes and ...
      for (IGrammarScope subScope : getSubScopes()) {
        final Collection<MCProdComponentSymbol> resolvedFromSub = subScope
                .continueAsMCProdComponentSubScope(foundSymbols, name, modifier, predicate);
        foundSymbols = foundSymbols | resolved.size() > 0;
        // 3. unify results
        resolved.addAll(resolvedFromSub);
      }
    }
    Log.trace("END " + resolveCall + ". Found #" + resolved.size(), "");

    return resolved;
  }

  // resolveLocally Method for MCMCProdComponentSymbol
  default public Optional<MCProdComponentSymbol> resolveMCProdComponentLocally(String name) {
    return getResolvedOrThrowException(
            this.resolveMCProdComponentLocallyMany(false, name,  AccessModifier.ALL_INCLUSION, x -> true));
  }

  // all resolveImported Methods for MCMCProdComponentSymbol
  default public Optional<MCProdComponentSymbol> resolveMCProdComponentImported(String name, AccessModifier modifier) {
    return this.resolveMCProdComponentLocally(name);
  }

  // all resolveMany Methods for MCMCProdComponentSymbol
  default public Collection<MCProdComponentSymbol> resolveMCProdComponentMany(String name) {
    return resolveMCProdComponentMany(name, AccessModifier.ALL_INCLUSION);
  }

  default public Collection<MCProdComponentSymbol> resolveMCProdComponentMany(String name, AccessModifier modifier) {
    return resolveMCProdComponentMany(name, modifier, x -> true);
  }

  default public Collection<MCProdComponentSymbol> resolveMCProdComponentMany(String name, AccessModifier modifier, Predicate<MCProdComponentSymbol> predicate)  {
    return resolveMCProdComponentMany(false, name, modifier, predicate);
  }

  default public Collection<MCProdComponentSymbol> resolveMCProdComponentMany(String name, Predicate<MCProdComponentSymbol> predicate)  {
    return resolveMCProdComponentMany(false, name, AccessModifier.ALL_INCLUSION, predicate);
  }

  default public Collection<MCProdComponentSymbol> resolveMCProdComponentMany(boolean foundSymbols, String name, AccessModifier modifier) {
    return resolveMCProdComponentMany(foundSymbols, name, modifier, x -> true);
  }

  default public Collection<MCProdComponentSymbol> resolveMCProdComponentMany(boolean foundSymbols, String name, AccessModifier modifier, Predicate<MCProdComponentSymbol> predicate)  {
    final Set<MCProdComponentSymbol> resolvedSymbols = this.resolveMCProdComponentLocallyMany(foundSymbols, name, modifier, predicate);
    final Collection<MCProdComponentSymbol> resolvedFromEnclosing = continueMCProdComponentWithEnclosingScope((foundSymbols | resolvedSymbols.size() > 0), name, modifier, predicate);
    resolvedSymbols.addAll(resolvedFromEnclosing);
    return resolvedSymbols;
  }

  default Set<MCProdComponentSymbol> resolveMCProdComponentLocallyMany(boolean foundSymbols, String name, AccessModifier modifier,
                                                   Predicate<MCProdComponentSymbol> predicate) {

    final Set<MCProdComponentSymbol> resolvedSymbols = new LinkedHashSet<>();

    try {
      // TODO remove filter?
      Optional<MCProdComponentSymbol> resolvedSymbol = filterMCProdComponent(name,
              getMCProdComponentSymbols());
      if (resolvedSymbol.isPresent()) {
        resolvedSymbols.add(resolvedSymbol.get());
      }
    }
    catch (ResolvedSeveralEntriesForSymbolException e) {
      resolvedSymbols.addAll(e.getSymbols());
    }

    // filter out symbols that are not included within the access modifier
    Set<MCProdComponentSymbol> filteredSymbols = filterSymbolsByAccessModifier(modifier, resolvedSymbols);
    filteredSymbols = new LinkedHashSet<>(
            filteredSymbols.stream().filter(predicate).collect(Collectors.toSet()));

    return filteredSymbols;
  }

  /**
   * @deprecated use the method resolveMCProdComponentLocallyMany instead
   *             this one will be deleted soon
   */

  @Deprecated
  default Set<MCProdComponentSymbol> resolveMCProdComponentManyLocally(boolean foundSymbols, String name, AccessModifier modifier,
                                                   Predicate<MCProdComponentSymbol> predicate) {
    return resolveMCProdComponentLocallyMany(foundSymbols, name, modifier, predicate);
  }

  default Optional<MCProdComponentSymbol> filterMCProdComponent(String name, LinkedListMultimap<String, MCProdComponentSymbol> symbols) {
    final Set<MCProdComponentSymbol> resolvedSymbols = new LinkedHashSet<>();

    final String simpleName = Names.getSimpleName(name);

    if (symbols.containsKey(simpleName)) {
      for (MCProdComponentSymbol symbol : symbols.get(simpleName)) {
        if (symbol.getName().equals(name) || symbol.getFullName().equals(name)) {
          resolvedSymbols.add(symbol);
        }
      }
    }

    return getResolvedOrThrowException(resolvedSymbols);
  }


  default Collection<MCProdComponentSymbol> continueMCProdComponentWithEnclosingScope(boolean foundSymbols, String name, AccessModifier modifier,
                                                                  Predicate<MCProdComponentSymbol> predicate) {
    if (checkIfContinueWithEnclosingScope(foundSymbols) && (getEnclosingScope().isPresent())) {
      return getEnclosingScope().get().resolveMCProdComponentMany(foundSymbols, name, modifier, predicate);
    }
    return Collections.emptySet();
  }

  default Collection<MCProdComponentSymbol> continueAsMCProdComponentSubScope(boolean foundSymbols, String name, AccessModifier modifier, Predicate<MCProdComponentSymbol> predicate){
    if (checkIfContinueAsSubScope(name)) {
      final String remainingSymbolName = getRemainingNameForResolveDown(name);
      return this.resolveMCProdComponentDownMany(foundSymbols, remainingSymbolName, modifier, predicate);
    }
    return Collections.emptySet();
  }

}

