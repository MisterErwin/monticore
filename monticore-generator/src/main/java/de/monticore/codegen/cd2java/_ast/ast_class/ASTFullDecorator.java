package de.monticore.codegen.cd2java._ast.ast_class;

import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.codegen.cd2java.CompositeDecorator;
import de.monticore.codegen.cd2java._ast.ast_class.reference.ASTReferenceDecorator;
import de.monticore.codegen.cd2java.data.DataDecorator;

public class ASTFullDecorator extends CompositeDecorator<ASTCDClass> {

  public ASTFullDecorator(final DataDecorator dataDecorator,
      final ASTDecorator astDecorator,
      final ASTReferenceDecorator astReferencedSymbolDecorator) {
    super(dataDecorator, astDecorator, astReferencedSymbolDecorator);
  }
}
