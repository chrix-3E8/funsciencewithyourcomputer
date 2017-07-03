import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Hashtable;
import java.util.Scanner;
// Copyright 2007 by Chrix - visit www.funsciencewithyourcomputer.org
public class Translator2 {
	
	public static void main(String[] args) {
		Translator2 translator = new Translator2();
		translator.read();

		while (true) {
			System.out.println("Please enter sentence: ");
			String sentence = translator.input();
			if (sentence.startsWith("?")) {
				translator.ask(sentence.substring(1));
			} else {
				String translation = translator.translate(sentence);
				if (translation.length() > 0) {
					System.out.println(translation);
				} else {
					System.out.println("I don't understand");
					translator.ask(sentence);
				}
			}
		}
	}

	Hashtable dictionary = new Hashtable();

	public String input() {
		Scanner keyboard = new Scanner(System.in);
		String input = keyboard.nextLine().toLowerCase();
		if (input.length() == 0)
			return "smurf";
		return input;
	}

	public String translate(String sentence) {
		if (sentence.length() == 0)
			return "";
		String translation = (String) dictionary.get(sentence);
		if (translation != null)
			return translation;
		ask(sentence);
		return (String) dictionary.get(sentence);
	}

	public String[] extractSubSentences(String[] words, int size) {
		String[] subSentences = new String[words.length - size + 1];
		for (int i = 0; i <= words.length - size; i++) {
			String subSentence = add(words, i, size);
			subSentences[i] = subSentence;
		}
		return subSentences;
	}

	public String add(String[] words, int start, int count) {
		if (count == 0)
			return "";
		String result = "";
		for (int i = start; i < start + count - 1; i++) {
			if (words[i].length() > 0)
				result += words[i] + " ";
		}
		return result + words[start + count - 1];
	}

	public void ask(String sentence) {
		System.out.println("Teach me: '" + sentence + "'");
		String translation = input();
		dictionary.put(sentence, translation);

		System.out.println("I now know " + dictionary.keySet().size()
				+ " expressions");
		save();
	}

	public void read() {
		try {
			FileInputStream file = new FileInputStream("dictionary.data");
			ObjectInputStream stream = new ObjectInputStream(file);
			dictionary = (Hashtable) stream.readObject();
		} catch (Exception e) {
		}
	}

	public void save() {
		try {
			FileOutputStream file = new FileOutputStream("dictionary.data");
			ObjectOutputStream stream = new ObjectOutputStream(file);
			stream.writeObject(dictionary);
		} catch (Exception e) {
		}
	}
}
