package comp1140.ass2.players;

// authored by Dillon Chen
public enum Faction {
    Greek,   // Player1 'O'
    Trojan,  // Player2 'B'
    Random;

    public static Faction charToFaction(char c) {
        if (c == 'O')
            return Greek;
        if (c == 'B')
            return Trojan;
        throw new IllegalArgumentException("(charToFaction) Unexpected char input: " + c);
    }

    public char toChar() {
        return this == Greek ? 'O' : 'B';
    }
}
