import java.util.Scanner;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedWriter;

public class GA {
	static String[] words; //fill with possible answers
	public static String[] guesses; //fill with possible guesses
	static int wordsLen; //num answers
	static int guessesLen; //num guessable words
	static int elites = 2;
	static int[] trainingWords; //array of training words
	static int numTrainingWords = 70; //num words to train on
	static int population = 10;
	static Chromo[] chromos; //population array
	private static double crossoverRate = .5;
	private static double mutationRate = .6;
	private static double mutationRateE = .2; //mutation for elites
	static int generations = 1000;

	public GA() throws FileNotFoundException {
		trainingWords = new int[numTrainingWords];

		File file = new File("./src/words.txt");
		//File file = new File("./src/tester.txt");
		wordsLen = 0;
		guessesLen = 0;
		Scanner sc = new Scanner(file);
		while(sc.hasNext()){ //gets length
			String line = sc.nextLine();       
			wordsLen++;
		}
		sc.close();
		File file1 = new File("./src/guesses.txt");
		//File file1 = new File("./src/tester.txt");
		Scanner scc = new Scanner(file1);
		while(scc.hasNext()){ //gets length
			String line = scc.nextLine();       
			guessesLen++;
		}
		scc.close();
		words = new String[wordsLen];
		guesses = new String[guessesLen];
		chromos = new Chromo[population];

	}

	public void fillArrays() throws FileNotFoundException {
		File file = new File("./src/words.txt");
		//File file = new File("./src/tester.txt");
		Scanner sc1 = new Scanner(file);
		for (int i=0; i<wordsLen; i++) { //actually fills
			String line = sc1.nextLine();  
			words[i] = line;
		}
		sc1.close();
		File file1 = new File("./src/guesses.txt");
		//File file1 = new File("./src/tester.txt");
		Scanner sc2 = new Scanner(file1);
		for (int i=0; i<guessesLen; i++) { //actaully fills
			String line = sc2.nextLine();  
			guesses[i] = line;
		}
		sc2.close();
	}

	public void createPop() {
		for (int i=0; i<population; i++) {
			chromos[i] = new Chromo(); //creates initial population
		}
	}

	public void getWords() {
		Random rand = new Random();
		for (int i=0; i<numTrainingWords; i++) {
			trainingWords[i] = rand.nextInt(wordsLen); //training array so its the same for all chromos
		}
	}

	public void runGame() {
		for (int i=0; i<population; i++) {
			chromos[i].setFitness(0.0); //i know this is bad practice but... before it was using the previous fitness somehow...
			int maxCount = 0;
			for (int j=0; j<numTrainingWords; j++) {
				//System.out.println(trainingWords[j]);
				Game game = new Game(words[trainingWords[j]], chromos[i]);
				if(!game.testFitness()) maxCount++; //this is in case something is really doing poorly. stops it early
				if (maxCount > 5) {
					chromos[i].setFitness(numTrainingWords * 8);
					break;
				}
			}
			chromos[i].setFitness(chromos[i].getFitness() / numTrainingWords); //gets avg accross training 
		}
	}

	public Chromo selectParent() {

		//---------------- RANK PROPORTIONAL SELECTION --------------------
		//this portion of the code is from Vera Kazakova
		//since chromos in current generation have been sorted, their spot is their rank
		//System.out.print("\n");
		Random rand = new Random();

		double sumRank = 0;
		double overallSumRanks = (this.population+1)*this.population/2.0;

		double probability = rand.nextDouble();
		for (int c=0; c<this.population; c++) {
			sumRank += (c+1);
			if (probability < sumRank/overallSumRanks)
				return this.chromos[c];			
		}
		//dummy return; let's pick a parent at random
		return this.chromos[rand.nextInt(this.population)];
	}

	public void crossover() {

		Chromo[] tng = new Chromo[this.population];
		int tngSize = 0;
		Random rand = new Random();
		//------------ CROSSOVER

		while (tngSize < this.population-elites) {

			Chromo parent1 = selectParent();
			Chromo parent2 = selectParent();
			while (parent1 == parent2)
				parent2 = selectParent();

			Chromo child1 = new Chromo (parent1.strat);
			Chromo child2 = new Chromo (parent2.strat);

			if (rand.nextDouble()<crossoverRate)
				Chromo.crossover(child1,child2);
			if (rand.nextDouble()<mutationRate)
				child1.mutate();
			if (rand.nextDouble()<mutationRate)
				child2.mutate();


			tng[tngSize] = child1; 
			tngSize++;
			tng[tngSize] = child2;
			tngSize++;
		}
		// -------------------ELITISM----------------------------
		if (rand.nextDouble() < mutationRateE) {
			this.chromos[0].mutateE();
			this.chromos[1].mutateE();
		}

		tng[tngSize] = this.chromos[0];
		tngSize++;

		tng[tngSize] = this.chromos[1];
		tngSize++;


		this.chromos = tng;
	}

	public void tester() throws IOException {
		//this method is for testing how well a chromosome does on the whole population. used to get data for graphs to show
		//run seperately from rest of ga.
		BufferedWriter writer = new BufferedWriter(new FileWriter("./src/output.txt", true));
		//fitness values will be in output.txt
		Chromo c = new Chromo();
		c.word = "marse";
		c.word2 = "clout";
		c.strat[0] = 1;
		c.strat[1] = 8;
		c.strat[2] = 16;
		c.setFitness(0.0); 
		for (int j=0; j<wordsLen; j++) {
			Game game = new Game(words[j], c);
			game.testFitness();
			writer.append('\n');
			writer.append("" + game.tr);
		}
		c.setFitness(c.getFitness() / wordsLen); 
		writer.append('\n');
		writer.append(Double.toString(c.getFitness()));
		writer.close();
	}

	public static void main(String[] args) throws IOException {
		GA wordle = new GA();
		wordle.fillArrays();
		wordle.getWords();
		//wordle.tester();

		wordle.createPop();

		//System.out.println(Arrays.toString(words));
		for (int i=0; i<generations; i++) {
			System.out.println("\n\nnext gen " + i);
			wordle.runGame();
			Arrays.sort(wordle.chromos);
			//i'm sorry I know I should just make a tostring and it would be easier than this but it was not the vibe for some reason
			System.out.println("best? " + wordle.chromos[0].getFitness() + " " + wordle.chromos[0].word + " " + wordle.chromos[0].word2 + " strat: " + Arrays.toString(wordle.chromos[0].strat));
			wordle.crossover();
		}
	}
}
