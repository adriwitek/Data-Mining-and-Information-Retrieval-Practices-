package es.uam.eps.bmi.search.index.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

import es.uam.eps.bmi.search.index.AbstractIndexBuilder;
import es.uam.eps.bmi.search.index.Config;
import es.uam.eps.bmi.search.index.Index;
import es.uam.eps.bmi.search.index.structure.impl.RAMPostingsList;

public class SerializedRAMIndexBuilder extends AbstractIndexBuilder {

	HashMap<String, RAMPostingsList> index;
	ArrayList<String> paths;
	String indexPath;
	
	Integer tamanioTexto=0;

	
	@Override
	public void build(String collectionPath, String indexPath) throws IOException {
		
		this.indexPath = indexPath;
		
		clear(indexPath);
		
		this.index = new HashMap<>();
		this.paths = new ArrayList<>();
		
		// Buscar coleccion y acceder a ella.
        File f = new File(collectionPath);
        if (f.isDirectory()) indexFolder(f);                // A directory containing text files.
        else if (f.getName().endsWith(".zip")) indexZip(f); // A zip file containing compressed text files.
        else indexURLs(f);                                  // A file containing a list of URLs.
		
        savePaths(indexPath);
        serializarIndex(indexPath);
        saveDocNorms(indexPath);
	}

	@Override
	protected void indexText(String text, String path) throws IOException {
		
		String[] terms = text.toLowerCase().split("\\P{Alpha}+");
		
		// Le damos un ID al documento y lo guardamos junto con su path.
		int docID = this.paths.size();
		this.paths.add(path);
		
		RAMPostingsList termList;
		
		for (String term : terms) {
			
			termList = this.index.get(term);
			
			if (termList == null) {
				termList = new RAMPostingsList();
				this.index.put(term, termList);
			}
				
			termList.add( docID );
		}
	}


	@Override
	protected Index getCoreIndex() throws IOException {
		return new SerializedRAMIndex(this.indexPath);
	}

	
	protected void serializarIndex(String indexPath) throws IOException {
		
		FileOutputStream fos = new FileOutputStream(indexPath + "/" + Config.INDEX_FILE);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(this.index);
		oos.close();
		fos.close();
	}
	
	protected void savePaths(String indexPath) throws FileNotFoundException {
		
		int numDocs = paths.size();
		
        PrintStream out = new PrintStream(indexPath + "/" + Config.PATHS_FILE);
        
        out.println( numDocs );
        
        for (String doc : this.paths) {
            out.println( doc );
        }
        
        out.close();
	}
	
	
}
