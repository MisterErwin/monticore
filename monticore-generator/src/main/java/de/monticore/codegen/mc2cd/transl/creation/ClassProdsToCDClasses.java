/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl.creation;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cd4analysis._ast.CD4AnalysisNodeFactory;
import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.utils.Link;

import java.util.function.UnaryOperator;

public class ClassProdsToCDClasses implements
    UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {
  
  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
      Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {
    
    for (Link<ASTMCGrammar, ASTCDDefinition> link : rootLink.getLinks(ASTMCGrammar.class,
        ASTCDDefinition.class)) {
      createClassProdToCDClassLinks(link);
    }
    return rootLink;
  }
  
  private void createClassProdToCDClassLinks(Link<ASTMCGrammar, ASTCDDefinition> link) {
    for (ASTClassProd classProd : link.source().getClassProdList()) {
      ASTCDClass cdClass = CD4AnalysisNodeFactory.createASTCDClass();
      cdClass.setModifier(CD4AnalysisNodeFactory.createASTModifier());
      link.target().getCDClassesList().add(cdClass);
      new Link<>(classProd, cdClass, link);
    }
  }
}
