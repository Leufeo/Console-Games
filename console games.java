/* CONSOLE GAMES by Leon Broda

To choose a field on a board type in the row number first and the column number second.
You do not need any charcter or blank space in between.
examples: "12" referes to the second field of the first line.
          "31" referes to the first field of the third line.

If in tic tac toe the gravity is activated type only one number.
For checkers you additionally need to choose a direction.
Add r for right or l for left.
To move a king u (up) or d (down) needs to be the last character of your input.*/

import java.util.Scanner;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

class Main {
	public static void main(String[] args) {
		GameManager gameManager = new GameManager();
		gameManager.gameSelector();
	}
}
class GameManager {
	final static String TIC_TAC_TOE_GAME_CODE = "1";
	final static String MEMORY_GAME_CODE = "2";
	final static String CHECKERS_GAME_CODE = "3";

	public void gameSelector() {
		String selectedGame = getSelectedGame();

		if (selectedGame.equals(TIC_TAC_TOE_GAME_CODE)) {
			TicTacToeGame ticTacToeGame = new TicTacToeGame();
		} else if (selectedGame.equals(MEMORY_GAME_CODE)) {
			MemoryGame memoryGame = new MemoryGame();
		} else if (selectedGame.equals(CHECKERS_GAME_CODE)) {
			CheckersGame checkersGame = new CheckersGame();
		}
	}

	private String getSelectedGame() {
		Scanner playerInput = new Scanner(System.in);
		String input = " ";

		while (!(input.equals(TIC_TAC_TOE_GAME_CODE) || input.equals(MEMORY_GAME_CODE) || input.equals(CHECKERS_GAME_CODE))) {
			System.out.println("Which game do you want to play?\n1 - tic tac toe\n2 - memory\n3 - checkers");
			input = playerInput.nextLine();
		}
		return input;
	}
}
class TicTacToeGame {
	TTTPlayer players[];
	int amountOfPlayers;
	TTTGameBoard gameBoard;

	public TicTacToeGame() {
		setup();
		int nextPlayer = determineBeginner();
		do {
			gameBoard.display();
			nextPlayer = enterMove(nextPlayer);
		} while (gameBoard.finished() == null);
		gameBoard.display();
		System.out.printf(findWinnerName(gameBoard.finished()) + " won.");
	}
	private void setup() {
		gameBoard = new TTTGameBoard();
		amountOfPlayers = gameBoard.inputNumber("How many players will take part?");
		players = new TTTPlayer[amountOfPlayers];
		initializePlayers();
		gameBoard.setup();
	}
	private void initializePlayers() {
		ArrayList<Character> alreadyUsedSymbols = new ArrayList<>();
		for (int i = 0; i < amountOfPlayers; i++) {
			players[i] = new TTTPlayer(alreadyUsedSymbols);
			alreadyUsedSymbols.add(players[i].symbol);
		}
	}
	private int determineBeginner() {
		double random = Math.random();
		for (int i = 0; i < players.length; i++) {
			if (random < (float) 1 / players.length * (i + 1)) {
				System.out.printf(players[i].name + " begins." + '\n');
				return i;
			}
		}
		return -1;
	}
	private int enterMove(int nextPlayer) {
		gameBoard.requestInputMove(players[nextPlayer]);

		if (nextPlayer < players.length - 1) {
			return nextPlayer + 1;
		}
		return 0;
	}
	private String findWinnerName(char winnerSymbol) {
		for (int i = 0; i < players.length; i++) {
			if (players[i].symbol == winnerSymbol) {
				return players[i].name;
			}
		}
		return "Nobody";
	}
}
class TTTPlayer {
	String name;
	char symbol;

	public TTTPlayer(ArrayList<Character> alreadyUsedSymbols) {
		Scanner sc = new Scanner(System.in);
		System.out.println("Who is the next player? ");
		this.name = sc.nextLine();

		boolean exception;
		do {
			exception = false;
			try {
				System.out.println("Which symbol has the player? ");
				this.symbol = sc.nextLine().charAt(0);
				if (isAlreadyUsedSymbol(alreadyUsedSymbols, symbol)) {
					System.out.println("Symbol already in use.");
					exception = true;
				}
			} catch (StringIndexOutOfBoundsException inputSymbol) {
				System.out.println("invalid input");
				exception = true;
			}
		} while (exception);
	}
	private boolean isAlreadyUsedSymbol(ArrayList<Character> alreadyUsedSymbols, Character symbol) {
		for (int i = 0; i < alreadyUsedSymbols.size(); i++) {
			if (alreadyUsedSymbols.get(i) == symbol) {
				return true;
			}
		}
		return false;
	}
}
class TTTGameBoard {
	int row, col, forWin, inputMoveConversion;
	Character[][] state;
	boolean gravitation;

	public void setup() {
		row = inputNumber("How many rows do you wish to play with?");
		col = inputNumber("How many columns do you wish to play with?");
		do {
			forWin = inputNumber("How many in a row do you need for the win?");
		} while (forWin > row && forWin > col);

		Scanner sc = new Scanner(System.in);
		String input;
		do {
			System.out.println("Do you want gravitation? (yes/no)");
			input = sc.nextLine();
		} while (input.charAt(0) != 'y' && input.charAt(0) != 'n');
		if (input.charAt(0) == 'y') {
			gravitation = true;
		}
		if (input.charAt(0) == 'n') {
			gravitation = false;
		}

		state = new Character[row][col];
		inputMoveConversion = (int) Math.pow(10, String.valueOf(col).length());
	}

	public int inputNumber(String print) {
		Scanner console = new Scanner(System.in);
		boolean exception;
		int number = -1;
		do {
			exception = false;
			System.out.println(print);
			try {
				number = Integer.parseInt(console.nextLine());
				if (number < 1) {
					System.out.println("invalid input");
					exception = true;
				}
			} catch (NumberFormatException input) {
				System.out.println("invalid input");
				exception = true;
			}
		} while (exception);
		return number;
	}

	public Character finished() {
		ArrayList<Character> winners = new ArrayList<>();

		winners.add(rowsWin());
		winners.add(columnWin());
		winners.add(diagonalWin());

		if (!isWinPossible()) {
			return ' ';
		}

		for (int i = 0; i < 3; i++) {
			if (winners.get(i) != null) {
				return winners.get(i);
			}
		}

		return null;
	}

	public void display() {
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col - 1; j++) {
				System.out.printf(nullToSpace(state[i][j]) + " | ");
			}
			System.out.printf("" + nullToSpace(state[i][col-1]) + '\n');
		}
	}

	public void requestInputMove(TTTPlayer player) {
		int playerInput = inputNumber(player.name + ", enter your move: ");
		try {
			if (gravitation) {
				for (int i = state.length - 1; i >= 0; i--) {
					if (state[i][String.valueOf(playerInput).charAt(0) - '0' - 1] == null) {
						state[i][String.valueOf(playerInput).charAt(0) - '0' - 1] = player.symbol;
						return;
					}
				}
			}
			else if (state[rowNum(playerInput)][colNum(playerInput)] == null) {
				state[rowNum(playerInput)][colNum(playerInput)] = player.symbol;
				return;
			}
			System.out.println("move not possible");
			requestInputMove(player);
		} catch (ArrayIndexOutOfBoundsException board) {
			System.out.println("invalid input");
			requestInputMove(player);
		}
	}

	private int rowNum(int playerInput) {
		return playerInput / inputMoveConversion - 1;
	}

	private int colNum(int playerInput) {
		return playerInput % inputMoveConversion - 1;
	}

	private Character rowsWin() {
		for (int i = 0; i < row; i++) {
			int numOfSameSymbols = 1;
			for (int j = 0; j < col - 1; j++) {
				if (state[i][j] == state[i][j + 1] && state[i][j] != null) {
					numOfSameSymbols++;
					if (numOfSameSymbols == forWin) {
						return state[i][j];
					}
				}
				else {
					numOfSameSymbols = 1;
				}
			}
		}
		return null;
	}

	private Character columnWin() {
		for (int i = 0; i < col; i++) {
			int numOfSameSymbols = 1;
			for (int j = 0; j < row - 1; j++) {
				if (state[j][i] == state[j + 1][i] && state[j][i] != null) {
					numOfSameSymbols++;
					if (numOfSameSymbols == forWin) {
						return state[j][i];
					}
				}
				else {
					numOfSameSymbols = 1;
				}
			}
		}
		return null;
	}
	private Character diagonalWin() {
		for (int i = 0; i < col - forWin + 1; i++) {
			for (int j = 0; j <= row - forWin; j++) {
				int numOfSameSymbols = 1;
				for (int k = i, l = j; k < col - 1 && l < row - 1; k++, l++) {
					if (state[l][k] != null && state[l][k] == state[l + 1][k + 1]) {
						numOfSameSymbols++;
						if (numOfSameSymbols == forWin) {
							return state[l][k];
						}
					}
					else {
						numOfSameSymbols = 1;
					}
				}
			}
		}
		for (int i = col - 1; i > forWin - 2; i--) {
			for (int j = 0; j <= row - forWin; j++) {
				int numOfSameSymbols = 1;
				for (int k = i, l = j; k > 0 && l < row - 1; k--, l++) {
					if (state[l][k] != null && state[l][k] == state[l + 1][k - 1]) {
						numOfSameSymbols++;
						if (numOfSameSymbols == forWin) {
							return state[l][k];
						}
					}
					else {
						numOfSameSymbols = 1;
					}
				}
			}
		}
		return null;
	}
	private boolean isWinPossible() {
		if (isRowWinPossible()) {
			return true;
		}
		if (isColWinPossible()) {
			return true;
		}
		if (isDiagonalWinPossible()) {
			return true;
		}
		return false;
	}
	private boolean isRowWinPossible() {
		for (int i = 0; i < row; i++) {
			int numOfSameSymbols = 1;
			Character symbol = state[i][0];
			for (int j = 0; j < col - 1; j++) {
				if (state[i][j + 1] == symbol || state[i][j + 1] == null) {
					numOfSameSymbols++;
					if (numOfSameSymbols == forWin) {
						return true;
					}
				}
				else if (symbol == null) {
					symbol = state[i][j + 1];
					numOfSameSymbols++;
					if (numOfSameSymbols == forWin) {
						return true;
					}
				}
				else {
					symbol = state[i][j + 1];
					numOfSameSymbols = 1;
				}
			}
		}
		return false;
	}
	private boolean isColWinPossible() {
		for (int i = 0; i < col; i++) {
			int numOfSameSymbols = 1;
			Character symbol = state[0][i];
			for (int j = 0; j < row - 1; j++) {
				if (state[j + 1][i] == symbol || state[j + 1][i] == null) {
					numOfSameSymbols++;
					if (numOfSameSymbols == forWin) {
						return true;
					}
				}
				else if (symbol == null) {
					symbol = state[j + 1][i];
					numOfSameSymbols++;
					if (numOfSameSymbols == forWin) {
						return true;
					}
				}
				else {
					symbol = state[j + 1][i];
					numOfSameSymbols = 1;
				}
			}
		}
		return false;
	}
	private boolean isDiagonalWinPossible() {
		for (int i = 0; i < col - forWin + 1; i++) {
			for (int j = 0; j <= row - forWin; j++) {
				int numOfSameSymbols = 1;
				Character symbol = state[j][i];
				for (int k = i, l = j; k < col - 1 && l < row - 1; k++, l++) {
					if (state[l + 1][k + 1] == symbol || state[l + 1][k + 1] == null) {
						numOfSameSymbols++;
						if (numOfSameSymbols == forWin) {
							return true;
						}
					}
					else if (symbol == null) {
						symbol = state[l + 1][k + 1];
						numOfSameSymbols++;
						if (numOfSameSymbols == forWin) {
							return true;
						}
					}
					else {
						symbol = state[l + 1][k + 1];
						numOfSameSymbols = 1;
					}
				}
			}
		}
		for (int i = col - 1; i > forWin - 2; i--) {
			for (int j = 0; j <= row - forWin; j++) {
				int numOfSameSymbols = 1;
				Character symbol = state[j][i];
				for (int k = i, l = j; k > 0 && l < row - 1; k--, l++) {
					if (state[l + 1][k - 1] == symbol || state[l + 1][k - 1] == null) {
						numOfSameSymbols++;
						if (numOfSameSymbols == forWin) {
							return true;
						}
					}
					else if (symbol == null) {
						symbol = state[l + 1][k - 1];
						numOfSameSymbols++;
						if (numOfSameSymbols == forWin) {
							return true;
						}
					}
					else {
						symbol = state[l + 1][k - 1];
						numOfSameSymbols = 1;
					}
				}
			}
		}
		return false;
	}
	private char nullToSpace(Character a)
	{
		return Objects.requireNonNullElse(a, ' ');
	}
}
class MemoryGame {
	MemoryPlayer players[];
	int amountOfPlayers;
	MemoryGameBoard gameBoard;

	public MemoryGame() {
		setup();
		int nextPlayer = determineBeginner();
		while (!gameBoard.isFinished(players)) {
			nextPlayer = gameBoard.enterMove(nextPlayer, players);
		}
		showResults();
	}
	private void setup() {
		gameBoard = new MemoryGameBoard();
		do {
			amountOfPlayers = gameBoard.inputNumber("How many players will take part?");
		} while (amountOfPlayers < 1);
		players = new MemoryPlayer[amountOfPlayers];
		initializePlayers();
		gameBoard.setup();
	}
	private void initializePlayers() {
		for (int i = 0; i < amountOfPlayers; i++) {
			players[i] = new MemoryPlayer(i);
		}
	}
	private int determineBeginner() {
		double random = Math.random();
		for (int i = 0; i < players.length; i++) {
			if (random < (float) 1 / players.length * (i + 1)) {
				System.out.printf(players[i].name + " begins." + '\n');
				return i;
			}
		}
		return -1;
	}
	private void showResults() {
		for (int i = 0; i < players.length - 1; i++) {
			for (int j = 0; j < players.length - 1; j++) {
				if (players[j + 1].numberOfPairs > players[j].numberOfPairs) {
					MemoryPlayer player = players[j];
					players[j] = players[j + 1];
					players[j + 1] = player;
				}
			}
		}
		System.out.println(findWinnerName() + " won.");
		for (int i = 0; i < players.length; i++) {
			System.out.println(players[i].numberOfPairs + " - " + players[i].name);
		}
		System.out.println("duration: " + gameBoard.duration + " turns");
	}
	private String findWinnerName() {
		try {
			if (players[0].numberOfPairs > players[1].numberOfPairs) {
				return players[0].name;
			}
		} catch (ArrayIndexOutOfBoundsException singlePlayer) {
			return players[0].name;
		}
		return "Nobody";
	}
}
class MemoryPlayer {
	String name;
	int numberOfPairs = 0;
	int input1;
	int input2;

	public MemoryPlayer(int i) {
		Scanner sc = new Scanner(System.in);
		System.out.println("Who is the " + (i + 1) + ". player?");
		this.name = sc.nextLine();
	}
}
class MemoryGameBoard {
	Scanner playerInput = new Scanner(System.in);
	int row = 3, col = 3, duration = 0, inputMoveConversion;
	boolean[][] state;
	char cards[];
	Character board[][];

	public void setup() {
		rowsAndColsInput();
		cardsInput();
		dealCards();
		shuffleCards();
	}
	private void rowsAndColsInput() {
		do {
			row = inputNumber("How many rows do you wish to play with?");
			col = inputNumber("How many columns do you wish to play with?");
		} while (row*col%2 != 0);
		state = new boolean[row][col];
		board = new Character[row][col];
		inputMoveConversion = (int) Math.pow(10, String.valueOf(col).length());
	}
	public int inputNumber(String print) {
		Scanner console = new Scanner(System.in);
		boolean exception;
		int number = -1;
		do {
			exception = false;
			System.out.println(print);
			try {
				number = Integer.parseInt(console.nextLine());
			} catch (NumberFormatException input) {
				System.out.println("invalid input");
				exception = true;
			}
		} while (exception);
		return number;
	}
	private void cardsInput() {
		cards = new char[row * col / 2];
		Scanner sc = new Scanner(System.in);
		int i = 0;
		System.out.println("Choose " + row * col / 2 + " symbols.");
		while (i < cards.length) {
			String input = sc.nextLine();
			try {
				for (int j = 0; j < input.length(); i++, j++) {
					cards[i] = input.charAt(j);
				}
			} catch (ArrayIndexOutOfBoundsException cards) {
				return;
			}
		}
	}
	private void dealCards() {
		int j = 0;
		for (int i = 0; i < col * row; i++) {
			board[i / col][i % col] = cards[j];
			i++;
			board[i / col][i % col] = cards[j];
			j++;
		}
	}
	private void shuffleCards() {
		Random rand = new Random();
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				int randomIndex = rand.nextInt(row * col);
				int randRow = randomIndex / col;
				int randCol = randomIndex % col;
				char temp = board[randRow][randCol];
				board[randRow][randCol] = board[i][j];
				board[i][j] = temp;
			}
		}
	}
	public void display() {
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				if (!state[i][j]) {
					System.out.printf("[]");
				} else {
					System.out.printf("  ");
				}
			}
			System.out.printf("\n");
		}
	}
	public void displayBoard()
	{
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				System.out.printf(board[i][j] + " ");
			}
			System.out.printf("\n");
		}
	}
	public int enterMove(int nextPlayer, MemoryPlayer players[]) {
		do {
			display();
			requestInputMove(players[nextPlayer]);
			compareSymbols(players[nextPlayer]);
		} while (isPair(players[nextPlayer].input1, players[nextPlayer].input2) && !isFinished(players));

		duration++;

		if (nextPlayer < players.length - 1) {
			return nextPlayer + 1;
		}
		return 0;
	}
	private void requestInputMove(MemoryPlayer player)
	{
		do {
			player.input1 = inputNumber(player.name + ", enter your first move: ");
		} while (!isInputValid(player.input1, 0));
		System.out.println(board[rowNum(player.input1)][colNum(player.input1)]);

		do {
			player.input2 = inputNumber(player.name + ", enter your second move: ");
		} while (!isInputValid(player.input2, player.input1));
		System.out.println(board[rowNum(player.input2)][colNum(player.input2)]);
	}
	private boolean isInputValid(int input, int input1)
	{
		try {
			if (state[rowNum(input)][colNum(input)]) {
				System.out.println("invalid input");
				return false;
			}
			if (input == input1) {
				System.out.println("invalid input");
				return false;
			}
		} catch (ArrayIndexOutOfBoundsException board) {
			System.out.println("invalid input");
			return false;
		}
		return true;
	}
	private void compareSymbols(MemoryPlayer player)
	{
		if (isPair(player.input1, player.input2)) {
			player.numberOfPairs++;
			state[rowNum(player.input1)][colNum(player.input1)] = true;
			state[rowNum(player.input2)][colNum(player.input2)] = true;
			System.out.println("pair");
		}
	}
	private boolean isPair(int input1, int input2) {
		return Character.compare(board[rowNum(input1)][colNum(input1)], board[rowNum(input2)][colNum(input2)]) == 0;
	}
	public boolean isFinished(MemoryPlayer players[]) {
		int pairsCollected = 0;
		for (int i = 0; i < players.length; i++) {
			pairsCollected = pairsCollected + players[i].numberOfPairs;
		}
		if (pairsCollected < row * col / 2) {
			return false;
		}
		return true;
	}
	private int rowNum(int playerInput) {
		return playerInput / inputMoveConversion - 1;
	}
	private int colNum(int playerInput) {
		return playerInput % inputMoveConversion - 1;
	}
}
class CheckersGame {
	CheckersPlayer player1;
	CheckersPlayer player2;
	CheckersGameboard gameBoard;

	public CheckersGame() {
		initializePlayers();
		gameBoard = new CheckersGameboard();
		gameBoard.setup(player1.symbol, player2.symbol);

		short nextPlayer = determineBeginner(player1.name, player2.name);
		while (player1.remainingCounters > 0 && player2.remainingCounters > 0 && gameBoard.isTurnPossible(nextPlayer, player1, player2))
		{
			gameBoard.display();
			nextPlayer = gameBoard.enterMove(nextPlayer, player1, player2);
		}
		System.out.printf(gameBoard.findWinnerName(player1, player2, nextPlayer) + " won.");
	}
	private void initializePlayers() {
		ArrayList<Character> alreadyUsedSymbols = new ArrayList<>();
		player1 = new CheckersPlayer((short) 1, alreadyUsedSymbols);
		alreadyUsedSymbols.add(player1.symbol);
		alreadyUsedSymbols.add(player1.kingSymbol);
		player2 = new CheckersPlayer((short) 2, alreadyUsedSymbols);
	}
	private short determineBeginner(String name1, String name2) {
		short nextPlayer = 2;
		if (Math.random() < 0.5) {
			System.out.println(name1 + " begins.");
			nextPlayer = 1;
		}
		if (nextPlayer == 2) {
			System.out.println(name2 + " begins.");
		}
		return nextPlayer;
	}
}
class CheckersPlayer {
	String name;
	short number;
	Character symbol;
	Character kingSymbol;
	int remainingCounters = 12;

	public CheckersPlayer(short number, ArrayList<Character> alreadyUsedSymbols) {
		this.number = number;
		Scanner sc = new Scanner(System.in);
		System.out.println("Who is the next player? ");
		this.name = sc.nextLine();

		this.symbol = inputSymbol(alreadyUsedSymbols, "Which symbol has the player?");
		alreadyUsedSymbols.add(this.symbol);
		this.kingSymbol = inputSymbol(alreadyUsedSymbols, "Which symbol for kings has the player?");
	}
	private Character inputSymbol(ArrayList<Character> alreadyUsedSymbols, String print) {
		Character symbol = ' ';
		Scanner sc = new Scanner(System.in);
		boolean exception;
		do {
			exception = false;
			try {
				System.out.println(print);
				symbol = sc.nextLine().charAt(0);
				if (isAlreadyUsedSymbol(alreadyUsedSymbols, symbol)) {
					System.out.println("Symbol already in use.");
					exception = true;
				}
			} catch (StringIndexOutOfBoundsException inputSymbol) {
				System.out.println("invalid input");
				exception = true;
			}
		} while (exception);
		return symbol;
	}
	private boolean isAlreadyUsedSymbol(ArrayList<Character> alreadyUsedSymbols, Character symbol) {
		for (int i = 0; i < alreadyUsedSymbols.size(); i++) {
			if (alreadyUsedSymbols.get(i) == symbol) {
				return true;
			}
		}
		return false;
	}
}
class CheckersGameboard {
	Counter state[][];

	public void setup(char symbol1, char symbol2) {
		state = new Counter[8][4];

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 4; j++) {
				state[i][j] = new Counter(symbol1);
			}
		}
		for (int i = 5; i < 8; i++) {
			for (int j = 0; j < 4; j++) {
				state[i][j] = new Counter(symbol2);
			}
		}
	}
	public void display() {
		for (int i = 0; i < 8; i++) {
			System.out.printf("| ");
			for (int j = 0; j < 4; j++) {
				if (i % 2 == 0) {
					if (state[i][j] == null) {
						System.out.printf("  _ ");
					} else {
						System.out.printf("  " + state[i][j].symbol + " ");
					}
				}
				else {
					if (state[i][j] == null) {
						System.out.printf("_   ");
					} else {
						System.out.printf(state[i][j].symbol + "   ");
					}
				}
			}
			System.out.printf("|\n");
		}
	}
	public short enterMove(short nextPlayer, CheckersPlayer player1, CheckersPlayer player2) {
		if (nextPlayer == 1) {
			requestInputMove(player1, player2);
			return 2;
		}
		if (nextPlayer == 2) {
			requestInputMove(player2, player1);
			return 1;
		}
		return nextPlayer;
	}
	public boolean isTurnPossible(short playerNumber, CheckersPlayer player1, CheckersPlayer player2) {
		if (playerNumber == player1.number) {
			for (int i = 0; i < 8; i++) {
				for (int j = 0; j < 4; j++) {
					try {
						if (state[i][j].symbol == player1.symbol || state[i][j].symbol == player1.kingSymbol) {
							if (isAnyMovePossible(player1.number, i, j) || isAnyCapturingPossible(player1.number, player2, i, j)) {
								return true;
							}
						}
					} catch (NullPointerException board) {}
				}
			}
		}
		if (playerNumber == player2.number) {
			for (int i = 0; i < 8; i++) {
				for (int j = 0; j < 4; j++) {
					try {
						if (state[i][j].symbol == player2.symbol || state[i][j].symbol == player2.kingSymbol) {
							if (isAnyMovePossible(player2.number, i, j) || isAnyCapturingPossible(player2.number, player1, i, j)) {
								return true;
							}
						}
					} catch (NullPointerException board) {}
				}
			}
		}
		return false;
	}
	public String findWinnerName(CheckersPlayer player1, CheckersPlayer player2, short nextPlayer) {
		if (player1.remainingCounters == 0) {
			return player2.name;
		}
		if (player2.remainingCounters == 0) {
			return player1.name;
		}
		if (!isTurnPossible(nextPlayer, player1, player2)) {
			if (nextPlayer == player1.number) {
				return player2.name;
			}
			if (nextPlayer == player2.number) {
				return player1.name;
			}
		}
		return "Nobody";
	}
	private void requestInputMove(CheckersPlayer currentTurnPlayer, CheckersPlayer opponent) {
		Scanner sc = new Scanner(System.in);
		String input;
		do {
			System.out.println(currentTurnPlayer.name + ", enter your move: ");
			input = sc.nextLine();
		} while (!isMoveInputValid(input, currentTurnPlayer));

		executeMove(currentTurnPlayer, opponent, input);
	}
	private boolean isMoveInputValid(String input, CheckersPlayer player) {
		if (!isFormatValid(input)) {
			System.out.println("invalid input");
			return false;
		}
		short vertNumber = getVertNumber(player.number, input);
		try {
			try {
				if (state[charToIndex(input.charAt(0))][charToIndex(input.charAt(1))].symbol != player.symbol && state[charToIndex(input.charAt(0))][charToIndex(input.charAt(1))].symbol != player.kingSymbol) {
					System.out.println("not your counter");
					return false;
				}
			} catch (NullPointerException counter) {
				System.out.println("empty spot");
				return false;
			}
			if (state[getVertIndex(vertNumber, input.charAt(0), 1)][getHoriIndex(input.charAt(1), input.charAt(0), input.charAt(2), 1)] == null) {
				return true;
			}
			if (state[getVertIndex(vertNumber, input.charAt(0), 1)][getHoriIndex(input.charAt(1), input.charAt(0), input.charAt(2), 1)].symbol == player.symbol || state[getVertIndex(vertNumber, input.charAt(0), 1)][getHoriIndex(input.charAt(1), input.charAt(0), input.charAt(2), 1)].symbol == player.kingSymbol) {
				System.out.println("move not possible");
				return false;
			}
			if (state[getVertIndex(vertNumber, input.charAt(0), 2)][getHoriIndex(input.charAt(1), input.charAt(0), input.charAt(2), 2)] != null) {
				System.out.println("no empty spot behind");
				return false;
			}
		} catch (ArrayIndexOutOfBoundsException board) {
			System.out.println("move out of board");
			return false;
		}
		return true;
	}
	private boolean isFormatValid(String input) {
		if (input.length() < 3) {
			return false;
		}
		if (input.charAt(0) < '1' || input.charAt(0) > '8') {
			return false;
		}
		if (input.charAt(1) < '1' || input.charAt(1) > '4') {
			return false;
		}
		if (input.charAt(2) != 'r' && input.charAt(2) != 'l') {
			return false;
		}
		try {
			if (state[charToIndex(input.charAt(0))][charToIndex(input.charAt(1))].isKing) {
				try {
					if (input.charAt(3) != 'u' && input.charAt(3) != 'd') {
						return false;
					}
				} catch (StringIndexOutOfBoundsException inputKing) {
					return false;
				}
			}
		} catch (NullPointerException counter) {
			System.out.printf("no counter ");
			return false;
		}
		return true;
	}
	private void executeMove(CheckersPlayer currentTurnPlayer, CheckersPlayer opponent, String input) {
		int row = charToIndex(input.charAt(0)), col = charToIndex(input.charAt(1));
		short vertNumber = getVertNumber(currentTurnPlayer.number, input);

		if (state[getVertIndex(vertNumber, row, 1)][getHoriIndex(col, row, input.charAt(2), 1)] == null) {
			state[getVertIndex(vertNumber, row, 1)][getHoriIndex(col, row, input.charAt(2), 1)] = state[row][col];
			state[row][col] = null;

			col = getHoriIndex(col, row, input.charAt(2), 1);
			row = getVertIndex(vertNumber, row, 1);

			state[row][col] = setCounterToKingIfComeToEnd(state[row][col], currentTurnPlayer, row);
		}
		else {
			String direction = "" + input.charAt(2);

			state[getVertIndex(vertNumber, row, 2)][getHoriIndex(col, row, direction.charAt(0), 2)] = state[row][col];
			state[row][col] = null;
			state[getVertIndex(vertNumber, row, 1)][getHoriIndex(col, row, direction.charAt(0), 1)] = null;
			opponent.remainingCounters--;

			col = getHoriIndex(col, row, direction.charAt(0), 2);
			row = getVertIndex(vertNumber, row, 2);

			while (isAnyCapturingPossible(vertNumber, opponent, row, col)) {
				Scanner sc = new Scanner(System.in);
				display();

				boolean exception;
				do {
					exception = false;
					try {
						System.out.println(currentTurnPlayer.name + ", enter your next hop: ");
						direction = sc.nextLine();
						direction = direction + ' ';
						if (direction.charAt(0) != 'l' && direction.charAt(0) != 'r' || state[row][col].isKing && direction.charAt(1) != 'u' && direction.charAt(1) != 'd') {
							System.out.println("invalid input");
							exception = true;
						}
						else {
							vertNumber = getVertNumber(currentTurnPlayer.number, row, col, direction.charAt(1));
						}
					} catch (StringIndexOutOfBoundsException inputDirection) {
						System.out.println("invalid input");
						exception = true;
					}
				} while (exception || !isCapturingPossible(vertNumber, opponent, row, col, direction.charAt(0)));

				state[getVertIndex(vertNumber, row, 2)][getHoriIndex(col, row, direction.charAt(0), 2)] = state[row][col];
				state[row][col] = null;
				state[getVertIndex(vertNumber, row, 1)][getHoriIndex(col, row, direction.charAt(0), 1)] = null;
				opponent.remainingCounters--;

				col = getHoriIndex(col, row, direction.charAt(0), 2);
				row = getVertIndex(vertNumber, row, 2);
			}
			state[row][col] = setCounterToKingIfComeToEnd(state[row][col], currentTurnPlayer, row);
		}
	}
	private Counter setCounterToKingIfComeToEnd(Counter counter, CheckersPlayer player, int row) {
		if (player.number == 1 && row == 7) {
			counter.isKing = true;
			counter.symbol = player.kingSymbol;
		}
		if (player.number == 2 && row == 0) {
			counter.isKing = true;
			counter.symbol = player.kingSymbol;
		}
		return counter;
	}
	private boolean isAnyCapturingPossible(short vertNumber, CheckersPlayer opponent, int row, int col) {
		if (!state[row][col].isKing) {
			if (isCapturingPossible(vertNumber, opponent, row, col, 'l')) {
				return true;
			}
			if (isCapturingPossible(vertNumber, opponent, row, col, 'r')) {
				return true;
			}
		}
		else {
			if (isCapturingPossible((short) 1, opponent, row, col, 'l')) {
				return true;
			}
			if (isCapturingPossible((short) 1, opponent, row, col, 'r')) {
				return true;
			}
			if (isCapturingPossible((short) 2, opponent, row, col, 'l')) {
				return true;
			}
			if (isCapturingPossible((short) 2, opponent, row, col, 'r')) {
				return true;
			}
		}
		return false;
	}
	private boolean isCapturingPossible(short vertNumber, CheckersPlayer opponent, int row, int col, char direction) {
		try {
			try {
				if (state[getVertIndex(vertNumber, row, 1)][getHoriIndex(col, row, direction, 1)] == null) {
					return false;
				}
			} catch (NullPointerException counter) {
				return false;
			}
			try {
				if (state[getVertIndex(vertNumber, row, 1)][getHoriIndex(col, row, direction, 1)].symbol == opponent.symbol && state[getVertIndex(vertNumber, row, 2)][getHoriIndex(col, row, direction, 2)] == null) {
					return true;
				}
			} catch (NullPointerException counter) {
				return true;
			}
		} catch (ArrayIndexOutOfBoundsException board) {
			return false;
		}
		return false;
	}
	private boolean isAnyMovePossible(short vertNumber, int row, int col) {
		try {
			if (!state[row][col].isKing) {
				if (isMovePossible(vertNumber, row, col, 'l') || isMovePossible(vertNumber, row, col, 'r')) {
					return true;
				}
			}
			else {
				if (isMovePossible((short) 1, row, col, 'l') || isMovePossible((short) 1, row, col, 'r')) {
					return true;
				}
				if (isMovePossible((short) 2, row, col, 'l') || isMovePossible((short) 2, row, col, 'r')) {
					return true;
				}
			}
		} catch (NullPointerException counter) {
			return false;
		}
		return false;
	}
	private boolean isMovePossible(short vertNumber, int row, int col, char direction) {
		try {
			if (state[getVertIndex(vertNumber, row, 1)][getHoriIndex(col, row, direction, 1)] == null) {
				return true;
			}
		} catch (ArrayIndexOutOfBoundsException board) {
			return false;
		}
		return false;
	}
	private short getVertNumber(short playerNumber, String input) {
		short vertNumber = playerNumber;
		if (state[charToIndex(input.charAt(0))][charToIndex(input.charAt(1))].isKing) {
			if (input.charAt(3) == 'u') {
				vertNumber = 2;
			}
			if (input.charAt(3) == 'd') {
				vertNumber = 1;
			}
		}
		return vertNumber;
	}
	private short getVertNumber(short playerNumber, int row, int col, char direction) {
		short vertNumber = playerNumber;
		if (state[row][col].isKing) {
			if (direction == 'u') {
				vertNumber = 2;
			}
			if (direction == 'd') {
				vertNumber = 1;
			}
		}
		return vertNumber;
	}
	private int charToIndex(char character) {
		return character - '0' - 1;
	}
	private int getVertIndex(short vertNumber, char row, int distance) {
		return charToIndex(row) + vertDirection(vertNumber) * distance;
	}
	private int getVertIndex(short vertNumber, int row, int distance) {
		return row + vertDirection(vertNumber) * distance;
	}
	private short vertDirection (short vertNumber) {
		if (vertNumber == 1) {
			return 1;
		}
		if (vertNumber == 2) {
			return -1;
		}
		return 10;
	}
	private int getHoriIndex(char col, char row, char direction, int distance) {
		return charToIndex(col) + horiDirection(distance, direction, row);
	}
	private int getHoriIndex(int col, int row, char direction, int distance) {
		return col + horiDirection(distance, direction, row);
	}
	private short horiDirection(int distance, char direction, char row) {
		if (distance == 1) {
			if (direction == 'l' && (row - '0') % 2 == 0) {
				return -1;
			}
			if (direction == 'l' && (row - '0') % 2 == 1) {
				return 0;
			}
			if (direction == 'r' && (row - '0') % 2 == 0) {
				return 0;
			}
			if (direction == 'r' && (row - '0') % 2 == 1) {
				return 1;
			}
		}
		if (distance == 2) {
			if (direction == 'l') {
				return -1;
			}
			if (direction == 'r') {
				return 1;
			}
		}
		return 10;
	}
	private short horiDirection(int distance, char direction, int row) {
		if (distance == 1) {
			if (direction == 'l' && row % 2 == 0) {
				return 0;
			}
			if (direction == 'l' && row % 2 == 1) {
				return -1;
			}
			if (direction == 'r' && row % 2 == 0) {
				return 1;
			}
			if (direction == 'r' && row % 2 == 1) {
				return 0;
			}
		}
		if (distance == 2) {
			if (direction == 'l') {
				return -1;
			}
			if (direction == 'r') {
				return 1;
			}
		}
		return 10;
	}
}
class Counter {
	Character symbol;
	boolean isKing = false;

	public Counter(Character symbol) {
		this.symbol = symbol;
	}
}
