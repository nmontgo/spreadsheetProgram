package model.Spreadsheet.src.view;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * SpreadsheetGUI contains the GUI components for the Spreadsheet
 * and displays the GUI to the user.
 * @author Patrick Hern
 * @author Nathameion Montgomery
 */
public class SpreadsheetGUI extends JFrame implements PropertyChangeListener {
    private static final Toolkit KIT = Toolkit.getDefaultToolkit();
    /**
     * The users screen size.
     */
    private static final Dimension SCREEN_SIZE = KIT.getScreenSize();

    private static final int ROWS = 26;

    private static final int COLUMNS = 26;
    private static final LayoutManager LAYOUT = new GridLayout(ROWS, COLUMNS);

    private static CellGUI[][] myCells = new CellGUI[ROWS][COLUMNS];

    private static JPanel cellPanel = new JPanel();

    public SpreadsheetGUI() {
        setupGUI();
    }

    /**
     * This method gets called when a bound property is changed.
     *
     * @param evt A PropertyChangeEvent object describing the event source
     *            and the property that has changed.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }

    private void setupGUI() {
        setTitle("Spreadsheet");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);
        setUpCells();
        add(cellPanel);
        pack();
        setSize(new Dimension(1000, 500));
    }

    private void setUpCells() {
        cellPanel.setLayout(LAYOUT);
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                CellGUI cellAdding = new CellGUI(row, col);
                myCells[row][col] = cellAdding;
                cellPanel.add(cellAdding);
                //System.out.println(row + "," + col);
            }
        }
        cellPanel.revalidate();
        cellPanel.setVisible(true);
    }
}
