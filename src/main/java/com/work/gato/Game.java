package com.work.gato;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 *
 * @author linux
 */
public class Game extends JFrame {

    private char currentPlayer = 'X';
    private char[][] board = new char[3][3];
    private boolean gameEnded = false;

    public Game() {
        setTitle("Juego del Gato");
        setSize(600, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setLocationRelativeTo(null);

        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Juego");
        JMenuItem newGameItem = new JMenuItem("Nuevo Juego");
        JMenuItem exitItem = new JMenuItem("Salir");

        newGameItem.addActionListener((ActionEvent e) -> {
            resetGame();
        });

        exitItem.addActionListener((ActionEvent e) -> {
            System.exit(0);
        });

        gameMenu.add(newGameItem);
        gameMenu.add(exitItem);
        menuBar.add(gameMenu);
        setJMenuBar(menuBar);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBoard(g);
            }
        };
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (gameEnded) {
                    return;
                }
                int x = e.getX() / (getWidth() / 3);
                int y = e.getY() / (getHeight() / 3);
                if (board[y][x] == '\0') {
                    board[y][x] = currentPlayer;
                    if (checkWin(currentPlayer)) {
                        gameEnded = true;
                        showMessage("Jugador " + currentPlayer + " gana!");
                    } else if (boardFull()) {
                        gameEnded = true;
                        showMessage("Es un empate!");
                    } else {
                        currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
                        if (currentPlayer == 'O') {
                            aiMove();
                        }
                    }
                    repaint();
                }
            }
        });
        add(panel);
    }

    private void resetGame() {
        currentPlayer = 'X';
        board = new char[3][3];
        gameEnded = false;
        repaint();
    }

    private void drawBoard(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
         
        int width = getWidth();
        int height = getHeight() - 40;

        int cellWidth = width / 3;
        int cellHeight = height / 3;
        
        g2d.setBackground(Color.BLACK);
        g2d.clearRect(0, 0, width, height);
        
        
        g2d.setColor(Color.WHITE);
        // Dibujar líneas horizontales
        for (int i = 1; i < 3; i++) {
            g2d.drawLine(0, i * cellHeight, width, i * cellHeight);
        }

        // Dibujar líneas verticales
        for (int i = 1; i < 3; i++) {
            g2d.drawLine(i * cellWidth, 0, i * cellWidth, height);
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == 'X') {
                    g2d.setColor(Color.RED);
                    g2d.drawLine(j * width / 3, i * height / 3, (j + 1) * width / 3, (i + 1) * height / 3);
                    g2d.drawLine((j + 1) * width / 3, i * height / 3, j * width / 3, (i + 1) * height / 3);
                } else if (board[i][j] == 'O') {
                    g2d.setColor(Color.YELLOW);
                    g2d.drawOval(j * width / 3, i * height / 3, width / 3, height / 3);                         
                }
            }
        }
    }

    private boolean checkWin(char player) {
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == player && board[i][1] == player && board[i][2] == player) {
                return true;
            }
            if (board[0][i] == player && board[1][i] == player && board[2][i] == player) {
                return true;
            }
        }
        if (board[0][0] == player && board[1][1] == player && board[2][2] == player) {
            return true;
        }
        return board[0][2] == player && board[1][1] == player && board[2][0] == player;
    }

    private boolean boardFull() {
        for (char[] row : board) {
            for (char cell : row) {
                if (cell == '\0') {
                    return false;
                }
            }
        }
        return true;
    }

    private void aiMove() {
        if (gameEnded) {
            return;
        }
        int[] bestMove = findBestMove();
        int x = bestMove[1];
        int y = bestMove[0];
        board[y][x] = 'O';

        if (checkWin('O')) {
            gameEnded = true;
            showMessage("Jugador O gana!");
        } else if (boardFull()) {
            gameEnded = true;
            showMessage("Es un empate!");
        } else {
            currentPlayer = 'X';
        }
    }

    private void showMessage(String msg) {
        JOptionPane.showMessageDialog(Game.this, msg);
    }

    private int[] findBestMove() {
        int bestValue = Integer.MIN_VALUE;
        int[] bestMove = new int[2];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == '\0') {
                    board[i][j] = 'O';
                    int moveValue = minimax(0, false);
                    board[i][j] = '\0';
                    if (moveValue > bestValue) {
                        bestMove[0] = i;
                        bestMove[1] = j;
                        bestValue = moveValue;
                    }
                }
            }
        }

        return bestMove;
    }

    private int minimax(int depth, boolean isMax) {
        if (checkWin('O')) {
            return 10 - depth;
        }
        if (checkWin('X')) {
            return depth - 10;
        }
        if (boardFull()) {
            return 0;
        }

        if (isMax) {
            int best = Integer.MIN_VALUE;

            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j] == '\0') {
                        board[i][j] = 'O';
                        best = Math.max(best, minimax(depth + 1, false));
                        board[i][j] = '\0';
                    }
                }
            }
            return best;
        } else {
            int best = Integer.MAX_VALUE;

            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j] == '\0') {
                        board[i][j] = 'X';
                        best = Math.min(best, minimax(depth + 1, true));
                        board[i][j] = '\0';
                    }
                }
            }
            return best;
        }
    }
}
