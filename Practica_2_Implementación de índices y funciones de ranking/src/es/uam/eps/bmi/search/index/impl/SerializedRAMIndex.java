package es.uam.eps.bmi.search.index.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Scanner;

import es.uam.eps.bmi.search.index.AbstractIndex;
import es.uam.eps.bmi.search.index.Config;
import es.uam.eps.bmi.search.index.NoIndexException;
import es.uam.eps.bmi.search.index.structure.Posting;
import es.uam.eps.bmi.search.index.structure.PostingsList;
import es.uam.eps.bmi.search.index.structure.impl.RAMPostingsList;

public class SerializedRAMIndex extends AbstractIndex {

	HashMap<String, PostingsList> index;
	ArrayList<String> paths;
	
	public SerializedRAMIndex(String indexFolder) throws NoIndexException {
		
		try {
			// Cargamos el indice
			loadIndex(indexFolder);
	        
			// Cargamos el fichero de paths.
			loadPathFile(indexFolder);
			
			// Cargamos los modulos
			loadNorms(indexFolder);
        
		} catch (Exception ex) {

            throw new NoIndexException(indexFolder);
        }
	}

	@Override
	public int numDocs() {
		return this.paths.size();
	}

	@Override
	public PostingsList getPostings(String term) throws IOException {
		
		PostingsList lista = this.index.get(term);
		
		if (lista == null) lista = new RAMPostingsList();
		
		return lista;
	}

	@Override
	public Collection<String> getAllTerms() throws IOException {
		return this.index.keySet();
	}

	@Override
	public long getTotalFreq(String term) throws IOException {
		
		long totalFreq = 0;
		
		for (Posting posting : this.getPostings(term)) {
			
			totalFreq += posting.getFreq();
		}
		
		return totalFreq;
	}

	@Override
	public long getDocFreq(String term) throws IOException {
		return this.getPostings(term).size();
	}

	@Override
	public String getDocPath(int docID) throws IOException {
		return paths.get(docID);
	}

	
	private void loadIndex(String indexFolder) throws ClassNotFoundException, IOException {
		
		// Cargamos el indice
		FileInputStream fis = new FileInputStream(indexFolder + "/" + Config.INDEX_FILE );
		ObjectInputStream ois = new ObjectInputStream(fis);
		
		this.index = (HashMap<String, PostingsList>) ois.readObject();
		
		ois.close();
		fis.close();
	}
	
	private void loadPathFile(String indexFolder) throws NoIndexException, FileNotFoundException {
		
		// Cargamos el fichero de paths.
		File f = new File(indexFolder + "/" + Config.PATHS_FILE);
		
        if (!f.exists()) throw new NoIndexException(indexFolder);
        
        Scanner scn = new Scanner(f);
        
        int numDocs = new Integer(scn.nextLine());
        this.paths = new ArrayList<>();
        
        for (int docID = 0; docID < numDocs; docID++) {
        	
        	paths.add( scn.nextLine() );
		}
        
        scn.close();
	}
}
