package uk.ac.babraham.BamQC.Modules;

import java.io.IOException;

import javax.swing.JPanel;
import javax.xml.stream.XMLStreamException;

import net.sf.samtools.SAMRecord;

import org.apache.log4j.Logger;

import uk.ac.babraham.BamQC.Annotation.AnnotationSet;
import uk.ac.babraham.BamQC.Graphs.BarGraph;
import uk.ac.babraham.BamQC.Graphs.LineGraph;
import uk.ac.babraham.BamQC.Report.HTMLReportArchive;
import uk.ac.babraham.BamQC.Sequence.SequenceFile;

public class QualityDistribution extends AbstractQCModule {

	private static Logger log = Logger.getLogger(QualityDistribution.class);

	private final static int QUALITY_MAP_SIZE = 256;

	private int maxCount = 0;
	private int[] distribution = new int[QUALITY_MAP_SIZE];
	private String[] label = new String[QUALITY_MAP_SIZE];

	public QualityDistribution() {
		for (int i = 0; i < QUALITY_MAP_SIZE; i++) {
			label[i] = Integer.toString(i);
		}
	}

	@Override
	public void processSequence(SAMRecord read) {
		int quality = read.getMappingQuality();

		log.info("quality = " + quality);

		distribution[quality]++;

		if (distribution[quality] > maxCount) maxCount = distribution[quality];
	}

	@Override
	public void processFile(SequenceFile file) {}

	@Override
	public void processAnnotationSet(AnnotationSet annotation) {
		throw new UnsupportedOperationException("processAnnotationSet called");
	}

	@Override
	public JPanel getResultsPanel() {
		double[] distributionFloat = getDistributionFolat();
		String[] xTitles = new String[] { "" };

		
		
		return new BarGraph(distributionFloat, 0.0D, maxCount, "Distribution", xTitles, label, "Quality Mapping Distribution");
	}

	@Override
	public String name() {
		return "Quality Mapping Distribution";
	}

	@Override
	public String description() {
		return "Quality Mapping Distribution";
	}

	@Override
	public void reset() {
		distribution = new int[QUALITY_MAP_SIZE];
		maxCount = 0;
	}

	@Override
	public boolean raisesError() {
		return false;
	}

	@Override
	public boolean raisesWarning() {
		return false;
	}

	@Override
	public boolean needsToSeeSequences() {
		return true;
	}

	@Override
	public boolean needsToSeeAnnotation() {
		return false;
	}

	@Override
	public boolean ignoreInReport() {
		return false;
	}

	@Override
	public void makeReport(HTMLReportArchive report) throws XMLStreamException, IOException {
		// TODO Auto-generated method stub

	}

	public int[] getDistribution() {
		return distribution;
	}

	public double[] getDistributionFolat() {
		double[] distributionFloat = new double[QUALITY_MAP_SIZE];

		for (int i = 0; i < QUALITY_MAP_SIZE; i++) {
			distributionFloat[i] = distribution[i];
		}
		return distributionFloat;
	}

	public int getMaxCount() {
		return maxCount;
	}
	
	

}