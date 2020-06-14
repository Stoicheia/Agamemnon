package comp1140.ass2.players.minimax;

// authored by Alexander Cox
public class Action {
    public int score;
    public String action;

    Action(int value, String action) {
        this.score = value;
        this.action = action;
    }

    @Override
    public String toString() {
        return String.format("Action: %s with expected utility: %d",action,score);
    }
}
