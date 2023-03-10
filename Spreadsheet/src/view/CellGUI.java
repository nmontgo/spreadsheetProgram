package model.Spreadsheet.src.view;

import model.Spreadsheet.src.controller.Spreadsheet;
import model.Spreadsheet.src.controller.SpreadsheetApp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * CellGUI contains some basic information of a cell for
 * SpreadsheetGUI.java to use when constructing the spreadsheet.
 * @author Patrick Hern
 * @author Nathameion Montgomery
 * @author Moon chang
 */
public class CellGUI extends JPanel {
    /** The rows of the cell GUI. */
    private int myRow;
    /** The columns of the cell GUI. */
    private int myCol;
    /** Boolean of if there's an error in the cell. */
    private boolean errorInCell;
    /** The text field in the cells. */
    private JTextField myTextField;

    /**
     * Sets up all the CellGUI and the action listeners on them.
     * @param theRow The rows in the CellGUI.
     * @param theCol The columns in the CellGUI.
     */
    public CellGUI(final int theRow, final int theCol) {
        myRow = theRow;
        myCol = theCol;

        myTextField = new JTextField(10);
        myTextField.setHorizontalAlignment(SwingConstants.CENTER);
        myTextField.setBorder(BorderFactory.createEmptyBorder());
        myTextField.setForeground(ColorData.getColor(SpreadsheetGUI.theme, "text"));
        myTextField.setToolTipText("Row: " + Integer.toString(theRow) + ", Column: " + getColumnString(theCol + 1));

        myTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                highlightCell();
            }

            @Override
            public void focusLost(FocusEvent e) {
                try {
                    if (!myTextField.getText().equals("")) {
                        setCellToNormal(false);
                        errorInCell = false;
                    } else {
                        setBackground(ColorData.getColor(SpreadsheetGUI.theme, "normal"));
                        myTextField.setBackground(ColorData.getColor(SpreadsheetGUI.theme, "normal"));
                    }
                } catch (Exception exception) {
                    setCellToNormal(true);
                    errorInCell = true;
                    exception.printStackTrace();
                }
            }
        });

        setBorder(BorderFactory.createLineBorder(ColorData.getColor(SpreadsheetGUI.theme, "border")));
        setLayout(new BorderLayout());
        setSize(new Dimension(60, 20));
        add(myTextField, BorderLayout.CENTER);
        setCellToNormal(false);
        setVisible(true);
        myTextField.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been typed.
             * This event occurs when a key press is followed by a key release.
             *
             * @param e
             */
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    myTextField.transferFocus();
                }
            }
        });
        //errorInCell = false;
    }

    /**
     * When a cell is selected or has an error, then there
     * it is highlighted with the correct color.
     */
    public void highlightCell() {
        setBackground(ColorData.getColor(SpreadsheetGUI.theme, "highlight"));
        myTextField.setBackground(ColorData.getColor(SpreadsheetGUI.theme, "highlight"));
        //System.out.println(myRow + ", " + myCol);
        myTextField.grabFocus();
        String formula = "";
        if (Spreadsheet.getCell(myRow, myCol) != null) {
            formula = Spreadsheet.getCell(myRow, myCol).getInOrderFormula();
        }
        if (errorInCell) {
            formula = myTextField.getText();
        }
        myTextField.setText(formula);
        //System.out.println(formula);
        //System.out.println("focus gained");
    }

    /**
     *  Sets the background to the default color if there
     *  are no errors with the cell, or the error color
     *  if there are errors with the cell. Also changes
     *  the text in the cell to its value.
     */
    public void setCellToNormal(boolean error) {
        if (error) {
            setBackground(ColorData.getColor(SpreadsheetGUI.theme, "error"));
            myTextField.setBackground(ColorData.getColor(SpreadsheetGUI.theme, "error"));
            System.out.println("error");
            return;
        }

        setBackground(ColorData.getColor(SpreadsheetGUI.theme, "normal"));
        myTextField.setBackground(ColorData.getColor(SpreadsheetGUI.theme, "normal"));
        if (!myTextField.getText().equals("")) {
            SpreadsheetApp.GUIChangeCell(myRow, myCol, myTextField.getText());
        }
        String value = "";
        if (Spreadsheet.getCell(myRow, myCol) != null) {
            try {
                value = Integer.toString(Spreadsheet.getCell(myRow, myCol).getValue());
            } catch (Exception e) {
                return;
            }
        }
        myTextField.setText(value);
    }

    /**
     * Sets a cell back to its normal state.
     * @param error If there is an error or not.
     * @param message The message to be displayed.
     */
    public void setCellToNormal(boolean error, String message) {
        setCellToNormal(error);
        myTextField.setText(message);
    }

    /**
     * Gets the String representation of the integer used for the
     * columns.
     * @param theColumn The integer column.
     * @return Returns the string representation of the column.
     */
    private String getColumnString(int theColumn) {
        StringBuilder result = new StringBuilder();
        int currentCol = theColumn;
        while (currentCol / 26 > 0) {
            result.append((char) (currentCol % 26 + 'A'));
            currentCol = currentCol / 26;
        }
        result.append((char) (currentCol % 26 + 'A' - 1));
        return result.reverse().toString();
    }

    /**
     * Changes the theme of the spreadsheet app.
     */
    public void changeTheme() {
        if (!errorInCell) {
            setBackground(ColorData.getColor(SpreadsheetGUI.theme, "normal"));
            myTextField.setBackground(ColorData.getColor(SpreadsheetGUI.theme, "normal"));
            myTextField.setBackground(ColorData.getColor(SpreadsheetGUI.theme, "normal"));
        } else {
            System.out.println("error in cell");
            setBackground(ColorData.getColor(SpreadsheetGUI.theme, "error"));
            myTextField.setBackground(ColorData.getColor(SpreadsheetGUI.theme, "error"));
        }
        setBorder(BorderFactory.createLineBorder(ColorData.getColor(SpreadsheetGUI.theme, "border")));
        myTextField.setForeground(ColorData.getColor(SpreadsheetGUI.theme, "text"));
    }

    /**
     * Updates the text in the text field.
     * @param newText The new text to be displayed.
     */
    public void updateText(final String newText) {
        myTextField.setText(newText);
    }

    /**
     * Sets the error message in the cell.
     * @param setting If the error message needs setting.
     * @param message The message to be set to.
     */
    public void setErrorInCell(boolean setting, String message) {
        errorInCell = setting;
        if (message == null) {
            setCellToNormal(setting);
        } else {
            setCellToNormal(setting, message);
        }
    }
}
