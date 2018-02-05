package model;

import java.io.*;

/**
 * @author Woon Jeen Tang 
 * @Section: Tuesday and Thursday
 * @CourseNumber: COEN 275
 * @AssignmentNumber: Final Project
 * @DateOfSubmission: 3/2014
 * 
 */
public class Serialize_Deserialize {
	static final String dir = "./";
	static final String fileName = "RMOS.ser";

	// Serialize & Deserialize Object
	public static void serializeObject(Object obj) {
		FileOutputStream fout = null;
		ObjectOutputStream out = null;

		try {
			fout = new FileOutputStream(fileName);
			out = new ObjectOutputStream(fout);
			out.writeObject(obj);
			out.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static RMOS deserializeRMOS() {
		String file = dir + fileName;
		RMOS result = null;
		FileInputStream fin = null;
		ObjectInputStream in = null;

		File f = new File(dir);

		File[] matchingFiles = f.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.equals(fileName);
			}
		});

		if (matchingFiles.length == 1) {
			try {
				fin = new FileInputStream(file);
				in = new ObjectInputStream(fin);
				result = (RMOS) in.readObject();
				fin.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
}
