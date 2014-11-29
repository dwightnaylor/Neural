package helpers.dataGatherers;

import helpers.dataStructures.UnorderedPair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashSet;

public class SpringerSearcher {

	private static final String[] inputFiles = new String[] { "SchoolData/rpi/cs" };
	private static String outputFileDir = "SchoolData/rpi/csLinks";
	static HashSet<String> names = new HashSet<String>();
	static HashSet<UnorderedPair<String>> links = new HashSet<UnorderedPair<String>>();

	public static void main(String[] args) throws IOException {
		for (String file : inputFiles) {
			BufferedReader br = new BufferedReader(new FileReader(new File(file)));
			String line;
			while ((line = br.readLine()) != null) {
				names.add(line);
			}
			br.close();
		}
		for (String name : names) {
			String url = "http://link.springer.com/search?query=" + name.replace(' ', '+');
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
				String line;
				while ((line = br.readLine()) != null) {
					if (line.trim().startsWith("<a href=\"/search?facet-author=")) {
						String subName = line.substring(line.indexOf("\">") + 2, line.indexOf("</a>"));
						if (names.contains(subName) && !name.equals(subName)) {
							links.add(new UnorderedPair<String>(name, subName));
						}
					}
				}
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("READ:" + url);
		}
		File outputFile = new File(outputFileDir);
		if (!outputFile.exists()) {
			outputFile.createNewFile();
		}
		FileWriter output = new FileWriter(outputFile);
		for (UnorderedPair<String> s : links) {
			output.append(s.first() + ":" + s.second() + "\n");
		}
		output.flush();
		output.close();
	}
}
