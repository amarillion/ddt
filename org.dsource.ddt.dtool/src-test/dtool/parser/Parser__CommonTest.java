package dtool.parser;


import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import melnorme.utilbox.core.CoreUtil;

import org.junit.Before;

import dtool.ast.ASTSourceRangeChecker;
import dtool.ast.ASTNeoNode;
import dtool.ast.definitions.Module;
import dtool.ast.references.RefIdentifier;
import dtool.ast.references.RefQualified;
import dtool.ast.references.Reference;
import dtool.tests.CommonTestUtils;
import dtool.tests.DToolBaseTest;

public abstract class Parser__CommonTest extends DToolBaseTest {
	
	protected static final String TESTFILESDIR = "parser/";
	
	public static Module parseTestFile(String filename) throws IOException {
		return testDtoolParse(readTestResourceFile(TESTFILESDIR + filename));
	}
	
	public static Module testDtoolParse(final String source) {
		return testParse(source, false, true);
	}
	
	public static Module testParseInvalidSyntax(final String source) {
		return testParse(source, true, false);
	}
	
	public static Module testParse(String source, Boolean expectErrors) {
		return testParse(source, expectErrors, true);
	}
	
	public static Module testParse(String source, Boolean expectErrors, boolean checkAST) {
		return testParseDo(source, expectErrors, checkAST).neoModule;
	}
	
	public static DeeParserSession testParseDo(String source, Boolean expectErrors) {
		return testParseDo(source, expectErrors, false);
	}
	
	public static DeeParserSession testParseDo(String source, Boolean expectErrors, boolean checkSourceRanges) {
		return parseSource(source, expectErrors, checkSourceRanges, "_tests_unnamed_");
	}
	
	public static DeeParserSession parseSource(String source, Boolean expectErrors, String defaultModuleName) {
		return parseSource(source, expectErrors, false, defaultModuleName);
	}
	
	public static DeeParserSession parseSource(String source, Boolean expectErrors, boolean checkSourceRanges,
			String defaultModuleName) {
		DeeParserSession parseResult = DeeParserSession.parseSource(source, defaultModuleName);
		
		if(expectErrors != null) {
			assertTrue(parseResult.hasSyntaxErrors() == expectErrors, "expectedErrors is not: " + expectErrors);
		}
		if(checkSourceRanges && !parseResult.hasSyntaxErrors()) {
			// We rarely get good source ranges with syntax errors; 
			ASTSourceRangeChecker.checkConsistency(parseResult.neoModule);
		}
		return parseResult;
	}
	
	public static Set<String> executedTests = new HashSet<String>();
	
	@Before
	public void printSeparator() throws Exception {
		String simpleName = getClass().getSimpleName();
		if(!executedTests.contains(simpleName)) {
			System.out.println("===============================  "+simpleName+"  ===============================");
			executedTests.add(simpleName);
		}
	}
	
	public static <T, D extends T> D downCast(T object) {
		return CoreUtil.downCast(object);
	}
	
	public static <T, D extends T> D downCast(T object, Class<D> klass) {
		assertTrue(object != null && klass.isInstance(object));
		return klass.cast(object);
	}
	
	protected static Reference reference(String identifier) {
		return new RefIdentifier(identifier);
	}
	
	protected static Reference reference(String... identifiers) {
		RefIdentifier subRef = new RefIdentifier(identifiers[identifiers.length-1]);
		if(identifiers.length == 1) {
			return subRef;
		} else {
			return new RefQualified(reference(CommonTestUtils.removeLast(identifiers, 1)), subRef);
		}
	}
	
	public static void checkEqualAsElement(ASTNeoNode[] a, ASTNeoNode[] a2) {
        int length = a.length;
        assertTrue(a2.length == length);

        for (int i=0; i<length; i++) {
            ASTNeoNode o1 = a[i];
            ASTNeoNode o2 = a2[i];
            
            if (o1 == null) {
            	assertTrue(o2 == null);
			} else {
				assertAreEqual(o1.toStringAsElement(), o2.toStringAsElement());
			}
        }
    }
	
	public static void checkParent(ASTNeoNode parent, ASTNeoNode... nodes) {
		for (ASTNeoNode child : nodes) {
			assertTrue(child.getParent() == parent);
		}
	}
	
}