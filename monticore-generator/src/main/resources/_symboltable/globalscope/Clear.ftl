<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("resolverMethodList", "symbolList")}
  clearLoadedFiles();
<#list resolverMethodList as resolverMethod>
  ${resolverMethod}().clear();
</#list>
<#list symbolList as symbol>
  get${symbol}s().clear();
</#list>
  this.modelPath = new de.monticore.io.paths.ModelPath();
  this.subScopes.clear();
  this.symbolDeSers.clear();
  init();