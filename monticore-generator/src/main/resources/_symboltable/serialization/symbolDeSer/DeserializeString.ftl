<#-- (c) https://github.com/MontiCore/monticore -->
  de.monticore.symboltable.serialization.json.JsonObject symbol =
    de.monticore.symboltable.serialization.JsonParser.parseJsonObject(serialized);
  return deserialize(symbol,enclosingScope);