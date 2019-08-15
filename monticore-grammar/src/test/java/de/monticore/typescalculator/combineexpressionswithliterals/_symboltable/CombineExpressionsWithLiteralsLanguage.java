package de.monticore.typescalculator.combineexpressionswithliterals._symboltable;

public class CombineExpressionsWithLiteralsLanguage extends CombineExpressionsWithLiteralsLanguageTOP {

  public CombineExpressionsWithLiteralsLanguage(){
    super("CombineExpressionsWithLiteralsLanguage","ce");
  }

  @Override
  protected CombineExpressionsWithLiteralsModelLoader provideModelLoader() {
    return new CombineExpressionsWithLiteralsModelLoader(this);
  }
}
