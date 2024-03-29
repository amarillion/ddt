package dtool.ast;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import descent.internal.compiler.parser.ast.IASTNode;
import dtool.Logg;


/**
 * Checks for AST validity. Namely:
 * Source range consistency. 
 */
public class ASTSourceRangeChecker extends ASTNeoHomogenousVisitor {
	
	/** Checks an AST for errors, such as source range errors. */
	public static void checkConsistency(ASTNeoNode elem){
		elem.accept(new ASTSourceRangeChecker(elem.getStartPos()));
	}
	
	private int offsetCursor;
	protected StringBuffer strbuffer;
	
	public ASTSourceRangeChecker(int offsetCursor) {
		this.offsetCursor = offsetCursor;
	}
	
	@Override
	public boolean preVisit(ASTNeoNode elem) {
		if(elem.hasNoSourceRangeInfo()) {
			return handleSourceRangeNoInfo(elem);
		} else if(elem.getOffset() < offsetCursor) {
			return handleSourceRangeStartPosBreach(elem);
		} else {
			offsetCursor = elem.getOffset();
			return true; // Go to children
		}
	}
	
	@Override
	public void postVisit(ASTNeoNode elem) {
		if(elem.hasNoSourceRangeInfo()) {
			return;
		} else if(elem.getEndPos() < offsetCursor) {
			handleSourceRangeEndPosBreach(elem);
			return;
		} else {
			offsetCursor = elem.getEndPos();
			return;
		}
	}
	
	/* ====================================================== */
	
	protected boolean handleSourceRangeNoInfo(IASTNode elem) {
//		assertFail();
		Logg.astmodel.print("Source range no info on: ");
		Logg.astmodel.println(elem.toStringAsNode(true));
		return false;
	}
	
	protected boolean handleSourceRangeStartPosBreach(IASTNode elem) {
//		assertFail();
		Logg.astmodel.print("Source range start-pos error on: ");
		Logg.astmodel.println(elem.toStringAsNode(true));
		return false;
	}
	
	protected void handleSourceRangeEndPosBreach(IASTNode elem) {
//		assertFail();
		Logg.astmodel.print("Source range end-pos error on: ");
		Logg.astmodel.println(elem.toStringAsNode(true));
	}
	
	public static class ASTAssertChecker extends ASTSourceRangeChecker {
		
		public static void checkConsistency(ASTNeoNode elem){
			elem.accept(new ASTAssertChecker(elem.getStartPos()));
		}
		
		public ASTAssertChecker(int offsetCursor) {
			super(offsetCursor);
		}
		
		@Override
		protected void handleSourceRangeEndPosBreach(IASTNode elem) {
			assertFail();
		}
		
		@Override
		protected boolean handleSourceRangeNoInfo(IASTNode elem) {
			throw assertFail();
		}
		
		@Override
		protected boolean handleSourceRangeStartPosBreach(IASTNode elem) {
			throw assertFail();
		}
	}
	
}

