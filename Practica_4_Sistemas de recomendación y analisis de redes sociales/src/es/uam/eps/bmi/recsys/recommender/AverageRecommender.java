package es.uam.eps.bmi.recsys.recommender;

import java.util.HashMap;
import java.util.Map;

import es.uam.eps.bmi.recsys.data.Ratings;

public class AverageRecommender extends AbstractRecommender {
	
	Map<Integer, Double> ratingSum;
	
	public AverageRecommender(Ratings ratings, int min) {
		
		super(ratings);
		
        ratingSum = new HashMap<Integer, Double>();
        
        for (int item : ratings.getItems()) {
        	
            double sum = 0;
            int cont = 0;
            
            for (int u : ratings.getUsers(item)) {
            	
                sum += ratings.getRating(u, item);
                cont++;
            }

            if (cont >= min)  ratingSum.put(item, sum / cont);
            else              ratingSum.put(item, 0.0);
        }
	}
	
	public double score (int user, int item) {
        return ratingSum.get(item);
    }

    public String toString() {
        return "average";
    }
}
