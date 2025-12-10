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

            // Very simple parsing (we expect the exact format we write)
            String content = json.toString().trim();
            if (content.length() < 2) return;

            String[] entries = content.substring(1, content.length() - 1).split("},");
            for (String e : entries) {
                if (!e.endsWith("}")) e += "}";

                // Extract username
                String name = e.substring(e.indexOf("\"") + 1, e.indexOf("\":"));

                // Extract values
                String body = e.substring(e.indexOf("{") + 1, e.lastIndexOf("}"));
                String[] fields = body.split(",");

                String pass = "";
                int wWins = 0, bWins = 0, bestW = Integer.MAX_VALUE, bestB = Integer.MAX_VALUE;

                for (String f2 : fields) {
                    String[] kv = f2.split(":");
                    String key = kv[0].replace("\"", "").trim();
                    String val = kv[1].replace("\"", "").trim();

                    switch (key) {
                        case "passwordHash": pass = val; break;
                        case "whiteWins":    wWins = Integer.parseInt(val); break;
                        case "blackWins":    bWins = Integer.parseInt(val); break;
                        case "bestWhiteWinMoves": bestW = Integer.parseInt(val); break;
                        case "bestBlackWinMoves": bestB = Integer.parseInt(val); break;
                    }
                }

                Player p = new Player(name, pass);
                p.whiteWins = wWins;
                p.blackWins = bWins;
                p.bestWhiteWinMoves = bestW;
                p.bestBlackWinMoves = bestB;

                players.put(name, p);
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
                pw.println("    \"passwordHash\": \"" + p.passwordHash + "\",");
                pw.println("    \"whiteWins\": " + p.whiteWins + ",");
                pw.println("    \"blackWins\": " + p.blackWins + ",");
                pw.println("    \"bestWhiteWinMoves\": " + p.bestWhiteWinMoves + ",");
                pw.println("    \"bestBlackWinMoves\": " + p.bestBlackWinMoves);
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
        String hash = Integer.toString(password.hashCode());
        Player p = new Player(username, hash);
        players.put(username, p);
        save();
        return p;
    }

    public static boolean validate(String username, String password) {
        Player p = players.get(username);
        if (p == null) return false;
        return p.passwordHash.equals(Integer.toString(password.hashCode()));
    }
    
    public static void update(Player p) {
        players.put(p.username, p);
        save();
    }

}

