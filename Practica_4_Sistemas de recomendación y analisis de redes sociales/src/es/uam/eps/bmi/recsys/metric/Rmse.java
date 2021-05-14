package es.uam.eps.bmi.recsys.metric;

import es.uam.eps.bmi.recsys.Recommendation;
import es.uam.eps.bmi.recsys.data.Ratings;
import es.uam.eps.bmi.recsys.ranking.Ranking;
import es.uam.eps.bmi.recsys.ranking.RankingElement;

public class Rmse implements Metric{

	private Ratings ratings;
	
	
	
	public Rmse(Ratings test) {
		this.ratings = test;
	}

	
	
	@Override
	public double compute(Recommendation rec) {

		Double aux = 0.0;
		Double aux2 = 0.0;
		Ranking rank;
		Double rat = 0.0;
		int nElementos = 0;
		
		for(Integer user :  rec.getUsers()) {
			
			rank =  rec.getRecommendation(user);
			for(RankingElement re : rank) {
				rat = ratings.getRating(user, re.getID());
				if(rat != null) {
					aux2 =  Math.pow( (re.getScore()-rat)   , 2);
					if(aux2.isNaN()) aux2 = 0.0;
					aux +=aux2;
					nElementos++;
				}
			}
		}
		
		
		if(nElementos != 0) {
			return Math.sqrt( aux/nElementos    );
		}else {
			return 0.0;
		}
		
	}
	
	
	
    @Override
    public String toString() {
        return "Rmse ";
    }
}
