
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class Guess{
	public List<String> tries;
	public String[] green;
	public String[] yellow;
	public List<String> gray; 
	public int searchSpace;
	public  Map<String, int[]> freq;
	public List<String> remGuess;
	public List<String> invalid;
	public int notIt;
	public boolean correct = false;
	public int[] info;


	public Guess () {
		tries = new ArrayList<String>();
		green = new String[5];
		yellow = new String[5];
		for (int i=0; i<5; i++) {
			yellow[i] = "";
		}
		gray = new ArrayList<String>();
		searchSpace = GA.guessesLen;
		//searchSpace = GA.wordsLen;
		notIt = 0;
		copyMapandList();
		info = new int[3];
	}

	public void copyMapandList() {
		this.remGuess = new ArrayList<String>();
		this.invalid = new ArrayList<String>();

		for (int i=0; i<GA.guessesLen; i++) {
			this.remGuess.add(GA.guesses[i]);
		}

		/*
		for (int i=0; i<GA.wordsLen; i++) {
			this.remGuess.add(GA.words[i]);
		}
		 */
	}

	public boolean newGuess(String guess) {
		if (guess.equals(Game.answer)) {
			correct = true;
			return correct;
		}
		this.tries.add(guess);
		for (int i=0; i<5; i++) {
			String c = "" + guess.charAt(i);
			//check if is answer
			int ind = Game.answer.indexOf(c); 
			if (ind == -1) {
				this.gray.add(c);
			} else if (Game.answer.charAt(i) == guess.charAt(i)) {
				this.green[i] = c;
			} else {
				this.yellow[i] += c;
			}		  
		}
		calcSearchSpace();
		return false;
	}

	public void calcSearchSpace() {
		info[0] = 0;
		for (int i=0; i<5; i++) {
			if (this.green[i] != null) {
				info[0]+=1;
			}
		}
		String letters = "";
		info[1] = 0;
		for (int i=0; i<5; i++) {
			for (int j=0; j<this.yellow[i].length(); j++) {
				if (letters.contains("" + this.yellow[i].charAt(j))) {
					//info[1]++;
				} else {
					info[1]+=1;
				}
				letters += this.yellow[i].charAt(j);
			}
		}

		info[2] = this.gray.size();

		List<String> temp = new ArrayList<String>();  
		this.remGuess.forEach((w) -> {
			boolean valid = true;
			for (int i=0; i<5; i++) {
				String c = "" + w.charAt(i);
				if (this.green[i] != null) {
					if (!this.green[i].equals(c)) { 
						//this.remGuess.remove(w);
						if (w.equals(Game.answer)) System.out.println("gree");
						valid = false;
						break; 
					}
				}
			}
			for (int i=0; i<5; i++) {
				String c = "" + w.charAt(i);
				if (this.gray.contains(c) || (this.yellow[i] != null && this.yellow[i].contains(c))) { //double check yellow 
					//this.remGuess.remove(w);
					if (w.equals(Game.answer)) System.out.println("ISSUE with Gray/yellow: char  = " + c);
					valid = false;
					break; 
				}
			}
			if (valid) {
				temp.add(w);
			} else {
				this.searchSpace--;
				if (!this.tries.contains(w)) {
					this.invalid.add(w);
					notIt++;
				}
			}
		});
		remGuess = temp;
		/*
		for (int i=0; i<searchSpace; i++) {
			System.out.print(remGuess.get(i) + " ");
		}
		System.out.print("\n");
		 */
		//return this.searchSpace;
	}
}
