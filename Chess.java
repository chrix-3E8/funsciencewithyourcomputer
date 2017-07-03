import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
// Copyright 2007 by Chrix - visit www.funsciencewithyourcomputer.org
public class Chess {

	public static void main(String[] args) {
		Chess chess = new Chess();
		while (true) {
			chess.displayBoard();
			chess.whiteMove();
			if (chess.gameOver())
				break;
			chess.blackMove();
			if (chess.gameOver())
				break;
		}
		chess.displayWinner();
	}

	String board = "rnbqkbnr" + "pppppppp" + "        " + "        " + "        "
			+ "        " + "PPPPPPPP" + "RNBQKBNR";

	VectorGraphics.VectorScreen console;

	public Chess() {
		console = new VectorGraphics.VectorScreen();
		console.read("chess.svg");
		console.setVisible(true);
	}

	boolean gameOver() {
		return board.indexOf("k") == -1 || board.indexOf("K") == -1;
	}

	void displayBoard() {
		console.drawBoard(board);
	}

	void displayWinner() {
		displayBoard();
		if (board.indexOf("K") == -1)
			System.out.println("Black wins");
		else if (board.indexOf("k") == -1)
			System.out.println("White wins");
		else
			System.out.println("Stalemate");
	}

	void whiteMove() {
		do {
			System.out.println();
			System.out.println("Please enter white move: ");
			Scanner keyboard = new Scanner(System.in);
			String move = keyboard.nextLine();
			String newBoard = getNewBoard(board, move);
			String[] boards = getPossibleMoves(board, true);
			for (int i = 0; i < boards.length; i++) {
				if (boards[i].equals(newBoard)) {
					board = newBoard;
					return;
				}
			}
		} while (true);
	}

	String getNewBoard(String board, String move) {
		String row = "abcdefgh";
		String column = "12345678";
		int x1 = row.indexOf(move.charAt(0));
		int y1 = column.indexOf(move.charAt(1));
		int x2 = row.indexOf(move.charAt(2));
		int y2 = column.indexOf(move.charAt(3));
		return move(board, x1, y1, x2, y2);
	}

	String move(String board, int x1, int y1, int x2, int y2) {
		String piece = get(board, x1, y1);
		board = set(board, x1, y1, " ");
		board = set(board, x2, y2, piece);
		return board;
	}

	int pos(int x1, int y1) {
		return x1 + (7 - y1) * 8;
	}

	String get(String board, int x1, int y1) {
		if (x1 < 0 || x1 > 7 || y1 < 0 || y1 > 7)
			return "X";
		return "" + board.charAt(pos(x1, y1));
	}

	String set(String board, int x1, int y1, String piece) {
		int pos = pos(x1, y1);
		return board.substring(0, pos) + piece + board.substring(pos + 1);
	}

	String[] getPossibleMoves(String board, boolean white) {
		List result = new ArrayList();
		for (int x = 0; x < 8; x++)
			for (int y = 0; y < 8; y++) {
				String piece = get(board, x, y);
				if ((white && isWhite(piece)) || (!white && isBlack(piece))) {
					String[] boards = getPossibleMoves(board, x, y);
					result.addAll(Arrays.asList(boards));
				}
			}
		return (String[]) result.toArray(new String[result.size()]);
	}

	boolean isWhite(String piece) {
		return "PRNBQK".indexOf(piece) != -1;
	}

	boolean isBlack(String piece) {
		return "prnbqk".indexOf(piece) != -1;
	}

	boolean isPawn(String piece) {
		return "Pp".indexOf(piece) != -1;
	}

	boolean isRook(String piece) {
		return "Rr".indexOf(piece) != -1;
	}

	boolean isKnight(String piece) {
		return "Nn".indexOf(piece) != -1;
	}

	boolean isBishop(String piece) {
		return "Bb".indexOf(piece) != -1;
	}

	boolean isQueen(String piece) {
		return "Qq".indexOf(piece) != -1;
	}

	boolean isKing(String piece) {
		return "Kk".indexOf(piece) != -1;
	}

	boolean isEmpty(String piece) {
		return piece.equals(" ");
	}

	boolean isDifferentColor(String piece, String other) {
		return (isWhite(piece) && isBlack(other))
				|| (isBlack(piece) && isWhite(other));
	}

	String[] getPossibleMovesPawn(String board, int x, int y) {
		List result = new ArrayList();
		if (isWhite(get(board, x, y))) {
			if (isEmpty(get(board, x, y + 1))) {
				result.add(move(board, x, y, x, y + 1));
				if (y == 1)
					if (isEmpty(get(board, x, y + 2))) {
						result.add(move(board, x, y, x, y + 2));
					}
			}
			if (isBlack(get(board, x - 1, y + 1)))
				result.add(move(board, x, y, x - 1, y + 1));
			if (isBlack(get(board, x + 1, y + 1)))
				result.add(move(board, x, y, x + 1, y + 1));
		} else {
			if (isEmpty(get(board, x, y - 1))) {
				result.add(move(board, x, y, x, y - 1));
				if (y == 6)
					if (isEmpty(get(board, x, y - 2))) {
						result.add(move(board, x, y, x, y - 2));
					}
			}
			if (isWhite(get(board, x - 1, y - 1)))
				result.add(move(board, x, y, x - 1, y - 1));
			if (isWhite(get(board, x + 1, y - 1)))
				result.add(move(board, x, y, x + 1, y - 1));
		}
		return (String[]) result.toArray(new String[result.size()]);
	}

	String[] getPossibleMovesRook(String board, int x, int y) {
		List result = new ArrayList();
		String piece = get(board, x, y);
		for (int i = 1; i <= 7; i++) {
			String destPiece = get(board, x - i, y);
			boolean isEmpty = isEmpty(destPiece);
			if (isEmpty || isDifferentColor(piece, destPiece)) {
				result.add(move(board, x, y, x - i, y));
			}
			if (!isEmpty)
				break;
		}
		for (int i = 1; i <= 7; i++) {
			String destPiece = get(board, x + i, y);
			boolean isEmpty = isEmpty(destPiece);
			if (isEmpty || isDifferentColor(piece, destPiece)) {
				result.add(move(board, x, y, x + i, y));
			}
			if (!isEmpty)
				break;
		}
		for (int i = 1; i <= 7; i++) {
			String destPiece = get(board, x, y - i);
			boolean isEmpty = isEmpty(destPiece);
			if (isEmpty || isDifferentColor(piece, destPiece)) {
				result.add(move(board, x, y, x, y - i));
			}
			if (!isEmpty)
				break;
		}
		for (int i = 1; i <= 7; i++) {
			String destPiece = get(board, x, y + i);
			boolean isEmpty = isEmpty(destPiece);
			if (isEmpty || isDifferentColor(piece, destPiece)) {
				result.add(move(board, x, y, x, y + i));
			}
			if (!isEmpty)
				break;
		}
		return (String[]) result.toArray(new String[result.size()]);
	}

	String[] getPossibleMovesBishop(String board, int x, int y) {
		List result = new ArrayList();
		String piece = get(board, x, y);
		for (int i = 1; i <= 7; i++) {
			String destPiece = get(board, x - i, y - i);
			boolean isEmpty = isEmpty(destPiece);
			if (isEmpty || isDifferentColor(piece, destPiece)) {
				result.add(move(board, x, y, x - i, y - i));
			}
			if (!isEmpty)
				break;
		}
		for (int i = 1; i <= 7; i++) {
			String destPiece = get(board, x - i, y + i);
			boolean isEmpty = isEmpty(destPiece);
			if (isEmpty || isDifferentColor(piece, destPiece)) {
				result.add(move(board, x, y, x - i, y + i));
			}
			if (!isEmpty)
				break;
		}
		for (int i = 1; i <= 7; i++) {
			String destPiece = get(board, x + i, y + i);
			boolean isEmpty = isEmpty(destPiece);
			if (isEmpty || isDifferentColor(piece, destPiece)) {
				result.add(move(board, x, y, x + i, y + i));
			}
			if (!isEmpty)
				break;
		}
		for (int i = 1; i <= 7; i++) {
			String destPiece = get(board, x + i, y - i);
			boolean isEmpty = isEmpty(destPiece);
			if (isEmpty || isDifferentColor(piece, destPiece)) {
				result.add(move(board, x, y, x + i, y - i));
			}
			if (!isEmpty)
				break;
		}
		return (String[]) result.toArray(new String[result.size()]);
	}

	String[] getPossibleMovesKnight(String board, int x, int y) {
		List result = new ArrayList();
		String piece = get(board, x, y);
		int[][] moves = { { x - 1, y + 2 }, { x + 1, y + 2 }, { x - 2, y + 1 },
				{ x + 2, y + 1 }, { x - 2, y - 1 }, { x + 2, y - 1 }, { x - 1, y - 2 },
				{ x + 1, y - 2 } };
		for (int i = 0; i < moves.length; i++) {
			String destPiece = get(board, moves[i][0], moves[i][1]);
			if (isEmpty(destPiece) || isDifferentColor(piece, destPiece))
				result.add(move(board, x, y, moves[i][0], moves[i][1]));
		}
		return (String[]) result.toArray(new String[result.size()]);
	}

	String[] getPossibleMovesQueen(String board, int x, int y) {
		String[] movesBishop = getPossibleMovesBishop(board, x, y);
		String[] movesRook = getPossibleMovesRook(board, x, y);
		String[] result = new String[movesBishop.length + movesRook.length];
		System.arraycopy(movesBishop, 0, result, 0, movesBishop.length);
		System
				.arraycopy(movesRook, 0, result, movesBishop.length, movesRook.length);
		return result;
	}

	String[] getPossibleMovesKing(String board, int x, int y) {
		List result = new ArrayList();
		String piece = get(board, x, y);
		for (int i = -1; i <= 1; i++)
			for (int j = -1; j <= 1; j++) {
				String destPiece = get(board, x + i, y + j);
				if (isEmpty(destPiece) || isDifferentColor(piece, destPiece))
					result.add(move(board, x, y, x + i, y + j));
			}
		return (String[]) result.toArray(new String[result.size()]);
	}

	String[] getPossibleMoves(String board, int x, int y) {
		String piece = get(board, x, y);
		if (isPawn(piece))
			return getPossibleMovesPawn(board, x, y);
		if (isRook(piece))
			return getPossibleMovesRook(board, x, y);
		if (isBishop(piece))
			return getPossibleMovesBishop(board, x, y);
		if (isKnight(piece))
			return getPossibleMovesKnight(board, x, y);
		if (isQueen(piece))
			return getPossibleMovesQueen(board, x, y);
		if (isKing(piece))
			return getPossibleMovesKing(board, x, y);
		return new String[0];
	}

	void blackMove() {
		String[] boards = getPossibleMoves(board, false);
		boards = evalBest(boards, 1);
		board = boards[Math.abs(new Random().nextInt()) % boards.length];
	}

	int value(String piece) {
		if (isPawn(piece))
			return 1;
		if (isBishop(piece) || isKnight(piece))
			return 3;
		if (isRook(piece))
			return 5;
		if (isQueen(piece))
			return 9;
		return 0;
	}

	String[] evalBest(String[] boards, int depth) {
		int bestEval = Integer.MIN_VALUE;
		List bestBoards = new ArrayList();
		for (int i = 0; i < boards.length; i++) {
			int eval = eval(boards[i], false, depth);
			if (eval > bestEval) {
				bestEval = eval;
				bestBoards.clear();
				bestBoards.add(boards[i]);
			}
			if (eval == bestEval)
				bestBoards.add(boards[i]);
		}
		return (String[]) bestBoards.toArray(new String[bestBoards.size()]);
	}

	int eval(String board, boolean white, int depth) {
		if (depth == 0)
			return eval(board);
		String[] otherPlayerBoards = getPossibleMoves(board, !white);
		int bestBlackEval = Integer.MIN_VALUE;
		int bestWhiteEval = Integer.MAX_VALUE;
		for (int i = 0; i < otherPlayerBoards.length; i++) {
			int eval = eval(otherPlayerBoards[i], !white, depth - 1);
			if (eval > bestBlackEval)
				bestBlackEval = eval;
			if (eval < bestWhiteEval)
				bestWhiteEval = eval;
		}
		return white ? bestBlackEval : bestWhiteEval;
	}

	int eval(String board) {
		if (board.indexOf("K") == -1)
			return Integer.MAX_VALUE;
		if (board.indexOf("k") == -1)
			return Integer.MIN_VALUE;
		int score = 0;
		for (int x = 0; x <= 7; x++)
			for (int y = 0; y <= 7; y++) {
				String piece = get(board, x, y);
				int value = value(piece);
				if (isBlack(piece))
					score += value;
				else
					score -= value;
			}
		return score;
	}
}