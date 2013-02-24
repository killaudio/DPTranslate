package com.pigote.dpfinal.db;


public class WordDbTableEntry {
	 //private variables
    private int _id;
    private String _word;
    private String _definition;
    private String _uri;
 
    // Empty constructor
    public WordDbTableEntry(){
 
    }
    // constructor
    public WordDbTableEntry(int id, String word, String definition, String uri){
        this._id = id;
        this._word = word;
        this._definition = definition;
        this._uri = uri;
    }
 
    // constructor
    public WordDbTableEntry(String word, String definition, String uri){
        this._word = word;
        this._definition = definition;
        this._uri = uri;
    }
    // getting ID
    public int getID(){
        return this._id;
    }
 
    // setting id
    public void setID(int id){
        this._id = id;
    }
 
    // getting word
    public String getWord(){
        return this._word;
    }
 
    // setting word
    public void setWord(String word){
        this._word = word;
    }
 
    // getting definition
    public String getDefinition(){
        return this._definition;
    }
 
    // setting definition
    public void setDefinition(String definition){
        this._definition = definition;
    }

    // getting uri
    public String getUri(){
        return this._uri;
    }
 
    // setting uri
    public void setUri(String uri){
        this._uri = uri;
    }
    
} 
