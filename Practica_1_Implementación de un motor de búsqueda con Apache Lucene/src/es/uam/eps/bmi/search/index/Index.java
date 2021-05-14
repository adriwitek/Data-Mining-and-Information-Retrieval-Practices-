package es.uam.eps.bmi.search.index;

import java.io.IOException;
import java.util.ArrayList;

import es.uam.eps.bmi.search.index.freq.FreqVector;

public interface Index {

	public ArrayList<String> getAllTerms() throws IOException;

	public long getTotalFreq(String t) throws IOException;

	public FreqVector getDocVector(int docID) throws IOException;

	public String getDocPath(int docID) throws IOException;

	public long getTermFreq(String word, int docID) throws IOException;

	public long getDocFreq(String word) throws IOException;
	
}
