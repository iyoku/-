import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AnimalShogi {

    public static void main(String[] args) {
        //プレイヤー2人の駒を用意して盤に並べる
        Board board = new Board(

                //先手
                new Player1(
                        new ArrayList<Piece>(Arrays.asList(
                                //x,y座標は、盤の左上 = (0,0)
                                new Chick(1, 2), //ひよこ

                                new Elephants(0, 3), //ぞう
                                new Lion(1, 3), //らいおん
                                new Giraffe(2, 3) //きりん
                        ))),

                //後手
                new Player2(
                        new ArrayList<Piece>(Arrays.asList(
                                new Chick(1, 1),

                                new Elephants(2, 0),
                                new Lion(1, 0),
                                new Giraffe(0, 0)))));

        //ゲーム開始
        board.play();

    }

}

class Board {
    private Player player1;
    private Player player2;
    private Player currentPlayer;
    private String piecesMap[][] = new String[4][3];

    Board(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.currentPlayer = player1;
    }

    void play() {
        int i = 0;
        while (true) {
            System.out.println("\n=== (" + ++i + ") ===");
            print();
            command();

            changePlayer(); //プレイヤー交代
        }

    }

    private void print() {
        initPicesMap();
        setPicesMap(player1);
        setPicesMap(player2);

        System.out.println(player2.getLongName() + "　持ち駒:" + player2.getPrintPiecesOnHand());
        System.out.println("        A            B            C");
        for (int i = 0; i < piecesMap.length; i++) {
            System.out.println(" +------------+------------+------------+");
            System.out.print(i + 1);
            for (int j = 0; j < piecesMap[i].length; j++) {
                System.out.print("|");
                System.out.print(piecesMap[i][j]);
            }
            System.out.println("|");
        }
        System.out.println(" +------------+------------+------------+");

        System.out.println(player1.getLongName() + "　持ち駒:" + player1.getPrintPiecesOnHand());
    }

    private void initPicesMap() {
        for (int i = 0; i < piecesMap.length; i++) {
            for (int j = 0; j < piecesMap[i].length; j++) {
                piecesMap[i][j] = "　　　　　　";
            }
        }
    }

    private void setPicesMap(Player player) {
        for (Piece p : player.getPiecesOnBoard()) {
            piecesMap[p.y][p.x] = ("" + player + p + "　　　　　　").substring(0, 7);
        }
    }

    private void command() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line;
        //コマンド入力　→エラー時リトライする
        while (true) {
            try {
                System.out.println("\n入力例) h=help / q=quit / B2H=B2に駒（H:ひよこ）を移動 / MB2H=持ち駒（M）の中からB2に駒（H:ひよこ）を移動");
                System.out.print("\nCommand " + currentPlayer.getLongName() + " > ");
                line = br.readLine();
                line = line.trim().toUpperCase();
                switch (line.charAt(0)) {
                case 'Q':
                    System.out.println("\n<<ゲーム終了>>");
                    System.exit(0);
                    break;
                case 'H':
                    System.out.println("<help>");
                    System.out.println("\t駒の動かしたとかはネットで検索してください...");
                    break;
                case 'M':
                    break;
                default:
                    if (movePiece(
                            line.charAt(0) - 'A',
                            Integer.parseInt(line.substring(1, 2)) - 1,
                            line.substring(2, 3)))
                        return;
                }
            } catch (Exception e) {
            } finally {

            }
        }
    }

    private boolean movePiece(int x, int y, String pieceName) {
        Piece p;
        p = currentPlayer.getPieceOnBoard(x, y);
        //移動先 = 自分の駒　→エラー
        if (p != null) {
            System.out.println("Error : 自分の駒の上には移動できません");
            return false;
        }

        //移動先 = 相手の駒　→自分の持ち駒にする
        Player vsPlayer = getVsPlayer(currentPlayer);
        p = vsPlayer.getPieceOnBoard(x, y);
        if (p != null) {
            currentPlayer.addPiecesOnHand(p);
            vsPlayer.removePiecesOnBoard(p);
        }

        //自分の駒を移動する
        p = currentPlayer.getPieceOnBoard(pieceName);
        p.x = x;
        p.y = y;

        return true;
    }

    private void changePlayer() {
        currentPlayer = getVsPlayer(currentPlayer);
    }

    private Player getVsPlayer(Player player) {
        if (player == player1)
            return player2;
        else
            return player1;
    }
}

abstract class Player {
    private List<Piece> piecesOnBoard = new ArrayList<Piece>(); //盤上の駒
    private List<Piece> piecesOnHand = new ArrayList<Piece>(); //持ち駒
    private String name;
    private String longName;

    Player(List<Piece> piecesOnBoard, String name, String longName) {
        this.piecesOnBoard = piecesOnBoard;
        this.name = name;
        this.longName = longName;
    }

    public String toString() {
        return name;
    }

    String getLongName() {
        return longName;
    }

    List<Piece> getPiecesOnBoard() {
        return piecesOnBoard;
    }

    Piece getPieceOnBoard(String name) {
        for (Piece p : piecesOnBoard) {
            if (p.equals(name))
                return p;
        }
        return null;
    }

    Piece getPieceOnBoard(int x, int y) {
        for (Piece p : piecesOnBoard) {
            if (p.equals(x, y))
                return p;
        }
        return null;
    }

    String getPrintPiecesOnHand() {
        StringBuilder s = new StringBuilder();
        for (Piece p : piecesOnHand) {
            s.append(p + "　");
        }
        return s.toString();
    }

    void addPiecesOnHand(Piece p) {
        piecesOnHand.add(p);
    }

    void removePiecesOnBoard(Piece p) {
        piecesOnBoard.remove(p);
    }
}

class Player1 extends Player {
    Player1(List<Piece> piecesOnBoard) {
        super(piecesOnBoard, "▲", "先手:▲"); //先手
    }
}

class Player2 extends Player {
    Player2(List<Piece> piecesOnBoard) {
        super(piecesOnBoard, "▽", "後手:▽"); //後手
    }
}

abstract class Piece {
    int x, y;
    String name;

    Piece(int x, int y, String name) {
        this.x = x;
        this.y = y;
        this.name = name;
    }

    public String toString() {
        return name;
    }

    boolean equals(String name) {
        return this.name.charAt(0) == name.charAt(0);
    }

    boolean equals(int x, int y) {
        return this.x == x && this.y == y;
    }
}

class Lion extends Piece {
    Lion(int x, int y) {
        super(x, y, "L:らいおん"); //らいおん
    }
}

class Elephants extends Piece {
    Elephants(int x, int y) {
        super(x, y, "Z:ぞう"); //ぞう
    }
}

class Giraffe extends Piece {
    Giraffe(int x, int y) {
        super(x, y, "K:きりん"); //きりん
    }
}

class Chick extends Piece {
    Chick(int x, int y) {
        super(x, y, "H:ひよこ"); //ひよこ
    }
}

class Chicken extends Piece {
    Chicken(int x, int y) {
        super(x, y, "N:にわとり"); //にわとり
    }
}
