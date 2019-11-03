package algorithms;

import java.awt.Point;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class GraphGenerator {
	private static String filename = "input.points";
	private static int numberOfPoints = 500;
	private static int maxWidth = 1400;
	private static int maxHeight = 900;
	private static int radius = 140;
	static byte edgeThreshold = 55;

	public GraphGenerator() {
	}

	public static double distanceToCenter(int x, int y) {
		return Math.min(Math.min(Math.min(Math.sqrt(Math.pow((double)(x - maxWidth / 2), 2.0D) + Math.pow((double)(y - maxHeight / 2), 2.0D)), Math.sqrt(Math.pow((double)x - 2.5D * (double)maxWidth / 6.0D, 2.0D) + Math.pow((double)(y - 2 * maxHeight / 6), 2.0D))), Math.min(Math.sqrt(Math.pow((double)(x - 4 * maxWidth / 6), 2.0D) + Math.pow((double)(y - 2 * maxHeight / 6), 2.0D)), Math.sqrt(Math.pow((double)(x - 2 * maxWidth / 6), 2.0D) + Math.pow((double)(y - 4 * maxHeight / 6), 2.0D)))), Math.sqrt(Math.pow((double)(x - 4 * maxWidth / 6), 2.0D) + Math.pow((double)(y - 4 * maxHeight / 6), 2.0D)));
	}

	public static void main(String[] args) {
		try {
			for(int e = 0; e < 100; ++e) {
				int numberOfPoints = (e+1)*10;
				PrintStream output = new PrintStream(new FileOutputStream("testlength/input" + numberOfPoints + ".points"));
				Random generator = new Random();
				ArrayList<Point> points = new ArrayList<>();
				System.out.println("e vaut " + e);
				//for(int i = 0; i < numberOfPoints; ++i) {
				while(points.size()!=numberOfPoints){
					int x;
					int y;
					int deg;
					do {
						do {
							x = generator.nextInt(maxWidth);
							y = generator.nextInt(maxHeight);
						} while(distanceToCenter(x, y) >= (double)radius * 1.4D && (distanceToCenter(x, y) >= (double)radius * 1.6D || generator.nextInt(5) != 1) && (distanceToCenter(x, y) >= (double)radius * 1.8D || generator.nextInt(10) != 1) && (maxHeight / 9 >= x || x >= 4 * maxHeight / 5 || maxHeight / 9 >= y || y >= 7 * maxHeight / 9 || generator.nextInt(100) != 1));

						Point p = new Point(x, y);
						deg = 0;
						Iterator<Point> var11 = points.iterator();

						while(var11.hasNext()) {
							Point q = (Point)var11.next();
							if(p.distance(q) <= (double)edgeThreshold) {
								++deg;
							}
						}
					} while(deg >= 5);
					points.add(new Point(x, y));

				}
				//}

				output.close();
			}
		} catch (FileNotFoundException var13) {
			var13.printStackTrace();;
		}

	}

	private static boolean connecte(Point point, ArrayList<Point> points) {
		if(points.isEmpty()) return true;
		for(Point p: points){
			if(point.distance(p)<=(double)edgeThreshold) return true;
		}
		return false;
	}
	

	public static ArrayList<Point> generate(int nbPoints) {
			Random generator = new Random();
			ArrayList<Point> res = new ArrayList<Point>();
			for(int i = 0; i < nbPoints; ++i) {
				int x;
				int y;
				do {
					x = generator.nextInt(maxWidth);
					y = generator.nextInt(maxHeight);
				} while(distanceToCenter(x, y) >= (double)radius * 1.4D && (distanceToCenter(x, y) >= (double)radius * 1.6D || generator.nextInt(5) != 1) && (distanceToCenter(x, y) >= (double)radius * 1.8D || generator.nextInt(10) != 1) && (maxHeight / 5 >= x || x >= 4 * maxHeight / 5 || maxHeight / 5 >= y || y >= 4 * maxHeight / 5 || generator.nextInt(100) != 1));
				res.add(new Point(x, y));
			}
			return res;
	}
}