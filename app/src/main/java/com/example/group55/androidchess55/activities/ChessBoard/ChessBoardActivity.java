package com.example.group55.androidchess55.activities.ChessBoard;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.example.group55.androidchess55.R;
import com.example.group55.androidchess55.activities.ChessBoard.adapters.ChessBoardAdapter;
import com.example.group55.androidchess55.models.Bishop;
import com.example.group55.androidchess55.models.ChessPiece;
import com.example.group55.androidchess55.models.King;
import com.example.group55.androidchess55.models.Knight;
import com.example.group55.androidchess55.models.Pawn;
import com.example.group55.androidchess55.models.Queen;
import com.example.group55.androidchess55.models.Rook;

import java.util.LinkedList;

public class ChessBoardActivity extends AppCompatActivity {

    public static ChessPiece[][] board;
    static ChessPiece[] horizon_board;
    static BaseAdapter adapter;
    static GridView board_grid;
    static boolean moving = false;
    static int prev_pos[] = new int[2];
    static ChessPiece prev_piece = null;
    static char turn_color = '\0';
    /**
     * Initialize turn to 1.
     */
    static int turn = 1;
    /**
     * Set default promotion to Queen.
     */
    static char promotion = '\0';

    /**
     * Black King's current position.
     */
    static int[] black_king = new int[]{0,4};
    /**
     * White King's current position.
     */
    static int[] white_king = new int[]{7,4};
    /**
     * <code>true</code> if either Black or White King is in check.
     */
    static boolean isInCheck = false;
    /**
     * <code>true</code> when testing for safe zones for check.
     */
    static boolean zone_check_mode = false;
    /**
     * Contains valid coordinates for escaping check.
     */
    static LinkedList<int[]> escape_check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chess_board);

        //Setup toolbar
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        myToolbar.setTitle("Cancel Game");
        setSupportActionBar(myToolbar);
        ActionBar ab = null;
        while(ab == null){ ab = getSupportActionBar(); }
        ab.setDisplayHomeAsUpEnabled(true);

        //Create board
        board  = new ChessPiece[8][8];
        horizon_board = new ChessPiece[64];
        initBoard();


        adapter = new ChessBoardAdapter(ChessBoardActivity.this, horizon_board);
        convertToHorizon();
        board_grid = findViewById(R.id.board_grid);
        board_grid.setAdapter(adapter);

        board_grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                Log.d("stuff", String.valueOf(moving));
                if(!moving && horizon_board[position] != null){
                    prev_pos[0] = position / 8;
                    prev_pos[1] = position % 8;
                    prev_piece = board[position/8][position%8];
                    moving = true;
                }else if(moving){
                    int dest[] = new int[2];
                    dest[0] = position/8;
                    dest[1] = position%8;
                    //Log.d("stuff", Integer.toString(dest[0]) + "," + Integer.toString(dest[1]));
                    if(prev_piece.move(dest)){
                        convertToHorizon();
                        board_grid.setAdapter(adapter);
                        if(horizon_board[prev_pos[0]*8 + prev_pos[1]] == null){
                            Log.d("stuff", "null");
                        }
                        Log.d("stuff", horizon_board[position].toString());
                        moving = false;
                    }
                }
            }
        });

    }

    /**
     * Shows dialog to select newly promoted piece type.
     */
    public void promotePiece() {
        AlertDialog.Builder d = new AlertDialog.Builder(ChessBoardActivity.this);
        d.setCancelable(false);
        d.setTitle("Select piece to promote to");
        final CharSequence[] pieces = {"Rook", "Bishop", "Knight", "Queen"};
        d.setItems(pieces, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch(pieces[i].toString()) {
                    case "Rook": promotion = 'R'; break;
                    case "Bishop": promotion = 'B'; break;
                    case "Knight": promotion = 'N'; break;
                    default: promotion = '\0';
                }
            }
        });
        d.create().show();
    }

    /**
     * Adapt 2D chessboard to 1D
     */
    public static void convertToHorizon(){
        for(int i = 0; i < 64; i++){
            horizon_board[i] = board[i/8][i%8];
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * Converts a given string coordinate to its position on the board.
     *
     * @param pos Coordinate in string form. Ex: a1
     * @return <code>[row, col]</code> of given pos.
     */
    public static int[] strPositionToXY(String pos) {
        //Create array for final pos
        int[] final_pos = new int[2];

        //Convert letter/number to separate ints
        final_pos[0] = 8 - Character.getNumericValue(pos.charAt(1));
        final_pos[1] = pos.charAt(0) - 'a';

        return final_pos;
    }

    /**
     * Places given piece on board at given position and sets the pieces pos field.
     *
     * @param pos Position to place piece.
     * @param piece ChessPiece to be placed.
     */
    public static void placePiece(int[] pos, ChessPiece piece) {
        board[pos[0]][pos[1]] = piece;
        piece.setPos(pos);
    }

    /**
     * Initializes board with all the pieces for both Black and White in their correct starting positions.
     */
    public static void initBoard() {

        // Place black pieces
        placePiece(strPositionToXY("a7"), new Pawn('b'));
        placePiece(strPositionToXY("b7"), new Pawn('b'));
        placePiece(strPositionToXY("c7"), new Pawn('b'));
        placePiece(strPositionToXY("d7"), new Pawn('b'));
        placePiece(strPositionToXY("e7"), new Pawn('b'));
        placePiece(strPositionToXY("f7"), new Pawn('b'));
        placePiece(strPositionToXY("g7"), new Pawn('b'));
        placePiece(strPositionToXY("h7"), new Pawn('b'));
        placePiece(strPositionToXY("e8"), new King('b'));
        placePiece(strPositionToXY("d8"), new Queen('b'));
        placePiece(strPositionToXY("c8"), new Bishop('b'));
        placePiece(strPositionToXY("f8"), new Bishop('b'));
        placePiece(strPositionToXY("b8"), new Knight('b'));
        placePiece(strPositionToXY("g8"), new Knight('b'));
        placePiece(strPositionToXY("a8"), new Rook('b'));
        placePiece(strPositionToXY("h8"), new Rook('b'));

        // Place white pieces
        placePiece(strPositionToXY("a2"), new Pawn('w'));
        placePiece(strPositionToXY("b2"), new Pawn('w'));
        placePiece(strPositionToXY("c2"), new Pawn('w'));
        placePiece(strPositionToXY("d2"), new Pawn('w'));
        placePiece(strPositionToXY("e2"), new Pawn('w'));
        placePiece(strPositionToXY("f2"), new Pawn('w'));
        placePiece(strPositionToXY("g2"), new Pawn('w'));
        placePiece(strPositionToXY("h2"), new Pawn('w'));
        placePiece(strPositionToXY("e1"), new King('w'));
        placePiece(strPositionToXY("d1"), new Queen('w'));
        placePiece(strPositionToXY("c1"), new Bishop('w'));
        placePiece(strPositionToXY("f1"), new Bishop('w'));
        placePiece(strPositionToXY("b1"), new Knight('w'));
        placePiece(strPositionToXY("g1"), new Knight('w'));
        placePiece(strPositionToXY("a1"), new Rook('w'));
        placePiece(strPositionToXY("h1"), new Rook('w'));

    }

    /**
     * Given a piece returns a clone of it.
     *
     * @param item Piece to clone.
     * @return Cloned piece.
     */
    public static ChessPiece cloner(ChessPiece item){
        switch (item.getName()) {
            case 'P':
                return new Pawn(item);
            case 'Q':
                return new Queen(item);
            case 'N':
                return new Knight(item);
            case 'B':
                return new Bishop(item);
            case 'R':
                return new Rook(item);
            case 'K':
                return new King(item);
            default:
                return new Pawn(item);
        }
    }

    /**
     * Checks that the coordinates given are inside the bounds
     *
     * @param input Coordinates to check.
     * @return <code>true</code> if input is within the bounds of the board.
     */
    public static boolean inBounds(int[] input){
        return input[0] >= 0 && input[1] >= 0 && input[0] <= 7 && input[1] <= 7;
    }
}
