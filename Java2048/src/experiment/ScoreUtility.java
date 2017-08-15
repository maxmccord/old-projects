// Author:  Max McCord
// Created: 04/04/2014

import java.io.*;
import java.util.*;

public class ScoreUtility {
	private static final int MAX_SAVED_SCORES = 128;

	public static List<Score> scores;

	public static List<String> getNames() {
		List<String> namesList = new ArrayList<String>();
		for (Score s : scores)
			namesList.add(s.name);
		return namesList;
	}

	public static List<Integer> getScores() {
		List<Integer> scoresList = new ArrayList<Integer>();
		for (Score s : scores)
			scoresList.add(s.score);
		return scoresList;
	}

	public static void resetScores() {
		scores = new ArrayList<Score>();
	}

	public static void loadScores(String dir) {
		scores = new ArrayList<Score>();

		File file = new File(dir);
		try {
			DataInputStream input = new DataInputStream(new FileInputStream(file));

			int numOfScores = input.readInt();

			for (int i = 0; i < numOfScores; i++) {
				// read the name
				int nameLength = input.readInt();
				byte[] nameBytes = new byte[nameLength];
				input.read(nameBytes, 0, nameBytes.length);

				// read the score
				int score = input.readInt();

				// store
				scores.add(new Score(new String(nameBytes), score));
			}

			input.close();
		} catch (FileNotFoundException ex) {
			System.out.println("Could not find specified file.");
		} catch (Exception ex) {
			System.out.println("Error reading from file.");
		}

		sortScores();
	}

	public static void saveScores(String dir) {
		File file = new File(dir);
		try {
			DataOutputStream output = new DataOutputStream(new FileOutputStream(file));

			// write the number of scores in the file
			output.writeInt(scores.size());

			int scoresSaved = 0;
			for (Score s : scores) {
				// write the name
				output.writeInt(s.name.length());
				output.writeBytes(s.name);

				// write the score
				output.writeInt(s.score);

				scoresSaved++;
				if (scoresSaved >= MAX_SAVED_SCORES)
					break;
			}

			// write(byte[])
			String test = "This is a test."; // len = 15
			byte[] bytes = test.getBytes();
			output.write(bytes, 0, bytes.length);

			output.flush();
			output.close();
		} catch (FileNotFoundException ex) {
			System.out.println("Could not write to specified file.");
		} catch (IOException ex) {
			System.out.println("Error writing to file.");
		}
	}

	public static void addScore(String name, int score) {
		scores.add(new Score(name, score));
		sortScores();
	}

	private static void sortScores() {
		// sorts the scores from highest score to lowest score
		Collections.sort(scores, new Comparator<Score>() {
			public int compare(Score s1, Score s2) {
				return (int)Math.signum(s2.score - s1.score);
			}
		});
	}

	///////////
	// SCORE //

	private static class Score {
		public String name;
		public int score;

		public Score(String name, int score) {
			this.name = name;
			this.score = score;
		}
	}
}