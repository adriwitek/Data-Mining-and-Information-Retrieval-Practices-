package es.uam.eps.bmi.sna.metric.network;

import es.uam.eps.bmi.sna.metric.GlobalMetric;
import es.uam.eps.bmi.sna.metric.user.UserClusteringCoefficient;
import es.uam.eps.bmi.sna.ranking.Ranking;
import es.uam.eps.bmi.sna.ranking.RankingElement;
import es.uam.eps.bmi.sna.structure.UndirectedSocialNetwork;

public class AvgUserMetric<U extends Comparable<U>> implements GlobalMetric<U> {

	private UserClusteringCoefficient<U> userClusteringCoefficient;
	
	public AvgUserMetric(UserClusteringCoefficient<U> userClusteringCoefficient) {
		
		this.userClusteringCoefficient = userClusteringCoefficient;
	}

	@Override
	public double compute(UndirectedSocialNetwork<U> network) {
		
		double avg = 0;
		
		Ranking<U> rank = this.userClusteringCoefficient.compute(network);
		
		for (RankingElement<U> data : rank) {
			
			avg += data.getScore();
		}
		
		if (rank.size() == 0) return 0;
		
		return avg / (double)rank.size();
	}

	@Override
	public String toString() {
		return "Avg(UserClusteringCoefficient)";
	}
}
