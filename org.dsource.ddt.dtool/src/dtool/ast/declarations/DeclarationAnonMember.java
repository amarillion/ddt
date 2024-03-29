package dtool.ast.declarations;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.util.Iterator;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.NodeList;
import dtool.ast.SourceRange;
import dtool.refmodel.INonScopedBlock;

public class DeclarationAnonMember extends ASTNeoNode implements INonScopedBlock {
	
	public final NodeList body;
	
	public DeclarationAnonMember(NodeList body, SourceRange sourceRange) {
		assertNotNull(body);
		initSourceRange(sourceRange);
		this.body = NodeList.parentizeNodeList(body, this);
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, body.nodes);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public Iterator<ASTNeoNode> getMembersIterator() {
		return body.nodes.iterator();
	}
	
}
