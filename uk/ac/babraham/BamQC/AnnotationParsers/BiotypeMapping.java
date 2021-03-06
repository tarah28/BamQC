/*
 * Changelog: 
 * - Simon Andrews: Class creation.
 */
package uk.ac.babraham.BamQC.AnnotationParsers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.apache.log4j.Logger;

import uk.ac.babraham.BamQC.BamQCConfig;
import uk.ac.babraham.BamQC.Modules.ModuleConfig;

/**
 * 
 * @author Simon Andrews
 *
 */
public class BiotypeMapping {

	private static Logger log = Logger.getLogger(BiotypeMapping.class);
	
	private HashMap<String, String> mappings = new HashMap<String, String>();
	private static BiotypeMapping biotypeMapping = null;
	
	public static BiotypeMapping getInstance () {
		if (biotypeMapping == null) {
			biotypeMapping = new BiotypeMapping();
		}
		
		return biotypeMapping;
	}


	private BiotypeMapping () {

		BufferedReader br = null;

		try {
			if (BamQCConfig.getInstance().biotype_mapping_file == null) {
				InputStream rsrc = ModuleConfig.class.getResourceAsStream("/Configuration/biotype_mappings.txt");
				if (rsrc == null) throw new FileNotFoundException("cannot find Configuration/biotype_mappings.txt");
				br = new BufferedReader(new InputStreamReader(rsrc));
			}
			else {
				br = new BufferedReader(new FileReader(BamQCConfig.getInstance().biotype_mapping_file));
			}

			String line;
			while ((line = br.readLine()) != null) {

				if (line.startsWith("#")) continue;

				if (line.trim().length() == 0) continue;

				String[] sections = line.split("\\s+");
				if (sections.length != 2) {
					log.error("Biotype mapping line '" + line + "' didn't contain the 2 required sections");
				}

				mappings.put(sections[0], sections[1]);

			}
		}
		catch (IOException e) {
			log.error(e, e);
		}
		finally {
			try{ 
				if(br != null) {
					br.close();
				}
			} catch(IOException e) {
				log.error(e, e);
			}
		}

	}
	
	public String getEffectiveBiotype (String biotype) {
		if (mappings.containsKey(biotype)) {
			return mappings.get(biotype);
		}
		return biotype;
	}
	



}
