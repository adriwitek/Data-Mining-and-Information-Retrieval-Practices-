package es.uam.eps.bmi.search.lucene;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import es.uam.eps.bmi.search.AbstractEngine;
import es.uam.eps.bmi.search.index.lucene.LuceneIndex;
import es.uam.eps.bmi.search.ranking.SearchRanking;
import es.uam.eps.bmi.search.ranking.lucene.LuceneRanking;

public class LuceneEngine extends AbstractEngine {

	private IndexSearcher searcher;
	
	public LuceneEngine(String indexPath) throws IOException {
	
		super(new LuceneIndex(indexPath));
		
		Directory directory = FSDirectory.open(Paths.get(indexPath));

		IndexReader index = DirectoryReader.open(directory);
		
		this.searcher = new IndexSearcher(index);
		// searcher.setSimilarity(similarity)
	}

	@Override
	public SearchRanking search(String query, int cutoff) throws IOException {
	
		if(this.searcher == null) {
			throw new IOException();
		}

		StandardAnalyzer analyzer = new StandardAnalyzer();
		QueryParser parser = new QueryParser("content", analyzer);

		try {
			Query q = parser.parse(query);
		
			TopDocs topDocs = searcher.search(q, cutoff);
			
			return new LuceneRanking(topDocs.scoreDocs, super.index);
			
		} catch (ParseException e) {
			throw new IOException();
		}
	}
}
