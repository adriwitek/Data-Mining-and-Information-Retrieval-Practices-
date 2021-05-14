package es.uam.eps.bmi.recsys.recommender.similarity;

import java.util.HashSet;
import java.util.Set;

import es.uam.eps.bmi.recsys.data.Features;
import es.uam.eps.bmi.recsys.data.Ratings;

public class JaccardFeatureSimilarity<T> extends FeatureSimilarity<T> {

	
    public JaccardFeatureSimilarity(Features<T> features) {
        super(features);

    	
    }
    
    
    
    @Override
    public double sim(int x, int y) {
    	
    	Set<T> xFeatures = this.getFeatures().getFeatures(x);
    	Set<T> yFeatures = this.getFeatures().getFeatures(y);
    	int xTam;
    	
    	if(xFeatures == null || yFeatures == null || xFeatures.size() == 0 || yFeatures.size() == 0 ) return 0.0;
    	
    	
        xTam = xFeatures.size();
        xFeatures.retainAll(yFeatures);//union

    	return xFeatures.size()/  (xTam + yFeatures.size()  - xFeatures.size());
    }
    
    
    
    
    @Override
    public String toString() {
        return  "JaccardFeatureSimilarity";
    }

}
