package de.monticore.expressions.commonexpressions._ast;

import de.monticore.expressions.expressionsbasis._ast.ASTEMethod;
import de.monticore.expressions.expressionsbasis._symboltable.EMethodSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

public class ASTCallExpression  extends ASTCallExpressionTOP {

  protected Optional<ASTEMethod> nameDefinition = Optional.empty();

  public ASTCallExpression() {
    super();
  }

  public ASTCallExpression(String name, ASTArguments arguments, Optional<ASTEMethod> nameDefinition) {
    super(name, arguments);
    this.nameDefinition = nameDefinition;
  }

  public ASTCallExpression(String name, ASTArguments arguments) {
    super(name, arguments);
  }

  public  Optional<EMethodSymbol> getNameSymbolOpt()   {
    if(!nameDefinition.isPresent()){
      if (getNameDefinitionOpt().isPresent()) {
        return getNameDefinitionOpt().get().getEMethodSymbolOpt();
      }else {
        return Optional.empty();
      }
    }
    return nameDefinition.get().getEMethodSymbolOpt();
  }



  public  de.monticore.expressions.expressionsbasis._symboltable.EMethodSymbol getNameSymbol()   {
    if (getNameSymbolOpt().isPresent()) {
      return getNameSymbolOpt().get();
    }
    Log.error("0xA7003x672 getNameSymbolOpt() can't return a value. It is empty.");
    // Normally this statement is not reachable
    throw new IllegalStateException();
  }



  public  boolean isPresentNameSymbol()   {
    return getNameSymbolOpt().isPresent();
  }

  public  Optional<ASTEMethod> getNameDefinitionOpt()   {

    if(!nameDefinition.isPresent()){
      if ((name != null) && isPresentEnclosingScope()) {
        Optional<de.monticore.expressions.expressionsbasis._symboltable.EMethodSymbol> symbol = enclosingScope.get().resolve(name, de.monticore.expressions.expressionsbasis._symboltable.EMethodSymbol.KIND);
        nameDefinition = symbol.get().getEMethodNode();
      }
    }
    return nameDefinition;
  }



  public  ASTEMethod getNameDefinition()   {

    if (getNameDefinitionOpt().isPresent()) {
      return getNameDefinitionOpt().get();
    }
    Log.error("0xA7003x271 getNameDefinitionOpt() can't return a value. It is empty.");
    // Normally this statement is not reachable
    throw new IllegalStateException();
  }



  public  boolean isPresentNameDefinition()   {


    return getNameDefinitionOpt().isPresent();
  }



  public  void setNameDefinition(ASTEMethod ast)   {
    /* generated by template ast.ErrorIfNull*/
    // MontiCore generally assumes that null is not used, but if you are
    // unsure then override template ast.ErrorIfNull
    // Log.errorIfNull(ast, "0xA7006x740 Parameter 'ast' must not be null.");

    /* generated by template ast.additionalmethods.SetReferencedDefinition*/

    setNameDefinitionAbsent();
    setName(ast.getName());
  }

  /* generated by template ast.ClassMethod*/


  public  void setNameDefinitionOpt(Optional<ASTEMethod> astOpt)   {

    /* generated by template ast.additionalmethods.SetReferencedDefinitionOpt*/

    setNameDefinitionAbsent();
    setName(astOpt.get().getName());
  }

  /* generated by template ast.ClassMethod*/


  public  void setNameDefinitionAbsent()   {

    /* generated by template ast.additionalmethods.SetReferencedDefinitionAbsent*/

    nameDefinition = Optional.empty();
  }


}