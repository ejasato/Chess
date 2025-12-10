package chess.user;

import java.io.*;
import java.util.HashMap;

public class PlayerDatabase {

    private static final String FILE = "players.json";
    private static HashMap<String, Player> players = new HashMap<>();


    /** Load players.json manually */
    public static void load() {
        try {
            File f = new File(FILE);
            if (!f.exists()) return;

            BufferedReader br = new BufferedReader(new FileReader(f));
            StringBuilder json = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null)
                json.append(line);

            br.close();

            String content = json.toString().trim();
            if (content.length() < 2) return;

            // Remove outer braces
            content = content.substring(1, content.length() - 1).trim();

            // Split entries
            String[] entries = content.split("},");
            for (String e : entries) {
                if (!e.endsWith("}")) e += "}";

                String username = e.substring(e.indexOf("\"") + 1, e.indexOf("\":"));

                String body = e.substring(e.indexOf("{") + 1, e.lastIndexOf("}"));
                String[] fields = body.split(",");

                String password = "";
                int whiteWins = 0;
                int blackWins = 0;
                int bestTest = Integer.MAX_VALUE;
                int bestEasy = Integer.MAX_VALUE;
                int bestMedium = Integer.MAX_VALUE;
                int bestHard = Integer.MAX_VALUE;

                for (String f2 : fields) {
                    String[] kv = f2.split(":");
                    String key = kv[0].replace("\"", "").trim();
                    String val = kv[1].replace("\"", "").trim();

                    switch (key) {
                        case "password": password = val; break;
                        case "whiteWins": whiteWins = Integer.parseInt(val); break;
                        case "blackWins": blackWins = Integer.parseInt(val); break;

                        case "bestTestMoves": bestTest = Integer.parseInt(val); break;
                        case "bestEasyMoves": bestEasy = Integer.parseInt(val); break;
                        case "bestMediumMoves": bestMedium = Integer.parseInt(val); break;
                        case "bestHardMoves": bestHard = Integer.parseInt(val); break;
                    }
                }

                Player p = new Player(username, password);
                p.whiteWins = whiteWins;
                p.blackWins = blackWins;
                p.bestTestMoves = bestTest;
                p.bestEasyMoves = bestEasy;
                p.bestMediumMoves = bestMedium;
                p.bestHardMoves = bestHard;

                players.put(username, p);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /** Save database manually */
    public static void save() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE))) {

            pw.println("{");
            int count = 0;
            int size = players.size();

            for (Player p : players.values()) {

                pw.println("  \"" + p.username + "\": {");
                pw.println("    \"password\": \"" + p.password + "\",");
                pw.println("    \"whiteWins\": " + p.whiteWins + ",");
                pw.println("    \"blackWins\": " + p.blackWins + ",");
                pw.println("    \"bestTestMoves\": " + p.bestTestMoves + ",");
                pw.println("    \"bestEasyMoves\": " + p.bestEasyMoves + ",");
                pw.println("    \"bestMediumMoves\": " + p.bestMediumMoves + ",");
                pw.println("    \"bestHardMoves\": " + p.bestHardMoves);
                pw.print("  }");

                if (++count < size) pw.println(",");
                else pw.println();
            }

            pw.println("}");
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public static Player get(String username) {
        return players.get(username);
    }

    public static Player create(String username, String password) {
        Player p = new Player(username, Integer.toString(password.hashCode()));
        players.put(username, p);
        save();
        return p;
    }

    public static boolean validate(String username, String password) {
        Player p = players.get(username);
        if (p == null) return false;
        return p.password.equals(Integer.toString(password.hashCode()));
    }

    public static void update(Player p) {
        players.put(p.username, p);
        save();
    }
}
