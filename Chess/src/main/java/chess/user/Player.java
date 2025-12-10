package chess.user;

import java.io.Serializable;

public class Player implements Serializable {

    private static final long serialVersionUID = 1L;

    // Account info
    public String username;
    public String password;

    // 1v1 stats
    public int whiteWins = 0;
    public int blackWins = 0;

    // AI stats – fewest moves required to beat the AI
    // Using Integer.MAX_VALUE = no wins yet
    public int bestTestMoves = Integer.MAX_VALUE;    // Depth ≤ 5
    public int bestEasyMoves = Integer.MAX_VALUE;    // Depth ≤ 5
    public int bestMediumMoves = Integer.MAX_VALUE;  // Depth ≤ 10
    public int bestHardMoves = Integer.MAX_VALUE;    // Depth > 10

    // Constructor
    public Player(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Empty constructor for serialization
    public Player() {}

}


