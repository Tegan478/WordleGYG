import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map;

public class Chromo  implements Comparable<Chromo>{
	public int chromoID;
	public double fitness;
	private int wordInd; //location of start word in guess array
	public String word; //start word
	public String word2; //second word
	public int wordInd2; //location of second work in guess array
	//private List<String> remain; 
	//private int remaining = GA.guessesLen;
	int[] strat; //array of g, y, g values
	//public static int length=10;
	//private static int strats=2; 
	//private int max = 1;
	private static int totalChromos=0;

	public Chromo()  {
		this.fitness=0;
		this.chromoID = Chromo.totalChromos;
		Chromo.totalChromos++;
		//remain = Arrays.asList(GA.guesses); 
		Random rand = new Random();
		this.wordInd = rand.nextInt(GA.guessesLen);
		this.word = GA.guesses[wordInd];
		this.wordInd2 = rand.nextInt(GA.guessesLen);
		this.word2 = GA.guesses[wordInd2];
		if (this.strat == null)
			setupStrat();
	}

	public void setupStrat() {
		//puts random values for strat
		Random rand = new Random();
		this.strat = new int[3];
		this.strat[0] = rand.nextInt(5);
		this.strat[1] = rand.nextInt(5);
		this.strat[2] = rand.nextInt(20);
	}

	public Chromo (int[] strat) { 
		this.fitness = 0;
		Random rand = new Random();
		this.strat = strat.clone();
		if (this.strat==null)
			setupStrat();
		
		//gets random words
		if (this.word==null) {
			this.wordInd = rand.nextInt(GA.guessesLen);
			this.word = GA.guesses[wordInd]; 
		}
		if (this.word2==null) {
			this.wordInd2 = rand.nextInt(GA.guessesLen);
			this.word2 = GA.guesses[wordInd2];
		}
		this.chromoID = Chromo.totalChromos;
		Chromo.totalChromos++;
	}

	public void mutate() {
		Random rand = new Random();
		int which = rand.nextInt(5); //which mutation to do
		int stdev = rand.nextInt((GA.guessesLen)/10); //standard deviation
		if (which == 0) {
			//mutate start word. pivots around location
			int newWord = (int) Math.round(rand.nextGaussian() * stdev + wordInd);
			if (newWord < 0 || newWord >= GA.guessesLen) newWord = rand.nextInt(GA.guessesLen);
			this.word = GA.guesses[newWord];
		} else if (which == 1 || which == 2) {
			//mutate start word2
			int newWord = (int) Math.round(rand.nextGaussian() * stdev + wordInd2);
			if (newWord < 0 || newWord >= GA.guessesLen) newWord = rand.nextInt(GA.guessesLen);
			this.word2 = GA.guesses[newWord];
		} else {
			//mutate strat array
			which = rand.nextInt(4); //which value to change?
			if (which == 0) {
				//change greens
				which = rand.nextInt(2);
				//if 0, decrease, if 1, increase
				if (which == 0 && this.strat[0] > 0) {
					this.strat[0]--;
				} else {
					this.strat[0]++;
				}
			} else if (which == 1 || which == 2) {
				//change yellows
				which = rand.nextInt(2);
				//if 0, decrease, if 1, increase
				if (which == 0 && this.strat[1] > 0) {
					this.strat[1]--;
				} else {
					this.strat[1]++;
				}
			} else {
				//change grays. 
				stdev = rand.nextInt(5);
				int n = (int) Math.round(rand.nextGaussian() * stdev + this.strat[2]);
				if (n < 0) n = 0;
				this.strat[2] = n;
			}
		}
	}

	public void mutateE() {
		//smaller range mutation for elites
		Random rand = new Random();
		int which = rand.nextInt(4);
		int stdev = rand.nextInt((GA.guessesLen)/5);
		if (which == 0) {
			//mutate start word
			int newWord = (int) Math.round(rand.nextGaussian() * stdev + wordInd);
			if (newWord < 0 || newWord >= GA.guessesLen) newWord = rand.nextInt(GA.guessesLen);
			this.word = GA.guesses[newWord];
		} else if (which == 1 || which == 2) {
			int newWord = (int) Math.round(rand.nextGaussian() * stdev + wordInd2);
			if (newWord < 0 || newWord >= GA.guessesLen) newWord = rand.nextInt(GA.guessesLen);
			this.word2 = GA.guesses[newWord];
		} else {
			//mutate strat val
			which = rand.nextInt(5);
			if (which == 0) {
				which = rand.nextInt(2);
				if (which == 0 && this.strat[0] > 0) {
					this.strat[0]--;
				} else {
					this.strat[0]++;
				}
			} else if (which == 1 || which == 2) {
				which = rand.nextInt(2);
				if (which == 0 && this.strat[1] > 0) {
					this.strat[1]--;
				} else {
					this.strat[1]++;
				}
			} else {
				stdev = rand.nextInt(3);
				int n = (int) Math.round(rand.nextGaussian() * stdev + this.strat[2]);
				if (n < 0) n = 0;
				this.strat[2] = n;
			}
		}
	}

	public static void crossover(Chromo c1, Chromo c2) {
		Random rand = new Random();
		int which = rand.nextInt(6); //which to do!
		if (which == 0) {
			//crossover for word
			List<String> possible = new LinkedList<String>(); //holds words it could be
			String comb = "";
			boolean clean = true;
			comb = c1.word + c2.word;
			for (int i=0; i<GA.guessesLen; i++) {
				String w = GA.guesses[i];
				for (int j=0; j<5; j++) {
					if (!comb.contains("" + w.charAt(j))) {
						clean = false;
						break;
					}
				}
				if (clean) possible.add(GA.guesses[i]);
				clean = true;
			}
			c1.word = possible.get(rand.nextInt(possible.size()));
			c2.word = possible.get(rand.nextInt(possible.size()));
		} else if (which == 1 || which == 2) {
			//strat avg\
			which = rand.nextInt(3);
			int temp = c1.strat[which] + c2.strat[which];
			if (c1.getFitness() > c2.getFitness()) {
				c2.strat[which] = temp/2;
			} else {
				c1.strat[which] = temp/2;
			}

		} else if (which == 3) {
			//swap start words
			String temp = c1.word2;
			c1.word2 = c2.word2;
			c2.word2 = temp;
		} else {
			//crossover for word2
			List<String> possible = new LinkedList<String>();
			String comb = "";
			boolean clean = true;
			comb = c1.word2 + c2.word2;
			for (int i=0; i<GA.guessesLen; i++) {
				String w = GA.guesses[i];
				for (int j=0; j<5; j++) {
					if (!comb.contains("" + w.charAt(j))) {
						clean = false;
						break;
					}
				}
				if (clean) possible.add(GA.guesses[i]);
				clean = true;
			}
			c1.word2 = possible.get(rand.nextInt(possible.size()));
			c2.word2 = possible.get(rand.nextInt(possible.size()));
		}		
		return;
	}

	public double getFitness() {
		return this.fitness;
	}

	public void setFitness(double newFit) {
		this.fitness = newFit;
	}

	@Override
	public int compareTo(Chromo other) {
		if (this.fitness<other.fitness)
			return -1;
		else if (this.fitness>other.fitness)
			return 1;
		else return 0;
	}
}
