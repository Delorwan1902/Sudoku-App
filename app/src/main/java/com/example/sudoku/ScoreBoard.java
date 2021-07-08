package com.example.sudoku;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ScoreBoard {
    String SCORE_BOARD = "scoreBoard";
    String SCORE_MAP = "scoreMap";

    default Map<String, Score> defaultScores() {
        Map<String, Score> map = new HashMap<>();
        String[] difficulties = new String[] {"beginner", "easy", "medium", "hard", "expert"};
        for (String difficulty : difficulties) {
            map.put(difficulty, new Score());
        }
        return map;
    }

    default boolean isNullOrEmpty( final Map<?, ?> m ) {
        return m == null || m.isEmpty();
    }
}
