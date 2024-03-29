package dtool.descentadapter;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.utilbox.core.Assert;
import descent.internal.compiler.parser.AddAssignExp;
import descent.internal.compiler.parser.AddExp;
import descent.internal.compiler.parser.AddrExp;
import descent.internal.compiler.parser.AndAndExp;
import descent.internal.compiler.parser.AndAssignExp;
import descent.internal.compiler.parser.AndExp;
import descent.internal.compiler.parser.ArrayExp;
import descent.internal.compiler.parser.ArrayInitializer;
import descent.internal.compiler.parser.ArrayLengthExp;
import descent.internal.compiler.parser.ArrayLiteralExp;
import descent.internal.compiler.parser.AssertExp;
import descent.internal.compiler.parser.AssignExp;
import descent.internal.compiler.parser.AssocArrayLiteralExp;
import descent.internal.compiler.parser.BinExp;
import descent.internal.compiler.parser.CallExp;
import descent.internal.compiler.parser.CastExp;
import descent.internal.compiler.parser.CatAssignExp;
import descent.internal.compiler.parser.CatExp;
import descent.internal.compiler.parser.CmpExp;
import descent.internal.compiler.parser.ComExp;
import descent.internal.compiler.parser.CommaExp;
import descent.internal.compiler.parser.CompileDeclaration;
import descent.internal.compiler.parser.CompileExp;
import descent.internal.compiler.parser.CompileStatement;
import descent.internal.compiler.parser.CondExp;
import descent.internal.compiler.parser.DeleteExp;
import descent.internal.compiler.parser.DivAssignExp;
import descent.internal.compiler.parser.DivExp;
import descent.internal.compiler.parser.DollarExp;
import descent.internal.compiler.parser.EqualExp;
import descent.internal.compiler.parser.ErrorExp;
import descent.internal.compiler.parser.ExpInitializer;
import descent.internal.compiler.parser.FileExp;
import descent.internal.compiler.parser.FileInitExp;
import descent.internal.compiler.parser.FuncExp;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.IdentityExp;
import descent.internal.compiler.parser.InExp;
import descent.internal.compiler.parser.IndexExp;
import descent.internal.compiler.parser.IntegerExp;
import descent.internal.compiler.parser.IsExp;
import descent.internal.compiler.parser.LineInitExp;
import descent.internal.compiler.parser.MinAssignExp;
import descent.internal.compiler.parser.MinExp;
import descent.internal.compiler.parser.ModAssignExp;
import descent.internal.compiler.parser.ModExp;
import descent.internal.compiler.parser.MulAssignExp;
import descent.internal.compiler.parser.MulExp;
import descent.internal.compiler.parser.NegExp;
import descent.internal.compiler.parser.NewAnonClassExp;
import descent.internal.compiler.parser.NewExp;
import descent.internal.compiler.parser.NotExp;
import descent.internal.compiler.parser.NullExp;
import descent.internal.compiler.parser.OrAssignExp;
import descent.internal.compiler.parser.OrExp;
import descent.internal.compiler.parser.OrOrExp;
import descent.internal.compiler.parser.PostExp;
import descent.internal.compiler.parser.PtrExp;
import descent.internal.compiler.parser.RealExp;
import descent.internal.compiler.parser.ScopeExp;
import descent.internal.compiler.parser.ShlAssignExp;
import descent.internal.compiler.parser.ShlExp;
import descent.internal.compiler.parser.ShrAssignExp;
import descent.internal.compiler.parser.ShrExp;
import descent.internal.compiler.parser.SliceExp;
import descent.internal.compiler.parser.StringExp;
import descent.internal.compiler.parser.StructInitializer;
import descent.internal.compiler.parser.SuperExp;
import descent.internal.compiler.parser.TOK;
import descent.internal.compiler.parser.TY;
import descent.internal.compiler.parser.ThisExp;
import descent.internal.compiler.parser.TraitsExp;
import descent.internal.compiler.parser.TypeBasic;
import descent.internal.compiler.parser.TypeFunction;
import descent.internal.compiler.parser.TypeidExp;
import descent.internal.compiler.parser.UAddExp;
import descent.internal.compiler.parser.UnaExp;
import descent.internal.compiler.parser.UshrAssignExp;
import descent.internal.compiler.parser.UshrExp;
import descent.internal.compiler.parser.VoidInitializer;
import descent.internal.compiler.parser.XorAssignExp;
import descent.internal.compiler.parser.XorExp;
import dtool.ast.ASTNeoNode;
import dtool.ast.SourceRange;
import dtool.ast.declarations.DeclarationStringMacro;
import dtool.ast.definitions.BaseClass;
import dtool.ast.definitions.IFunctionParameter;
import dtool.ast.expressions.ExpArrayIndex;
import dtool.ast.expressions.ExpArrayLength;
import dtool.ast.expressions.ExpAssert;
import dtool.ast.expressions.ExpCast;
import dtool.ast.expressions.ExpCond;
import dtool.ast.expressions.ExpDefaultInit;
import dtool.ast.expressions.ExpDelete;
import dtool.ast.expressions.ExpDollar;
import dtool.ast.expressions.ExpIftype;
import dtool.ast.expressions.ExpLiteralBool;
import dtool.ast.expressions.ExpLiteralFunc;
import dtool.ast.expressions.ExpLiteralImportedString;
import dtool.ast.expressions.ExpLiteralInteger;
import dtool.ast.expressions.ExpLiteralMapArray;
import dtool.ast.expressions.ExpLiteralNewAnonClass;
import dtool.ast.expressions.ExpLiteralNull;
import dtool.ast.expressions.ExpLiteralReal;
import dtool.ast.expressions.ExpLiteralString;
import dtool.ast.expressions.ExpNew;
import dtool.ast.expressions.ExpReference;
import dtool.ast.expressions.ExpSlice;
import dtool.ast.expressions.ExpStringMacro;
import dtool.ast.expressions.ExpSuper;
import dtool.ast.expressions.ExpThis;
import dtool.ast.expressions.ExpTraits;
import dtool.ast.expressions.ExpTypeid;
import dtool.ast.expressions.Expression;
import dtool.ast.expressions.InfixExpression;
import dtool.ast.expressions.Initializer;
import dtool.ast.expressions.InitializerArray;
import dtool.ast.expressions.InitializerExp;
import dtool.ast.expressions.InitializerStruct;
import dtool.ast.expressions.InitializerVoid;
import dtool.ast.expressions.PostfixExpression;
import dtool.ast.expressions.PrefixExpression;
import dtool.ast.expressions.Resolvable;
import dtool.ast.references.RefIdentifier;
import dtool.ast.references.Reference;
import dtool.util.ArrayView;

abstract class ExpressionConverterVisitor extends DeclarationConverterVisitor {
	
	@Override
	public boolean visit(FileExp node) {
		return endAdapt(
			new ExpLiteralImportedString(
				ExpressionConverter.convert(node.e1, convContext),
				DefinitionConverter.sourceRange(node)
			)
		);
	}
	
	@Override
	public boolean visit(TraitsExp node) {
		return endAdapt(
			new ExpTraits(
				node.ident.ident,
				DescentASTConverter.convertMany(node.args, ASTNeoNode.class, convContext),
				DefinitionConverter.sourceRange(node)
			)
		);
	}
	
	@Override
	public boolean visit(AssocArrayLiteralExp node) {
		Assert.isTrue(node.keys.size() == node.values.size());
		return endAdapt(
			new ExpLiteralMapArray(
				DescentASTConverter.convertMany(node.keys, Resolvable.class, convContext),
				DescentASTConverter.convertMany(node.values, Resolvable.class, convContext),
				DefinitionConverter.sourceRange(node)
			)
		);
	}

	@Override
	public boolean visit(CompileDeclaration node) {
		return endAdapt(
			new DeclarationStringMacro(
				ExpressionConverter.convert(node.exp, convContext),
				DefinitionConverter.sourceRange(node)
			)
		);
	}

	@Override
	public boolean visit(CompileExp node) {
		return endAdapt(
			new ExpStringMacro(
				ExpressionConverter.convert(node.e1, convContext),
				DefinitionConverter.sourceRange(node)	
			)
		);
	}

	@Override
	public boolean visit(CompileStatement node) {
		return endAdapt(
			new DeclarationStringMacro(
				ExpressionConverter.convert(node.exp, convContext),
				DefinitionConverter.sourceRange(node)
			)
		);
	}

	
	/* Initializers */
	
	@Override
	public boolean visit(ArrayInitializer element) {
		return endAdapt(
			new InitializerArray(
				ExpressionConverter.convertMany(element.index, convContext),
				DescentASTConverter.convertMany(element.value, Initializer.class, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}

	@Override
	public boolean visit(ExpInitializer element) {
		return endAdapt(
			new InitializerExp(
				ExpressionConverter.convert(element.exp, convContext),
				DefinitionConverter.sourceRange(element, !(element.exp instanceof ErrorExp))
			)
		);
	}

	@Override
	public boolean visit(StructInitializer element) {
		RefIdentifier[] indices = new RefIdentifier[element.field.size()];
		for(int i = 0; i < element.field.size(); ++i) {
			IdentifierExp id = element.field.get(i);
			ExpReference expref = (ExpReference) DescentASTConverter.convertElem(id, convContext);
			if(expref == null) {
				indices[i] = null;
			} else {
				indices[i] = (RefIdentifier) expref.ref;
				indices[i].parent = null; //reset parent
			}
		}
		
		return endAdapt(
			new InitializerStruct(
				ArrayView.create(indices),
				DescentASTConverter.convertMany(element.value, Initializer.class, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}

	@Override
	public boolean visit(VoidInitializer element) {
		return endAdapt(new InitializerVoid(DefinitionConverter.sourceRange(element)));
	}

	
	/* ===================== Special ===================== */
	@Override
	public boolean visit(ArrayExp elem) {
		Resolvable array = ExpressionConverter.convert(elem.e1, convContext);
		ArrayView<Resolvable> args = ExpressionConverter.convertMany(elem.arguments, convContext);
		return endAdapt(new ExpArrayIndex(array, args, DefinitionConverter.sourceRange(elem)));
	}
	
	@Override
	public boolean visit(ArrayLengthExp element) {
		return endAdapt(new ExpArrayLength(DefinitionConverter.sourceRange(element)));
	}
	
	@Override
	public boolean visit(ArrayLiteralExp element) {
		return endAdapt(ExpressionConverter.createExpArrayLiteral(element, convContext));
	}
	
	@Override
	public boolean visit(AssertExp elem) {
		Expression exp = ExpressionConverter.convert(elem.e1, convContext);
		Expression msg = ExpressionConverter.convert(elem.msg, convContext);
		return endAdapt(new ExpAssert(exp, msg, DefinitionConverter.sourceRange(elem)));
	}
	
	@Override
	public boolean visit(CallExp elem) {
		return endAdapt(ExpressionConverter.createExpCall(elem, convContext));
	}
	
	@Override
	public boolean visit(CastExp elem) {
		Expression exp = ExpressionConverter.convert(elem.sourceE1, convContext);
		Reference type = ReferenceConverter.convertType(elem.sourceTo, convContext);
		return endAdapt(new ExpCast(exp, type, DefinitionConverter.sourceRange(elem)));
	}
	
	@Override
	public boolean visit(CondExp elem) {
		Resolvable predExp = ExpressionConverter.convert(elem.econd, convContext); 
		Resolvable trueExp = ExpressionConverter.convert(elem.e1, convContext);
		Resolvable falseExp = ExpressionConverter.convert(elem.e2, convContext); 
		
		return endAdapt(new ExpCond(predExp, trueExp, falseExp, DefinitionConverter.sourceRange(elem)));
	}
	
	@Override
	public boolean visit(DeleteExp element) {
		return endAdapt(
			new ExpDelete(
				ExpressionConverter.convert(element.e1, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(DollarExp element) {
		return endAdapt(new ExpDollar(DefinitionConverter.sourceRange(element)));
	}
	
	@Override
	public boolean visit(FileInitExp element) {
		return endAdapt(
			new ExpDefaultInit(
				ExpDefaultInit.DefaultInit.FILE,
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(LineInitExp element) {
		return endAdapt(
			new ExpDefaultInit(
				ExpDefaultInit.DefaultInit.LINE,
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(FuncExp element) {
		TypeFunction elemTypeFunc = (TypeFunction) element.fd.type;
		
		return endAdapt(
			new ExpLiteralFunc(
				ReferenceConverter.convertType(elemTypeFunc.next, convContext),
				DescentASTConverter.convertMany(elemTypeFunc.parameters, IFunctionParameter.class, convContext),
				DefinitionConverter.convertVarArgs(elemTypeFunc.varargs),
				StatementConverterVisitor.convertStatement(element.fd.frequire, convContext),
				StatementConverterVisitor.convertStatement(element.fd.fbody, convContext),
				StatementConverterVisitor.convertStatement(element.fd.fensure, convContext),
				DefinitionConverter.sourceRange(element)
			) 
		);
	}
	
	@Override
	public boolean visit(IdentifierExp element) {
		return endAdapt(ReferenceConverter.createExpReference(element));
	}
	
	@Override
	public boolean visit(IsExp element) {
		return endAdapt(
			new ExpIftype(
				ReferenceConverter.convertType(element.targ, convContext),
				element.tok,
				ReferenceConverter.convertType(element.tspec, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(IntegerExp element) {
		if (((TypeBasic) element.type).ty == TY.Tbool) {
			return endAdapt(
				new ExpLiteralBool(
					element.value.intValue() != 0,
					DefinitionConverter.sourceRange(element)
				)
			);
		} else {
			return endAdapt(
				new ExpLiteralInteger(
					element.value != null ? element.value.bigIntegerValue() : null,
					DefinitionConverter.sourceRange(element, !(element instanceof ErrorExp))
				)
			);
		}
	}
	
	@Override
	public boolean visit(NewAnonClassExp element) {
		return endAdapt(
			new ExpLiteralNewAnonClass(
				ExpressionConverter.convertMany(element.newargs, convContext),
				ExpressionConverter.convertMany(element.arguments, convContext),
				DescentASTConverter.convertMany(element.cd.sourceBaseclasses, BaseClass.class, convContext),
				DescentASTConverter.convertMany(element.cd.members, ASTNeoNode.class, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(NewExp element) {
		Reference type = element.newtype == null ? 
				new Reference.InvalidSyntaxReference() : 
				ReferenceConverter.convertType(element.newtype, convContext);
		return endAdapt(
			new ExpNew(
				ExpressionConverter.convertMany(element.newargs, convContext),
				type,
				ExpressionConverter.convertMany(element.arguments, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(NullExp element) {
		return endAdapt(new ExpLiteralNull(DefinitionConverter.sourceRange(element)));
	}
	
	@Override
	public boolean visit(RealExp element) {
		double doubleValue = element.value != null ? element.value.doubleValue() : Double.NaN;
		return endAdapt(new ExpLiteralReal(doubleValue, DefinitionConverter.sourceRange(element)));
	}
	
	@Override
	public boolean visit(ScopeExp element) {
		return endAdapt(ReferenceConverter.createExpReference(element, convContext));
	}
	
	@Override
	public boolean visit(SliceExp element) {
		return endAdapt(
			new ExpSlice(
				ExpressionConverter.convert(element.e1, convContext),
				ExpressionConverter.convert(element.lwr, convContext),
				ExpressionConverter.convert(element.upr, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(StringExp element) {
// TODO: AST CONV: deal with elem.allStringExps
//		this.strings = new char[elem.strings.size()][];
//		for (int i = 0; i < elem.strings.size(); i++) {
//			this.strings[i] = elem.strings.get(i).string;
//		}
		return endAdapt(new ExpLiteralString(new String(element.string), DefinitionConverter.sourceRange(element)));
	}
	

	@Override
	public boolean visit(SuperExp element) {
		return endAdapt(new ExpSuper(DefinitionConverter.sourceRange(element)));
	}
	
	@Override
	public boolean visit(ThisExp element) {
		return endAdapt(new ExpThis(DefinitionConverter.sourceRange(element)));
	}
	
	@Override
	public boolean visit(TypeidExp element) {
		assertTrue(element.typeidType != null || element.argumentExp__DDT_ADDITION != null);
		return endAdapt(
			element.typeidType != null
			? new ExpTypeid(
				ReferenceConverter.convertType(element.typeidType, convContext),
				DefinitionConverter.sourceRange(element)
			)
			: new ExpTypeid(
				ExpressionConverter.convert(element.argumentExp__DDT_ADDITION, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}	

	/* ===================== Unary ===================== */
	

	@Override
	public boolean visit(UnaExp element) {
		return assertFailABSTRACT_NODE();
	}
	
	@Override
	public boolean visit(AddrExp element) {
		return endAdapt(
			new PrefixExpression(
				(Resolvable) DescentASTConverter.convertElem(element.e1, convContext),
				PrefixExpression.Type.ADDRESS,
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(ComExp element) {
		return endAdapt(
			new PrefixExpression(
				(Resolvable) DescentASTConverter.convertElem(element.e1, convContext),
				PrefixExpression.Type.INVERT,
				DefinitionConverter.sourceRange(element)
			)
		);
	}	
	
	@Override
	public boolean visit(NegExp element) {
		return endAdapt(
			new PrefixExpression(
				(Resolvable) DescentASTConverter.convertElem(element.e1, convContext),
				PrefixExpression.Type.NEGATIVE,
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(NotExp element) {
		return endAdapt(
			new PrefixExpression(
				(Resolvable) DescentASTConverter.convertElem(element.e1, convContext),
				PrefixExpression.Type.NOT,
				DefinitionConverter.sourceRange(element)
			)
		);
	}	

	
	@Override
	public boolean visit(IndexExp node) {
		return assertFailFAKENODE();
	}

	@Override
	public boolean visit(PostExp node) {
		int type = 0;
		switch (node.op) {
		case TOKplusplus: type = PostfixExpression.Type.POST_INCREMENT; break;
		case TOKminusminus: type = PostfixExpression.Type.POST_DECREMENT; break;
		default: Assert.fail();
		}
		
		return endAdapt(
			new PostfixExpression(
				(Resolvable) DescentASTConverter.convertElem(node.e1, convContext),
				type,
				DefinitionConverter.sourceRange(node)
			)
		);
	}

	@Override
	public boolean visit(PtrExp element) {
		return endAdapt(
			new PrefixExpression(
				(Resolvable) DescentASTConverter.convertElem(element.e1, convContext),
				PrefixExpression.Type.POINTER,
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(UAddExp element) {
		return endAdapt(
			new PrefixExpression(
				(Resolvable) DescentASTConverter.convertElem(element.e1, convContext),
				PrefixExpression.Type.POSITIVE,
				DefinitionConverter.sourceRange(element)
			)
		);
	}	

	/* ===================== Binary ===================== */
	
	@Override
	public boolean visit(BinExp element) {
		Assert.fail("Error visited abstract class."); return false;
	}

	@Override
	public boolean visit(AddAssignExp element) {
		Resolvable exp1 = (Resolvable) DescentASTConverter.convertElem(element.e1, convContext);
		SourceRange sourceRange = DefinitionConverter.sourceRange(element);
		Resolvable newelem = element.isPreIncrement
				? new PrefixExpression(exp1, PrefixExpression.Type.PRE_INCREMENT, sourceRange)
				: new InfixExpression(
					exp1,
					InfixExpression.Type.ADD_ASSIGN,
					(Resolvable) DescentASTConverter.convertElem(element.e2, convContext),
					sourceRange
				);
		return endAdapt(newelem);
	}
	

	@Override
	public boolean visit(MinAssignExp element) {
		Resolvable exp1 = (Resolvable) DescentASTConverter.convertElem(element.e1, convContext);
		SourceRange sourceRange = DefinitionConverter.sourceRange(element);
		Resolvable newelem = element.isPreDecrement
				? new PrefixExpression(exp1, PrefixExpression.Type.PRE_DECREMENT, sourceRange)
				: new InfixExpression(
					exp1, 
					InfixExpression.Type.MIN_ASSIGN,
					(Resolvable) DescentASTConverter.convertElem(element.e2, convContext),
					sourceRange
				);
		return endAdapt(newelem);
	}
	
	
	@Override
	public boolean visit(AddExp element) {
		return endAdapt(
			new InfixExpression(
				(Resolvable) DescentASTConverter.convertElem(element.e1, convContext),
				InfixExpression.Type.ADD,
				(Resolvable) DescentASTConverter.convertElem(element.e2, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(AndAndExp element) {
		return endAdapt(
			new InfixExpression(
				(Resolvable) DescentASTConverter.convertElem(element.e1, convContext),
				InfixExpression.Type.AND_AND,
				(Resolvable) DescentASTConverter.convertElem(element.e2, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(AndAssignExp element) {
		return endAdapt(
			new InfixExpression(
				(Resolvable) DescentASTConverter.convertElem(element.e1, convContext),
				InfixExpression.Type.AND_ASSIGN,
				(Resolvable) DescentASTConverter.convertElem(element.e2, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(AndExp element) {
		return endAdapt(
			new InfixExpression(
				(Resolvable) DescentASTConverter.convertElem(element.e1, convContext),
				InfixExpression.Type.AND,
				(Resolvable) DescentASTConverter.convertElem(element.e2, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(AssignExp element) {
		return endAdapt(
			new InfixExpression(
				(Resolvable) DescentASTConverter.convertElem(element.e1, convContext),
				InfixExpression.Type.ASSIGN,
				(Resolvable) DescentASTConverter.convertElem(element.e2, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(CatAssignExp element) {
		return endAdapt(
			new InfixExpression(
				(Resolvable) DescentASTConverter.convertElem(element.e1, convContext),
				InfixExpression.Type.CAT_ASSIGN,
				(Resolvable) DescentASTConverter.convertElem(element.e2, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(CatExp element) {
		return endAdapt(
			new InfixExpression(
				(Resolvable) DescentASTConverter.convertElem(element.e1, convContext),
				InfixExpression.Type.CAT,
				(Resolvable) DescentASTConverter.convertElem(element.e2, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(CmpExp element) {
		return endAdapt(
			new InfixExpression(
				(Resolvable) DescentASTConverter.convertElem(element.e1, convContext),
				InfixExpression.Type.CMP,
				(Resolvable) DescentASTConverter.convertElem(element.e2, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(CommaExp element) {
		return endAdapt(
			new InfixExpression(
				(Resolvable) DescentASTConverter.convertElem(element.e1, convContext),
				InfixExpression.Type.COMMA,
				(Resolvable) DescentASTConverter.convertElem(element.e2, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(DivAssignExp element) {
		return endAdapt(
			new InfixExpression(
				(Resolvable) DescentASTConverter.convertElem(element.e1, convContext),
				InfixExpression.Type.DIV_ASSIGN,
				(Resolvable) DescentASTConverter.convertElem(element.e2, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(DivExp element) {
		return endAdapt(
			new InfixExpression(
				(Resolvable) DescentASTConverter.convertElem(element.e1, convContext),
				InfixExpression.Type.DIV,
				(Resolvable) DescentASTConverter.convertElem(element.e2, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(EqualExp element) {
		return endAdapt(
			new InfixExpression(
				(Resolvable) DescentASTConverter.convertElem(element.e1, convContext),
				InfixExpression.Type.EQUAL,
				(Resolvable) DescentASTConverter.convertElem(element.e2, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(IdentityExp element) {
		Assert.isTrue(element.op == TOK.TOKis || element.op == TOK.TOKnotis);
		
		return endAdapt(
			new InfixExpression(
				(Resolvable) DescentASTConverter.convertElem(element.e1, convContext),
				element.op == TOK.TOKis ? InfixExpression.Type.IDENTITY : InfixExpression.Type.NOT_IDENTITY,
				(Resolvable) DescentASTConverter.convertElem(element.e2, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(InExp element) {
		return endAdapt(
			new InfixExpression(
				(Resolvable) DescentASTConverter.convertElem(element.e1, convContext),
				InfixExpression.Type.IN,
				(Resolvable) DescentASTConverter.convertElem(element.e2, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(MinExp element) {
		return endAdapt(
			new InfixExpression(
				(Resolvable) DescentASTConverter.convertElem(element.e1, convContext),
				InfixExpression.Type.MIN,
				(Resolvable) DescentASTConverter.convertElem(element.e2, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(ModAssignExp element) {
		return endAdapt(
			new InfixExpression(
				(Resolvable) DescentASTConverter.convertElem(element.e1, convContext),
				InfixExpression.Type.MOD_ASSIGN,
				(Resolvable) DescentASTConverter.convertElem(element.e2, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(ModExp element) {
		return endAdapt(
			new InfixExpression(
				(Resolvable) DescentASTConverter.convertElem(element.e1, convContext),
				InfixExpression.Type.MOD,
				(Resolvable) DescentASTConverter.convertElem(element.e2, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(MulAssignExp element) {
		return endAdapt(
			new InfixExpression(
				(Resolvable) DescentASTConverter.convertElem(element.e1, convContext),
				InfixExpression.Type.MUL_ASSIGN,
				(Resolvable) DescentASTConverter.convertElem(element.e2, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(MulExp element) {
		return endAdapt(
			new InfixExpression(
				(Resolvable) DescentASTConverter.convertElem(element.e1, convContext),
				InfixExpression.Type.MUL,
				(Resolvable) DescentASTConverter.convertElem(element.e2, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(OrAssignExp element) {
		return endAdapt(
			new InfixExpression(
				(Resolvable) DescentASTConverter.convertElem(element.e1, convContext),
				InfixExpression.Type.OR_ASSIGN,
				(Resolvable) DescentASTConverter.convertElem(element.e2, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(OrExp element) {
		return endAdapt(
			new InfixExpression(
				(Resolvable) DescentASTConverter.convertElem(element.e1, convContext),
				InfixExpression.Type.OR,
				(Resolvable) DescentASTConverter.convertElem(element.e2, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(OrOrExp element) {
		return endAdapt(
			new InfixExpression(
				(Resolvable) DescentASTConverter.convertElem(element.e1, convContext),
				InfixExpression.Type.OR_OR,
				(Resolvable) DescentASTConverter.convertElem(element.e2, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(ShlAssignExp element) {
		return endAdapt(
			new InfixExpression(
				(Resolvable) DescentASTConverter.convertElem(element.e1, convContext),
				InfixExpression.Type.SHIFT_LEFT_ASSIGN,
				(Resolvable) DescentASTConverter.convertElem(element.e2, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(ShlExp element) {
		return endAdapt(
			new InfixExpression(
				(Resolvable) DescentASTConverter.convertElem(element.e1, convContext),
				InfixExpression.Type.SHIFT_LEFT,
				(Resolvable) DescentASTConverter.convertElem(element.e2, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(ShrAssignExp element) {
		return endAdapt(
			new InfixExpression(
				(Resolvable) DescentASTConverter.convertElem(element.e1, convContext),
				InfixExpression.Type.SHIFT_RIGHT_ASSIGN,
				(Resolvable) DescentASTConverter.convertElem(element.e2, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(ShrExp element) {
		return endAdapt(
			new InfixExpression(
				(Resolvable) DescentASTConverter.convertElem(element.e1, convContext),
				InfixExpression.Type.SHIFT_RIGHT,
				(Resolvable) DescentASTConverter.convertElem(element.e2, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(UshrAssignExp element) {
		return endAdapt(
			new InfixExpression(
				(Resolvable) DescentASTConverter.convertElem(element.e1, convContext),
				InfixExpression.Type.UNSIGNED_SHIFT_RIGHT_ASSIGN,
				(Resolvable) DescentASTConverter.convertElem(element.e2, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(UshrExp element) {
		return endAdapt(
			new InfixExpression(
				(Resolvable) DescentASTConverter.convertElem(element.e1, convContext),
				InfixExpression.Type.UNSIGNED_SHIFT_RIGHT,
				(Resolvable) DescentASTConverter.convertElem(element.e2, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(XorAssignExp element) {
		return endAdapt(
			new InfixExpression(
				(Resolvable) DescentASTConverter.convertElem(element.e1, convContext),
				InfixExpression.Type.XOR_ASSIGN,
				(Resolvable) DescentASTConverter.convertElem(element.e2, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(XorExp element) {
		return endAdapt(
			new InfixExpression(
				(Resolvable) DescentASTConverter.convertElem(element.e1, convContext),
				InfixExpression.Type.XOR,
				(Resolvable) DescentASTConverter.convertElem(element.e2, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}
}
