package es.uam.eps.bmi.search.index.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import es.uam.eps.bmi.search.index.structure.impl.RAMPostingsList;

public class HeapSerializedRAMIndexBuilder extends SerializedRAMIndexBuilder {

	
	//Clase para comprobar la ley del heap,modificando solo los metodos necesarios
	//De este modo no afectamos al rendimiento original
	
	
	private OutputStream fileLeyDeHeap;
	Integer tamanioTexto=0;
	
	
	
	public HeapSerializedRAMIndexBuilder() {
		super();
	}
	
	
	@Override
	public void build(String collectionPath, String indexPath) throws IOException {
		
		this.indexPath = indexPath;
		
		clear(indexPath);
		
		this.index = new HashMap<>();
		this.paths = new ArrayList<>();
		fileLeyDeHeap = new FileOutputStream(indexPath + File.separator + "leyDeHeap.txt");
		
		// Buscar coleccion y acceder a ella.
        File f = new File(collectionPath);
        if (f.isDirectory()) indexFolder(f);                // A directory containing text files.
        else if (f.getName().endsWith(".zip")) indexZip(f); // A zip file containing compressed text files.
        else indexURLs(f);                                  // A file containing a list of URLs.
		
        savePaths(indexPath);
        serializarIndex(indexPath);
        saveDocNorms(indexPath);
        
        fileLeyDeHeap.close();
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
		
		//ley del heap
		tamanioTexto += text.length(); 
		fileLeyDeHeap.write( (tamanioTexto + "\t" + index.keySet().size() + "\n").getBytes() );

		
	}

}
