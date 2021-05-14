package es.uam.eps.bmi.recsys.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class CentroidFeatures<T> implements Features<T> {

	private HashMap<Integer, HashMap<T, Double>> features;

	
	public CentroidFeatures(Ratings ratings,  Features<T> features2) throws FileNotFoundException {
		
		HashMap<Integer, HashMap<T, Double>> scores = new HashMap<>();
		features = scores;
		
		Double r =0.0;
		Double score;
		
        for(Integer user: ratings.getUsers()){
            for(int item: ratings.getItems(user)) {
            	r = ratings.getRating(user, item);
            	if(features2.getFeatures(item)!=null) {
            		
            		
            		for(T f :features2.getFeatures(item)) {
                        if (features2.getFeature(user, f)!=null) {//a
                        	score = r * features2.getFeature(item, f);
                        	
                        	if(this.features.get(user) != null && this.getFeature(user,  f) != null) {
                        		score += getFeature(user,  f);
                        	}
                    		this.setFeature(user, f, score);
                        }

                		
                	}
            	}
            	

            	
            	
            }
        }
		
	}
	
	
	
	
	@Override
	public Set<T> getFeatures(int id) {
		 if (features.containsKey(id)) {
	            return this.features.get(id).keySet();
		 }else {
			 return null;
		 }
	}

	@Override
	public Double getFeature(int id, T feature) {
		return this.features.get(id).get(feature);
	}

	@Override
	public void setFeature(int id, T feature, double value) {
		
		//this.features.get(id).get(feature);
		
		HashMap<T, Double> Suser = this.features.get(id);
		
		if (Suser == null) Suser = new HashMap<>();
		
		Suser.put(feature, value);
		
		this.features.put(id, Suser);
	}

	@Override
	public Set<Integer> getIDs() {
		return this.features.keySet();
	}
	
	
}
