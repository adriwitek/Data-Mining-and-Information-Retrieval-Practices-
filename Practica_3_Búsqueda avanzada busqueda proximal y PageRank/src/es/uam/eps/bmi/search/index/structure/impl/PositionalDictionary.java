package es.uam.eps.bmi.search.index.structure.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import es.uam.eps.bmi.search.index.structure.Dictionary;
import es.uam.eps.bmi.search.index.structure.EditablePostingsList;
import es.uam.eps.bmi.search.index.structure.PostingsList;
import es.uam.eps.bmi.search.index.structure.positional.PositionalPosting;
public class PositionalDictionary implements Dictionary   {

	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected HashMap<String,PositionalPostingList> termPostings;

    public PositionalDictionary() {
        termPostings = new HashMap<String,PositionalPostingList >();
    }    

	
    
    public PositionalPostingList getPostings(String term) {
        return termPostings.get(term);
    }
    
    

    
    // We assume docIDs are inserter by order
    public void add(String term, int docID, int pos) {
        if (termPostings.containsKey(term)) {
        	termPostings.get(term).add(docID,pos);
        } 
        else {
        	termPostings.put(term, new PositionalPostingList (docID,pos));}
    }

    
    public Collection<String> getAllTerms() {
        return termPostings.keySet();
    }
    
    
    public long getDocFreq(String term) throws IOException {
        return termPostings.get(term).size();
    }



	
	
}