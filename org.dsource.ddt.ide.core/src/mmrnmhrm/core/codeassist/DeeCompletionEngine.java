package mmrnmhrm.core.codeassist;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import mmrnmhrm.core.DeeCore;

import org.dsource.ddt.ide.core.model.engine.DeeModelEngine;
import org.eclipse.dltk.codeassist.ScriptCompletionEngine;
import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.dltk.core.CompletionContext;
import org.eclipse.dltk.core.CompletionProposal;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;

import dtool.DeeNamingRules;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.Module;
import dtool.contentassist.CompletionSession;
import dtool.refmodel.PrefixDefUnitSearch;
import dtool.refmodel.PrefixDefUnitSearch.IDefUnitMatchAccepter;
import dtool.refmodel.PrefixSearchOptions;

public class DeeCompletionEngine extends ScriptCompletionEngine {
	
	@Override
	public void complete(IModuleSource module, final int position, int i) {
		assertNotNull(requestor);
		requestor.beginReporting();
		try {
			CompletionContext context = new CompletionContext();
			requestor.acceptContext(context);
			
			if(!(module.getModelElement() instanceof ISourceModule)) {
				return;
			}
			final ISourceModule sourceModule = (ISourceModule) module.getModelElement();
			
			String sourceContents = module.getSourceContents();
			IDefUnitMatchAccepter collectorAdapter = new IDefUnitMatchAccepter() {
				@Override
				public void accept(DefUnit defUnit, PrefixSearchOptions searchOptions) {
					CompletionProposal proposal = createProposal(defUnit, position, sourceModule, searchOptions);
					requestor.accept(proposal);
				}
			};
			doCompletionSearch(position, sourceModule, sourceContents, collectorAdapter);
		} finally {
			requestor.endReporting();
		}
	}
	
	protected CompletionProposal createProposal(DefUnit defUnit, int ccOffset, ISourceModule sourceModule,
			PrefixSearchOptions searchOptions) {
		String rplStr = defUnit.getName().substring(searchOptions.namePrefixLen);
		
		CompletionProposal proposal = createProposal(CompletionProposal.TYPE_REF, ccOffset);
		proposal.setName(defUnit.toStringForCodeCompletion());
		proposal.setCompletion(rplStr);
		proposal.setReplaceRange(ccOffset, ccOffset + searchOptions.rplLen);
		proposal.setExtraInfo(defUnit);
		
		Module moduleNode = defUnit.getModuleNode();
		if(moduleNode != null) {
			// We need the check above because of synthetic defUnits TODO address this in a different way
			
			DeeProjectModuleResolver moduleResolver = new DeeProjectModuleResolver(sourceModule);
			try {
				ISourceModule defUnitSourceModule = moduleResolver.findModuleUnit(moduleNode, sourceModule);
				if(defUnitSourceModule != null) {
					IMember me = DeeModelEngine.findCorrespondingModelElement(defUnit, defUnitSourceModule);
					proposal.setModelElement(me);
				}
			} catch(ModelException e) {
				// Just log, don't set model element
				DeeCore.log(e);
			}
		}
		
		return proposal;
	}
	
	public static PrefixDefUnitSearch doCompletionSearch(final int offset, ISourceModule moduleUnit, String source,
			IDefUnitMatchAccepter defUnitAccepter) {
		CompletionSession session = new CompletionSession();
		return doCompletionSearch(offset, moduleUnit, source, session, defUnitAccepter);
	}
	
	public static PrefixDefUnitSearch doCompletionSearch(final int offset, ISourceModule moduleUnit, String source,
			CompletionSession session, IDefUnitMatchAccepter defUnitAccepter) {
		String defaultModuleName = DeeNamingRules.getModuleNameFromFileName(moduleUnit.getElementName());
		DeeProjectModuleResolver mr = new DeeProjectModuleResolver(moduleUnit);
		return PrefixDefUnitSearch.doCompletionSearch(session, defaultModuleName, source, offset, mr, defUnitAccepter);
	}
	
}