package dtool.ast.references;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;

import java.util.Collection;

import melnorme.utilbox.misc.StringUtil;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.EArcheType;
import dtool.ast.definitions.Module;
import dtool.refmodel.DefUnitSearch;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.PrefixDefUnitSearch;
import dtool.refmodel.pluginadapters.IModuleResolver;
import dtool.util.ArrayView;

/** 
 * A module reference (in import declarations only).
 */
public class RefModule extends NamedReference {
	
	public final ArrayView<String> packages;
	public final String module;
	
	public RefModule(ArrayView<String> packages, String module, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.packages = packages;
		this.module = module;
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			//TreeVisitor.acceptChildren(visitor, root);
			//TreeVisitor.acceptChildren(visitor, subent);
		}
		visitor.endVisit(this);	
	}
	
	@Override
	public Collection<DefUnit> findTargetDefUnits(IModuleResolver moduleResolver, boolean findOneOnly) {
		Module targetMod;
		try {
			targetMod = moduleResolver.findModule(packages.getInternalArray(), module);
		} catch (Exception e) {
			throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
		}
		return DefUnitSearch.wrapResult(targetMod);
	}
	
	public static class LiteModuleDummy extends DefUnit {
		
		public LiteModuleDummy(String defname) {
			super(defname);
		}
		
		@Override
		public EArcheType getArcheType() {
			return EArcheType.Module;
		}
		
		@Override
		public IScopeNode getMembersScope(IModuleResolver moduleResolver) {
			assertFail(); return null;
		}
		
		@Override
		public void accept0(IASTNeoVisitor visitor) {
			assertFail();
		}
		
		@Override
		public String toStringForCodeCompletion() {
			return getName();
		}
		
		@Override
		public String toStringForHoverSignature() {
			return getName();
		}
	}
	
	
	@Override
	public String getReferenceName() {
		return module;
	}
	
	@Override
	public void doSearch(PrefixDefUnitSearch search) {
		String prefix = search.searchOptions.searchPrefix;
		
		String[] strings = search.resolveModules(prefix);
		for (int i = 0; i < strings.length; i++) {
			String name = strings[i];
			
			search.addMatch(new LiteModuleDummy(name));		
		}
	}
	
	@Override
	public String toStringAsElement() {
		String str = StringUtil.collToString(packages, ".");
		if(str.length() == 0)
			return module;
		else
			return str + "." + module;
	}
	
}