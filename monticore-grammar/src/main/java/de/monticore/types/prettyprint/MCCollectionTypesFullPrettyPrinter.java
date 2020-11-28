package de.monticore.types.prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.MCBasicsPrettyPrinter;
import de.monticore.types.mccollectiontypes.MCCollectionTypesMill;
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;
import de.monticore.types.mccollectiontypes._visitor.MCCollectionTypesTraverser;

public class MCCollectionTypesFullPrettyPrinter extends MCBasicTypesFullPrettyPrinter {

  private MCCollectionTypesTraverser traverser;

  public MCCollectionTypesFullPrettyPrinter(IndentPrinter printer){
    super(printer);
    this.traverser = MCCollectionTypesMill.traverser();

    MCCollectionTypesPrettyPrinter collectionTypes = new MCCollectionTypesPrettyPrinter(printer);
    traverser.setMCCollectionTypesHandler(collectionTypes);
    traverser.addMCCollectionTypesVisitor(collectionTypes);

    MCBasicTypesPrettyPrinter basicTypes = new MCBasicTypesPrettyPrinter(printer);
    traverser.setMCBasicTypesHandler(basicTypes);
    traverser.addMCBasicTypesVisitor(basicTypes);

    MCBasicsPrettyPrinter basics = new MCBasicsPrettyPrinter(printer);
    traverser.addMCBasicsVisitor(basics);
  }

  public MCCollectionTypesTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(MCCollectionTypesTraverser traverser) {
    this.traverser = traverser;
  }

  /**
   * This method prettyprints a given node from type grammar.
   *
   * @param a A node from type grammar.
   * @return String representation.
   */
  public String prettyprint(ASTMCTypeArgument a) {
    getPrinter().clearBuffer();
    a.accept(getTraverser());
    return getPrinter().getContent();
  }

}
