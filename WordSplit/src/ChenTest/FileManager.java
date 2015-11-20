/**
 * From others
 */
package ChenTest;

import java.io.BufferedReader;  
import java.io.FileInputStream;  
import java.io.FileOutputStream;  
import java.io.IOException;  
import java.io.InputStreamReader;  
import java.io.OutputStreamWriter;  
  
public class FileManager {  
    public static String read(String fileName, String encoding) {  
        StringBuffer fileContent = new StringBuffer();  
        BufferedReader br = null;  
        try {  
             br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName),encoding));  
            String line = null;  
            while ((line = br.readLine()) != null) {  
                fileContent.append(line.trim());  
                fileContent.append("\n");  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        finally{  
            if(br!=null)  
                try {  
                    br.close();  
                } catch (IOException e) {  
                    // TODO Auto-generated catch block  
                    e.printStackTrace();  
                }  
        }  
        return fileContent.toString();  
    }  
  
    public static void write(String fileContent, String fileName,  
            String encoding) {  
        OutputStreamWriter osw = null;  
        try {  
            osw = new OutputStreamWriter(new FileOutputStream(fileName,false), encoding);  //false: cover   default
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