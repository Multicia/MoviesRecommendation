/**
 * @project:TestSplit
 * @createtime:10:08:51 AM  Nov 4, 2015 
 * @author:Chen
 * One application of Kevin Zhang's NLPIR
 * Every line of the input represents a movie. 
 * Spliting the movies and outputting the key words.
 */
package ChenTest;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import kevin.zhang.NLPIR;

public class WordSplit {
	public static void main(String[] args) {
		String testfile = "./test/MyTest.txt";  //input
		try {
			splitWords(testfile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	static void splitWords(String vodFile) throws Exception {
		String outFile = "./test/OutTest.txt";  //output
		NLPIR nlpir = new NLPIR();
		if (!NLPIR.NLPIR_Init("./file/".getBytes("utf-8"), 1)) {     // 1:utf-8  0:gbk
			System.out.println("NLPIR initialization failed ...");
			return;
		}
		BufferedReader reader = null;
		reader = new BufferedReader(new InputStreamReader(new FileInputStream(vodFile),"UTF-8"));  
		String temp = null;
		String result = null;
		String strline = null;
		int line = 0;
		while ((temp = reader.readLine()) != null) {
			line ++;
		}
		strline = String.valueOf(line);
		appendFile(strline, outFile, "UTF-8"); // lines
		temp = null;
	    reader = new BufferedReader(new InputStreamReader(new FileInputStream(vodFile),"UTF-8"));  	 // read lines
		while ((temp = reader.readLine()) != null) {
			byte [] resBytes = nlpir.NLPIR_ParagraphProcess(temp.getBytes("UTF-8"), 1);   // Processing the results of words spliting
			result = contentFilter(new String(resBytes, "UTF-8"));                        // UTF-8 
			appendFile("\r\n", outFile, "UTF-8");                                         // Write to file with specific format
			appendFile(result, outFile, "UTF-8");
		}
		NLPIR.NLPIR_Exit();
	}
    public static String contentFilter(String content){
    	String result="";       
    	String strArray[]=content.split(" ");
    	String word;
    	String cixing;
    	int index,count=0;
    	int words=strArray.length;    	
    	for(int i=0;i<words;i++){
    		count++;
    		index=strArray[i].indexOf("/");  //  position of "/"
    		if(index>0){
    			word=strArray[i].substring(0,index);  //  words
    			if(word.length()<2){
    				continue;
    			}
				cixing=strArray[i].substring(index+1,strArray[i].length());	// the part of speech			
				if(cixing.startsWith("n")||cixing.startsWith("v")||cixing.startsWith("a")||cixing.startsWith("t")||cixing.startsWith("d")||cixing.startsWith("s")){
					result += "[" + word + "] ";
				}
    		}
    	}
    	return result;
    }
    public static void appendFile(String fileContent, String fileName, String encoding) {  
        OutputStreamWriter osw = null;  
        try {  
            osw = new OutputStreamWriter(new FileOutputStream(fileName,true), encoding);  
            osw.write(fileContent);  
            osw.flush();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        finally{  
            if(osw!=null)  
                try {  
                    osw.close();  
                } catch (IOException e) {  
                    // TODO Auto-generated catch block  
                    e.printStackTrace();  
                }  
        }  
    } 
}