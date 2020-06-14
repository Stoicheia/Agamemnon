package comp1140.ass2.components;

// authored by Dillon Chen
public enum EdgeType { // represents the types of edges that exist between nodes
    Strength,
    Leadership,
    Force,
    Empty;

    // useful for getting png name
    public static String charToEdgeName(char letter) {
        switch (letter) {
            case 'S':
                return "strength";
            case 'L':
                return "leadership";
            case 'F':
                return "force";
            case 'E':
                return "empty";
        }
        throw new IllegalArgumentException("(charToEdgeName) Unexpected char input: " + letter);
    }

    public static EdgeType charToEdgeType(char letter) {
        switch (letter) {
            case 'S':
                return Strength;
            case 'L':
                return Leadership;
            case 'F':
                return Force;
            case 'E':
                return Empty;
        }
        throw new IllegalArgumentException("(charToEdgeType) Unexpected char input: " + letter);
    }

    public static EdgeType intToEdgeType(int integer) {
        switch (integer) {
            case 0:
                return Strength;
            case 1:
                return Leadership;
            case 2:
                return Force;
            case 3:
                return Empty;
        }
        throw new IllegalArgumentException("(intToEdgeType) Unexpected int input: " + integer);
    }

    public char edgeTypeToChar() {
        switch (this) {
            case Strength:
                return 'S';
            case Leadership:
                return 'L';
            case Force:
                return 'F';
            case Empty:
                return 'E';
        }
        throw new IllegalArgumentException("(edgeTypeToChar) Unexpected input: " + this);
    }
}
