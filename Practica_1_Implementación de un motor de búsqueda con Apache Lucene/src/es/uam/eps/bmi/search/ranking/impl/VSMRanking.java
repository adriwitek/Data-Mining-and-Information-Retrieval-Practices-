package es.uam.eps.bmi.search.ranking.impl;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import es.uam.eps.bmi.search.index.Index;
import es.uam.eps.bmi.search.ranking.SearchRanking;
import es.uam.eps.bmi.search.ranking.SearchRankingDoc;

public class VSMRanking implements SearchRanking {

	private List<Entry<Integer, Double>> scores;
	private Index index;
	
	public VSMRanking (List<Entry<Integer, Double>> scores, Index index, int cutoff) {

		scores.sort(Entry.comparingByValue());
		
		Collections.reverse(scores);
		

		if (scores.size() > cutoff) this.scores = scores.subList(0, cutoff);
		else this.scores = scores;

		
		this.index = index;
	}

	@Override
	public Iterator<SearchRankingDoc> iterator() {
		
		return new VSMRankingIterator(this.index, this.scores);
	}

	@Override
	public int size() {
		return this.scores.size();
	}
}
