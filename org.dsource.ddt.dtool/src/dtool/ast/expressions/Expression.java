package dtool.ast.expressions;


import java.util.Collection;
import java.util.Collections;

import dtool.ast.definitions.DefUnit;
import dtool.refmodel.IDefUnitReferenceNode;
import dtool.refmodel.pluginadapters.IModuleResolver;

public abstract class Expression extends Resolvable implements IDefUnitReferenceNode {
	
	// deprecate
	public Collection<DefUnit> getType(IModuleResolver moduleResolver) {
		return findTargetDefUnits(moduleResolver, false);
	}
	
	@Override
	public Collection<DefUnit> findTargetDefUnits(IModuleResolver moduleResolver, boolean findFirstOnly) {
		return Collections.emptySet();
	}
	
}