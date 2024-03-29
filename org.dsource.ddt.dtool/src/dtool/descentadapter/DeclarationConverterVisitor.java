package dtool.descentadapter;


import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.core.CoreUtil.array;
import melnorme.utilbox.core.Assert;
import descent.internal.compiler.parser.AggregateDeclaration;
import descent.internal.compiler.parser.AnonDeclaration;
import descent.internal.compiler.parser.Argument;
import descent.internal.compiler.parser.AttribDeclaration;
import descent.internal.compiler.parser.ClassDeclaration;
import descent.internal.compiler.parser.DebugCondition;
import descent.internal.compiler.parser.DebugSymbol;
import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.EnumDeclaration;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.IftypeCondition;
import descent.internal.compiler.parser.Import;
import descent.internal.compiler.parser.InterfaceDeclaration;
import descent.internal.compiler.parser.Modifier;
import descent.internal.compiler.parser.StaticIfCondition;
import descent.internal.compiler.parser.StructDeclaration;
import descent.internal.compiler.parser.TemplateAliasParameter;
import descent.internal.compiler.parser.TemplateThisParameter;
import descent.internal.compiler.parser.TemplateTupleParameter;
import descent.internal.compiler.parser.TemplateTypeParameter;
import descent.internal.compiler.parser.TemplateValueParameter;
import descent.internal.compiler.parser.UnionDeclaration;
import descent.internal.compiler.parser.Version;
import descent.internal.compiler.parser.VersionCondition;
import descent.internal.compiler.parser.VersionSymbol;
import dtool.DToolBundle;
import dtool.ast.ASTNeoNode;
import dtool.ast.NodeList;
import dtool.ast.SourceRange;
import dtool.ast.declarations.DeclarationAliasThis;
import dtool.ast.declarations.DeclarationAlign;
import dtool.ast.declarations.DeclarationAnonMember;
import dtool.ast.declarations.DeclarationConditionalDefinition;
import dtool.ast.declarations.DeclarationImport;
import dtool.ast.declarations.DeclarationImport.ImportFragment;
import dtool.ast.declarations.DeclarationInvariant;
import dtool.ast.declarations.DeclarationLinkage;
import dtool.ast.declarations.DeclarationPragma;
import dtool.ast.declarations.DeclarationProtection;
import dtool.ast.declarations.DeclarationStaticAssert;
import dtool.ast.declarations.DeclarationStorageClass;
import dtool.ast.declarations.DeclarationUnitTest;
import dtool.ast.declarations.ImportAliasing;
import dtool.ast.declarations.ImportContent;
import dtool.ast.declarations.ImportSelective;
import dtool.ast.declarations.ImportSelective.ImportSelectiveAlias;
import dtool.ast.declarations.ImportStatic;
import dtool.ast.declarations.InvalidSyntaxDeclaration;
import dtool.ast.definitions.BaseClass;
import dtool.ast.definitions.DefModifier;
import dtool.ast.definitions.DefUnit.DefUnitDataTuple;
import dtool.ast.definitions.DefinitionAlias;
import dtool.ast.definitions.DefinitionClass;
import dtool.ast.definitions.DefinitionEnum;
import dtool.ast.definitions.DefinitionInterface;
import dtool.ast.definitions.DefinitionPostBlit;
import dtool.ast.definitions.DefinitionStruct;
import dtool.ast.definitions.DefinitionTemplate;
import dtool.ast.definitions.DefinitionTypedef;
import dtool.ast.definitions.DefinitionUnion;
import dtool.ast.definitions.DefinitionVariable;
import dtool.ast.definitions.EnumContainer;
import dtool.ast.definitions.EnumMember;
import dtool.ast.definitions.Symbol;
import dtool.ast.definitions.TemplateParamAlias;
import dtool.ast.definitions.TemplateParamTuple;
import dtool.ast.definitions.TemplateParamType;
import dtool.ast.definitions.TemplateParamValue;
import dtool.ast.definitions.TemplateParameter;
import dtool.ast.expressions.Initializer;
import dtool.ast.references.RefIdentifier;
import dtool.ast.references.RefImportSelection;
import dtool.ast.references.RefModule;
import dtool.ast.references.Reference;
import dtool.ast.statements.BlockStatement;
import dtool.ast.statements.IStatement;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
import dtool.util.ArrayView;

/**
 * Converts from DMD's AST to a nicer AST ("Neo AST")
 */
public abstract class DeclarationConverterVisitor extends RefConverterVisitor {

	@Override
	public boolean visit(Version node) {
		return assertFailHandledDirectly();
	}
	
	@Override
	public boolean visit(DebugSymbol elem) {
		Symbol identifier = elem.ident != null ? 
				DefinitionConverter.convertId(elem.ident) : 
				new Symbol(new String(elem.version.value), DefinitionConverter.sourceRange(elem.version));
		
		return endAdapt(
			new DeclarationConditionalDefinition(
				identifier,
				DeclarationConditionalDefinition.Type.DEBUG,
				DefinitionConverter.sourceRange(elem)
			)
		);
	}
	
	@Override
	public boolean visit(VersionSymbol elem) {
		Symbol identifier = elem.ident != null ? 
				DefinitionConverter.convertId(elem.ident) : 
				new Symbol(new String(elem.version.value), DefinitionConverter.sourceRange(elem.version));;
		
		return endAdapt(
			new DeclarationConditionalDefinition(
				identifier,
				DeclarationConditionalDefinition.Type.VERSION,
				DefinitionConverter.sourceRange(elem)
			)
		);
	}
	
	@Override
	public boolean visit(StaticIfCondition node) {
		return assertFailHandledDirectly();
	}
	
	@Override
	public boolean visit(DebugCondition node) {
		return assertFailHandledDirectly();
	}
	
	@Override
	public boolean visit(VersionCondition node) {
		return assertFailHandledDirectly();
	}
	
	@Override
	public boolean visit(IftypeCondition node) {
		return assertFailHandledDirectly();
	}

	@Override
	public boolean visit(AnonDeclaration node) {
		NodeList body = DeclarationConverter.createNodeList(node.decl, convContext);
		return endAdapt(new DeclarationAnonMember(body, DefinitionConverter.sourceRange(node)));
	}

	@Override
	public boolean visit(AttribDeclaration node) {
		return assertFailABSTRACT_NODE();
	}
	
	@Override
	public boolean visit(Modifier node) {
		return endAdapt(new DefModifier(node.tok, DefinitionConverter.sourceRange(node)));
	}

	/*  =======================================================  */

	@Override
	public boolean visit(descent.internal.compiler.parser.Module elem) {
		Assert.fail(); return false;
	}
	
	@Override
	public boolean visit(descent.internal.compiler.parser.BaseClass elem) {
		return endAdapt(
			new BaseClass(
				elem.protection,
				ReferenceConverter.convertType(elem.type, convContext),
				DefinitionConverter.sourceRange(elem.hasNoSourceRangeInfo() ? elem.type : elem)
			)
		);
	}
	
	@Override
	public boolean visit(descent.internal.compiler.parser.TemplateParameter elem) {
		Assert.fail(); return false; // abstract class
	}

	/*  ---------  Decls  --------  */
	

	@Override
	public boolean visit(descent.internal.compiler.parser.AlignDeclaration elem) {
		DeclarationConverter.doSetParent(elem, elem.decl);
		NodeList body = DeclarationConverter.createNodeList(elem.decl, convContext);
		return endAdapt(new DeclarationAlign(elem.salign, body, DefinitionConverter.sourceRange(elem)));
	}

	@Override
	public boolean visit(descent.internal.compiler.parser.ConditionalDeclaration elem) {
		return endAdapt(DeclarationConverter.convert(elem, convContext));
	}
	
	// Helper function for the ImportSelective conversion.
	private static ASTNeoNode createSelectionFragment(IdentifierExp name, IdentifierExp alias, ImportSelective impSel) {
		assertTrue(!(name.ident.length == 0));
		RefImportSelection impSelection = new RefImportSelection(
			new String(name.ident),
			impSel,
			DefinitionConverter.sourceRange(name)
		);
		
		if(alias == null) {
			return impSelection; //implements IImportSelectiveFragment
		}
		else {
			DefUnitDataTuple dudt = new DefUnitDataTuple(
				DefinitionConverter.sourceRange(alias),
				DefinitionConverter.convertIdToken(alias),
				null
			);
			
			return new ImportSelectiveAlias(
				dudt, impSelection,
				new SourceRange(alias.start, name.getEndPos() - alias.start)
			);
		}
	}
	
	@Override
	public boolean visit(descent.internal.compiler.parser.Import elem) {
		int importsNum = 1;
		Import imprt = elem;
		while(imprt.next != null) {
			imprt = imprt.next;
			importsNum++;
		}
		
		// Selective import are at the end		
		ImportFragment[] imports = new ImportFragment[importsNum];
		imprt = elem;
		for(int i = 0; i < importsNum; i++, imprt = imprt.next) {
			
			ImportFragment imprtFragment = null;
			
			// Storing the packages.
			String[] packages;
			SourceRange sr;
			if (imprt.packages == null) {
				packages = new String[0];
				sr = DefinitionConverter.sourceRange(imprt.id);
			} else {
				packages = new String[imprt.packages.size()];
				int idx = 0;
				for (IdentifierExp ie : imprt.packages) {
					packages[idx++] = new String(ie.ident);
				}
				int startPos = imprt.packages.get(0).getStartPos();
				sr = new SourceRange(startPos, imprt.id.getEndPos() - startPos);
			}
			
			if(elem.isstatic) {
				imprtFragment = new ImportStatic(
					new RefModule(ArrayView.create(packages), new String(imprt.id.ident), sr),
					DefinitionConverter.sourceRange(imprt)
				);
				//Ignore FQN aliasing for now.
				//Assert.isTrue(imprt.alias == null);
			} else if(imprt.aliasId != null) {
				SourceRange entireRange = DefinitionConverter.sourceRange(imprt);
				DefUnitDataTuple dudt = new DefUnitDataTuple(
					new SourceRange(imprt.aliasId.start, entireRange.getEndPos() - imprt.aliasId.start),
					DefinitionConverter.convertIdToken(imprt.aliasId), null
				);
				imprtFragment = new ImportAliasing(
					dudt,
					new RefModule(ArrayView.create(packages), new String(imprt.id.ident), sr),
					entireRange
				);
			} else if(imprt.names != null) {
				assertTrue(imprt.names.size() == imprt.aliases.size());
				assertTrue(imprt.names.size() > 0 );
				ASTNeoNode[] impSelFrags = new ASTNeoNode[imprt.names.size()];
				for(int selFragment = 0; selFragment < imprt.names.size(); selFragment++) {
					impSelFrags[selFragment] = createSelectionFragment(
						imprt.names.get(selFragment),
						imprt.aliases.get(selFragment),
						(ImportSelective) null
					);
				}
				
				imprtFragment = new ImportSelective(
					new RefModule(ArrayView.create(packages), new String(imprt.id.ident), sr),
					ArrayView.create(impSelFrags), DefinitionConverter.sourceRange(imprt)
				);
			} else {
				imprtFragment = new ImportContent(
					new RefModule(ArrayView.create(packages), new String(imprt.id.ident), sr),
					DefinitionConverter.sourceRange(imprt)
				);
			}
			
			imports[i] = imprtFragment;
		}
		assertTrue(imprt == null);
		
		boolean isTransitive = false; //isTransitive is adapted in post conversion;
		return endAdapt(new DeclarationImport(
				ArrayView.create(imports), elem.isstatic, isTransitive, DefinitionConverter.sourceRange(elem)
				)
		);
	}
	
	@Override
	public boolean visit(descent.internal.compiler.parser.InvariantDeclaration elem) {
		return endAdapt(
			new DeclarationInvariant(
				(BlockStatement) StatementConverterVisitor.convertStatement(elem.fbody, convContext),
				DefinitionConverter.sourceRange(elem)
			)
		);
	}

	@Override
	public boolean visit(descent.internal.compiler.parser.LinkDeclaration elem) {
		DeclarationConverter.doSetParent(elem, elem.decl);
		NodeList body = DeclarationConverter.createNodeList(elem.decl, convContext);
		return endAdapt(new DeclarationLinkage(elem.linkage, body, DefinitionConverter.sourceRange(elem)));
	}

	@Override
	public boolean visit(descent.internal.compiler.parser.PragmaDeclaration elem) {
		DeclarationConverter.doSetParent(elem, elem.decl);
		NodeList body = DeclarationConverter.createNodeList(elem.decl, convContext);
		return endAdapt(
			new DeclarationPragma(
				DefinitionConverter.convertId(elem.ident),
				ExpressionConverter.convertMany(elem.args, convContext),
				body,
				DefinitionConverter.sourceRange(elem)
			)
		);
	}

	@Override
	public boolean visit(descent.internal.compiler.parser.ProtDeclaration elem) {
		DeclarationConverter.doSetParent(elem, elem.decl);
		NodeList body = DeclarationConverter.createNodeList(elem.decl, convContext);
		return endAdapt(new DeclarationProtection(
				elem.protection, elem.modifier, body,
				DefinitionConverter.sourceRange(elem)
		));
	}

	@Override
	public void endVisit(descent.internal.compiler.parser.ProtDeclaration elem) {
		DeclarationProtection scDecl = (DeclarationProtection) ret;
		scDecl.processEffectiveModifiers();
	}

	@Override
	public boolean visit(descent.internal.compiler.parser.StorageClassDeclaration elem) {
		DeclarationConverter.doSetParent(elem, elem.decl);
		NodeList body = DeclarationConverter.createNodeList(elem.decl, convContext);
		return endAdapt(new DeclarationStorageClass(elem.stc, body, DefinitionConverter.sourceRange(elem)));
	}
	
	@Override
	public void endVisit(descent.internal.compiler.parser.StorageClassDeclaration elem) {
		DeclarationStorageClass scDecl = (DeclarationStorageClass) ret;
		scDecl.processEffectiveModifiers();
	}

	@Override
	public boolean visit(descent.internal.compiler.parser.UnitTestDeclaration elem) {
		IStatement stmt = StatementConverterVisitor.convertStatement(elem.fbody, convContext);
		if (stmt instanceof BlockStatement) {
			return endAdapt(new DeclarationUnitTest((BlockStatement) stmt, DefinitionConverter.sourceRange(elem)));
		} else {
			// Syntax errors
			IStatement[] stmts = (stmt == null) ? new IStatement[0] : new IStatement[] { stmt };
			return endAdapt(
				new DeclarationUnitTest(
					new BlockStatement(ArrayView.create(stmts), false, DefinitionConverter.sourceRange(elem)),
					DefinitionConverter.sourceRange(elem)
				)
			);
		}
	}
	
	
	@Override
	public boolean visit(descent.internal.compiler.parser.StaticAssert elem) {
		return endAdapt(
			new DeclarationStaticAssert(
				ExpressionConverter.convert(elem.exp, convContext),
				ExpressionConverter.convert(elem.msg, convContext),
				DefinitionConverter.sourceRange(elem)
			)
		);
	}

	@Override
	public boolean visit(descent.internal.compiler.parser.StaticIfDeclaration elem) {
		return endAdapt(DeclarationConverter.convert(elem, convContext));
	}
	
	@Override
	public boolean visit(descent.internal.compiler.parser.AliasThis elem) {
		return endAdapt(
			new DeclarationAliasThis(
				new RefIdentifier(new String(elem.ident.ident)),
				DefinitionConverter.sourceRange(elem)
			)
		);
	}

	
	
	/*  ---------  DEFINITIONS  --------  */

	@Override
	public final boolean visit(descent.internal.compiler.parser.AliasDeclaration elem) {
		return endAdapt(
			new DefinitionAlias(
				DefinitionConverter.convertDsymbol(elem, convContext), elem.prot(),
				(Reference) DescentASTConverter.convertElem(elem.type, convContext)
			)
		);
	}	

	
	@Override
	public boolean visit(descent.internal.compiler.parser.TemplateDeclaration elem) {
		ArrayView<TemplateParameter> tplParams = 
				DescentASTConverter.convertMany(elem.parameters, TemplateParameter.class, convContext);
		
		if(elem.wrapper && DToolBundle.UNIMPLEMENTED_FUNCTIONALITY) { 
			// This code is disabled until we can fix some ref resolver bugs
			assertTrue(elem.members.size() == 1);
			Dsymbol dsymbol = elem.members.get(0);
			if(dsymbol instanceof AggregateDeclaration) {
				AggregateDeclaration aggrDeclaration = (AggregateDeclaration) dsymbol;
				assertTrue(aggrDeclaration.templated);
				
				if(dsymbol instanceof ClassDeclaration) {
					return convertClass((ClassDeclaration) dsymbol, tplParams);
				} else if(dsymbol instanceof InterfaceDeclaration) {  // BUG HERE
					return convertInterface((InterfaceDeclaration) dsymbol, tplParams);
				} else if(dsymbol instanceof StructDeclaration) {
					return convertStruct((StructDeclaration) dsymbol, tplParams);
				} else if(dsymbol instanceof UnionDeclaration) {
					return convertUnion((UnionDeclaration) dsymbol, tplParams);
				}
			}
		}
		
		return endAdapt(
			new DefinitionTemplate(
				DefinitionConverter.convertDsymbol(elem, convContext),
				elem.prot(),
				DescentASTConverter.convertManyNoNulls(elem.members, ASTNeoNode.class, convContext),
				tplParams,
				elem.wrapper
			)
		);
	}
	
	@Override
	public boolean visit(descent.internal.compiler.parser.TemplateMixin elem) {
		return endAdapt(ReferenceConverter.convertMixinInstance(elem, convContext));
	}	
	
	@Override
	public boolean visit(descent.internal.compiler.parser.TypedefDeclaration elem) {
		return endAdapt(
			new DefinitionTypedef(
				DefinitionConverter.convertDsymbol(elem, convContext), elem.prot(),
				ReferenceConverter.convertType(elem.sourceBasetype, convContext),
				DescentASTConverter.convertElem(elem.init, Initializer.class, convContext)
			)
		);
	}	
	
	@Override
	public boolean visit(descent.internal.compiler.parser.VarDeclaration elem) {
		if(elem.ident == null) {
			return endAdapt(new InvalidSyntaxDeclaration(
				DefinitionConverter.sourceRange(elem), 
				ArrayView.create(array(
						ReferenceConverter.convertType(elem.type, convContext),
						DescentASTConverter.convertElem(elem.init, Initializer.class, convContext)
				))
			));
		}  else {
			return endAdapt(new DefinitionVariable(
				DefinitionConverter.convertDsymbol(elem, convContext), elem.prot(),
				ReferenceConverter.convertType(elem.type, convContext),
				DescentASTConverter.convertElem(elem.init, Initializer.class, convContext)
			));
		}
	}
	
	public static ASTNeoNode convertEnumDecl(EnumDeclaration elem, ASTConversionContext convContext) {
		if(elem.ident != null) {
			return new DefinitionEnum(
				DefinitionConverter.convertDsymbol(elem, convContext), elem.prot(),
				DescentASTConverter.convertMany(elem.members, EnumMember.class, convContext),
				ReferenceConverter.convertType(elem.memtype, convContext)
			);
		} else {
			return new EnumContainer(
				DescentASTConverter.convertMany(elem.members, EnumMember.class, convContext),
				ReferenceConverter.convertType(elem.memtype, convContext),
				DefinitionConverter.sourceRange(elem)
			);
		}
	}
	
	@Override
	public boolean visit(descent.internal.compiler.parser.EnumDeclaration elem) {
		return endAdapt(convertEnumDecl(elem, convContext));
	}
	
	@Override
	public boolean visit(descent.internal.compiler.parser.EnumMember elem) {
		return endAdapt(DefinitionConverter.createEnumMember(elem, convContext));
	}

	/* aggregates */
	
	
	@Override
	public boolean visit(descent.internal.compiler.parser.ClassDeclaration elem) {
		return convertClass(elem, null);
	}
	
	private boolean convertClass(descent.internal.compiler.parser.ClassDeclaration elem,
			ArrayView<TemplateParameter> tplParams) {
		return endAdapt(
			new DefinitionClass(
				DefinitionConverter.convertDsymbol(elem, convContext),
				elem.prot(),
				tplParams,
				DescentASTConverter.convertMany(elem.sourceBaseclasses, BaseClass.class, convContext),
				DescentASTConverter.convertMany(elem.members, ASTNeoNode.class, convContext)
			)
		);
	}
	
	@Override
	public boolean visit(descent.internal.compiler.parser.InterfaceDeclaration elem) {
		return convertInterface(elem, null);
	}
	
	private boolean convertInterface(descent.internal.compiler.parser.InterfaceDeclaration elem,
			ArrayView<TemplateParameter> tplParams) {
		return endAdapt(
			new DefinitionInterface(
				DefinitionConverter.convertDsymbol(elem, convContext),
				elem.prot(),
				tplParams,
				DescentASTConverter.convertMany(elem.sourceBaseclasses, BaseClass.class, convContext),
				DescentASTConverter.convertMany(elem.members, ASTNeoNode.class, convContext)
			)
		);
	}	
	
	@Override
	public boolean visit(descent.internal.compiler.parser.StructDeclaration elem) {
		return convertStruct(elem, null);
	}
	
	private boolean convertStruct(descent.internal.compiler.parser.StructDeclaration elem,
			ArrayView<TemplateParameter> tplParams) {
		return endAdapt(
			new DefinitionStruct(
				DefinitionConverter.convertDsymbol(elem, convContext),
				elem.prot(),
				tplParams,
				DescentASTConverter.convertMany(elem.members, ASTNeoNode.class, convContext)
			)
		);
	}
	
	@Override
	public boolean visit(descent.internal.compiler.parser.UnionDeclaration elem) {
		return convertUnion(elem, null);
	}
	
	private boolean convertUnion(descent.internal.compiler.parser.UnionDeclaration elem,
			ArrayView<TemplateParameter> tplParams) {
		return endAdapt(
			new DefinitionUnion(
				DefinitionConverter.convertDsymbol(elem, convContext),
				elem.prot(),
				tplParams,
				DescentASTConverter.convertMany(elem.members, ASTNeoNode.class, convContext)
			)
		);
	}
	
	
	/* --- func --- */

	@Override
	public boolean visit(descent.internal.compiler.parser.FuncDeclaration elem) {
		return endAdapt(DefinitionConverter.createDefinitionFunction(elem, convContext));
	}
	
	@Override
	public boolean visit(Argument elem) {
		return endAdapt(DefinitionConverter.convertFunctionParameter(elem, convContext));
	}
	
	@Override
	public boolean visit(descent.internal.compiler.parser.CtorDeclaration elem) {
		return endAdapt(DefinitionConverter.createDefinitionCtor(elem, convContext));
	}
	@Override
	public boolean visit(descent.internal.compiler.parser.PostBlitDeclaration elem) {
		return endAdapt(
			new DefinitionPostBlit(
				StatementConverterVisitor.convertStatement(elem.fbody, convContext),
				DefinitionConverter.sourceRange(elem)
			)
		);
	}
	@Override
	public boolean visit(descent.internal.compiler.parser.DtorDeclaration elem) {
		return endAdapt(DefinitionConverter.createDefinitionCtor(elem, convContext));
	}
	@Override
	public boolean visit(descent.internal.compiler.parser.StaticCtorDeclaration elem) {
		return endAdapt(DefinitionConverter.createDefinitionCtor(elem, convContext));
	}	
	@Override
	public boolean visit(descent.internal.compiler.parser.StaticDtorDeclaration elem) {
		return endAdapt(DefinitionConverter.createDefinitionCtor(elem, convContext));
	}

	@Override
	public boolean visit(descent.internal.compiler.parser.NewDeclaration elem) {
		return endAdapt(DefinitionConverter.createDefinitionCtor(elem, convContext));
	}
	@Override
	public boolean visit(descent.internal.compiler.parser.DeleteDeclaration elem) {
		return endAdapt(DefinitionConverter.createDefinitionCtor(elem, convContext));
	}

	/* ---- other ---- */
	

	@Override
	public boolean visit(TemplateAliasParameter elem) {
		DefUnitDataTuple dudt = new DefUnitDataTuple(
			DefinitionConverter.sourceRange(elem),
			DefinitionConverter.convertIdToken(elem.ident),
			null
		);
		return endAdapt(new TemplateParamAlias(dudt));
	}
	
	@Override
	public boolean visit(TemplateTupleParameter elem) {
		DefUnitDataTuple dudt = new DefUnitDataTuple(
			DefinitionConverter.sourceRange(elem),
			DefinitionConverter.convertIdToken(elem.ident),
			null
		);
		return endAdapt(new TemplateParamTuple(dudt));
	}
	
	@Override
	public boolean visit(TemplateTypeParameter elem) {
		DefUnitDataTuple dudt = new DefUnitDataTuple(
			DefinitionConverter.sourceRange(elem),
			DefinitionConverter.convertIdToken(elem.ident),
			null
		);
		return endAdapt(
			new TemplateParamType(
				dudt,
				ReferenceConverter.convertType(elem.specType, convContext),
				ReferenceConverter.convertType(elem.defaultType, convContext)
			)
		);
	}
	
	@Override
	public boolean visit(TemplateThisParameter elem) {
		// TODO: There was something to do with this...
		return visit((TemplateTypeParameter) elem);
	}
	
	@Override
	public boolean visit(TemplateValueParameter elem) {
		DefUnitDataTuple dudt = new DefUnitDataTuple(
			DefinitionConverter.sourceRange(elem),
			DefinitionConverter.convertIdToken(elem.ident),
			null
		);
		return endAdapt(
			new TemplateParamValue(
				dudt,
				ReferenceConverter.convertType(elem.valType, convContext),
				ExpressionConverter.convert(elem.specValue, convContext),
				ExpressionConverter.convert(elem.defaultValue, convContext)
			)
		);
	}

}
