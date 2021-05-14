package es.uam.eps.bmi.recsys.recommender;

import java.util.Set;

import es.uam.eps.bmi.recsys.data.Ratings;
import es.uam.eps.bmi.recsys.recommender.similarity.Similarity;

public class ItemNNRecommender  extends AbstractRecommender {

	private Similarity similitary;
	
	public ItemNNRecommender(Ratings ratings, Similarity similitary) {
		super(ratings);
		this.similitary = similitary;
	}

	@Override
	public double score(int user, int item) {
		
		Double score = 0.0;
		//System.out.println("--->");
		Double userRating;
		
		Set<Integer> userItems = super.ratings.getItems(user);
		//userItems.remove(item);
		
		for(Integer item2: userItems) {
			if(item2 == item)continue;
			userRating = super.ratings.getRating(user, item2);
			//if(userRating == 0) continue;
			score += userRating  * this.similitary.sim(item, item2);
		}

		return score;
	}

	
	@Override
	public String toString() {
        return "item-based ("  +this.similitary + ")";
	}

}
  