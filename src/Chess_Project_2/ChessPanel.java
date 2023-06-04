/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Chess_Project_2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author pj
 */
public class ChessPanel extends JPanel {
    private static final int BOARD_SIZE = 8;
    private static final int CELL_SIZE = 63;
    private final PiecesOnBoard board;
    private boolean[][] availableMoves;
    private int selectedRow = -1;
    private int selectedCol = -1;
    private Player player1;
    private Player player2;
    private Player currentPlayer;
    
    private JTextField player1NameField;
    private JTextField player2NameField;
    private JButton startButton;

    public ChessPanel() 
    {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(704 + 100, 565));
        board = new PiecesOnBoard();
        availableMoves = new boolean[8][8];
       
        createPlayerLoginScreen();
        addMouseListener();
        
    }
    
    private void addMouseListener() {
        addMouseListener(new MouseAdapter() {
            Piece selectedPiece = null;
            boolean whiteTurn = true;
            @Override
            public void mousePressed(MouseEvent e) {
                if (player1 != null && player2 != null) 
                {
                
                    int col = e.getX() / CELL_SIZE;
                    int row = (BOARD_SIZE - 1) - e.getY() / CELL_SIZE;
                    currentPlayer = player1;
                    if (!whiteTurn) {
                        currentPlayer = player2;
                    }
                    if (col >= 0 && col < BOARD_SIZE && row >= 0 && row < BOARD_SIZE) {
                        Piece clickedPiece = board.getBoard()[col][row];

                        if (selectedPiece == null) {
                            if (clickedPiece != null && clickedPiece.getColour() == currentPlayer.getColourPiece()) {
                                selectedPiece = clickedPiece;
                                selectedRow = row;
                                selectedCol = col;
                                availableMoves = selectedPiece.getAvailableMoves();
                            }
                        } else {
                            if (availableMoves[col][row]) {
                                if (selectedPiece.getColour() == currentPlayer.getColourPiece() && board.movePiece(selectedCol, selectedRow, col, row)) {
                                    whiteTurn = !whiteTurn;
                                } 
                                else
                                {
                                    JOptionPane.showMessageDialog(null, "Illegal move, " + currentPlayer.getColourPiece() + " King is under attack.");
                                }
                            }

                            selectedPiece = null;
                            selectedRow = -1;
                            selectedCol = -1;
                            availableMoves = new boolean[8][8];
                        }

                        repaint();
                    }
            }
            }
        });
    }
    
    private void createPlayerLoginScreen() 
    {
        // Create player login components
        JLabel player1Label = new JLabel("Player 1 Name:");
        JLabel player2Label = new JLabel("Player 2 Name:");
        player1NameField = new JTextField(10);
        player2NameField = new JTextField(10);
        startButton = new JButton("Start Game");
        
        // Set font for labels and button
        Font labelFont = new Font(Font.SANS_SERIF, Font.BOLD, 14);
        player1Label.setFont(labelFont);
        player2Label.setFont(labelFont);
        startButton.setFont(labelFont);

        // Add action listener to start button
        startButton.addActionListener((ActionEvent e) -> {
            String player1Name = player1NameField.getText().trim();
            String player2Name = player2NameField.getText().trim();
            
            if (player1Name.isEmpty() || player2Name.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter both player names.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
            } else {
                player1 = new Player(PieceColour.WHITE, player1Name);
                player2 = new Player(PieceColour.BLACK, player2Name);
                startButton.setEnabled(false);
                startButton.setVisible(false);
                player1NameField.setVisible(false);
                player2NameField.setVisible(false);
                player1Label.setVisible(false);
                player2Label.setVisible(false);
                createGameButtons();
                repaint();
            }
        });
        JPanel loginPanel = new JPanel();
        // Add player login components to the panel
        loginPanel.add(player1Label);
        loginPanel.add(player1NameField);
        loginPanel.add(player2Label);
        loginPanel.add(player2NameField);
        loginPanel.add(startButton);
        add(loginPanel, BorderLayout.SOUTH);
    }

    @Override
    protected void paintComponent(Graphics g) 
    {
        super.paintComponent(g);
       
        drawChessBoard(g);
        
        int frameWidth = 4; 
        int boardWidth = BOARD_SIZE * CELL_SIZE;
        int boardHeight = BOARD_SIZE * CELL_SIZE;

        // Draw the frame
        g.setColor(Color.BLACK);
        g.fillRect(CELL_SIZE - frameWidth - 59, CELL_SIZE - frameWidth - 58, frameWidth, boardHeight + 2 * frameWidth); // LEFT
        g.fillRect(CELL_SIZE - frameWidth - 59, CELL_SIZE - frameWidth - 58, boardWidth + 2 * frameWidth, frameWidth); // TOP
        g.fillRect(CELL_SIZE - frameWidth - 59, CELL_SIZE + boardHeight  - 58, boardWidth + 2 * frameWidth, frameWidth);
        g.fillRect(CELL_SIZE + boardWidth - 59, CELL_SIZE - frameWidth - 58, frameWidth, boardHeight + 2 * frameWidth);
        if (player1 == null || player2 == null) 
        {
            // Draw a translucent overlay if players haven't logged in yet
            g.setColor(new Color(255, 255, 255, 200));
            g.fillRect(CELL_SIZE - 59, CELL_SIZE - 59, BOARD_SIZE * CELL_SIZE, BOARD_SIZE * CELL_SIZE);
        }
        if(availableMoves != null)
        {
            drawAvailableMoves(g);
        }
        
    }

    private void drawChessBoard(Graphics g) 
    {
        int currentRow = 7;  
        boolean flag = true;
        for (int row = 0; row < 8; row++) 
        {
            for (int col = 0; col < 8; col++) 
            {
                int x = col * CELL_SIZE;
                int y = row * CELL_SIZE;
                x += 4;
                y += 5;
                Piece piece = board.getBoard()[col][currentRow];

                if ((row + col) % 2 == 0) 
                {
                    g.setColor(Color.WHITE);
                } 
                else 
                {
                    g.setColor(Color.LIGHT_GRAY);

                }
                
                g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                
                if(piece != null)
                {
                    g.drawImage(piece.getImage(), x + 8, y + 7, null);
                }

                if (row == BOARD_SIZE - 1) 
                {   
                    if(flag)
                    {
                        g.setColor(Color.WHITE);
                        flag = !flag;
                    }
                    else
                    {
                        g.setColor(Color.LIGHT_GRAY);
                        flag = !flag;
                    }
                    g.drawString(String.valueOf((char) ('a' + col)), x + CELL_SIZE / 2 + 20, y + CELL_SIZE - 6);
                }

                if (col == 0) 
                {   
                    g.setColor(Color.BLACK);
                    g.drawString(String.valueOf(BOARD_SIZE - row), x + 3, y + CELL_SIZE / 2 - 16);
                }
            }
            currentRow--; 
        }
    }
    
    private void drawAvailableMoves(Graphics g) 
    {
        if (selectedRow != -1 && selectedCol != -1) 
        {
            g.setColor(new Color(0, 0, 0, 50));

            for (int row = 0; row < 8; row++) 
            {
                for (int col = 0; col < 8; col++) 
                {
                    if (availableMoves[col][row]) 
                    {
                        int x = col * CELL_SIZE;
                        int y = (BOARD_SIZE - 1 - row) * CELL_SIZE; 
                        g.fillOval(x + 22, y + 20, 25, 25);
                    }
                }
            }
        }
    }

    /**
     * @param player1 the player1 to set
     */
    public void setPlayer1(Player player1) 
    {
        this.player1 = player1;
    }

    /**
     * @param player2 the player2 to set
     */
    public void setPlayer2(Player player2)
    {
        this.player2 = player2;
    }

    private void createGameButtons() 
    {
        JPanel buttonPanel = new JPanel();
        JButton resignButton = new JButton("Resign");
        JButton drawButton = new JButton("Draw");
        JButton saveButton = new JButton("Save");
        JButton quitButton = new JButton("Quit");
        
        Font buttonFont = new Font(Font.SERIF, Font.BOLD, 14);
        resignButton.setFont(buttonFont);
        drawButton.setFont(buttonFont);
        saveButton.setFont(buttonFont);
        quitButton.setFont(buttonFont);
        
        resignButton.addActionListener((ActionEvent e) -> 
        {
            
        });

        drawButton.addActionListener((ActionEvent e) -> 
        {
            
        });

        saveButton.addActionListener((ActionEvent e) -> 
        {
            
        });

        quitButton.addActionListener((ActionEvent e) -> 
        {
            System.exit(0);
        });

        buttonPanel.add(resignButton);
        buttonPanel.add(drawButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(quitButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }
}