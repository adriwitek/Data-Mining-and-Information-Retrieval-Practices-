package es.uam.eps.bmi.recsys.metric;

import es.uam.eps.bmi.recsys.Recommendation;
import es.uam.eps.bmi.recsys.data.Ratings;
import es.uam.eps.bmi.recsys.ranking.Ranking;
import es.uam.eps.bmi.recsys.ranking.RankingElement;

public class Precision  implements Metric{

	
	
	private Ratings ratings;
	private double threshold; 
	private int  cutoff;

	public Precision(Ratings test , double threshold, int  cutoff) {
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
		
		if(cutoff == 0 || rec.getUsers().isEmpty()) return 0.0;
		
		for(Integer user :  rec.getUsers()) {
			cutoffCounter =0;
			nRelevantes = 0;
			rank =  rec.getRecommendation(user);
			for(RankingElement re : rank) { //Ranking recomendaciones de cada user
				if(cutoffCounter == cutoff) break;
				cutoffCounter++;
				rat = ratings.getRating(user, re.getID());
				if(rat != null && rat >= threshold && !rat.isNaN()) {
					nRelevantes++;	
				}	
			}
			aux +=(double) nRelevantes/cutoff; //contribucion al numerador de cada user
		}
		//System.out.println("Aux vale" + aux);
		return (aux / rec.getUsers().size());

	}
	
	
	
    @Override
    public String toString() {
        return "Precision@" + this.cutoff;
    }
}

