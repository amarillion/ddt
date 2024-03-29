package mmrnmhrm.tests;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.File;
import java.io.IOException;

import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.projectmodel.ProjectModelUtil;
import mmrnmhrm.tests.utils.BundleResourcesUtil;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;

import dtool.tests.DToolTestResources;


public class DeeCoreTestResources {
	
	private static final String TESTDATA_BUNDLE_PATH = "testdata/";
	
	public static void createSrcFolderFromDeeCoreResource(String resourcePath, IContainer destFolder) 
			throws CoreException {
		createWorkspaceFolderFromDeeResource(resourcePath, destFolder);
		ProjectModelUtil.addSourceFolder(destFolder, null);
	}
	
	public static void createWorkspaceFolderFromDeeResource(String resourcePath, IContainer destFolder)
			throws CoreException {
		File destFolder_File = destFolder.getLocation().toFile();
		copyTestFolderContentsFromDeeResource(resourcePath, destFolder_File);
		
		destFolder.refreshLocal(IResource.DEPTH_INFINITE, null);
		assertTrue(destFolder.exists());
	}
	
	
	/** Copies the contents of a test bundle resource folder into given destFolder destination */
	public static void copyTestFolderContentsFromDeeResource(String resourcePath, File destFolder) 
			throws CoreException {
		String pluginId = DeeCore.TESTS_PLUGIN_ID;
		String bundleResourcePath = new Path(TESTDATA_BUNDLE_PATH).append(resourcePath).toString();
		try {
			BundleResourcesUtil.copyDirContents(pluginId, bundleResourcePath, destFolder);
		} catch(IOException e) {
			throw DeeCore.createCoreException("Error copying resource contents", e);
		}
	}
	
	public static File getTestResource(String relativePath) {
		return new File(DToolTestResources.getWorkingDir(), relativePath);
	}
	
}