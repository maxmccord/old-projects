

import static java.lang.System.*;
import java.util.*;

public class ScoreTest {
	public static void main(String[] args) {
		ScoreUtility.loadScores("test.scores");
		ScoreUtility.resetScores();
		
		String name = "Random Guy";
		int score = (int)(Math.random() * 2000) * 2 + 100;
		ScoreUtility.addScore(name, score);
		
		ScoreUtility.saveScores("test.scores");
		
		List<String> names = ScoreUtility.getNames();
		List<Integer> scores = ScoreUtility.getScores();
		
		for (int i = 0; i < names.size(); i++) {
			out.println(names.get(i) + ": " + scores.get(i));
		}
	}
}