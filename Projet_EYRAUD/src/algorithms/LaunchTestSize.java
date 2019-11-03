package algorithms;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;
public class LaunchTestSize {
	
	private static byte edgeThreshold = 55;
	public static void launchHeuristic(String filename, PrintStream output) throws IOException, InterruptedException {
		Runtime rt = Runtime.getRuntime();
		Process pr = rt.exec("python graphegen.py " + filename + " " + edgeThreshold);
		BufferedReader br = new BufferedReader( new InputStreamReader(pr.getInputStream()));
		String time_last = br.readLine();
		String nb_points = br.readLine();
		
		output.print(time_last + " " + nb_points);
		pr.waitFor();
	}
	
	public static void main(String [] args) throws IOException, InterruptedException {
		  File dir = new File("testlength");
		  File[] directoryListing = dir.listFiles();
		  PrintStream output = new PrintStream(new FileOutputStream("reslength.txt"));
		  if (directoryListing != null) {
		    for (File child : directoryListing) {
		      DefaultTeam dt = new DefaultTeam();
		      System.out.println("Processing file " + child.getName());
		      String intValue = child.getName().replaceAll("[^0-9]", ""); 
		      ArrayList<Point> points = dt.readFromFile(child.getAbsolutePath());
		      long time_b = System.currentTimeMillis();
		      ArrayList<Point> cds = dt.calculConnectedDominatingSet1(points, GraphGenerator.edgeThreshold);
		      long time_a = System.currentTimeMillis();
		      long time_last = time_a - time_b;
		      
		      output.print(intValue + " " + time_last + " " + cds.size() + " ");
		      
		      /*
		      time_b = System.currentTimeMillis();
		      cds = dt.calculConnectedDominatingSet2(points, GraphGenerator.edgeThreshold);
		      time_a = System.currentTimeMillis();
		      time_last = time_a - time_b;
		      output.println(time_last + " " + cds.size() + " ");*/
		      launchHeuristic(child.getAbsolutePath(), output);
		      output.println();
		      
		    }
		  } else {
		    System.out.println("directory not found");
		  }
		  output.close();
	}
}
