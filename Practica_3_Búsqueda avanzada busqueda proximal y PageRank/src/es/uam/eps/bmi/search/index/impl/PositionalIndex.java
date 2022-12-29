package es.uam.eps.bmi.search.index.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collection;
import java.util.Scanner;

import es.uam.eps.bmi.search.index.AbstractIndex;
import es.uam.eps.bmi.search.index.Config;
import es.uam.eps.bmi.search.index.DocumentMap;
import es.uam.eps.bmi.search.index.Index;
import es.uam.eps.bmi.search.index.NoIndexException;
import es.uam.eps.bmi.search.index.structure.Dictionary;
import es.uam.eps.bmi.search.index.structure.Posting;
import es.uam.eps.bmi.search.index.structure.PostingsList;
import es.uam.eps.bmi.search.index.structure.impl.PositionalDictionary;
import es.uam.eps.bmi.search.index.structure.impl.PositionalPostingList;

//public class PositionalIndex extends AbstractIndex {
public class PositionalIndex extends AbstractIndex {
    PositionalDictionary dictionary;
    int numDocs;
    String docPaths[];
	
    
    
    public PositionalIndex(String indexFolder) throws IOException {
        loadPaths(indexFolder);
        loadNorms(indexFolder);
        File f = new File(indexFolder + "/" + Config.INDEX_FILE);
        if (!f.exists()) throw new NoIndexException(indexFolder);
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(f));
        try {
            dictionary = (PositionalDictionary) in.readObject();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        
        
    }

    public PositionalIndex(PositionalDictionary dic, int nDocs) {
    	 dictionary = dic;
         numDocs = nDocs;
    }
    
    
    
    public int numDocs() {
        return numDocs;
    }

    public PostingsList getPostings(String term) throws IOException {
        return dictionary.getPostings(term);
    }

    public Collection<String> getAllTerms() throws IOException {
        return dictionary.getAllTerms();
    }

    public long getTotalFreq(String term) throws IOException {
        long freq = 0;
        for (Posting p : getPostings(term)) freq += p.getFreq();
        return freq;
    }

    public long getDocFreq(String term) throws IOException {
        return dictionary.getDocFreq(term);
    }

    public String getDocPath(int docID) {
        return docPaths[docID];
    }

    public void loadPaths(String path) throws FileNotFoundException {
        File f = new File(path + "/" + Config.PATHS_FILE);
        if (!f.exists()) return;
        Scanner scn = new Scanner(f);
        numDocs = new Integer(scn.nextLine());
        docPaths = new String[numDocs];
        for (int docID = 0; docID < numDocs; docID++)
            docPaths[docID] = scn.nextLine();
        scn.close();
    }
    
}
