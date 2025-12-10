package chess.user;

public class Player {

    public String username;
    public String passwordHash;

    public int whiteWins = 0;
    public int blackWins = 0;

    public int bestWhiteWinMoves = Integer.MAX_VALUE;
    public int bestBlackWinMoves = Integer.MAX_VALUE;

    public Player(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public void recordWhiteWin(int moves) {
        whiteWins++;
        bestWhiteWinMoves = Math.min(bestWhiteWinMoves, moves);
    }

    public void recordBlackWin(int moves) {
        blackWins++;
        bestBlackWinMoves = Math.min(bestBlackWinMoves, moves);
    }
}


