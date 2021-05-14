package es.uam.eps.bmi.recsys.metric;

import java.util.HashSet;
import java.util.Set;

import es.uam.eps.bmi.recsys.Recommendation;
import es.uam.eps.bmi.recsys.data.Ratings;
import es.uam.eps.bmi.recsys.ranking.Ranking;
import es.uam.eps.bmi.recsys.ranking.RankingElement;

public class Recall implements Metric{

	
	
	private Ratings ratings;
	private double threshold; 
	private int  cutoff;

	public Recall(Ratings test , double threshold, int  cutoff) {
		this.ratings = test;
		this.threshold = threshold;
		this.cutoff =cutoff;
	}

	
	
	@Override
	public double compute(Recommendation rec) {

		
		Ranking rank;
		Integer cutoffCounter=0;
		Integer nRelevantes;
		double aux = 0.0;
		Double rat = 0.0;
		HashSet<Integer> itemsExplorados;
		Set<Integer> auxHashSet;
		int relevantesTotalesUsuario;
		
		if(cutoff == 0 || rec.getUsers().isEmpty()) return 0.0;
		
		for(Integer user :  rec.getUsers()) {
			
			rank =  rec.getRecommendation(user);
			cutoffCounter =0;
			nRelevantes = 0;
			itemsExplorados = new HashSet<>();
			relevantesTotalesUsuario = 0;
			
			for(RankingElement re : rank) { //Ranking recomendaciones de cada user
				if(cutoffCounter == cutoff) break;
				cutoffCounter++;
				itemsExplorados.add(re.getID());
				rat = ratings.getRating(user, re.getID());
				if(rat != null && rat >= threshold && !rat.isNaN()) {
					nRelevantes++;	
					relevantesTotalesUsuario++;

				}	
			}
			
			//calculo del denominador de cada user(optimizado para los items ya explorados)
			auxHashSet = new HashSet<>(ratings.getItems(user));
			auxHashSet.removeAll(itemsExplorados); //quitamos los ya explorados
			for(Integer item:auxHashSet) {//vemos cuantos supera en threshold
				rat = ratings.getRating(user, item);
				if(rat != null && rat >= threshold && !rat.isNaN())relevantesTotalesUsuario++;
			}
			
			if(relevantesTotalesUsuario != 0) aux += (double) nRelevantes/relevantesTotalesUsuario; //contribucion al numerador de cada user

		}
		
		return (aux / rec.getUsers().size());

	}
	
	
	
	
	 @Override
	    public String toString() {
	        return "Recall@" + this.cutoff;
	  }
	

}
