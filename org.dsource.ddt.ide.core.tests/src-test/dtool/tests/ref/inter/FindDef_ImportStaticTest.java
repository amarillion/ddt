package dtool.tests.ref.inter;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.eclipse.dltk.core.ModelException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * XXX: The code to modify testImportStatic is very messy
 */
@RunWith(Parameterized.class)
public class FindDef_ImportStaticTest extends FindDef__ImportsCommon  {
	
	static final String testSrcFile = "testImportStatic.d";
	
	protected static final int ix1, ix2;
	
	static {
		setupDefault(testSrcFile);
		ix1 = getContents(defaultModule).indexOf("/++/", 0);
		ix2 = getContents(defaultModule).indexOf("/++/", ix1+1);
	}
	
	private static String getContents(ParseSource module) {
		try {
			return module.scriptModule.getBuffer().getContents();
		} catch (ModelException e) {
			throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
		}
	}
	
	@BeforeClass
	public static void classSetup() throws ModelException {
		setupDefault(testSrcFile);
		defaultModule.scriptModule.getBuffer().replace(ix1, 4, "    ");
		assertEquals(defaultModule.scriptModule.getBuffer().getText(ix1, 4), "    ");
		defaultModule.scriptModule.getBuffer().replace(ix2, 4, "//  ");
		assertEquals(defaultModule.scriptModule.getBuffer().getText(ix2, 4), "//  ");
		setupDefault(testSrcFile);
	}
	
	@Parameters
	public static List<Object[]> data() {
		return Arrays.asList(new Object[][]{
				
				{100, 12, "pack/mod1.d"}, // ALT 1 only
				{124, 12, "pack/mod2.d"}, // ALT 1 only
				{135, 12, "pack/sample.d"}, // ALT 1 only
				{156, 20, "pack/subpack/mod3.d"}, // ALT 1 only
				
				{251, 12, "pack/sample.d"},
				{279, 60, "pack/sample.d"},
				{335, 60, "pack/sample.d"},
				{347, 86, "pack/sample.d"},
				
				{370, 55, "pack2/foopublic.d"},
				{406, -1, null},
				{470, 55, "pack2/foopublic.d"}, // This is ugly D behavior
				{536, -1, null},
				
		});
	}
	
	
	public FindDef_ImportStaticTest(int defOffset, int refOffset, String targetFile) throws Exception {
		super(defOffset, refOffset, targetFile);
	}
	
	@Override
	@Test
	public void test() throws Exception {
		assertEquals(defaultModule.scriptModule.getBuffer().getContents(), sourceModule.source);
		assertEquals(defaultModule.scriptModule, sourceModule.scriptModule);
		testFindRefWithConfiguredValues();
	}
	
}
