package org.aksw.agdistis.algorithm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Properties;

public class CorporationAffixCleaner {

	HashSet<String> corporationAffixes = new HashSet<String>();

	public CorporationAffixCleaner() throws IOException {
		Properties prop = new Properties();
		InputStream input = CorporationAffixCleaner.class.getResourceAsStream("/config/agdistis.properties");
		prop.load(input);
		String envCorpAffixes = System.getenv("AGDISTIS_CORPORATION_AFFIXES");
		String file = envCorpAffixes != null ? envCorpAffixes : prop.getProperty("corporationAffixes");

		loadCorporationAffixes(file);
	}

	private void loadCorporationAffixes(String file) throws IOException {
		BufferedReader br = new BufferedReader(
				new InputStreamReader(CorporationAffixCleaner.class.getResourceAsStream(file)));
		while (br.ready()) {
			String line = br.readLine();
			corporationAffixes.add(line);
		}
		br.close();
	}

	String cleanLabelsfromCorporationIdentifier(String label) {
		for (String corporationAffix : corporationAffixes) {
			if (label.endsWith(corporationAffix)) {
				label = label.substring(0, label.lastIndexOf(corporationAffix));
			}
		}
		return label.trim();
	}

}
