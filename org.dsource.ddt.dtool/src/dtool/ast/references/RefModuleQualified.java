package dtool.ast.references;

import java.util.Collection;
import java.util.Collections;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.NodeUtil;
import dtool.ast.SourceRange;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.Module;
import dtool.refmodel.IDefUnitReferenceNode;
import dtool.refmodel.pluginadapters.IModuleResolver;

/** An entity reference starting at module scope. 
 * Example: "a = .foo;"
 */
public class RefModuleQualified extends CommonRefQualified {
	
	public RefModuleQualified(RefIdentifier subref, SourceRange sourceRange) {
		super(subref);
		initSourceRange(sourceRange);
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, qualifiedName);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public String toStringAsElement() {
		return "." + qualifiedName.toStringAsElement();
	}
	
	@Override
	public IDefUnitReferenceNode getQualifier() {
		return null;
	}
	
	@Override
	public Collection<DefUnit> findRootDefUnits(IModuleResolver moduleResolver) {
		final Module module = NodeUtil.getParentModule(this);
		return Collections.singletonList((DefUnit)module);
	}
	
}