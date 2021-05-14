package es.uam.eps.bmi.sna.metric.network;

import java.util.HashSet;
import java.util.Set;

import es.uam.eps.bmi.sna.metric.GlobalMetric;
import es.uam.eps.bmi.sna.structure.UndirectedSocialNetwork;

public class Assortativity<U> implements GlobalMetric<U> {
	
	@Override
	public double compute(UndirectedSocialNetwork<U> network) {

		double S12 = 0, S2 = 0, S3 = 0, aux;
		int size;
		
		Set<U> users = network.getUsers();
		Set<U> contacts;
		Set<U> visit = new HashSet<>();
		
		for (U u : users) {
			
			contacts = new HashSet<>(network.getContacts(u));
			
			size = contacts.size();
			
			contacts.removeAll(visit);
			
			for (U v : contacts) {
				
				S12 += size * network.getContacts(v).size();
			}
			
			visit.add(u);
			
			aux = size * size;
			
			S2 += aux;
			S3 += aux * size;
		}
		
		S2 = S2 * S2;
		
		return ((4 * network.nEdges() * S12) - S2) / ((2 * network.nEdges() * S3) - S2);
	}

	@Override
	public String toString() {
		return "Assortativity";
	}
}
