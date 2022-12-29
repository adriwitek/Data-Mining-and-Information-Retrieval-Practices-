package es.uam.eps.bmi.search.ranking.impl;

import java.util.Iterator;
import java.util.*;

import es.uam.eps.bmi.search.index.Index;
import es.uam.eps.bmi.search.ranking.*;

/**
*
* @author adrian
*/



public class RankingImpl implements SearchRanking {

	Index index;
	//El cutoff o numero de resultados determina el tamannio del heap
	public int cutoff;
    PriorityQueue<SearchRankingDoc> ranking;
	
	public RankingImpl(Index index, int cutoff){
		this.index=index;
		this.cutoff= cutoff;
		//Maxheap
		this.ranking = new PriorityQueue<SearchRankingDoc>(); 
		
	}
	
	
	
	@Override
	public int size() {
		return this.ranking.size();
	}
	
	
	@Override
    public long nResults(){
		return this.ranking.size();
	}
	
	
	public Iterator<SearchRankingDoc> iterator() {
	   return this.ranking.iterator();
       //List<SearchRankingDoc> auxList =Arrays.asList(this.ranking.toArray(new SearchRankingDoc[0]));
      // auxList.sort(SearchRankingDoc::compareTo);
	   //return auxList.iterator();
	}

	
	
	
	//Anniadir un doc al ranking
	 public void add(int docID, double score) {
		 //Hay que controlar el tam, ya que las queues son ilimitadas
		 
		 if(this.ranking.size() >= cutoff) {
			
			 Iterator<SearchRankingDoc> it = this.iterator();
			 RankingImplDoc p;
			 Boolean anniadido = false;
			 int counter =0;
			 p = (RankingImplDoc)it.next();
			 PriorityQueue<SearchRankingDoc> auxRanking = new PriorityQueue<SearchRankingDoc>( ); 

			 while(it.hasNext() && (counter <Math.min(this.cutoff, this.ranking.size()))  ) {
				if(score>p.getScore() && !anniadido) {
					auxRanking.add(new RankingImplDoc(this.index, docID,score));
				}else {
					auxRanking.add(p);
					p = (RankingImplDoc)it.next();
				}
				counter++;
			 }
			 this.ranking = auxRanking;

		 }else {
			 this.ranking.add(new RankingImplDoc(this.index, docID,score));
		 }
		 
		 
	 }



	
	 

}
