package dtool.ast.statements;

import java.util.Iterator;
import java.util.List;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.IASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.refmodel.IScope;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.pluginadapters.IModuleResolver;
import dtool.util.ArrayView;

/**
 * A compound statement. Allways introduces a new Scope.
 */
public class BlockStatement extends Statement implements IScopeNode {
	
	public final ArrayView<IStatement> statements;
	public boolean hasCurlyBraces; // syntax-structural?
	
	public BlockStatement(ArrayView<IStatement> statements, boolean hasCurlyBraces, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.statements = parentizeI(statements);
		this.hasCurlyBraces = hasCurlyBraces;
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, statements);
		}
		visitor.endVisit(this);
	}
	
	
	@Override
	public Iterator<? extends IASTNeoNode> getMembersIterator(IModuleResolver moduleResolver) {
		return statements.iterator();
	}
	
	@Override
	public List<IScope> getSuperScopes(IModuleResolver moduleResolver) {
		return null;
	}
	
	@Override
	public boolean hasSequentialLookup() {
		return true;
	}
	
}