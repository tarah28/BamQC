/**
 * Copyright Copyright 2014 Simon Andrews
 *
 *    This file is part of BamQC.
 *
 *    BamQC is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    BamQC is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with BamQC; if not, write to the Free Software
 *    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
/*
 * Changelog: 
 * - Piero Dalle Pezze: Added printout, testBooleans, class standardization.
 * - Bart Ailey: Class creation.
 */
package test.java.uk.ac.babraham.BamQC.Modules;

import static org.junit.Assert.*;

import java.util.List;

import net.sf.samtools.SAMRecord;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.ac.babraham.BamQC.Modules.SequenceQualityDistribution;

/**
 * 
 * @author Bart Ailey
 * @author Piero Dalle Pezze
 *
 */
public class SequenceQualityDistributionTest {

	private static Logger log = Logger.getLogger(SequenceQualityDistributionTest.class);
	
	private SequenceQualityDistribution sequenceQualityDistribution = null;
	private TestObjectFactory testObjectFactory = null;
	private List<SAMRecord> samRecords = null;
	

	@Before
	public void setUp() throws Exception {
		testObjectFactory = new TestObjectFactory();
		samRecords = testObjectFactory.getSamRecords();
		sequenceQualityDistribution = new SequenceQualityDistribution();
	}

	@After
	public void tearDown() throws Exception {
		testObjectFactory = null;
		samRecords = null;
		sequenceQualityDistribution = null;
	}

	@Test
	public void testProcessSequence() {
		System.out.println("Running test SequenceQualityDistributionTest.testProcessSequence");
		log.info("Running test SequenceQualityDistributionTest.testProcessSequence");
		
		for (SAMRecord samRecord : samRecords) {
			sequenceQualityDistribution.processSequence(samRecord);
		}
		List<Integer> distribution = sequenceQualityDistribution.getDistribution();
		
		assertEquals(0, (int) distribution.get(0));
		assertEquals(0, (int) distribution.get(1));
		assertEquals(0, (int) distribution.get(2));
		assertEquals(2, (int) distribution.get(3));
		assertEquals(0, (int) distribution.get(4));
		assertEquals(0, (int) distribution.get(5));
		assertEquals(1, (int) distribution.get(6));
	}

	@Test(expected= IndexOutOfBoundsException.class)
	public void testProcessSequenceException() {
		System.out.println("Running test SequenceQualityDistributionTest.testProcessSequenceException");
		log.info("Running test SequenceQualityDistributionTest.testProcessSequenceException");
		
		for (SAMRecord samRecord : samRecords) {
			sequenceQualityDistribution.processSequence(samRecord);
		}
		List<Integer> distribution = sequenceQualityDistribution.getDistribution();
		
		assertEquals(0, (int) distribution.get(7));
	}
	
	@Test
	public void testBooleans() {
		System.out.println("Running test SequenceQualityDistributionTest.testBooleans");	
		log.info("Running test SequenceQualityDistributionTest.testBooleans");
		
		assertTrue(sequenceQualityDistribution.ignoreInReport());
		assertFalse(sequenceQualityDistribution.needsToSeeAnnotation());
		assertFalse(sequenceQualityDistribution.raisesError());
		assertFalse(sequenceQualityDistribution.raisesWarning());
		assertTrue(sequenceQualityDistribution.needsToSeeSequences());
	}
	
}