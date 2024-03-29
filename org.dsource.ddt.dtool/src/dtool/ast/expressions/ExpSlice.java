package dtool.ast.expressions;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.refmodel.IDefUnitReferenceNode;

public class ExpSlice extends Expression {
	
	public final IDefUnitReferenceNode slicee;
	public final Resolvable from;
	public final Resolvable to;
	
	public ExpSlice(Resolvable slicee, Resolvable from, Resolvable to, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.slicee = parentizeI(slicee);
		this.from = parentize(from);
		this.to = parentize(to);
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, slicee);
			TreeVisitor.acceptChildren(visitor, from);
			TreeVisitor.acceptChildren(visitor, to);
		}
		visitor.endVisit(this);
	}
	
}