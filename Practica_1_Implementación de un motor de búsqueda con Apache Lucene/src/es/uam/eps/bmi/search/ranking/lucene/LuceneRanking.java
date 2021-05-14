package es.uam.eps.bmi.search.ranking.lucene;

import java.util.Iterator;
import org.apache.lucene.search.ScoreDoc;

import es.uam.eps.bmi.search.index.Index;
import es.uam.eps.bmi.search.ranking.SearchRanking;
import es.uam.eps.bmi.search.ranking.SearchRankingDoc;

public class LuceneRanking implements SearchRanking {

	private ScoreDoc[] scoreDocs;
	private Index index;
	
	public LuceneRanking (ScoreDoc[] scoreDocs, Index index) {
		
		this.scoreDocs = scoreDocs;
		this.index = index;
	}

	@Override
	public Iterator<SearchRankingDoc> iterator() {
		
		return new LuceneRankingIterator(this.index, this.scoreDocs);
	}

	@Override
	public int size() {
		return this.scoreDocs.length;
	}

}
