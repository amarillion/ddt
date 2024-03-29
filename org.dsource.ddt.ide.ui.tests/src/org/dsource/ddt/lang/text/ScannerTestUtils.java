/*******************************************************************************
 * Copyright (c) 2010, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package org.dsource.ddt.lang.text;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.utilbox.misc.ArrayUtil;

import org.dsource.ddt.lang.text.BlockHeuristicsScannner.BlockTokenRule;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;

import dtool.tests.CommonTestUtils;


public class ScannerTestUtils extends CommonTestUtils {
	
	protected static final String NL = "\r\n";
	protected static final String TAB = "\t";
	
	public static final String NEUTRAL_SRC1 = 
		line("void func() {")+
		line(TAB+"blah();")+
		line(TAB+"blah2([1, 2, 3]);")+
		line("}")
		;
	public static final String NEUTRAL_SRC2 = NEUTRAL_SRC1; // TODO: should write some other sample code
	public static final String NEUTRAL_SRC3 = NEUTRAL_SRC1;
	
	public static String line(String string) {
		return string+NL;
	}
	
	
	protected Document document;
	
	protected Document getDocument() {
		if(document == null) {
			document = createDocument();
		}
		return document;
	}
	
	protected Document createDocument() {
		Document document = new Document();
		assertTrue(ArrayUtil.contains(document.getLegalLineDelimiters(), NL));
		setupSamplePartitioner(document);
		return document;
	}
	
	public static BlockHeuristicsScannner createBlockHeuristicScannerWithSamplePartitioning(IDocument document) {
		String partitioning = SamplePartitionScanner.LANG_PARTITIONING;
		String contentType = IDocument.DEFAULT_CONTENT_TYPE;
		BlockTokenRule[] blockTokens = BlockHeuristicsScannnerTest.SAMPLE_BLOCK_TOKENS;
		return new BlockHeuristicsScannner(document, partitioning, contentType, blockTokens);
	}
	
	public static void setupSamplePartitioner(Document document) {
		SamplePartitionScanner partitionScanner = new SamplePartitionScanner();
		String[] legalContentTypes = SamplePartitionScanner.LEGAL_CONTENT_TYPES;
		installPartitioner(document, SamplePartitionScanner.LANG_PARTITIONING, partitionScanner, legalContentTypes);
	}
	
	public static FastPartitioner installPartitioner(Document document, String partitioning,
			IPartitionTokenScanner partitionScanner, String[] legalContentTypes) {
		partitionScanner.setRange(document, 0, document.getLength());
		
		FastPartitioner fp = new FastPartitioner(partitionScanner, legalContentTypes);
		fp.connect(document, false);
		document.setDocumentPartitioner(partitioning, fp);
		return fp;
	}
	
}