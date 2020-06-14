package comp1140.ass2.components;

// authored by Dillon Chen
public enum TileType {
    Leader, Warrior, Weft, Warp;

    public static TileType charToTileType(char letter) {
        if ('a' <= letter && letter <= 'e')
            return Leader;
        if ('f' <= letter && letter <= 'h')
            return Warrior;
        if (letter == 'i')
            return Weft;
        if (letter == 'j')
            return Warp;
        throw new IllegalArgumentException("(charToTileType) Unexpected char input: " + letter);
    }
}
