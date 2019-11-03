package algorithms;

import java.awt.Point;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.Stack;



public class TestSize {
	
	public class Graph
	{
		public HashMap<Point, HashSet<Point>> adjacency;
		
		public HashMap<Point, HashSet<Point>> getAdjacency(){
			return adjacency;
		}


		public Graph(ArrayList<Point> points, int edgeThreshold)
		{

			this.adjacency = new HashMap<>();

			
			for (Point p : points)
				adjacency.put(p, new HashSet<>());
			
			int size = points.size();
			for (int i = 0; i < size; ++i)
			{
				for (int j = i + 1; j < size; ++j)
				{
					Point p1 = points.get(i);
					Point p2 = points.get(j);
					if(p1.distance(p2) <= edgeThreshold)
					{
						adjacency.get(p1).add(p2);
						adjacency.get(p2).add(p1);
					}
				}
			}
		}
		
		public HashSet<Point> getNeighbors(Point p)
		{
			return adjacency.get(p);
		}

	

		public int degree(Point p)
		{
			return adjacency.get(p).size();
		}
		

	}
	
	
	public ArrayList<Point> removePoints(ArrayList<Point> points, int edgeThreshold){
		//bfs over the graph
		Graph g = new Graph(points, edgeThreshold);
		HashMap<Point, Boolean> marked = new HashMap<Point, Boolean>();
		Stack<Point> fifo = new Stack<Point>();
		HashMap<Integer, ArrayList<Point>> compo = new HashMap<>();

		int cpt = 0;
		fifo.add(points.get(0));
		marked.put(points.get(0), true);
		compo.put(cpt, new ArrayList<>());
		compo.get(cpt).add(points.get(0));
		
		for(Point p1: g.getAdjacency().keySet()) {
			marked.put(p1, false);
		}
		
		boolean flag = false;
		 do {
			 flag = false;
			 

			 
			while(!fifo.isEmpty()) {
				Point p = fifo.pop();
				Set<Point> voisins = g.getNeighbors(p);
				for(Point v: voisins) {
					if(!marked.get(v)) {
						compo.get(cpt).add(v);
						fifo.add(v);
						marked.put(v, true);
					}
				}
			}
			for(Entry<Point,Boolean> m:marked.entrySet()) {
				if(!m.getValue()) {
					fifo.add(m.getKey());
					marked.put(m.getKey(), true);
					compo.put(++cpt,new ArrayList<Point>());
					compo.get(cpt).add(m.getKey());
					flag = true;
					
				}
			}
		}while(flag);
		 
		 //return max compo
		 int max_size = 0;
		 ArrayList<Point> maxCompo = null;
		 for(ArrayList<Point> c: compo.values()) {
			 if(c.size() > max_size) {
				 max_size = c.size();
				 maxCompo = c;
			 }
		 }
		 //System.out.println(compo);
		 return maxCompo;
	}
	
	public static void main(String[] args) {
		for(int e = 50; e < 120; ++e) {
			int numberOfPoints = (e+1)*10;
			System.out.println(numberOfPoints);
			ArrayList<Point> pts = GraphGenerator.generate(numberOfPoints);
			TestSize ts = new TestSize();
			pts = ts.removePoints(pts, GraphGenerator.edgeThreshold);
			String filename = "testlength/input" + pts.size() + ".points";
			DefaultTeam.printToFile(filename, pts);
			System.out.println("wrote file " + filename);
		}
	}
}
