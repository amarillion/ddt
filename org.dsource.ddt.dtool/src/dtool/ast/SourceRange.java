package dtool.ast;

import static melnorme.utilbox.core.CoreUtil.downCast;


public final class SourceRange {
	
	private final int offset;
	private final int length;
	
	public SourceRange(int offset, int length) {
		this.offset = offset;
		this.length = length;
	}
	
	public final int getOffset() {
		return offset;
	}
	
	public final int getLength() {
		return length;
	}
	
	public final int getStartPos() {
		return getOffset();
	}
	
	public final int getEndPos() {
		return getOffset() + getLength();
	}
	
	@Override
	public final String toString() {
		return "[" + offset + ":" + length + "]";
	}
	
	@Override
	public final boolean equals(Object obj) {
		if(!(obj instanceof SourceRange))
			return false;
		
		SourceRange other = downCast(obj);
		return this.offset == other.offset && this.length == other.length;
	}
}