import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class BiscuitClickerGameMac {
    public static void main(String[] args) throws InterruptedException {
        /* This game was optimized for playing on MacBook Air while not in fullscreen,
        so playing on other devices may lead to a poor experience. Play BiscuitClickerGame.java
        if not on MacBook Air or if you want to play on fullscreen. */

        // Loop so that if player exits to title screen from game, it goes back to title screen, where they can then Quit Game
        while (true) {
            // Gets player key (specific or "start" from title screen and sends to game
            String str = BiscuitClickerMac.titleScreen();
            BiscuitClickerMac.game(str);
        }
    }
}

class BiscuitClickerMac {
    private static long[] biscuits;
    private static int biscuitsPerSec;
    private static int multiplier;
    private static long[] upgradeCost;

    static String titleScreen() throws InterruptedException {
        // Creates frame with a name
        JFrame f = new JFrame("Biscuit Clicker");

        // Creates new buttons to put on title screen
        JButton newGame = new JButton("New Game");
        JButton loadGame = new JButton("Load Game");
        JButton quitGame = new JButton("Quit Game");

        // Sets bounds of the new buttons
        newGame.setBounds(520, 350, 400, 70);
        loadGame.setBounds(520, 450, 400, 70);
        quitGame.setBounds(520, 550, 400, 70);

        // Adds buttons to frame
        f.add(newGame);
        f.add(loadGame);
        f.add(quitGame);

        // Creates label with logo icon
        JLabel l = new JLabel(new ImageIcon(new ImageIcon("src/Biscuit-Clicker-Logo.png").getImage()));

        // Sets bounds of label
        l.setBounds(362, 150, 715, 78);

        // Adds label to frame
        f.add(l);

        // Sets layout to null to have no layout
        f.setLayout(null);

        // Sets frame size and makes it visible
        f.setSize(1440, 785);
        f.setVisible(true);

        // Used for getting player key
        String[] str = {""};

        // b[0] represents if a button (other than Quit Game) was clicked
        // b[1] represents if 'Load Game' was clicked
        boolean[] b = {false, false};

        // A button was clicked, but not the 'Load Game' button
        newGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                b[0] = true;
            }
        });

        // Got player key (str[0]), a button was clicked, the 'Load Game' button was clicked
        loadGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                str[0] = JOptionPane.showInputDialog(f, "Enter your player key");
                b[0] = true;
                b[1] = true;

                // If 'cancel' button is pressed, nothing occurs
                if(str[0] == null){
                    b[0] = false;
                    b[1] = false;
                    return;
                }
            }
        });

        // Exits code
        quitGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                System.exit(0);
            }
        });

        // Alternate way of exiting code by closing window
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent wE) {
                // Only activates if no buttons are pressed
                if (!b[0])
                    System.exit(0);
            }
        });

        // Checks if a button was pressed (b[0]), if so, continues
        while (true) {
            // This is here because it wouldn't work otherwise (I do not think the amount of waiting time matters though)
            Thread.sleep(50);
            if (b[0]) {
                // Closes frame (a new one will be opened in game())
                f.dispatchEvent(new WindowEvent(f, WindowEvent.WINDOW_CLOSING));

                // If 'Load Game' was pressed, return player key, otherwise return default "start"
                if (b[1])
                    return str[0];
                else
                    return "start";
            }
        }
    }

    static void game(String str) throws InterruptedException {
        // DecimalFormat for formatting large numbers (used later)
        DecimalFormat df = new DecimalFormat("###,###,###");

        // Creates frame with a name
        JFrame f = new JFrame("Biscuit Clicker");

        // If 'New Game' was selected, otherwise 'Load Game' was selected
        if (str.equals("start")) {
            // Sets initial multiplier
            multiplier = 1;

            // Declares and initializes cost of upgrading multiplier and upgrading biscuits per second
            upgradeCost = new long[2];
            upgradeCost[0] = 100;
            upgradeCost[1] = 25;

            // Sets initial BPS to 0
            biscuitsPerSec = 0;

            // Initializes biscuits array
            biscuits = new long[2];
        } else {
            // Gets ArrayList of values stored in instance variable
            // Order: Current biscuit count, multiplier, total biscuit count, BPS, upgradeCost of multiplier, upgradeCost of BPS, and seconds since Jan 01, 2023
            ArrayList<Long> nums = decrypter(str);

            // Declares arrays
            biscuits = new long[2];
            upgradeCost = new long[2];

            // Milliseconds from Jan 01, 1970 to Jan 01, 2023
            BigInteger ms = new BigInteger("1672531200000");

            // Date used to get time since 1970
            Date d = new Date();

            // Declares long of seconds since Jan 01, 2023
            long secondsElapsed = ((d.getTime() - ms.longValue()) / 1000);

            // Puts each value into respective place. If there's an error (definitely due to invalid player key), inform player with message and return to title screen.
            try {
                biscuits[0] = nums.get(0);
                multiplier = nums.get(1).intValue();
                biscuits[1] = nums.get(2);
                biscuitsPerSec = nums.get(3).intValue();
                upgradeCost[0] = nums.get(4);
                upgradeCost[1] = nums.get(5);
            } catch (Exception ex){
                JOptionPane.showMessageDialog(f, "You have input an invalid player key!");
                return;
            }

            // Seconds elapsed from the time the program closed to now
            secondsElapsed -= nums.get(6);

            // The number of seconds since program was closed and multiplies it with biscuits/sec to get offline 'offline passive income'
            long offlinePassiveIncome = secondsElapsed * biscuitsPerSec * multiplier;

            // Adds offline passive income to current and total biscuit counts
            biscuits[0] += offlinePassiveIncome;
            biscuits[1] += offlinePassiveIncome;
        }

        // Creates button with image of a biscuit
        JButton b = new JButton();
        try {
            BufferedImage bufferedImage = ImageIO.read(Objects.requireNonNull(BiscuitClickerGameMac.class.getResource("biscuit.png")));
            Image image = bufferedImage.getScaledInstance(30, 30, Image.SCALE_DEFAULT);
            b.setIcon(new ImageIcon(image));
        } catch (Exception ex) { // Prints exception if one occurs
            System.out.println(ex);
        }

        // Setting dimensions of button
        b.setBounds(645, 342, 150, 50);

        // Adds button to frame
        f.add(b);

        // Creates a label with starting text
        JLabel l = new JLabel("Biscuits: " + df.format(biscuits[0]));

        // Creates multiplier button
        JButton mult = new JButton("Buy " + df.format((multiplier + 1)) + "x Multiplier: " + df.format(upgradeCost[0]) + " Biscuits");

        // Setting dimensions of multiplier button
        mult.setBounds(720 - (mult.getPreferredSize().width / 2), 705, mult.getPreferredSize().width, 45);

        mult.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (biscuits[0] >= upgradeCost[0]) {
                    // Subtracts amount needed to upgrade multiplier from current biscuits
                    biscuits[0] -= upgradeCost[0];

                    // Updates biscuit counter;
                    l.setText("Biscuits: " + df.format(biscuits[0]));

                    // Increases multiplier
                    multiplier++;

                    // Increases cost to upgrade multiplier
                    upgradeCost[0] *= 10;

                    // Updates button text for next upgrade
                    mult.setText("Buy " + df.format((multiplier + 1)) + "x Multiplier: " + df.format(upgradeCost[0]) + " Biscuits");

                    // Changes width to preferred size (in case text no longer fits) and re-centers button
                    mult.setBounds(720 - (mult.getPreferredSize().width / 2), 705, mult.getPreferredSize().width, 45);
                } else
                    // Notifies player of insufficient biscuits
                    JOptionPane.showMessageDialog(f, "You do not have enough biscuits to buy this upgrade!");
            }
        });

        // Adds multiplier button to frame
        f.add(mult);

        // Creates biscuits per second button
        JButton BPS;

        // If 'New Game' was selected, sets BPS to 1 Biscuit/sec, else biscuitsPerSec + 1 BiscuitS/sec
        if (biscuitsPerSec == 0)
            BPS = new JButton("<html>Increase Passive Income to<br>1 Biscuit/sec: <html>" + df.format(upgradeCost[1]) + " Biscuits" + "<html><br><html>" + "(ignoring multiplier)");
        else
            BPS = new JButton("<html>Increase Passive Income to<br><html>" + df.format((biscuitsPerSec * 2L)) + " Biscuits/sec: " + df.format(upgradeCost[1]) + " Biscuits" + "<html><br><html>" + "(ignoring multiplier)");

        // Setting dimensions of upgrade button
        // If upgrade cost is too large, also increases height of BPS so all text will fit within button
        if(upgradeCost[1] > 25000000000000L)
            BPS.setBounds(1210, 317, 230, 100);
        else
            BPS.setBounds(1210, 392 - BPS.getPreferredSize().height, 230, BPS.getPreferredSize().height + 19);

        BPS.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (biscuits[0] >= upgradeCost[1]) {
                    // Subtracts amount needed to upgrade BPS from current biscuits
                    biscuits[0] -= upgradeCost[1];

                    // Updates biscuit counter;
                    l.setText("Biscuits: " + df.format(biscuits[0]));

                    // Increases BPS
                    if (biscuitsPerSec != 0)
                        biscuitsPerSec *= 2;
                    else
                        biscuitsPerSec = 1;

                    // Increases cost to upgrade BPS
                    upgradeCost[1] *= 10;

                    // Updates button text for next upgrade
                    BPS.setText("<html>Increase Passive Income to<br><html>" + df.format((biscuitsPerSec * 2L)) + " Biscuits/sec: " + df.format(upgradeCost[1]) + " Biscuits" + "<html><br><html>" + "(ignoring multiplier)");

                    // Updates BPS text and re-centers BPS
                    // If upgrade cost is too large, also increases height of BPS so all text will fit within button
                    if(upgradeCost[1] > 25000000000000L)
                        BPS.setBounds(1210, 342, 230, 100);
                    else
                        BPS.setBounds(1210, 392 - BPS.getPreferredSize().height, 230, BPS.getPreferredSize().height + 19);
                } else
                    // Notifies player of insufficient biscuits
                    JOptionPane.showMessageDialog(f, "You do not have enough biscuits to buy this upgrade!");
            }
        });

        // Adds upgrade button to frame
        f.add(BPS);

        // Button that displays total biscuit count when clicked
        JButton displayTotal = new JButton("Display Total Biscuits");

        // Sets bounds of display total biscuit button
        displayTotal.setBounds(0, 0, 160, 25);

        // Message to player showing total biscuit count
        displayTotal.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                JOptionPane.showMessageDialog(f, "Total Biscuits: " + df.format(biscuits[1]));
            }
        });

        // Adds display total biscuit button to frame
        f.add(displayTotal);

        // Creates panel, adds label to panel, adds panel to frame
        JPanel p = new JPanel();
        p.add(l);
        f.add(p);

        // Sets size of frame and makes it visible (previously show())
        f.setSize(1440, 785);
        f.setVisible(true);

        // Continue if BPS is not 0 (if 'New Game' was selected or 'Load Game' where BPS wasn't upgraded previously)
        // Repeat of beginning of game() method since variables in if-statement reside within the scope of the if-statement
        if (biscuitsPerSec != 0) {
            // Gets ArrayList of values stored in instance variable
            // Order: Current biscuit count, multiplier, total biscuit count, BPS, upgradeCost of multiplier, upgradeCost of BPS, and seconds since Jan 01, 2023
            ArrayList<Long> nums = decrypter(str);

            // Milliseconds from Jan 01, 1970 to Jan 01, 2023
            BigInteger ms = new BigInteger("1672531200000");

            // Date used to get time since 1970
            Date d = new Date();

            // Declares long of seconds since Jan 01, 2023
            long secondsElapsed = ((d.getTime() - ms.longValue()) / 1000);

            // Seconds elapsed from the time the program closed to now
            secondsElapsed -= nums.get(6);

            // The number of seconds since program was closed and multiplies it with biscuits/sec to get offline 'offline passive income'
            long offlinePassiveIncome = secondsElapsed * biscuitsPerSec * multiplier;

            // Message to player
            JOptionPane.showMessageDialog(f, "You earned " + df.format(offlinePassiveIncome) + " biscuits while you were away!");
        }

        // Declaration and initialization of event multiplier (changes to 2 during event)
        int[] eventMultiplier = {1};

        // Adds action when button is pressed
        b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                // Moves button randomly within frame
                // Exceptions: y cannot be less than 25 (to prevent label from being covered by button)
                //             Cannot cover up mult or BPS

                // Declares x and y for position of button
                int x;
                int y;

                // Makes sure mult and BPS aren't overlapped by button, if so, gets new random position
                do {
                    x = (int) (Math.random() * 1291);
                    y = 25 + (int) (Math.random() * 676);
                } while ((x > 720 - (mult.getPreferredSize().width / 2) - 150 && x < 720 + (mult.getPreferredSize().width / 2) && y > 655) || (x > 1060 && y > 342 - BPS.getPreferredSize().height & y < 411));

                // Sets new random location of button
                b.setBounds(x, y, 150, 50);

                // Adds to current biscuit counter and total biscuit counter
                biscuits[0] += (long) multiplier * eventMultiplier[0];
                biscuits[1] += (long) multiplier * eventMultiplier[0];

                // Updates label text
                l.setText("Biscuits: " + df.format(biscuits[0]));
            }
        });

        // If user wants to return to title screen
        boolean[] leave = {false};

        // Makes sure frame stays open if 'No' is selected in confirmation pop-up
        f.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        // If button to close game window was pressed, then perform actions below
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent wE) {
                // Method repeats if the if-statement is absent, cause unknown
                if (!leave[0]) {
                    // Confirmation message
                    int input;
                    Object[] options = {"Yes", "No"};
                    input = JOptionPane.showOptionDialog(f, "Are you sure you want to return to the title screen?", "Exit to Title Screen?",
                            JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
                            null, options, options[0]);

                    // If 'Yes' was clicked, continue
                    if (input == 0) {
                        // Makes sure frame will close since 'Yes' was selected
                        f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

                        // If no progress was made (total biscuit count is 0) then exit to title screen immediately, else give message and player key and then exit
                        if(biscuits[1] == 0){
                            // Sets leave[0] to true so that game() will be exited
                            leave[0] = true;
                        }
                        else {
                            // Player key
                            String key = encrypter();

                            // Message to the player
                            JOptionPane.showMessageDialog(f, "You will lose your progress if you do not copy down your player key: " + key + ". Click 'Ok' to copy your player key to your clipboard.");

                            // Copies player key to the clipboard
                            StringSelection sS = new StringSelection(key);
                            Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
                            clpbrd.setContents(sS, null);

                            // Sets leave[0] to true so that game() will be exited
                            leave[0] = true;
                        }
                    }
                }
            }
        });

        // ongoingEvent[0] is if there is an ongoing event
        // ongoingEvent[1] is if it is the first occurrence of the second if-statement after an event has started (to prevent the 60-second timer from happening more than once)
        boolean[] ongoingEvent = {false, false};

        while (true) {
            // If user wants to return to title screen, exits game(), else sets leave[0] to false
            if (leave[0]) {
                // Closes game frame and returns to title screen
                f.dispatchEvent(new WindowEvent(f, WindowEvent.WINDOW_CLOSING));
                return;
            }

            // Adds BPS to current biscuit counter and total biscuit counter
            biscuits[0] += (long) biscuitsPerSec * multiplier * eventMultiplier[0];
            biscuits[1] += (long) biscuitsPerSec * multiplier * eventMultiplier[0];

            // Updates biscuit counter
            l.setText("Biscuits: " + df.format(biscuits[0]));

            // If there is no ongoing event and a 1 / 900 chance of occurring (will happen approximately once every 15 minutes since loop repeats every second)
            if (!ongoingEvent[0] && Math.random() < 0.00111111111) {
                // Creates (but does not display) message to player
                JOptionPane pane = new JOptionPane("You get double the amount of biscuits for the next minute!", JOptionPane.INFORMATION_MESSAGE);
                JDialog dialog = pane.createDialog(f, "Special Event");

                // Displays message to player
                dialog.setModal(false);
                dialog.setVisible(true);

                // There is an ongoing event and is first occurrence
                ongoingEvent[0] = true;
                ongoingEvent[1] = true;

                // After 3 seconds, automatically hides message
                new Timer(3000, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        dialog.setVisible(false);
                    }
                }).start();
            }

            // If there is an ongoing event, and it is the first occurrence, continue
            if (ongoingEvent[0] && ongoingEvent[1]) {
                // Sets event multiplier to 2 (double bonus)
                eventMultiplier[0] = 2;

                // Is no longer the first occurrence
                ongoingEvent[1] = false;

                // Ends event after 60 seconds
                new Timer(60000, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // Event multiplier back to 1
                        eventMultiplier[0] = 1;

                        // Event ends
                        ongoingEvent[0] = false;
                    }
                }).start();
            }

            // Waits ~1 second
            Thread.sleep(999);
        }
    }

    private static String encrypter() {
        // Method intended to create unique player key

        // Milliseconds from Jan 01, 1970 to Jan 01, 2023
        BigInteger ms = new BigInteger("1672531200000");

        // Date used to get time since 1970
        Date d = new Date();

        // Calculates seconds since Jan 01, 2023 (will be added to player key later)
        long time = (d.getTime() - ms.longValue()) / 1000;

        // Creation of player key
        String str = "";

        // Array of numbers that will be added to player key. Used + "" to convert numbers into Strings
        String[] arr = {biscuits[0] + "", multiplier + "", biscuits[1] + "", biscuitsPerSec + "", upgradeCost[0] + "", upgradeCost[1] + "", time + ""};

        // Loop adding random letters and number in array (as specified above)
        for (int x = 0; x < 7; x++) {
            str += addRandomLetters() + arr[x];
        }

        // Adding random letters at the end
        str += addRandomLetters();

        // Player key finished, return to pop-up
        return str;
    }

    private static ArrayList<Long> decrypter(String str) {
        // list of letters to check for within player key
        String[] list = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m",
                "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};

        // ArrayList will have current biscuit count, multiplier, total biscuit count, BPS, upgradeCost of multiplier, upgradeCost of BPS, and seconds since Jan 01, 2023 (in that order)
        ArrayList<Long> temp = new ArrayList<>();

        // Temporary string for adding numbers within player key to ArrayList
        String s = "";

        // If current part of string (length of 1) is a letter, sets b to true, otherwise nothing changes
        for (int x = 0; x < str.length() - 1; x++) {
            boolean b = false;
            for (String value : list) {
                if (str.substring(x, x + 1).equalsIgnoreCase(value)) {
                    b = true;
                    break;
                }
            }

            // If current part of string is not a letter, it will be a number, so we want it to be added to ArrayList
            if (!b) {
                // Add current part of string to temporary string
                s += str.substring(x, x + 1);

                // If next part of string (also length of 1) is a letter, sets c to true, otherwise nothing changes
                boolean c = false;
                for (String value : list) {
                    if (str.substring(x + 1, x + 2).equalsIgnoreCase(value)) {
                        c = true;
                        break;
                    }
                }

                // If next part of string is a letter, enter if statement
                if (c) {
                    // Parses the temporary string into a long and add it to the ArrayList
                    temp.add(Long.parseLong(s));

                    // Resets the temporary string
                    s = "";
                }
            }
        }

        // ArrayList is complete, so it is returned
        return temp;
    }

    private static String addRandomLetters() {
        // Lists of letters which will be randomly selected from
        String[] list1 = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m",
                "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
        String[] list2 = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
                "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "y", "Z"};

        // Number of letters to be added (1-4)
        int num = (int) (Math.random() * 4) + 1;

        // Declaration and initialization of string that will be added to player key
        String str = "";

        // Adds random letters of number 'num' by randomly choosing an index from 0-25. Chooses whether to add from list1 or list2 (lowercase or capitalized) by using Math.random()
        for (int x = 0; x < num; x++) {
            if (Math.random() < 0.5)
                str += list1[(int) (Math.random() * 26)];
            else
                str += list2[(int) (Math.random() * 26)];
        }

        // Random letters complete, sent back to method call
        return str;
    }
}