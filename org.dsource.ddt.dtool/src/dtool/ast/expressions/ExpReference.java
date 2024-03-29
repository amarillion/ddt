package dtool.ast.expressions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.util.Collection;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.ast.definitions.DefUnit;
import dtool.ast.references.Reference;
import dtool.refmodel.pluginadapters.IModuleResolver;

/**
 * An Expression wrapping a {@link Reference}
 */
public class ExpReference extends Expression {
	
	public final Reference ref;
	
	public ExpReference(Reference ref, SourceRange sourceRange) {
		assertNotNull(ref);
		this.ref = parentize(ref);
		
		initSourceRange(sourceRange);
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, ref);
		}
		visitor.endVisit(this);	 
	}
	
	@Override
	public Collection<DefUnit> findTargetDefUnits(IModuleResolver moduleResolver, boolean findFirstOnly) {
		return ref.findTargetDefUnits(moduleResolver, findFirstOnly);
	}
	
	@Override
	public String toStringAsElement() {
		return ref.toStringAsElement();
	}
	
}