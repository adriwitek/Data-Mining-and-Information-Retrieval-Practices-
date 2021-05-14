package es.uam.eps.bmi.recsys.recommender;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import es.uam.eps.bmi.recsys.data.CentroidFeatures;
import es.uam.eps.bmi.recsys.data.Ratings;
import es.uam.eps.bmi.recsys.metric.Metric;
import es.uam.eps.bmi.recsys.recommender.similarity.CosineFeatureSimilarity;
import es.uam.eps.bmi.recsys.recommender.similarity.CosineUserSimilarity;

public class CentroidRecommender<T> extends AbstractRecommender {


    private CosineFeatureSimilarity<T> features;

	
	public CentroidRecommender(Ratings ratings, CosineFeatureSimilarity<T> features) {
		super(ratings);
		this.features = features;
		try {
			features.setXFeatures(ratings);
		} catch (FileNotFoundException e) {
	
		}
		
	}

	
	
	
	
	
	
	
	
	 @Override
	 public double score(int user, int item) {
	       return features.sim(user, item);
	 }

	 @Override
	 public String toString(){
	    return "centroid-based ";
	 }
	
	
	
	


}
