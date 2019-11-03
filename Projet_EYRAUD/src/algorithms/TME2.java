package algorithms;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;

/*
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
*/

public class TME2
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
	
	/* adjacency list graph */
	public class Graph
	{
		public ArrayList<Point> points;
		public HashMap<Point, HashSet<Point>> adjacency;
		public ArrayList<Point> dominants;
		public HashSet<Point> is_dominants;
		public HashMap<Point, Integer> covering;
		int cover_size;
		int edgeThreshold;

		public Graph(ArrayList<Point> points, int edgeThreshold)
		{
			this.points = (ArrayList<Point>) points.clone();
			this.adjacency = new HashMap<>();
			this.dominants = new ArrayList<>();
			this.is_dominants = new HashSet<>();
			this.covering = new HashMap<>();
			this.cover_size = 0;
			this.edgeThreshold = edgeThreshold;
			
			for (Point p : points)
				covering.put(p, 0);
			
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
		
		public void resetCover()
		{
			this.dominants.clear();
			this.is_dominants.clear();
			this.covering.clear();
			for (Point p : points)
				covering.put(p, 0);
			this.cover_size = 0;
		}
		
		public int score()
		{
			return dominants.size();
		}
		
		private void addCover(Point p)
		{
			int c = covering.put(p, covering.get(p) + 1);
			if(c == 0)
				++cover_size;
		}
		
		private void removeCover(Point p)
		{
			int c = covering.put(p, covering.get(p) - 1);
			if(c == 1)
				--cover_size;
		}
		
		public void addDominant(Point p)
		{
			assert !dominants.contains(p);
			dominants.add(p);
			is_dominants.add(p);
			addCover(p);
			for (Point neighbor : adjacency.get(p))
				addCover(neighbor);
		}
		
		public void removeDominant(Point p)
		{
			assert dominants.contains(p);
			removeSwap(dominants, dominants.indexOf(p));
			is_dominants.remove(p);
			removeCover(p);
			for (Point neighbor : adjacency.get(p))
				removeCover(neighbor);
		}
		
		public void removeDominant(int i)
		{
			Point p = dominants.get(i);
			assert dominants.contains(p);
			removeSwap(dominants, i);
			is_dominants.remove(p);
			removeCover(p);
			for (Point neighbor : adjacency.get(p))
				removeCover(neighbor);
		}
		
		public HashSet<Point> getNeighbors(Point p)
		{
			return (HashSet<Point>) adjacency.get(p).clone();
		}
		
		public int getNbNeighborsUncovered(Point p)
		{
			
			int nb_uncover = 0;
			for(Point neighbor : adjacency.get(p))
			{
				if(covering.get(neighbor) == 0)
					++nb_uncover;
			}
			return nb_uncover;
		}

		public int getNbNeighborsCovered(Point p)
		{
			int nb_cover = 0;
			for(Point neighbor : adjacency.get(p))
			{
				if(covering.get(neighbor) > 0)
					++nb_cover;
			}
			return nb_cover;
		}
		
		public ArrayList<Point> getNeighborsUncovered(Point p)
		{
			ArrayList<Point> res = new ArrayList<>();
			for(Point neighbor : adjacency.get(p))
			{
				if(covering.get(neighbor) == 0)
					res.add(neighbor);
			}
			return res;
		}
		
		public boolean isDominant(Point p)
		{
			return is_dominants.contains(p);
		}
		
		public boolean isCovered(Point p)
		{
			return covering.get(p) > 0;
		}
		
		public boolean isCovered()
		{
			return cover_size == points.size();
		}
		
		public int degree(Point p)
		{
			return adjacency.get(p).size();
		}
		
		public void setCover(ArrayList<Point> dominants)
		{
			resetCover();
			for(Point p : dominants)
				addDominant(p);
		}

		public void setCover(HashSet<Point> dominants)
		{
			resetCover();
			for(Point p : dominants)
				addDominant(p);
		}
		
	}
	
	public void removeSwap(ArrayList<Point> points, int idx)
	{
		int last_idx = points.size() - 1;
		points.set(idx, points.get(last_idx));
		points.remove(last_idx);
	}
	
	public interface GloutonHeuristic
	{
		public int test(Graph graph, Point point);
	}
	

	public int glouton(Graph graph, GloutonHeuristic heuristic)
	{
		int nb_rm_01 = remove_degree_0_1(graph);
		while(!graph.isCovered())
		{
			int max_h = -1;
			Point next_dominant = null;
			for (Point p : graph.points)
			{
				if(graph.isDominant(p))
					continue;
				else if(graph.getNbNeighborsUncovered(p) == 0)
					continue;
				int h = heuristic.test(graph, p);
				if(h > max_h)
				{
					max_h = h;
					next_dominant = p;
				}
			}
			assert next_dominant != null;
			
			graph.addDominant(next_dominant);

			nb_rm_01 += remove_degree_0_1(graph);
		}
		return nb_rm_01;
	}
	
	public int gloutonRandomized(Graph graph, GloutonHeuristic heuristic)
	{
		int nb_rm_01 = remove_degree_0_1(graph);
		while(!graph.isCovered())
		{
			int max_h = -1;
			ArrayList<Point> next_dominants = new ArrayList<>();
			
			for (Point p : graph.points)
			{
				if(graph.isDominant(p))
					continue;
				else if(graph.getNbNeighborsUncovered(p) == 0)
					continue;
				int h = heuristic.test(graph, p);
				if (h > max_h)
					max_h = h;
			}
			
			for (Entry<Point, HashSet<Point>> pair : graph.adjacency.entrySet())
			{
				Point p = pair.getKey();
				if(graph.isDominant(p))
					continue;
				else if(graph.getNbNeighborsUncovered(p) == 0)
					continue;
				int h = heuristic.test(graph, pair.getKey());
				if (h == max_h)
					next_dominants.add(pair.getKey());
			}

			assert !next_dominants.isEmpty();
			
			Collections.shuffle(next_dominants);
			Point next_dominant = next_dominants.get(next_dominants.size() - 1);

			graph.addDominant(next_dominant);
			
			nb_rm_01 += remove_degree_0_1(graph);
		}
		return nb_rm_01;
	}
	
	public ArrayList<Point> dominantsGloutonRandomizedMultiple(Graph graph, GloutonHeuristic heuristic, int n)
	{
		ArrayList<Point> best_dominants = new ArrayList<>();
		
		int best_score = Integer.MAX_VALUE;
		for(int i = 0; i < n; ++i)
		{
			graph.resetCover();
			
			gloutonRandomized(graph, heuristic);
			
			local_search_0_1(graph);
			local_search_1_2(graph);
			
			if(graph.score() < best_score)
			{
				best_dominants = (ArrayList<Point>) graph.dominants.clone();
				best_score = graph.score();
				System.out.println("Improved solution, score = " + best_score);
			}
		}
		return best_dominants;
	}
	
	public int naiveOrder(Graph graph)
	{
		int nb_rm_01 = remove_degree_0_1(graph);
		
		for(int i = 0; !graph.isCovered(); ++i)
		{
			Point p = graph.points.get(i);
			if(graph.getNbNeighborsUncovered(p) > 0)
			{
				graph.addDominant(p);

				nb_rm_01 += remove_degree_0_1(graph);
			}
		}
		return nb_rm_01;
	}
	
	public int randomizedBase(Graph graph)
	{
		Collections.shuffle(graph.points);
		return naiveOrder(graph);
	}
	
	public void randomized(Graph graph)
	{
		randomizedBase(graph);
		local_search_0_1(graph);
		local_search_1_2(graph);
	}
	
	public ArrayList<Point> dominantsRandomizedMultiple(Graph graph, int n)
	{
		ArrayList<Point> best_dominants = new ArrayList<>();
		
		int best_score = Integer.MAX_VALUE;
		for(int i = 0; i < n; ++i)
		{
			graph.resetCover();
			
			randomized(graph);
			
			if(graph.score() < best_score)
			{
				best_dominants = (ArrayList<Point>) graph.dominants.clone();
				best_score = graph.score();
				System.out.println("Improved solution, score = " + best_score);
			}
		}
		return best_dominants;
	}
	
	void copyList(ArrayList<Point> source, ArrayList<Point> dest)
	{
		dest.clear();
		for(Point p : source)
			dest.add(p);
	}
	
	void copySet(ArrayList<Point> source, HashSet<Point> dest)
	{
		dest.clear();
		dest.addAll(source);
	}
	
	public int randomizedIncrementalBase(Graph graph, boolean garanty, ArrayList<Point> alternative_dominants)
	{
		int nb_rm_01 = remove_degree_0_1(graph);
		
		int alternative_score = Integer.MAX_VALUE;
		
		ArrayList<Point> dominants = new ArrayList<>();
		HashSet<Point> dominants_left = new HashSet<>();
		HashSet<Point> dominants_right = new HashSet<>();
		
		while(!graph.isCovered())
		{
			copyList(graph.dominants, dominants);
			
			randomized(graph); // work on left
			dominants_left = graph.is_dominants;
			graph.is_dominants = new HashSet<>();
			if(graph.score() < alternative_score)
			{
				alternative_score = graph.score();
				copyList(graph.dominants, alternative_dominants);
			}

			graph.setCover(dominants);
			randomized(graph); // work on right
			dominants_right = graph.is_dominants;
			graph.is_dominants = new HashSet<>();
			if(graph.score() < alternative_score)
			{
				alternative_score = graph.score();
				copyList(graph.dominants, alternative_dominants);
			}
			
			
			// intersect left dominants with right dominants (dominants intersection)
			dominants_left.retainAll(dominants_right);
			
			graph.setCover(dominants_left);
			
			// if the cover didn't grow up, we add a random uncovered point as dominant (the first in the list)
			if(garanty)
			{
				if(dominants_left.size() == dominants.size())
				{
					for(Point p : graph.points)
					{
						if(!graph.isDominant(p) && graph.getNbNeighborsUncovered(p) > 0)
						{
							graph.addDominant(p);
							break;
						}
					}
				}
			}
			nb_rm_01 += remove_degree_0_1(graph);
		}
		return nb_rm_01;
	}
	
	public void randomizedIncremental(Graph graph, boolean garanty)
	{
		ArrayList<Point> alternative_dominants = new ArrayList<>();
		randomizedIncrementalBase(graph, garanty, alternative_dominants);
		local_search_0_1(graph);
		local_search_1_2(graph);
		if(alternative_dominants.size() < graph.score())
			graph.setCover(alternative_dominants);
	}
	
	public ArrayList<Point> dominantsRandomizedIncrementalMultiple(Graph graph, boolean garanty, int n)
	{
		ArrayList<Point> best_dominants = new ArrayList<>();
		
		int best_score = Integer.MAX_VALUE;
		for(int i = 0; i < n; ++i)
		{
			graph.resetCover();
			
			randomizedIncremental(graph, garanty);
			
			if(graph.score() < best_score)
			{
				best_dominants = (ArrayList<Point>) graph.dominants.clone();
				best_score = graph.score();
				System.out.println("Improved solution, score = " + best_score);
			}
		}
		return best_dominants;
	}
	
	public int randomizedIncrementalIncrementalBase(Graph graph, boolean subgaranty, boolean garanty, ArrayList<Point> alternative_dominants)
	{
		int nb_rm_01 = remove_degree_0_1(graph);
		
		alternative_dominants.ensureCapacity(100);
		int alternative_score = Integer.MAX_VALUE;
		
		ArrayList<Point> dominants = new ArrayList<>();
		HashSet<Point> dominants_left = new HashSet<>();
		HashSet<Point> dominants_right = new HashSet<>();
		dominants.ensureCapacity(100);
		
		while(!graph.isCovered())
		{
			copyList(graph.dominants, dominants);
			
			randomizedIncremental(graph, subgaranty); // work on left
			dominants_left = graph.is_dominants;
			graph.is_dominants = new HashSet<>();
			if(graph.score() < alternative_score)
			{
				alternative_score = graph.score();
				copyList(graph.dominants, alternative_dominants);
			}
			
			graph.setCover(dominants);
			randomizedIncremental(graph, subgaranty); // work on right
			dominants_right = graph.is_dominants;
			graph.is_dominants = new HashSet<>();
			if(graph.score() < alternative_score)
			{
				alternative_score = graph.score();
				copyList(graph.dominants, alternative_dominants);
			}
			
			// intersect left dominants with right dominants (dominants intersection)
			dominants_left.retainAll(dominants_right);
			
			graph.setCover(dominants_left);
			
			if(garanty)
			{
				if(dominants_left.size() == dominants.size())
				{
					for(Point p : graph.points)
					{
						if(!graph.isDominant(p) && graph.getNbNeighborsUncovered(p) > 0)
						{
							graph.addDominant(p);
							
							break;
						}
					}
				}
			}
			nb_rm_01 += remove_degree_0_1(graph);
		}
		return nb_rm_01;
	}
	
	public void randomizedIncrementalIncremental(Graph graph, boolean subgaranty, boolean garanty)
	{
		ArrayList<Point> alternative_dominants = new ArrayList<>();
		randomizedIncrementalIncrementalBase(graph, subgaranty, garanty, alternative_dominants);
		local_search_0_1(graph);
		local_search_1_2(graph);
		if(alternative_dominants.size() < graph.score())
			graph.setCover(alternative_dominants);
	}
	
	public int randomizedIncrementalIncrementalIncrementalBase(Graph graph, boolean subsubgaranty, boolean subgaranty, boolean garanty, ArrayList<Point> alternative_dominants)
	{
		int nb_rm_01 = remove_degree_0_1(graph);
		
		alternative_dominants.ensureCapacity(100);
		int alternative_score = Integer.MAX_VALUE;
		
		ArrayList<Point> dominants = new ArrayList<>();
		HashSet<Point> dominants_left = new HashSet<>();
		HashSet<Point> dominants_right = new HashSet<>();
		dominants.ensureCapacity(100);
		
		while(!graph.isCovered())
		{
			copyList(graph.dominants, dominants);
			
			randomizedIncrementalIncremental(graph, subsubgaranty, subgaranty); // work on left
			dominants_left = graph.is_dominants;
			graph.is_dominants = new HashSet<>();
			if(graph.score() < alternative_score)
			{
				alternative_score = graph.score();
				copyList(graph.dominants, alternative_dominants);
			}
			
			graph.setCover(dominants);
			randomizedIncrementalIncremental(graph, subsubgaranty, subgaranty); // work on right
			dominants_right = graph.is_dominants;
			graph.is_dominants = new HashSet<>();
			if(graph.score() < alternative_score)
			{
				alternative_score = graph.score();
				copyList(graph.dominants, alternative_dominants);
			}
			
			// intersect left dominants with right dominants (dominants intersection)
			dominants_left.retainAll(dominants_right);
			graph.setCover(dominants_left);
			
			if(garanty)
			{
				if(dominants_left.size() == dominants.size())
				{
					for(Point p : graph.points)
					{
						if(!graph.isDominant(p) && graph.getNbNeighborsUncovered(p) > 0)
						{
							graph.addDominant(p);
							
							break;
						}
					}
				}
			}
			nb_rm_01 += remove_degree_0_1(graph);
		}
		return nb_rm_01;
	}
	
	public void randomizedIncrementalIncrementalIncremental(Graph graph, boolean subsubgaranty, boolean subgaranty, boolean garanty)
	{
		ArrayList<Point> alternative_dominants = new ArrayList<>();
		randomizedIncrementalIncrementalIncrementalBase(graph, subsubgaranty, subgaranty, garanty, alternative_dominants);
		local_search_0_1(graph);
		local_search_1_2(graph);
		if(alternative_dominants.size() < graph.score())
			graph.setCover(alternative_dominants);
	}
	
	public int sumDegrees(Graph g, Set<Point> points)
	{
		int sum = 0;
		for(Point p : points)
			sum += g.degree(p);
		return sum;
	}

	public int remove_degree_0_1(Graph graph)
	{
		int size = graph.points.size();
		int i = 0;
		int nb_dominants = 0;
		while (i < size)
		{
			Point p = graph.points.get(i);
			if(!graph.isCovered(p))
			{
				ArrayList<Point> neighbors = graph.getNeighborsUncovered(p);
				if(neighbors.size() == 0)
				{
					graph.addDominant(p);
					i = 0;
					++nb_dominants;
				}
				else if(neighbors.size() == 1)
				{
					graph.addDominant(neighbors.get(0));
					i = 0;
					++nb_dominants;
				}
				else
					++i;
			}
			else
				++i;
		}
		return nb_dominants;
	}
	
	public int local_search_0_1(Graph graph)
	{
		int nb_dominants = 0;
		for (int i = graph.dominants.size() - 1; i != -1 ; --i)
		{
			Point p = graph.dominants.get(i);
			
			graph.removeDominant(i);
			
			if (graph.isCovered())
				++nb_dominants;
			else
				graph.addDominant(p);
		}
		return nb_dominants;
	}
	
	public int local_search_1_2(Graph graph)
	{
		for (int i = graph.dominants.size() - 1; i != -1; --i)
		{
			for(int j = i - 1; j != -1; --j)
			{
				Point p = graph.dominants.get(i);
				Point q = graph.dominants.get(j);
				if (p.distance(q) > 2 * graph.edgeThreshold)
					continue;

				for (int k = 0; k < graph.points.size(); ++k)
				{
					Point r = graph.points.get(k);
					if(graph.isDominant(r))
						continue;
					if (p.distance(r) > graph.edgeThreshold || q.distance(r) > graph.edgeThreshold)
						continue;
					
					graph.removeDominant(p);
					graph.removeDominant(q);
					graph.addDominant(r);
					
					if (graph.isCovered())
						return 1 + local_search_1_2(graph);
					else
					{
						graph.removeDominant(graph.dominants.size() - 1);
						graph.addDominant(p);
						graph.addDominant(q);
					}
				}
			}
		}
		return 0;
	}
	
	// determinist
	// 70ms (79)
	public ArrayList<Point> dominantsGlouton(Graph graph, GloutonHeuristic heuristic)
	{
		int nb_rm_01 = glouton(graph, heuristic);
		System.out.println("Added   " + nb_rm_01 + " dominants with RM 0-1, score = " + graph.score());
		int nb_ls_01 = local_search_0_1(graph);
		System.out.println("Removed " + nb_ls_01 + " dominants with LS 0-1, score = " + graph.score());
		int nb_ls_12 = local_search_1_2(graph);
		System.out.println("Removed " + nb_ls_12 + " dominants with LS 1-2, score = " + graph.score());
		return graph.dominants;
	}
	
	// 110ms (78-85)
	public ArrayList<Point> dominantsGloutonRandomized(Graph graph, GloutonHeuristic heuristic)
	{
		int nb_rm_01 = gloutonRandomized(graph, heuristic);
		System.out.println("Added   " + nb_rm_01 + " dominants with RM 0-1, score = " + graph.score());
		int nb_ls_01 = local_search_0_1(graph);
		System.out.println("Removed " + nb_ls_01 + " dominants with LS 0-1, score = " + graph.score());
		int nb_ls_12 = local_search_1_2(graph);
		System.out.println("Removed " + nb_ls_12 + " dominants with LS 1-2, score = " + graph.score());
		return graph.dominants;
	}
	
	// 50ms (85-90)
	public ArrayList<Point> dominantsRandomized(Graph graph)
	{
		int nb_rm_01 = randomizedBase(graph);
		System.out.println("Added   " + nb_rm_01 + " dominants with RM 0-1, score = " + graph.score());
		int nb_ls_01 = local_search_0_1(graph);
		System.out.println("Removed " + nb_ls_01 + " dominants with LS 0-1, score = " + graph.score());
		int nb_ls_12 = local_search_1_2(graph);
		System.out.println("Removed " + nb_ls_12 + " dominants with LS 1-2, score = " + graph.score());
		return graph.dominants;
	}
	
	// 1.000ms-3.000ms (73-76)
	public ArrayList<Point> dominantsRandomizedIncremental(Graph graph, boolean garanty)
	{
		ArrayList<Point> alternative_dominants = new ArrayList<>(100);
		int nb_rm_01 = randomizedIncrementalBase(graph, garanty, alternative_dominants);
		System.out.println("Added   " + nb_rm_01 + " dominants with RM 0-1, score = " + graph.score());
		int nb_ls_01 = local_search_0_1(graph);
		System.out.println("Removed " + nb_ls_01 + " dominants with LS 0-1, score = " + graph.score());
		int nb_ls_12 = local_search_1_2(graph);
		System.out.println("Removed " + nb_ls_12 + " dominants with LS 1-2, score = " + graph.score());
		if(alternative_dominants.size() < graph.score())
		{
			System.out.println("Alternative dominants found, score = " + alternative_dominants.size());
			return alternative_dominants;
		}
		return graph.dominants;
	}
	
	// 20.000ms to 100.000ms (71-72)
	public ArrayList<Point> dominantsRandomizedIncrementalIncremental(Graph graph, boolean subgaranty, boolean garanty)
	{
		ArrayList<Point> alternative_dominants = new ArrayList<>(100);
		int nb_rm_01 = randomizedIncrementalIncrementalBase(graph, subgaranty, garanty, alternative_dominants);
		System.out.println("Added   " + nb_rm_01 + " dominants with RM 0-1, score = " + graph.score());
		int nb_ls_01 = local_search_0_1(graph);
		System.out.println("Removed " + nb_ls_01 + " dominants with LS 0-1, score = " + graph.score());
		int nb_ls_12 = local_search_1_2(graph);
		System.out.println("Removed " + nb_ls_12 + " dominants with LS 1-2, score = " + graph.score());
		if(alternative_dominants.size() < graph.score())
		{
			System.out.println("Alternative dominants found, score = " + alternative_dominants.size());
			return alternative_dominants;
		}
		return graph.dominants;
	}
	
	// 800.000ms (70)
	public ArrayList<Point> dominantsRandomizedIncrementalIncrementalIncremental(Graph graph, boolean subsubgaranty, boolean subgaranty, boolean garanty)
	{
		ArrayList<Point> alternative_dominants = new ArrayList<>(100);
		int nb_rm_01 = randomizedIncrementalIncrementalIncrementalBase(graph, subsubgaranty, subgaranty, garanty, alternative_dominants);
		System.out.println("Added   " + nb_rm_01 + " dominants with RM 0-1, score = " + graph.score());
		int nb_ls_01 = local_search_0_1(graph);
		System.out.println("Removed " + nb_ls_01 + " dominants with LS 0-1, score = " + graph.score());
		int nb_ls_12 = local_search_1_2(graph);
		System.out.println("Removed " + nb_ls_12 + " dominants with LS 1-2, score = " + graph.score());
		if(alternative_dominants.size() < graph.score())
		{
			System.out.println("Alternative dominants found, score = " + alternative_dominants.size());
			return alternative_dominants;
		}
		return graph.dominants;
	}
	
	public ArrayList<Point> calculDominatingSet(ArrayList<Point> points, int edgeThreshold)
	{
		// remove identical points
		points = new ArrayList<>(new HashSet<>(points));
		
		ArrayList<Point> dominants = points;
		
		Graph graph = new Graph(points, edgeThreshold);
		
		//dominants = dominantsGlouton(graph, (g, p) -> g.getNbNeighborsUncovered(p));
		
		//dominants = dominantsGloutonRandomized(graph, (g, p) -> g.getNbNeighborsUncovered(p));
		
		//dominants = dominantsGloutonRandomizedMultiple(graph, (g, p) -> g.getNbNeighborsUncovered(p), 100);
		
		//dominants = dominantsRandomized(graph);
		
		//dominants = dominantsRandomizedMultiple(graph, 100);
		
		dominants = dominantsRandomizedIncremental(graph, false);
		
		//dominants = dominantsRandomizedIncrementalMultiple(graph, false, 10);
		
		//dominants = dominantsRandomizedIncrementalIncremental(graph, true, false);
		
		//dominants = dominantsRandomizedIncrementalIncrementalIncremental(graph, true, false, false);
		
		
		// if (false) result = readFromFile("output0.points");
		// else saveToFile("output",result);
		return dominants;
	}

	/*
	private void saveToFile(String filename,ArrayList<Point> result)
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

	private void printToFile(String filename, ArrayList<Point> points)
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

	private ArrayList<Point> readFromFile(String filename)
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
	*/
}
