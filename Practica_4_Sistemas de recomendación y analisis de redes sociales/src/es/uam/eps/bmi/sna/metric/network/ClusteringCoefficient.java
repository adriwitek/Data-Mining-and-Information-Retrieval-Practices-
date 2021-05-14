package es.uam.eps.bmi.sna.metric.network;

import java.util.HashSet;
import java.util.Set;

import es.uam.eps.bmi.sna.metric.GlobalMetric;
import es.uam.eps.bmi.sna.structure.UndirectedSocialNetwork;

public class ClusteringCoefficient<U> implements GlobalMetric<U> {
	
	@Override
	public double compute(UndirectedSocialNetwork<U> network) {

		int num = 0, den = 0;
		
		Set<U> users = network.getUsers();
		Set<U> contacts, neighbor;
		
		for (U u : users) {
			
			contacts = network.getContacts(u);
			
			for (U v : contacts) {

				neighbor = new HashSet<>(network.getContacts(v));
				
				neighbor.retainAll(contacts);
				
				num += neighbor.size();
				den += contacts.size() - 1 - neighbor.size();
			}
		}

		return num / ((double)den + num);
	}

	@Override
	public String toString() {
		return "ClusteringCoefficient";
	}
}
