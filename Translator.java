import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Hashtable;
import java.util.Scanner;
// Copyright 2007 by Chrix - visit www.funsciencewithyourcomputer.org
public class Translator {
	
	public static void main(String[] args) {
		Translator translator = new Translator();
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
		String[] words = sentence.split("\\s");
		if (words.length == 1) {
			ask(words[0]);
			return (String) dictionary.get(words[0]);
		}
		for (int size = words.length - 1; size > 0; size--) {
			String[] subSentences = extractSubSentences(words, size);
			for (int i = 0; i < subSentences.length; i++) {
				translation = (String) dictionary.get(subSentences[i]);
				if (translation != null) {
					String beforeWords = add(words, 0, i);
					String afterWords = add(words, i + size, words.length - i
							- size);
					String beforeTranslation = translate(beforeWords);
					String afterTranslation = translate(afterWords);
					return add(new String[] { beforeTranslation, translation,
							afterTranslation }, 0, 3);
				}
			}
		}
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

		String[] words = sentence.split("\\s");
		if (words.length > 1) {
			for (int i = 0; i < words.length; i++) {
				System.out.println("Teach me: '" + words[i] + "'");
				translation = input();
				dictionary.put(words[i], translation);
			}
		}
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