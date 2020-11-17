package org.dice.kgsmrstn.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.QueryParserBase;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.NumericUtils;
import org.apache.lucene.util.Version;
import org.slf4j.LoggerFactory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class TripleIndex {

	private static final Version LUCENE44 = Version.LUCENE_44;

	private org.slf4j.Logger log = LoggerFactory.getLogger(TripleIndex.class);

	public static final String FIELD_NAME_SUBJECT = "subject";
	public static final String FIELD_NAME_PREDICATE = "predicate";
	public static final String FIELD_NAME_OBJECT_URI = "object_uri";
	public static final String FIELD_NAME_OBJECT_LITERAL = "object_literal";
	public static final String FIELD_FREQ = "freq";

	private int defaultMaxNumberOfDocsRetrievedFromIndex = 100;

	private Directory directory;
	private IndexSearcher isearcher;
	private DirectoryReader ireader;
	private UrlValidator urlValidator;
	private Cache<BooleanQuery, List<Triple>> cache;
	StringUtils isInt = new StringUtils();

	public TripleIndex() throws IOException {
		Properties prop = new Properties();
		InputStream input = TripleIndex.class.getResourceAsStream("/application.properties");
		prop.load(input);

		String index = prop.getProperty("index");
		log.info("The index will be here: " + index);

		directory = new MMapDirectory(new File(index));
		ireader = DirectoryReader.open(directory);
		isearcher = new IndexSearcher(ireader);
		this.urlValidator = new UrlValidator();

		cache = CacheBuilder.newBuilder().maximumSize(50000).build();
	}
	
	public void setIndex(String index) throws IOException {
		directory = new MMapDirectory(new File(index));
		ireader = DirectoryReader.open(directory);
		isearcher = new IndexSearcher(ireader);
	}

	public List<Triple> search(String subject, String predicate, String object) {
		return search(subject, predicate, object, defaultMaxNumberOfDocsRetrievedFromIndex);
	}

	public List<Triple> search(String subject, String predicate, String object, int maxNumberOfResults) {
		BooleanQuery bq = new BooleanQuery();
		List<Triple> triples = new ArrayList<Triple>();

		try {
			if (subject != null && subject.equals("http://aksw.org/notInWiki")) {
				log.error(
						"A subject 'http://aksw.org/notInWiki' is searched in the index. That is strange and should not happen");
			}
			if (subject != null) {
				Query tq = new TermQuery(new Term(FIELD_NAME_SUBJECT, subject));
				bq.add(tq, BooleanClause.Occur.MUST);
			}
			if (predicate != null) {
				Query tq = new TermQuery(new Term(FIELD_NAME_PREDICATE, predicate));
				bq.add(tq, BooleanClause.Occur.MUST);
			}

			if (object != null && object.length() > 0) {
				Query q = null;
				if (urlValidator.isValid(object)) {

					q = new TermQuery(new Term(FIELD_NAME_OBJECT_URI, object));
					bq.add(q, BooleanClause.Occur.MUST);

				} else if (StringUtils.isNumeric(object)) {
					int tempInt = Integer.parseInt(object);
					BytesRef bytes = new BytesRef(NumericUtils.BUF_SIZE_INT);
					NumericUtils.intToPrefixCoded(tempInt, 0, bytes);
					q = new TermQuery(new Term(FIELD_NAME_OBJECT_LITERAL, bytes.utf8ToString()));
					bq.add(q, BooleanClause.Occur.MUST);

				}
				else {
					Analyzer analyzer = new LiteralAnalyzer(LUCENE44);
					QueryParser parser = new QueryParser(LUCENE44, FIELD_NAME_OBJECT_LITERAL, analyzer);
					parser.setDefaultOperator(QueryParser.Operator.AND);
					q = parser.parse(QueryParserBase.escape(object));
					bq.add(q, BooleanClause.Occur.MUST);
				}
			}
			
			//Remove the predicate 'occupation'
			Query tq2 = new TermQuery(new Term(FIELD_NAME_PREDICATE,"http://dbpedia.org/ontology/occupation"));
			bq.add(tq2, BooleanClause.Occur.MUST_NOT);
			// use the cache
			triples = getFromIndex(maxNumberOfResults, bq);
			cache.put(bq, triples);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage() + " -> " + subject);
			e.printStackTrace();
		}
		return triples;
	}

	private List<Triple> getFromIndex(int maxNumberOfResults, BooleanQuery bq) throws IOException {
		log.debug("\t start asking index...");
		TopScoreDocCollector collector = TopScoreDocCollector.create(maxNumberOfResults, true);
		isearcher.search(bq, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;

		List<Triple> triples = new ArrayList<Triple>();
		String s, p, o;
		for (int i = 0; i < hits.length; i++) {
			Document hitDoc = isearcher.doc(hits[i].doc);
			s = hitDoc.get(FIELD_NAME_SUBJECT);
			p = hitDoc.get(FIELD_NAME_PREDICATE);
			o = hitDoc.get(FIELD_NAME_OBJECT_URI);
			if (o == null) {
				o = hitDoc.get(FIELD_NAME_OBJECT_LITERAL);
			}
			Triple triple = new Triple(s, p, o);
			triples.add(triple);
		}
		log.debug("\t finished asking index...");
		return triples;
	}

	public void close() throws IOException {
		ireader.close();
		directory.close();
	}

	public DirectoryReader getIreader() {
		return ireader;
	}

}
