package de.monticore.expressions.bitexpressions.types3;

import com.google.common.base.Preconditions;
import de.monticore.expressions.bitexpressions._ast.ASTBinaryAndExpression;
import de.monticore.expressions.bitexpressions._ast.ASTBinaryExpression;
import de.monticore.expressions.bitexpressions._ast.ASTBinaryOrOpExpression;
import de.monticore.expressions.bitexpressions._ast.ASTBinaryXorExpression;
import de.monticore.expressions.bitexpressions._ast.ASTLeftShiftExpression;
import de.monticore.expressions.bitexpressions._ast.ASTLogicalRightShiftExpression;
import de.monticore.expressions.bitexpressions._ast.ASTRightShiftExpression;
import de.monticore.expressions.bitexpressions._ast.ASTShiftExpression;
import de.monticore.expressions.bitexpressions._visitor.BitExpressionsVisitor2;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypePrimitive;
import de.monticore.types3.AbstractTypeVisitor;
import de.monticore.types3.ISymTypeRelations;
import de.monticore.types3.util.SymTypeRelations;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;

public class BitExpressionsTypeVisitor extends AbstractTypeVisitor
    implements BitExpressionsVisitor2 {

  protected ISymTypeRelations typeRelations;

  public BitExpressionsTypeVisitor(ISymTypeRelations typeRelations) {
    this.typeRelations = typeRelations;
  }

  public BitExpressionsTypeVisitor() {
    this(new SymTypeRelations());
  }

  public void setSymTypeRelations(ISymTypeRelations symTypeRelations) {
    this.typeRelations = symTypeRelations;
  }

  protected ISymTypeRelations getTypeRel() {
    return typeRelations;
  }

  @Override
  public void endVisit(ASTLeftShiftExpression expr) {
    Preconditions.checkNotNull(expr);
    SymTypeExpression symType = this.deriveShift(expr, "<<");
    getType4Ast().setTypeOfExpression(expr, symType);
  }

  @Override
  public void endVisit(ASTRightShiftExpression expr) {
    Preconditions.checkNotNull(expr);
    SymTypeExpression symType = this.deriveShift(expr, ">>");
    getType4Ast().setTypeOfExpression(expr, symType);
  }

  @Override
  public void endVisit(ASTLogicalRightShiftExpression expr) {
    Preconditions.checkNotNull(expr);
    SymTypeExpression symType = this.deriveShift(expr, ">>>");
    getType4Ast().setTypeOfExpression(expr, symType);
  }

  protected SymTypeExpression deriveShift(ASTShiftExpression expr, String op) {
    // calculate the type of inner expressions
    SymTypeExpression left = getType4Ast().getPartialTypeOfExpr(expr.getLeft());
    SymTypeExpression right = getType4Ast().getPartialTypeOfExpr(expr.getRight());

    // result of inner type computation should be present
    if (left.isObscureType() || right.isObscureType()) {
      // if left or right obscure then error already logged
      return SymTypeExpressionFactory.createObscureType();
    }
    else {
      return calculateTypeShift(left, right, op, expr.get_SourcePositionStart());
    }
  }

  protected SymTypeExpression calculateTypeShift(SymTypeExpression leftResult,
      SymTypeExpression rightResult, String op, SourcePosition pos) {
    if (leftResult.isPrimitive() && rightResult.isPrimitive()) {
      SymTypePrimitive leftEx = (SymTypePrimitive) leftResult;
      SymTypePrimitive rightEx = (SymTypePrimitive) rightResult;

      //only defined on integral type - integral type
      if (typeRelations.isIntegralType(leftEx) && typeRelations.isIntegralType(rightEx)) {
        return shiftCalculator(leftResult, rightResult, op, pos);
      }
    }
    //should not happen
    Log.error("0xC0201 Operator " + op + " not applicable to the types" +
        "'" + leftResult.print() + "', '" + rightResult.print() + "'");
    return SymTypeExpressionFactory.createObscureType();
  }

  /**
   * helper method to calculate the type of the ShiftExpressions
   * cannot be linked with the BinaryExpressions because they are not calculated the same way
   */
  protected SymTypeExpression shiftCalculator(SymTypeExpression left, SymTypeExpression right,
      String op, SourcePosition pos) {
    if (!left.isPrimitive() || !right.isPrimitive()) {
      Log.error("0xC0204 The operator " + op + " is only applicable to primitive types.", pos);
      return SymTypeExpressionFactory.createObscureType();
    }
    SymTypePrimitive leftResult = (SymTypePrimitive) left;
    SymTypePrimitive rightResult = (SymTypePrimitive) right;

    //only defined on integral type - integral type
    if (typeRelations.isIntegralType(leftResult) && typeRelations.isIntegralType(rightResult)) {
      if (getTypeRel().isLong(rightResult)) {
        if (getTypeRel().isLong(leftResult)) {
          return SymTypeExpressionFactory.createPrimitive("long");
        }
        else {
          return SymTypeExpressionFactory.createPrimitive("int");
        }
      }
      else {
        return SymTypeExpressionFactory.createPrimitive("int");
      }
    }
    //should never happen
    Log.error("0xC0205 The operator " + op + " is only applicable to integral types.", pos);
    return SymTypeExpressionFactory.createObscureType();
  }

  @Override
  public void endVisit(ASTBinaryAndExpression expr) {
    Preconditions.checkNotNull(expr);
    SymTypeExpression symType = this.deriveBinary(expr, expr.getOperator());
    getType4Ast().setTypeOfExpression(expr, symType);
  }

  @Override
  public void endVisit(ASTBinaryOrOpExpression expr) {
    Preconditions.checkNotNull(expr);
    SymTypeExpression symType = this.deriveBinary(expr, expr.getOperator());
    getType4Ast().setTypeOfExpression(expr, symType);
  }

  @Override
  public void endVisit(ASTBinaryXorExpression expr) {
    Preconditions.checkNotNull(expr);
    SymTypeExpression symType = this.deriveBinary(expr, expr.getOperator());
    getType4Ast().setTypeOfExpression(expr, symType);
  }

  protected SymTypeExpression deriveBinary(ASTBinaryExpression expr, String operator) {
    // calculate the type of inner expressions
    SymTypeExpression leftRes = getType4Ast().getPartialTypeOfExpr(expr.getLeft());
    SymTypeExpression rightRes = getType4Ast().getPartialTypeOfExpr(expr.getRight());

    // result of inner type computation should be present
    if (leftRes.isObscureType() || rightRes.isObscureType()) {
      // if left or right obscure then error already logged
      return SymTypeExpressionFactory.createObscureType();
    }
    else {
      return calculateTypeBinary(leftRes, rightRes, operator, expr.get_SourcePositionStart());
    }
  }

  protected SymTypeExpression calculateTypeBinary(SymTypeExpression leftResult,
      SymTypeExpression rightResult, String operator, SourcePosition pos) {
    if (leftResult.isPrimitive() && rightResult.isPrimitive()) {
      SymTypePrimitive leftEx = (SymTypePrimitive) leftResult;
      SymTypePrimitive rightEx = (SymTypePrimitive) rightResult;

      //only defined on boolean - boolean and integral type - integral type
      if (getTypeRel().isBoolean(leftResult) &&
          getTypeRel().isBoolean(rightResult)) {
        return SymTypeExpressionFactory.createPrimitive("boolean");
      }
      else if (getTypeRel().isIntegralType(leftEx) && getTypeRel().isIntegralType(rightEx)) {
        return getTypeRel().numericPromotion(leftEx, rightEx);
      }
    }
    //should not happen, no valid result, error will be handled in traverse
    Log.error("0xC0203 The operator " + operator + " is not applicable to the types" +
        "'" + leftResult + "', '" + rightResult + "'", pos);
    return SymTypeExpressionFactory.createObscureType();
  }

}
