/* (c) https://github.com/MontiCore/monticore */

/* generated from model BasicSymbols */
/* generated by template core.Interface*/

package de.monticore.visitor;


import de.monticore.ast.ASTNode;
import de.monticore.symboltable.IScope;
import de.monticore.symboltable.ISymbol;

public interface IHandler {

  ITraverser getTraverser();

  default void handle(ASTNode node) {
    getTraverser().visit(node);
    getTraverser().endVisit(node);
  }

  default void handle(ISymbol symbol) {
    getTraverser().visit(symbol);
    getTraverser().endVisit(symbol);
  }

  default void handle(IScope scope) {
    getTraverser().visit(scope);
    getTraverser().endVisit(scope);
  }

}
