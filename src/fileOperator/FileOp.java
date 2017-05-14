package fileOperator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import fileOperator.FileOp;

public class FileOp {
	public static void createFile(String fileName) throws IOException {
		String filePath = "./Models/" + fileName;
		File pomdpModelFile = new File(filePath);
		if (!pomdpModelFile.exists()) {
			pomdpModelFile.createNewFile();
		} else {
			FileOp.cleanUpFileContent(pomdpModelFile);
		}
	}

	public static boolean cleanUpFileContent(File file) {
		if (file.exists()) {
			try {
				BufferedWriter pomdpBufferWriter = new BufferedWriter(new FileWriter(file));
				pomdpBufferWriter.write("");
				pomdpBufferWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public static ArrayList<String> readFileContent(String fileName) {
		ArrayList<String> pomdpFileContent = new ArrayList<String>();
		String filePath = "./Models/" + fileName;
		File pomdpModelFile = new File(filePath);
		if (pomdpModelFile.exists()) {
			try {
				BufferedReader pomdpBufferReader = new BufferedReader(new FileReader(pomdpModelFile));
				String tempContent = pomdpBufferReader.readLine();
				while (tempContent != null) {
					pomdpFileContent.add(tempContent);
					tempContent = pomdpBufferReader.readLine();
				}
				pomdpBufferReader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return pomdpFileContent;
	}

	public static boolean appendFileContent(String fileName, String content) {
		String filePath = "./Models/" + fileName;
		File pomdpModelFile = new File(filePath);
		if (pomdpModelFile.exists()) {
			try {
				BufferedWriter pomdpBufferWriter = new BufferedWriter(new
				FileWriter(pomdpModelFile, true));
				pomdpBufferWriter.write(content);
				pomdpBufferWriter.close(); 
				return true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}
	public static Object readObj(String filename){
		ObjectInputStream ois=null;
		File file = new File("./Solver/" + filename);
		Object ob=null;
		try {
			ois=new ObjectInputStream(new FileInputStream(file));
			ob=ois.readObject();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				ois.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		 
		return ob;
		
	}
	public static void writeObj(Object ob,String filename){
		 ObjectOutputStream oos =null;       
		 File file = new File("./Solver/" + filename);
		 
		try {
			 if (!file.exists()) {
				 file.createNewFile();
			} 
			 else {
				FileOp.cleanUpFileContent(file);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
         try {
        	 if(file.exists()){
        		 oos = new ObjectOutputStream(
    			         new FileOutputStream(file));
        		 oos.writeObject(ob);
        	 }
        	 else System.out.println("文件不存在");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				oos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
}
