package es.uam.eps.bmi.recsys.recommender.similarity;

import java.io.FileNotFoundException;

import es.uam.eps.bmi.recsys.data.CentroidFeatures;
import es.uam.eps.bmi.recsys.data.Features;
import es.uam.eps.bmi.recsys.data.Ratings;

public class CosineFeatureSimilarity<F> extends FeatureSimilarity<F> {

	public CosineFeatureSimilarity(Features<F> features) {
		super(features);
	}

	@Override
	public double sim(int x, int y) {
		
		Double moduloX,moduloY,a,b,sum = 0.0;
		
		
		if(xFeatures.getFeatures(x) != null && yFeatures.getFeatures(y) != null) {
			moduloX = this.getModuloint(x, xFeatures);
			moduloY = this.getModuloint(y, yFeatures);
			if(moduloX == 0.0 || moduloY == 0.0) return 0.0;
			
			
			for (F f : xFeatures.getFeatures(x)) {
                a = xFeatures.getFeature(x, f);
                b = yFeatures.getFeature(y, f);
                if (a!=null && b!=null) sum += a * b ;
            }
			
			return sum/(moduloX*moduloY);
		}else {
			return 0.0;
		}
		
	}

	
	
	
	public double getModuloint(int i, Features<F> features ) {
		
		
		 if (features==null) return 0.0;
		 if(  features.getFeatures(i) == null ||  features.getFeatures(i).size()==0)  return 0.0;
		
		 Double sum = 0.0;
		 Double aux = 0.0;
		 for (F f: features.getFeatures(i)) {
	         aux = features.getFeature(i, f);
	         if (aux !=null) sum += aux * aux;
	     }
	    
		 return Math.sqrt(sum); 
	}
	
	
	
	public void setXFeatures(Ratings ratings) throws FileNotFoundException {
		xFeatures  = new CentroidFeatures<>(ratings,yFeatures);

	}
	
	@Override
	public String toString() {
        return "cosine";
    }
}
