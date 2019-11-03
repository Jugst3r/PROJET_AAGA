
package algorithms;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.toMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Map.Entry;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import algorithms.Vertex.Color;


/*
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
*/

public class DefaultTeam
{
	public class Pair<L,R>
	{
		  public final L left;
		  public final R right;

		  public Pair(L left, R right)
		  {
		    this.left = left;
		    this.right = right;
		  }
	}
	
	
	// A union by rank and path compression 
	// based program to detect cycle in a graph 

		  
	// class to represent Subset 
	class subset 
	{ 
	    int parent; 
	    int rank; 
	} 
	  
	// A utility function to find  
	// set of an element i (uses  
	// path compression technique) 
	int find(ArrayList<subset> subsets , int i) 
	{ 
	if (subsets.get(i).parent != i) 
	    subsets.get(i).parent = find(subsets,  
	                             subsets.get(i).parent); 
	    return subsets.get(i).parent; 
	} 
	  
	// A function that does union 
	// of two sets of x and y 
	// (uses union by rank) 
	void Union(ArrayList<subset> subsets,  
	           int x , int y ) 
	{ 
	    int xroot = find(subsets, x); 
	        int yroot = find(subsets, y); 
	  
	    if (subsets.get(xroot).rank < subsets.get(yroot).rank) 
	        subsets.get(xroot).parent = yroot; 
	    else if (subsets.get(yroot).rank < subsets.get(xroot).rank) 
	        subsets.get(yroot).parent = xroot; 
	    else
	    { 
	        subsets.get(xroot).parent = yroot; 
	        subsets.get(yroot).rank++; 
	    } 
	}
	
	
	
	/* adjacency list graph */
	public class Graph
	{
		private ArrayList<ColoredPoint> points;
		private HashMap<ColoredPoint, HashSet<ColoredPoint>> adjacency;
		
		int cover_size;
		int edgeThreshold;

		public Graph(ArrayList<ColoredPoint> points, int edgeThreshold)
		{
			this.points = (ArrayList<ColoredPoint>) points.clone();
			this.adjacency = new HashMap<>();
			this.cover_size = 0;
			this.edgeThreshold = edgeThreshold;
			
			for (ColoredPoint p : points) {
				p.setColor(Couleur.WHITE);
				adjacency.put(p, new HashSet<>());
			}
			
			int size = points.size();
			for (int i = 0; i < size; ++i)
			{
				for (int j = i + 1; j < size; ++j)
				{
					ColoredPoint p1 = points.get(i);
					ColoredPoint p2 = points.get(j);
					if(p1.distance(p2) <= edgeThreshold)
					{
						adjacency.get(p1).add(p2);
						adjacency.get(p2).add(p1);
					}
				}
			}
		}
		
		
		
		public HashMap<ColoredPoint, HashSet<ColoredPoint>> getAdjacency() {
			return adjacency;
		}
		
		public void colorNeighbors(ColoredPoint p, Couleur c) {
			for (ColoredPoint p1:adjacency.get(p)) {
				p1.setColor(c);
			}
		}
		
		public HashSet<ColoredPoint> getNeighbors(ColoredPoint p)
		{
			return  adjacency.get(p);
		}
		
		public int degree(ColoredPoint p)
		{
			return adjacency.get(p).size();
		}
		
	}
	
	public ArrayList<ColoredPoint> MIS (Graph g){
		ArrayList<ColoredPoint> res = new ArrayList<>();
		
		for(ColoredPoint p:g.getAdjacency().keySet()) {
			if (p.getColor() == Couleur.WHITE) {
				res.add(p);
				g.colorNeighbors(p, Couleur.BLUE);
				p.setColor(Couleur.BLACK);
			}
		}
		return res;
	}

	public void computeBlackBlueComponent(ColoredPoint p, Graph g, Set<ColoredPoint> marked, Set<ColoredPoint> res){
		marked.add(p);
		res.add(p);
		for(ColoredPoint voisin:g.getNeighbors(p)) {
			if(voisin.getColor() == Couleur.GREY) continue;
			if(p.getColor() == Couleur.BLUE && voisin.getColor() == Couleur.BLUE) continue;;
			if(!marked.contains(voisin))
				computeBlackBlueComponent(voisin, g, marked, res);
		}
	}
	
	public Pair<Map<ColoredPoint, Integer>, ArrayList<subset>> computeBlackBlueComponents(Graph g) {
		Set<ColoredPoint> marked = new HashSet<>();
		Set<ColoredPoint> blackBluePoints = g.getAdjacency().keySet().stream()
								.filter(c -> c.getColor() == Couleur.BLUE || c.getColor() == Couleur.BLACK)
								.collect(Collectors.toSet());
		
		Map<ColoredPoint, Integer> pointToComponent = new HashMap<>();
		
		int i = 0;
		while(!blackBluePoints.isEmpty()) {
			Set<ColoredPoint> blackBlueComponent = new HashSet<>();
			ColoredPoint blackOrBluePoint = blackBluePoints.stream().findFirst().get();
			computeBlackBlueComponent(blackOrBluePoint, g, marked, blackBlueComponent);
			//not very effective still
			blackBluePoints.removeAll(marked);
			
			for(ColoredPoint cp: blackBlueComponent) {
				pointToComponent.put(cp, i);
			}
				
			i++;
		}
		
		ArrayList<subset> subsets = new ArrayList<>();
		for(int j = 0; j < i; j++) {
			subset subset_j = new subset();
			subsets.add(j, subset_j);
			subset_j.rank = 0;
			subset_j.parent = j;
		}
		
		return new Pair<>(pointToComponent, subsets);
	}

	
	public Set<ColoredPoint> computeSteiner(ArrayList<Point> MIS, Graph g){
		
		boolean flag = false;
		for(ColoredPoint p:g.adjacency.keySet()) {
			flag = false;
			for(Point p2:MIS) {
				if(p.x == p2.x && p.y == p2.y) {
					flag = true;
					p.setColor(Couleur.BLACK);
				}
			}
			if(!flag)
				p.setColor(Couleur.GREY);
		}
		
		Pair<Map<ColoredPoint, Integer>, ArrayList<subset>> p = computeBlackBlueComponents(g);
		ArrayList<subset> subsets = p.right;
		Map<ColoredPoint, Integer> pointComponent = p.left;
		
		Set<ColoredPoint> greyPoints = g.getAdjacency().keySet().stream()
				.filter(c -> c.getColor() == Couleur.GREY)
				.collect(Collectors.toSet());
		
		for(int i=5; i>1; i--) {

			flag = true;
			while (flag) {
				flag = false;
				ColoredPoint pointFound = null;
				Set<Integer> components = null;
				
				for(ColoredPoint gp: greyPoints) {
					components = new HashSet<>();
					for(ColoredPoint v: g.getNeighbors(gp)) {
						if(v.getColor() == Couleur.BLACK) {
							components.add(find(subsets, pointComponent.get(v)));

						}
						if(components.size() >= i) {
							flag = true;
							pointFound = gp;
							break;
						}
					}
				}
				
				if(flag) {
					pointFound.setColor(Couleur.BLUE);
					greyPoints.remove(pointFound);
					//know to which black blue component this point must be added (anything that is not blue)
					int c = -1; 
					for(ColoredPoint v: g.getNeighbors(pointFound)) {
						if (v.getColor() == Couleur.BLACK) {
							c = find(subsets, pointComponent.get(v));
							break;
						}
					}
					pointComponent.put(pointFound, c);
					
					//next union over black blue components on connecting blue nod
					for(ColoredPoint v: g.getNeighbors(pointFound)) {
						if(v.getColor() == Couleur.BLACK)
							Union(subsets, c, find(subsets, pointComponent.get(v)));
						
					}
				}
			}

		}
		System.out.println(greyPoints);
		return g.getAdjacency().keySet().stream().filter(c -> c.getColor() == Couleur.BLUE || c.getColor() == Couleur.BLACK).collect(Collectors.toSet());
	}

	public int getComp(Map<Integer, Set<Vertex>> comp, Vertex v){
		for(Integer key : comp.keySet()){
			if(comp.get(key).contains(v))
				return key;
		}
		
		return -1;
	}

	
	private ArrayList<Vertex> neighborWhite(Vertex u, ArrayList<Vertex> v, int edgeThreshold) {
		ArrayList<Vertex> neib = new ArrayList<>();
		for(Vertex r:neighbor(u, v, edgeThreshold)){
			if(r.color==Color.WHITE) neib.add(r);
		}
		return neib;
	}

	private Vertex existeWhite(ArrayList<Vertex> V) {
		for(Vertex v:V){
			if(v.color==Color.WHITE) return v;
		}
		return null;
	}
	
	public ArrayList<Vertex> neighbor(Vertex p, ArrayList<Vertex> vertices, int edgeThreshold){
		ArrayList<Vertex> result = new ArrayList<Vertex>();

		for (Vertex point:vertices) {
			if (point.distance(p)<edgeThreshold && !point.equals(p)) 
				result.add(point);
		}

		return result;
	}
	
	public ArrayList<Point> MIS(ArrayList<Point> points, int edgeThreshold) {
		ArrayList<Vertex> V = new ArrayList<>();
		ArrayList<Point> mis = new ArrayList<>();
		int i=0;

		for(Point p: points){
			Vertex v=new Vertex(p, i++);
			V.add(v);
		}

		Vertex s = V.get(0);
		s.color=Color.BLACK;
		mis.add(s);
		for(Vertex u:neighbor(s, V, edgeThreshold)){
			u.color=Color.GREY;

			ArrayList<Vertex> tmp = neighbor(u, V, edgeThreshold);
			for(Vertex p:tmp){
				p.setActif(true);
			}
		}
		while( (s=existeWhite(V))!=null ){
			Vertex actifMax= null;
			for(Vertex u: V){
				if(u.color==Color.WHITE && u.isActif()){ 
					actifMax = u;
					break;
				}
			}
			if(actifMax==null) {
				break;
			}
			for(Vertex u: V){
				if(u.actif && u.color==Color.WHITE){
					if(neighborWhite(u,V, edgeThreshold).size() > neighborWhite(actifMax,V, edgeThreshold).size()){
						actifMax=u;
					}
				}
			}
			actifMax.color=Color.BLACK;
			mis.add(actifMax);
			s=actifMax;

			for(Vertex u:neighbor(s, V, edgeThreshold)){
				u.color=Color.GREY;

				ArrayList<Vertex> tmp = neighbor(u, V, edgeThreshold);
				for(Vertex p:tmp){
					p.setActif(true);
				}
			}
		}
		return mis;
	}
	
	public ArrayList<Point> calculDominatingSet(ArrayList<Point> points, int edgeThreshold)
	{
		// remove identical points
		points = new ArrayList<>(new HashSet<>(points));
		ArrayList<ColoredPoint> pointsC = new ArrayList<>();
		
		/*
		points = new ArrayList<>();
		edgeThreshold = 2;
		points.add(new Point(1, 1));
		points.add(new Point(2, 2));
		points.add(new Point(3, 3));
		*/
		
		for(Point p:points) {
			pointsC.add(new ColoredPoint(p.x, p.y, Couleur.WHITE));
		}
		Graph g = new Graph(pointsC, edgeThreshold);
		
		ArrayList<Point> res = new ArrayList<>();
		 //res = MIS(points, edgeThreshold);
		res.addAll(computeSteiner(MIS(points, edgeThreshold), g));
		//res = CDS(MIS(points, edgeThreshold), points, edgeThreshold);
		//System.out.println(res);
		return res;
	}
	
	/*
	public ArrayList<Point> removePoints(ArrayList<ColoredPoint> sol, Graph g){
		//bfs over the graph
		HashMap<ColoredPoint, Boolean> marked = new HashMap<ColoredPoint, Boolean>();
		Stack<ColoredPoint> fifo = new Stack<ColoredPoint>();
		HashMap<Integer, ArrayList<ColoredPoint>> compo = new HashMap<>();
		
		fifo.add(sol.get(0));
		
		for(ColoredPoint p1: g.getAdjacency().keySet()) {
			marked.put(p1, false);
		}
		
		boolean flag = false;
		int cpt = 0;
		 do {
			 flag = false;
			 
			 compo.put(cpt, new ArrayList<>());
			 
			while(!fifo.isEmpty()) {
				ColoredPoint p = fifo.pop();
				Set<ColoredPoint> voisins = g.getNeighbors(p);
				for(ColoredPoint v: voisins) {
					if(!marked.get(v)) {
						compo.get(cpt).add(v);
						fifo.add(v);
						marked.put(v, true);
					}
				}
			}
			cpt++;
		}while(flag);
	}*/
	  public ArrayList<Point> calculConnectedDominatingSet(ArrayList<Point> points, int edgeThreshold) {
			points = new ArrayList<>(new HashSet<>(points));
			ArrayList<ColoredPoint> pointsC = new ArrayList<>();
			
			for(Point p:points) {
				pointsC.add(new ColoredPoint(p.x, p.y, Couleur.WHITE));
			}
			Graph g = new Graph(pointsC, edgeThreshold);
			
			ArrayList<Point> res = new ArrayList<>();
			 //res = MIS(points, edgeThreshold);
			res.addAll(computeSteiner(MIS(points, edgeThreshold), g));
			System.out.println("edgeThreshold : " + edgeThreshold);
			return res;
		  }
	  
	  public ArrayList<Point> calculConnectedDominatingSet1(ArrayList<Point> points, int edgeThreshold) {
			points = new ArrayList<>(new HashSet<>(points));
			ArrayList<ColoredPoint> pointsC = new ArrayList<>();
			
			for(Point p:points) {
				pointsC.add(new ColoredPoint(p.x, p.y, Couleur.WHITE));
			}
			Graph g = new Graph(pointsC, edgeThreshold);
			
			ArrayList<Point> res = new ArrayList<>();
			 //res = MIS(points, edgeThreshold);
			res.addAll(computeSteiner(MIS(points, edgeThreshold), g));
			
			//res = CDS(MIS(points, edgeThreshold), points, edgeThreshold);
			return res;
		  }
	  
	  public ArrayList<Point> calculConnectedDominatingSet2(ArrayList<Point> points, int edgeThreshold) {

			TME2 tme2 = new TME2();
			ArrayList<Point> hitPoints = tme2.calculDominatingSet(points, edgeThreshold);
			MySteiner ms = new MySteiner();
			Tree2D tree = ms.calculSteiner(points, edgeThreshold, hitPoints);
			return tree.treeToPoints();
		  }
	
	public void saveToFile(String filename,ArrayList<Point> result)
	{
		int index=0;
		try
		{
			while(true)
			{
				BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(filename+Integer.toString(index)+".points")));
				try
				{
					input.close();
				}
				catch (IOException e)
				{
					System.err.println("I/O exception: unable to close "+filename+Integer.toString(index)+".points");
				}
				index++;
			}
		}
		catch (FileNotFoundException e)
		{
			printToFile(filename+Integer.toString(index)+".points",result);
		}
	}

	static public void printToFile(String filename, ArrayList<Point> points)
	{
		try
		{
			PrintStream output = new PrintStream(new FileOutputStream(filename));
			for (Point p:points)
				output.println(Integer.toString((int)p.getX()) + " " + Integer.toString((int)p.getY()));
			output.close();
		}
		catch (FileNotFoundException e)
		{
			System.err.println("I/O exception: unable to create "+filename);
		}
	}

	public ArrayList<Point> readFromFile(String filename)
	{
		String line;
		String[] coordinates;
		ArrayList<Point> points=new ArrayList<Point>();
		try
		{
			BufferedReader input = 
					new BufferedReader(
							new InputStreamReader(new FileInputStream(filename))
					);
			try
			{
				while ((line=input.readLine()) != null)
				{
					coordinates=line.split("\\s+");
					points.add(new Point(Integer.parseInt(coordinates[0]),
							Integer.parseInt(coordinates[1])));
				}
			}
			catch (IOException e)
			{
				System.err.println("Exception: interrupted I/O.");
			}
			finally
			{
				try
				{
					input.close();
				}
				catch (IOException e)
				{
					System.err.println("I/O exception: unable to close " + filename);
				}
			}
		}
		catch (FileNotFoundException e)
		{
			System.err.println("Input file not found.");
		}
		return points;
	}
	
}
