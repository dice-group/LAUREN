
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.aksw.agdistis.util.Triple;
import org.aksw.agdistis.util.TripleIndex;
import org.apache.lucene.search.spell.NGramDistance;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TripleIndexTest {

	Logger log = LoggerFactory.getLogger(TripleIndexTest.class);
	private TripleIndex index;

	@Before
	public void init() {
		try {
			index = new TripleIndex();

		} catch (IOException e) {
			log.error(
					"Can not load index or DBpedia repository due to either wrong properties in agdistis.properties or missing index at location",
					e);
		}
	}

	@After
	public void close() {
		try {
			index.close();
		} catch (IOException e) {
			log.error(
					"Can not load index or DBpedia repository due to either wrong properties in agdistis.properties or missing index at location",
					e);
		}
	}

	@Test
	public void testRedirects() {
		String candidateURL = "http://dbpedia.org/resource/Barack_H_Obama_Junior";
		List<Triple> redirect = index.search(candidateURL, "http://dbpedia.org/ontology/wikiPageRedirects", null);
		for (Triple t : redirect) {
			log.debug(t.toString());
		}
		assertTrue(redirect.size() == 1);
		candidateURL = "http://dbpedia.org/resource/Barack_Obama";
		redirect = index.search(candidateURL, "http://dbpedia.org/ontology/wikiPageRedirects", null);
		assertTrue(redirect.size() == 0);
	}

	@Test
	public void testDisambiguation() {
		String candidateURL = "http://dbpedia.org/resource/Bama";
		List<Triple> dis = index.search(candidateURL, "http://dbpedia.org/ontology/wikiPageDisambiguates", null);
		assertTrue(dis.size() > 0);
		for (Triple t : dis) {
			log.debug(t.toString());
		}
	}

	@Test
	public void testType() {
		String candidateURL = "http://dbpedia.org/resource/Barack_Obama";
		List<Triple> type = index.search(candidateURL, "http://www.w3.org/1999/02/22-rdf-syntax-ns#type", null);
		assertTrue(type.size() > 0);
		for (Triple t : type) {
			log.debug(t.toString());
		}
	}

	@Test
	public void testRdfsLabel() {
		// String candidateURL = "http://dbpedia.org/resource/Berlin";
		String candidateURL = "http://dbpedia.org/resource/Barack_Obama";
		List<Triple> type = index.search(candidateURL, "http://www.w3.org/2000/01/rdf-schema#label", null);
		assertTrue(type.size() > 0);
		for (Triple t : type) {
			log.debug(t.toString());
		}
	}

	@Test
	public void testSurfaceForm() {
		String candidateURL = "http://dbpedia.org/resource/Jon_Voight";
		List<Triple> type = index.search(candidateURL, null, null);
		assertTrue(type.size() > 0);
		for (Triple t : type) {
			log.debug(t.toString());
		}
	}

	/*
	 * @Test public void testAcronym() { List<Triple> type = index.search(null,
	 * "http://dbpedia.org/property/acronym", null); assertTrue(type.size() >
	 * 0); for (Triple t : type) { log.debug(t.toString()); } }
	 */
	@Test
	public void testMultipleTermsPerField() {
		String candidate = "Berlin";
		List<Triple> type = index.search(null, null, candidate);
		assertTrue(type.size() > 1);
		for (Triple t : type) {
			log.debug(t.toString());
		}
	}

	@Test
	public void testdirectLink() {
		String candidate = "http://dbpedia.org/resource/Angelina_Jolie";
		String candidate2 = "http://dbpedia.org/resource/Jon_Voight";
		List<Triple> type = index.search(candidate, null, candidate2);
		assertTrue(type.size() >= 1);
		for (Triple t : type) {
			log.debug(t.toString());
		}
	}

	@Test
	public void testSurfaceFormsDistance() {
		String candidateURL = "http://dbpedia.org/resource/Barack_Obama";
		List<Triple> label = index.search(candidateURL, "http://www.w3.org/2000/01/rdf-schema#label", null);
		List<Triple> surfaceForms = index.search(candidateURL, "http://www.w3.org/2004/02/skos/core#altLabel", null);
		log.debug(" * " + surfaceForms.size());
		NGramDistance n = new NGramDistance(3);
		for (Triple t : surfaceForms) {
			log.debug(label.get(0).getObject() + " " + t.getObject() + " : "
					+ n.getDistance(label.get(0).getObject(), t.getObject()));
			assertTrue(n.getDistance(label.get(0).getObject(), t.getObject()) >= 0);

		}
	}
}
