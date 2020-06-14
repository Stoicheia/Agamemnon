Skeleton Plan
=============

Classes (see also actual files in `src`)
-------
`public class Node`
* constructor: (int id, HashMap<Node, Edge> connections)
* fields:
    * int id
    * Tile tile
    * HashMap<Node, Edge> connections
* behaviour:
    * Construct list of nodes from String[] state
    * Update connections and tiles from placements

`public enum EdgeType`

Strength, Leadership, Force

`public class Tile`
* constructor: (String subAction)

* fields:
    * TileType tileType;
    * char encoding;
    * char rank;
    * int strength;
    * Faction faction;
    * String connection1;
    * String connection2;

`public enum TileType`

Leader, Warrior, Weft, Warp

`public class Player`

* constructor: (String name, Faction turn)

* fields:
    * String name; (for game immersion)
    * Faction faction
    * int score;
    * int[] heldTiles;

`public class Agent extends Player`

has a playMove() method, which outputs a String action given an input state

`public enum Faction` (whose turn it is: 'O'/Greek for Player 1, 'B'/Trojan for Player 2)

Greek, Trojan

addtional things to put into `Agamemnon` class:

* constructor: (String[] state)

    - List of players
    - List of nodes
    - type
    - destination
    - node states
    - edge states
    - static stringToState(String string)
    - static stateToString(Game game)
    - update()
    - placeTile(Tile tile)
        - placeNormal
        - placeWarp
        - placeWeft
    - findStrings()
    - calculateScore()
    
`public class Loom extends Game`
    - static stringToState(String string)