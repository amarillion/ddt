package org.dsource.ddt.ide.core.model;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.List;

import melnorme.utilbox.core.CoreUtil;

import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.ast.parser.IModuleDeclaration;

import descent.internal.compiler.parser.ast.IASTNode;
import dtool.ast.definitions.Module;

public class DeeModuleDeclaration extends ModuleDeclaration implements IModuleDeclaration {

	public static interface EModelStatus {
		int OK = 0;
		int PARSER_INTERNAL_ERROR = 1;
		int PARSER_SYNTAX_ERRORS = 2;
	}
	
		public int status;
	
	public Module neoModule;
	public descent.internal.compiler.parser.Module dmdModule;
	
	
	public DeeModuleDeclaration(descent.internal.compiler.parser.Module dmdModule) {
		super(dmdModule.getLength());
		assertNotNull(dmdModule);
		this.dmdModule = dmdModule;
	}
	
	public int getParseStatus() {
		return status;
	}
	
	public void setNeoModule(Module neoModule) {
		this.neoModule = neoModule;
		assertTrue(neoModule.hasNoSourceRangeInfo() 
				|| (neoModule.getStartPos() == 0 && neoModule.getEndPos() == dmdModule.getEndPos()));
		CoreUtil.<List<Object>>blindCast(getStatements()).add(neoModule);
	}
	
	public IASTNode getEffectiveModuleNode() {
		return neoModule == null ? dmdModule : neoModule;
	}
}
