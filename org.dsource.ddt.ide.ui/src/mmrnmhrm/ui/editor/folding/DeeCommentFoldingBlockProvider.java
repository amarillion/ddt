package mmrnmhrm.ui.editor.folding;

import java.util.List;

import mmrnmhrm.ui.text.DeePartitioningProvider;
import mmrnmhrm.ui.text.DeePartitions;

import org.dsource.ddt.ide.core.model.DeeModuleParsingUtil;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.ui.text.folding.IFoldingBlockKind;
import org.eclipse.dltk.ui.text.folding.IFoldingContent;
import org.eclipse.dltk.ui.text.folding.PartitioningFoldingBlockProvider;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IRegion;

import dtool.ast.definitions.Module;

public class DeeCommentFoldingBlockProvider extends PartitioningFoldingBlockProvider {
	
	public DeeCommentFoldingBlockProvider() {
		super(DeePartitioningProvider.getInstance());
	}
	
	protected boolean fStringFolding;
	protected boolean fInitCollapseStrings;
	protected int offsetForFirstMember; // Used to determine header comments

	
	@Override
	public void initializePreferences(IPreferenceStore preferenceStore) {
		super.initializePreferences(preferenceStore);
		fStringFolding = preferenceStore.getBoolean(DeeFoldingPreferenceConstants.EDITOR_FOLDING_INIT_STRINGS);
		fInitCollapseStrings = preferenceStore.getBoolean(DeeFoldingPreferenceConstants.EDITOR_FOLDING_INIT_STRINGS);
	}
	
	public boolean isCollapseStrings() {
		return fInitCollapseStrings;
	}
	
	@Override
	public void computeFoldableBlocks(IFoldingContent content) {
		offsetForFirstMember = -1;
		
		if(isFoldingComments()) {
			ISourceModule sourceModule = (ISourceModule) content.getModelElement();
			
			// With changes in the parser perhaps this code could be simplified.
			Module deeModule = DeeModuleParsingUtil.parseAndGetAST(sourceModule);
			if (deeModule != null) {
				if(deeModule.md != null) {
					offsetForFirstMember = deeModule.md.getOffset();
				}
			}
			
			computeBlocksForPartitionType(content,
					DeePartitions.DEE_MULTI_COMMENT, DeeFoldingBlockKind.COMMENT, isCollapseComments());
			computeBlocksForPartitionType(content,
					DeePartitions.DEE_NESTED_COMMENT, DeeFoldingBlockKind.COMMENT, isCollapseComments());
		}
		if(isFoldingDocs()) {
			computeBlocksForPartitionType(content,
					DeePartitions.DEE_MULTI_DOCCOMMENT, DeeFoldingBlockKind.DOCCOMMENT, isCollapseDocs());
			computeBlocksForPartitionType(content,
					DeePartitions.DEE_NESTED_DOCCOMMENT, DeeFoldingBlockKind.DOCCOMMENT, isCollapseDocs());
		}
		if(fStringFolding) {
			computeBlocksForPartitionType(content,
					DeePartitions.DEE_STRING, DeeFoldingBlockKind.MULTILINESTRING, isCollapseStrings());
			computeBlocksForPartitionType(content,
					DeePartitions.DEE_RAW_STRING, DeeFoldingBlockKind.MULTILINESTRING, isCollapseStrings());
			computeBlocksForPartitionType(content,
					DeePartitions.DEE_DELIM_STRING, DeeFoldingBlockKind.MULTILINESTRING, isCollapseStrings());
		}
	}
	
	@Override
	protected void reportRegions(Document document, List<IRegion> regions, IFoldingBlockKind kind, boolean collapse)
			throws BadLocationException {
//		super.reportRegions(document, regions, kind, collapse);
		
//		// XXX: DLTK 3.0 copied/modified code
//		for (IRegion region : regions) {
//			// TODO
//			Object element = null;
//			requestor.acceptBlock(region.getOffset(), region.getOffset()
//					+ region.getLength(), kind, element, collapse);
//		}
		
		for (IRegion region : regions) {
			Object element = null;
			
			boolean effectiveCollapse = collapse;
			if(kind.isComment() && offsetForFirstMember != -1 && region.getOffset() < offsetForFirstMember) {
				effectiveCollapse = isCollapseHeaderComment();
			}
			
			requestor.acceptBlock(region.getOffset(), region.getOffset() + region.getLength(), 
					kind, element, effectiveCollapse);
		}
	}
}
