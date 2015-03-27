
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * Junior
 */
public class FileIOUtils {
    static boolean appendTextToFile(String fileName,String contents){
        try{
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)));
            out.println(contents);
            out.close();
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
