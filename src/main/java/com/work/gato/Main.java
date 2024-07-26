package com.work.gato;

import javax.swing.SwingUtilities;

/**
 *
 * @author linux
 */
public class Main {

    public static void main(String[] args) {
         SwingUtilities.invokeLater(() -> {
            Game game = new Game();
            game.setVisible(true);
        });
    }
}
