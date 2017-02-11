package crawler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import org.apache.xmlbeans.impl.common.Mutex;

public class FileHandler {
	
	final static String FETCHED_LIST = "fetch_NBC_News.csv";
	final static String VISITED_LIST = "visited_NBC_News.csv";
	final static String URLs_LIST = "URLs_NBC_News.csv";
    final char DEFAULT_SEPARATOR = ',';
    File file = null;
    Writer writer =null;
	
	final String PATH = "/home/akash/Documents/Crawler/Crawler/output/";	
	
	public File createFile(String fileName){
		this.file=new File(PATH+fileName);;
		try {			
				if(file.exists())
					file.delete();
				else				
					file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
		}
		return file;
	}
	
	public void writeLine(Writer w, String[] values){
		try{
			writeLine(w, values, DEFAULT_SEPARATOR);
		}catch(Exception e){
			e.printStackTrace();
		}        
    }

    public void writeLine(Writer w, String[] values, char separators) throws IOException {
        writeLine(w, values, separators, ' ');
    }
    private void writeLine(Writer w, String[] values, char separators, char customQuote) throws IOException {
        
        StringBuilder tempString = new StringBuilder();
        boolean first = true;

        if (separators == ' ') {
            separators = DEFAULT_SEPARATOR;
        }
        
        for (String value : values) {            
            if (customQuote == ' '){
            	if(first){
            		tempString.append(followCVSformat(value));
            		first =false;
            	}else tempString.append(separators+followCVSformat(value));
            }else tempString.append(customQuote).append(followCVSformat(value)).append(customQuote);
        }        
        tempString.append("\n");
        w.append(tempString.toString());
    }
    private String followCVSformat(String value) {        
        if (value.contains("\"")) return value.replace("\"", "\"\"");        
        else return value;
    }
    
    public File getFile(){
    	return this.file;    	
    }
    
    public void setFile(File file){
    	 if(file !=null)
    		 this.file =file;    	 
    }
    
    public void closeFileHandler(){
	    try{
	    	this.writer.close();    	
	    }catch(Exception e){
	    	e.printStackTrace();
	    }       
    }
    
    public Writer getWriter(File file){
    	try{
    	if(this.writer != null)
    		return this.writer;
    	else if(file !=null)
    		this.writer = new FileWriter(file.getAbsoluteFile(),true);
    	else if(this.file !=null)
    		this.writer = new FileWriter(this.file.getAbsoluteFile(),true);
    	else 
    		this.writer = null;
    	}catch(IOException e){
    		System.out.println("failed to create File writer :"+e.getMessage());
    		e.printStackTrace();
    	}
    	return this.writer;   	
    }
}
