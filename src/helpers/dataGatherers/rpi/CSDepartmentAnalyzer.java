package helpers.dataGatherers.rpi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Quick program to analyze the CS department homepage and find out who's
 * related to whom.
 */
public class CSDepartmentAnalyzer {
	private static final String INPUT = "rpidata/CSDeptWebpage";
	private static final String OUTPUT = "SchoolData/rpi/cs";

	public static void main(String[] args) throws IOException {
		File outputFile = new File(OUTPUT);
		if (!outputFile.exists()) {
			outputFile.createNewFile();
		}
		FileWriter output = new FileWriter(outputFile);
		BufferedReader br = new BufferedReader(new FileReader(new File(INPUT)));
		String line;
		while ((line = br.readLine()) != null) {

			if (line.startsWith("<p class=\"itemTitle\">")) {
				String firstName = line.substring(line.indexOf(',') + 1, line.indexOf("<span")).trim();
				String lastName = line.substring(21, line.indexOf(',')).trim();
				output.append(firstName + " " + lastName + "\n");
			}

		}
		br.close();
		output.flush();
		output.close();
	}
}
