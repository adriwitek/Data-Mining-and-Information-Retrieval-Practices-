package es.uam.eps.bmi.search.index.structure.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import es.uam.eps.bmi.search.index.structure.Posting;
import es.uam.eps.bmi.search.index.structure.PostingsList;
import es.uam.eps.bmi.search.index.structure.positional.PositionalPosting;

//public class PositionalPostingList implements Iterable<PositionalPosting>,Serializable {
public class PositionalPostingList implements PostingsList,Serializable {


    protected List<PositionalPosting> postings;

	
    public PositionalPostingList() {
        postings = new ArrayList<PositionalPosting>();
    }
    
    
    public PositionalPostingList(int docID,int position) {
        this();
        add(docID,position);
    }
    
    
    
    
    public int size() {
        return postings.size();
    }

	
	
	@Override
	public Iterator<Posting> iterator() {
		List<Posting> l=  (List<Posting> ) (List<?>)postings;
        return l.iterator() ;
	}
	
	
	 public void add(int docID,int pos) {
		 List<Integer> listaPosiciones ;
	     if (!postings.isEmpty() && docID == postings.get(postings.size() - 1).getDocID()) {
	    	 
	    	 listaPosiciones = postings.get(postings.size() - 1).getPositions();
	    	 listaPosiciones.add(pos);
	    	 postings.get(postings.size() - 1).add1();//aumentamos la freq de posting

	     }else {
	    	 listaPosiciones = new ArrayList<>();
	    	 listaPosiciones.add(pos);
	    	 this.postings.add(new PositionalPosting(docID, 1,listaPosiciones));
	    	;
	     } 
	 }
	
	
	// docIDs are supposed to be added by increasing docID
    public void add(int docID, long freq, List<Integer> posiciones) {
        add(new PositionalPosting(docID ,  freq,  posiciones));
    }

    
    
    protected void add(PositionalPosting posting) {
        postings.add(posting);
    }
    
    
 
}
