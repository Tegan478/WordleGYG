import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Game{
	public int maxGuesses = 8;
	public static String answer; 
	public Chromo chrom;
	private List<String> possible;
	public static int tr;
	public Game (String answer, Chromo chrom) {
		this.answer = answer;
		this.chrom = chrom;
		possible = new LinkedList<String>();
		this.tr = 0;
	}

	public boolean testFitness (){
		Guess guess = new Guess();
		String choice = chrom.word;
		int tries = 1;
		while (!guess.newGuess(choice) && tries < maxGuesses) {
			int g = chrom.strat[0];
			int y = chrom.strat[1]; 
			int gr = chrom.strat[2];
			//int k = g + (y%4);
			//int i = guess.info[0] + (guess.info[1]%4);
			int k = g+y;
			int i = guess.info[0] + guess.info[1];
			if (tries > 1 || i>=k) {
				if (i >= k || guess.info[0] >= g || (guess.info[1] > y && guess.info[2] > gr)) {
					choice = randSelect(guess);
				} else {
					choice = noverlap(guess);
					//System.out.println("nov");
				}
			} else {
				choice = chrom.word2;
			}

			tries++;
		}
		chrom.setFitness(chrom.getFitness() + tries);
		tr = tries;
		return guess.correct;
	}

	public String randSelect(Guess guess) {
		Random r = new Random();
		int rand = guess.searchSpace;
		if (guess.searchSpace == 0) return chrom.word;
		return guess.remGuess.get(r.nextInt(rand));
	}

	public String randSelectFull(Guess guess) {
		Random r = new Random();
		int rand = r.nextInt(GA.guessesLen);
		return GA.guesses[rand];
	}

	public String noverlap(Guess guess) {
		Random rand = new Random();
		if (guess.notIt == 0) return chrom.word;
		int which = rand.nextInt(guess.notIt);
		return guess.invalid.get(which);
	}
}


