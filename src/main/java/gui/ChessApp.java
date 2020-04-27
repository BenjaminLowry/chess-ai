package gui;

import bot.BotAgent;
import functionality.*;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ChessApp extends Application {
    private static final String IMAGE_PATH_PREFIX = "src/main/resources/piece images/";
    private static final int NUM_SQUARES = 64;

    private GridPane chessBoard;
    private VBox bestMovesBox;

    private ChessGame game = new ChessGame();
    private String[] boardState = new String[NUM_SQUARES];
    private String[] prevBoardState = new String[NUM_SQUARES];
    private BoardCoordinate selected = null;
    private boolean pvp = true;
    private TeamColor playerTeam = null;  // Only used when playing against computer.
    private BotAgent bot;
    private boolean moveAnalyzeMode = true;

    @Override
    public void start(Stage primaryStage) throws FileNotFoundException {
        // Set application icon.
        FileInputStream input = new FileInputStream(IMAGE_PATH_PREFIX + "BlackRook.LightMedium.jpg");
        primaryStage.getIcons().add(new Image(input));

        showSelectionScene(primaryStage);
    }

    // Show the beginning menu where user can select to play a player vs. player match or play against
    // a computer.
    private void showSelectionScene(Stage primaryStage) {
        primaryStage.setTitle("Chess");

        Label header = new Label("Welcome to chess!");
        header.setFont(new Font("Avenir Next Medium", 20));

        Label gameModeText = new Label("Select game mode:");
        gameModeText.setFont(new Font("Avenir Next", 16));

        Button pvpBtn = new Button("Player vs. Player");
        pvpBtn.setPrefWidth(180);
        pvpBtn.setPrefHeight(60);
        pvpBtn.setFont(new Font("Avenir Next", 14));
        pvpBtn.setOnAction(action -> showChessScene(primaryStage));

        Button pvcBtn = new Button("Player vs. Computer");
        pvcBtn.setPrefWidth(180);
        pvcBtn.setPrefHeight(60);
        pvcBtn.setFont(new Font("Avenir Next", 14));
        pvcBtn.setOnAction(action -> showTeamSelectionScene(primaryStage));

        HBox buttonDivider = new HBox(pvpBtn, pvcBtn);
        buttonDivider.setAlignment(Pos.CENTER);
        buttonDivider.setSpacing(40);

        VBox headerDivider = new VBox(header, gameModeText, buttonDivider);
        headerDivider.setAlignment(Pos.CENTER);
        headerDivider.setSpacing(40);

        Scene scene = new Scene(headerDivider, 500, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showTeamSelectionScene(Stage primaryStage) {
        pvp = false;

        primaryStage.hide();

        Label query = new Label("Which team would you like to be?");
        query.setFont(new Font("Avenir Next Medium", 16));

        HBox btnBox = new HBox();
        btnBox.setAlignment(Pos.CENTER);
        btnBox.setSpacing(30);

        String[] btnLabels = {"Black", "White"};
        TeamColor[] btnTeams = {TeamColor.BLACK, TeamColor.WHITE};
        for (int i = 0; i < 2; i++) {
            Button btn = new Button(btnLabels[i]);
            btn.setPrefSize(100, 40);
            btn.setFont(new Font("Avenir Next", 14));
            TeamColor team = btnTeams[i];
            btn.setOnAction(action -> {
                playerTeam = team;
                bot = new BotAgent(game, TeamColor.oppositeTeam(playerTeam));
                showChessScene(primaryStage);
            });
            btnBox.getChildren().add(btn);
        }

        VBox divider = new VBox(query, btnBox);
        divider.setAlignment(Pos.CENTER);
        divider.setSpacing(40);

        Scene scene = new Scene(divider, 400, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showChessScene(Stage primaryStage) {
        primaryStage.hide();
        primaryStage.setTitle("Chess");

        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();

        chessBoard = initializeChessBoard(primaryStage);
        bestMovesBox = initializeBestMovesBox();
        VBox padderBox = new VBox();
        padderBox.setPrefSize(300, 240);

        HBox masterHBox = new HBox(padderBox, chessBoard, bestMovesBox);
        masterHBox.setAlignment(Pos.CENTER);
        masterHBox.setSpacing(80);

        Scene scene = new Scene(masterHBox, screenDim.width, screenDim.height);
        primaryStage.setScene(scene);
        // Put the stage in the top-left corner of the screen.
        primaryStage.setX(0);
        primaryStage.setY(0);
        primaryStage.show();

        if (!pvp && playerTeam == TeamColor.BLACK) {
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }
                performComputersTurn();
            }).start();
        }
    }

    private VBox initializeBestMovesBox() {
        Label title = new Label();
        title.setFont(new Font("Avenir Next", 18));
        new Font("Avenir Next", 18);
        title.setPrefHeight(50);
        title.setUnderline(true);

        HBox lblCont = new HBox(title);
        lblCont.setAlignment(Pos.CENTER_LEFT);

        VBox box = new VBox(lblCont);
        box.setPrefSize(300, 320);
        for (int i = 0; i < 3; i++) {
            ImageView imageView = new ImageView();
            Label moveLabel = new Label();
            moveLabel.setFont(new Font("Avenir Next Medium", 16));
            HBox row = new HBox(imageView, moveLabel);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setSpacing(20);
            box.getChildren().add(row);
        }
        box.setAlignment(Pos.CENTER);
        box.setSpacing(10);
        return box;
    }

    private GridPane initializeChessBoard(Stage primaryStage) {
        updateBoardState();

        chessBoard = new GridPane();
        for (int i = 0; i < 100; i++) {
            // Edges of the board.
            if (i / 10 == 0 || i / 10 == 9) {
                // Top and bottom edge.
                if (i % 10 != 0 && i % 10 != 9) {
                    char c = (char) (97 + ((i - 1) % 10));
                    Label edgeLabel = new Label(String.valueOf(c));
                    edgeLabel.setPrefWidth(75);
                    edgeLabel.setPrefHeight(30);
                    edgeLabel.setFont(new Font("Avenir Next Medium", 14));
                    edgeLabel.setAlignment(Pos.CENTER);
                    edgeLabel.setBackground(new Background(
                            new BackgroundFill(Color.valueOf("FFE7B1"), CornerRadii.EMPTY, Insets.EMPTY)));
                    BorderStroke stroke;
                    if (i / 10 == 0) {
                        stroke = new BorderStroke(
                                Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK,
                                BorderStrokeStyle.SOLID, BorderStrokeStyle.NONE, BorderStrokeStyle.NONE,
                                BorderStrokeStyle.NONE, CornerRadii.EMPTY, new BorderWidths(3), Insets.EMPTY);
                    } else {
                        stroke = new BorderStroke(
                                Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK,
                                BorderStrokeStyle.NONE, BorderStrokeStyle.NONE, BorderStrokeStyle.SOLID,
                                BorderStrokeStyle.NONE, CornerRadii.EMPTY, new BorderWidths(3), Insets.EMPTY);
                    }
                    edgeLabel.setBorder(new Border(stroke));

                    chessBoard.add(edgeLabel, i % 10, i / 10);
                } else {
                    Label cornerLabel = new Label();
                    cornerLabel.setPrefWidth(30);
                    cornerLabel.setPrefHeight(30);
                    cornerLabel.setBackground(new Background(
                            new BackgroundFill(Color.valueOf("FFE7B1"), CornerRadii.EMPTY, Insets.EMPTY)));
                    BorderStroke stroke;
                    if (i == 0) {
                        stroke = new BorderStroke(
                                Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK,
                                BorderStrokeStyle.SOLID, BorderStrokeStyle.NONE, BorderStrokeStyle.NONE,
                                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(3), Insets.EMPTY);
                    } else if (i == 9) {
                        stroke = new BorderStroke(
                                Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK,
                                BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.NONE,
                                BorderStrokeStyle.NONE, CornerRadii.EMPTY, new BorderWidths(3), Insets.EMPTY);
                    } else if (i == 90) {
                        stroke = new BorderStroke(
                                Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK,
                                BorderStrokeStyle.NONE, BorderStrokeStyle.NONE, BorderStrokeStyle.SOLID,
                                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(3), Insets.EMPTY);
                    } else {
                        stroke = new BorderStroke(
                                Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK,
                                BorderStrokeStyle.NONE, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
                                BorderStrokeStyle.NONE, CornerRadii.EMPTY, new BorderWidths(3), Insets.EMPTY);
                    }
                    cornerLabel.setBorder(new Border(stroke));

                    chessBoard.add(cornerLabel, i % 10, i / 10);
                }
            } else if (i % 10 == 0 || i % 10 == 9) {
                Label edgeLabel = new Label(String.valueOf(9 - i / 10));
                edgeLabel.setPrefWidth(30);
                edgeLabel.setPrefHeight(75);
                edgeLabel.setFont(new Font("Avenir Next Medium", 14));
                edgeLabel.setAlignment(Pos.CENTER);
                edgeLabel.setBackground(new Background(
                        new BackgroundFill(Color.valueOf("FFE7B1"), CornerRadii.EMPTY, Insets.EMPTY)));
                BorderStroke stroke;
                if (i % 10 == 0) {
                    stroke = new BorderStroke(
                            Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK,
                            BorderStrokeStyle.NONE, BorderStrokeStyle.NONE, BorderStrokeStyle.NONE,
                            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(3), Insets.EMPTY);
                } else {
                    stroke = new BorderStroke(
                            Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK,
                            BorderStrokeStyle.NONE, BorderStrokeStyle.SOLID, BorderStrokeStyle.NONE,
                            BorderStrokeStyle.NONE, CornerRadii.EMPTY, new BorderWidths(3), Insets.EMPTY);
                }
                edgeLabel.setBorder(new Border(stroke));

                chessBoard.add(edgeLabel, i % 10, i / 10);
            } else {
                int boardIdx = i - 1 - 2 * (i / 10 - 1) - 10;
                Image image = getImageForPiece(this.boardState[boardIdx],
                        isLightSquare(new BoardCoordinate(boardIdx)), false);
                ImageView imageView = new ImageView(image);
                Button button = new Button("", imageView);
                button.setPadding(Insets.EMPTY);
                button.prefWidth(75);
                button.prefHeight(75);
                // Disable the button highlight upon selection.
                button.setStyle("-fx-focus-color: transparent;");
                button.setOnAction(action -> {
                    Button clickedBtn = (Button) action.getSource();
                    int row = GridPane.getRowIndex(clickedBtn);
                    int col = GridPane.getColumnIndex(clickedBtn);
                    handleSquareClick(primaryStage, new BoardCoordinate(row - 1, col - 1), clickedBtn);
                });
                chessBoard.add(button, i % 10, i / 10);
            }

        }

        chessBoard.setAlignment(Pos.CENTER);
        return chessBoard;
    }

    private void handleSquareClick(Stage primaryStage, BoardCoordinate coord, Button b) {
        // Do nothing if the game is over.
        if (this.game.isGameOver()) {
            return;
        }

        if (!pvp && this.game.currentTurn() != playerTeam) {
            return;
        }

        String piece = this.boardState[coord.toIndex()];
        if (this.selected == null) {
            // Select this square unless it is empty or is the opponent's piece.
            if (!piece.equals("--") &&
                    (piece.charAt(1) == 'b' ? TeamColor.BLACK : TeamColor.WHITE) == this.game.currentTurn()) {
                Image image = getImageForPiece(piece, isLightSquare(coord), true);
                b.setGraphic(new ImageView(image));
                this.selected = coord;
            }
        } else {
            // User clicked on the currently selected square, deselect it.
            if (this.selected.equals(coord)) {
                Image image = getImageForPiece(piece, isLightSquare(coord), false);
                b.setGraphic(new ImageView(image));
                this.selected = null;
            } else {  // Piece is trying to be moved.
                ChessGame.MoveOutcome outcome = this.game.attemptMove(selected, coord);

                if (outcome == ChessGame.MoveOutcome.FAILURE) {
                    return;
                }

                updateBoard();
                this.selected = null;

                checkForEndgameResult(primaryStage, outcome);
            }
        }
    }

    private void checkForEndgameResult(Stage primaryStage, ChessGame.MoveOutcome outcome) {
        if (outcome == ChessGame.MoveOutcome.PAWN_PROMOTION) {
            showPawnPromotionPrompt(primaryStage);
        } else if (outcome == ChessGame.MoveOutcome.CHECKMATE) {
            showCheckmateAlert();
        } else if (outcome == ChessGame.MoveOutcome.STALEMATE) {
            showStalemateAlert();
        } else if (outcome == ChessGame.MoveOutcome.INSUF_MAT_DRAW) {
            showInsufMatDrawAlert();
        } else {
            new Thread(() -> performComputersTurn()).start();
        }
    }

    private void performComputersTurn() {
        Move nextMove;
        if (!moveAnalyzeMode) {
            nextMove = bot.getNextMove();
        } else {
            Platform.runLater(() -> {
                HBox box = (HBox) this.bestMovesBox.getChildren().get(0);
                Label title = (Label) box.getChildren().get(0);
                title.setText("Thinking...");
            });

            List<BotAgent.MoveUtility> bestMoves = bot.getNextMoves();
            Platform.runLater(() -> {
                updateBestMovesBox(bestMoves);
            });
            nextMove = bestMoves.get(0).move;
        }

        ChessGame.MoveOutcome outcome = game.attemptMove(nextMove.getFirst(), nextMove.getSecond());
        if (outcome == ChessGame.MoveOutcome.FAILURE) {
            throw new RuntimeException("Computer selected move failed.");
        }

        if (outcome == ChessGame.MoveOutcome.PAWN_PROMOTION) {
            game.promotePawn(nextMove.getPawnPromotion());
        }

        Platform.runLater(() -> updateBoard());

        if (outcome == ChessGame.MoveOutcome.CHECKMATE) {
            Platform.runLater(() -> showCheckmateAlert());
        } else if (outcome == ChessGame.MoveOutcome.STALEMATE) {
            Platform.runLater(() -> showStalemateAlert());
        } else if (outcome == ChessGame.MoveOutcome.INSUF_MAT_DRAW) {
            Platform.runLater(() -> showInsufMatDrawAlert());
        }
    }

    private void showCheckmateAlert() {
        showAlert("Checkmate. " + (this.game.currentTurn() == TeamColor.BLACK ? "White wins." : "Black wins."));
    }

    private void showStalemateAlert() {
        showAlert("Stalemate. " + (this.game.currentTurn() == TeamColor.BLACK ? "White wins." : "Black wins."));
    }

    private void showInsufMatDrawAlert() {
        showAlert("Draw due to insufficient material.");
    }

    private void showAlert(String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(content);
        alert.show();
    }

    private void showPawnPromotionPrompt(Stage primaryStage) {
        final Stage prompt = new Stage();
        // Blocks the other windows while this prompt is visible.
        prompt.initModality(Modality.APPLICATION_MODAL);
        prompt.initOwner(primaryStage);

        int W = 560;
        int H = 200;

        Label lbl = new Label("Which piece would you like to promote your pawn to?");
        lbl.setFont(new Font("Avenir Next Medium", 20));

        Button queen = new Button("Queen");
        queen.setPrefWidth(80);
        queen.setPrefHeight(30);
        queen.setFont(new Font("Avenir Next Medium", 16));
        queen.setId("q");

        Button rook = new Button("Rook");
        rook.setPrefWidth(80);
        rook.setPrefHeight(30);
        rook.setFont(new Font("Avenir Next Medium", 16));
        rook.setId("r");

        Button knight = new Button("Knight");
        knight.setPrefWidth(80);
        knight.setPrefHeight(30);
        knight.setFont(new Font("Avenir Next Medium", 16));
        knight.setId("n");

        Button bishop = new Button("Bishop");
        bishop.setPrefWidth(80);
        bishop.setPrefHeight(30);
        bishop.setFont(new Font("Avenir Next Medium", 16));
        bishop.setId("b");

        List<Button> buttons = List.of(queen, rook, knight, bishop);
        for (Button b : buttons) {
            b.setOnAction(action -> {
                ChessGame.MoveOutcome outcome = this.game.promotePawn(b.getId().charAt(0));
                prompt.close();
                updateBoard();

                checkForEndgameResult(primaryStage, outcome);
            });
        }

        HBox buttonBox = new HBox(queen, rook, knight, bishop);
        buttonBox.setSpacing(20);
        buttonBox.setAlignment(Pos.CENTER);

        VBox vBox = new VBox(lbl, buttonBox);
        vBox.setSpacing(30);
        vBox.setAlignment(Pos.CENTER);

        Scene promptScene = new Scene(vBox, W, H);
        prompt.setScene(promptScene);

        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
        prompt.setX(screenDim.width / 2 - W / 2);
        prompt.setY(screenDim.height / 2 - H / 2);

        prompt.show();
    }

    private void updateBoardState() {
        String state = this.game.getBoardState();
        Scanner stateScan = new Scanner(state);
        int i = 0;
        while (stateScan.hasNext()) {
            this.boardState[i] = stateScan.next();
            i += 1;
        }
    }

    private void updateBoard() {
        for (int i = 0; i < NUM_SQUARES; i++) {
            this.prevBoardState[i] = this.boardState[i];
        }
        updateBoardState();
        for (Node n : chessBoard.getChildren()) {
            int row = GridPane.getRowIndex(n);
            int col = GridPane.getColumnIndex(n);
            BoardCoordinate coord = new BoardCoordinate(row - 1, col - 1);
            if (!ChessBoard.isOnBoard(coord)) {
                continue;
            }
            int i = coord.toIndex();

            // Update the images for the squares that have changed.
            if (!this.prevBoardState[i].equals(this.boardState[i])) {
                Button b = (Button) n;
                Image image = getImageForPiece(this.boardState[i], isLightSquare(coord), false);
                b.setGraphic(new ImageView(image));
            }
        }
    }

    private void updateBestMovesBox(List<BotAgent.MoveUtility> bestMoves) {
        HBox box = (HBox) this.bestMovesBox.getChildren().get(0);
        Label title = (Label) box.getChildren().get(0);
        title.setText("Best moves:");
        for (int i = 0; i < bestMoves.size(); i++) {
            BotAgent.MoveUtility move = bestMoves.get(i);
            HBox row = (HBox) this.bestMovesBox.getChildren().get(i + 1);
            ImageView imageView = (ImageView) row.getChildren().get(0);

            BoardCoordinate c1 = move.move.getFirst();
            BoardCoordinate c2 = move.move.getSecond();
            Image image =
                    getImageForPiece(this.boardState[c1.toIndex()],
                            isLightSquare(c1), false);
            imageView.setImage(image);
            Label label = (Label) row.getChildren().get(1);
            label.setText(c1.toAlgebraic() + ", " + c2.toAlgebraic() + ": " + move.utility);
        }
    }

    private Image getImageForPiece(String pieceStr, boolean isLightSquare, boolean isSelected) {
        String path = IMAGE_PATH_PREFIX;
        char type = pieceStr.charAt(0);
        char colorChar = pieceStr.charAt(1);

        if (pieceStr.equals("--")) {
            path += "Empty";
        } else {
            path += (colorChar == 'b' ? "Black" : "White");
            switch (type) {
                case 'r':
                    path += "Rook";
                    break;
                case 'n':
                    path += "Knight";
                    break;
                case 'b':
                    path += "Bishop";
                    break;
                case 'q':
                    path += "Queen";
                    break;
                case 'k':
                    path += "King";
                    break;
                case 'p':
                    path += "Pawn";
                    break;
            }
        }

        path += isLightSquare ? ".Light" : ".Dark";

        path += isSelected ? "Selected.jpg" : "Medium.jpg";

        try {
            FileInputStream input = new FileInputStream(path);
            return new Image(input);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Invalid image path: " + path);
        }
    }

    private boolean isLightSquare(BoardCoordinate coord) {
        int n = coord.r * 8 + coord.c;
        return (n + (coord.r)) % 2 == 0;
    }
}