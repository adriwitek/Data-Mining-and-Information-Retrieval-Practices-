package es.uam.eps.bmi.search.index.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.jsoup.Jsoup;

import es.uam.eps.bmi.search.index.AbstractIndexBuilder;
import es.uam.eps.bmi.search.index.Config;
import es.uam.eps.bmi.search.index.DocumentMap;
import es.uam.eps.bmi.search.index.Index;
import es.uam.eps.bmi.search.index.structure.EditableDictionary;
import es.uam.eps.bmi.search.index.structure.Posting;
import es.uam.eps.bmi.search.index.structure.impl.HashDictionary;
import es.uam.eps.bmi.search.index.structure.impl.PositionalDictionary;
import es.uam.eps.bmi.search.vsm.AbstractVSMEngine;

public class PositionalIndexBuilder extends AbstractIndexBuilder  {

	 int nDocs;
	 PositionalDictionary dictionary;
	 List<String> docPaths;
	 

	 public void build (String collectionPath, String indexPath) throws IOException {
	        init(indexPath);
	        indexDocuments(collectionPath);
	        close(indexPath);
	    }

	    public void init(String indexPath) throws IOException {
	        clear(indexPath);
	        nDocs = 0;
	        dictionary = new PositionalDictionary();
	        docPaths = new ArrayList<String>();
	    }
	 
	    

	    public void close(String indexPath) throws IOException {
	        save(indexPath);
	        saveDocPaths(indexPath);
	        saveDocNorms(indexPath);
	    }
	    
	    public void saveDocPaths(String indexPath) throws IOException {
	        PrintStream out = new PrintStream(indexPath + "/" + Config.PATHS_FILE);
	        out.println(nDocs);
	        for (String path : docPaths)
	            out.println(path);
	        out.close();
	    }
	    
	    
	    	    
	    
	  public void indexText(String text, String path) throws IOException {
		  int contadorTerminos =0;
	        for (String term : text.toLowerCase().split("\\P{Alpha}+")) {
	            dictionary.add(term, nDocs,contadorTerminos);
	            contadorTerminos++;
	        }
	        docPaths.add(path);
	        nDocs++;
	    }

	  
	  
	  
	  
	  public void save(String indexPath) throws IOException {
	        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(indexPath + "/" + Config.INDEX_FILE));
	        out.writeObject(dictionary);
	        out.close();
	    }

	    protected Index getCoreIndex() {
	        return new PositionalIndex(dictionary, nDocs);
	    }
	  
	  
	  

	 
}
