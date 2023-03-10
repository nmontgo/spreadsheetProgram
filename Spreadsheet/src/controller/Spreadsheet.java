package model.Spreadsheet.src.controller;

import model.Spreadsheet.src.model.*;
import model.Spreadsheet.src.view.SpreadsheetGUI;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * The Spreadsheet class contains all relevant information to the
 * actual spreadsheet itself. Includes the spreadsheet of cells,
 * getters, and print methods.
 * @author Patrick Hern
 * @author Nathameion Montgomery
 * @author Tim Ratsko
 */
public class Spreadsheet {
    /** A 2-Dimensional array that represents a spreadsheet. */
    private static Cell[][] mySpreadsheet;
    /** A "bad cell" will return -1 if an operation goes wrong. */
    private final int BadCell = -1;
    /** The GUI for the spreadsheet. */
    private final SpreadsheetGUI myGUI;
    /** The ArrayList of Cells that holds the evaluation order. */
    ArrayList<Cell> evaluationOrder = new ArrayList<>();

    /**
     * The constructor for the spreadsheet that initializes the
     * spreadsheet with the proper dimensions.
     * @param theRowAndCols The size of the square spreadsheet.
     */
    public Spreadsheet(final int theRowAndCols, final SpreadsheetGUI theGUI) {
        mySpreadsheet = new Cell[theRowAndCols][theRowAndCols];
        myGUI = theGUI;
    }

    /**
     * Prints out all the values stored in the spreadsheet.
     */
    public void printValues() {
        for (int rows = 0; rows < getNumRows(); rows++) {
            for (int cols = 0; cols < getNumColumns(); cols++) {
                if (mySpreadsheet[rows][cols] == null) {
                    System.out.print("\t0\t");
                }
                else {
                    System.out.print("\t" + mySpreadsheet[rows][cols].getValue() + "\t");
                }
            }
            System.out.println();
        }
    }

    /**
     * Getter for the number of rows in the spreadsheet.
     * @return Returns the number of rows.
     */
    public int getNumRows() {
        return mySpreadsheet.length;
    }

    /**
     * Getter for the number of columns in the spreadsheet.
     * @return Returns the number of columns.
     */
    public int getNumColumns() {
        return mySpreadsheet[0].length;
    }

    /**
     * Prints out the cell formula and calls getFormula from to get
     * the String of the formula.
     * @param cell The cell whose formula we wish to print
     */
    public void printCellFormula(Cell cell) {
//        System.out.println(cell.getFormula());
        System.out.println(cell.getInOrderFormula());
    }

    /**
     * Prints all the formulas in the spreadsheet out to the console
     * with row and column labels.
     */
    public void printAllFormulas() {
        System.out.print("\t\t");
        for (int i = 0; i < getNumRows(); i++) {
            System.out.print("\t\t" + ((char) (i % 26 + 'A')) + "\t\t");
        }
        System.out.println();
        for (int rows = 0; rows < getNumRows(); rows++) {
            System.out.print(rows + " ");
            for (int cols = 0; cols < getNumColumns(); cols++) {
                if (mySpreadsheet[rows][cols] == null) {
                    System.out.print("\t\t \t\t");
                }
                else {
                    System.out.printf("\t\t" + mySpreadsheet[rows][cols].getInOrderFormula() + "\t\t");
                }
            }
            System.out.println();
        }
    }

    /**
     * Changes the cells formula and recalculates the expression.
     * @param cellToken The CellToken passed where a new cell will
     *                  be declared and set.
     * @param expTreeTokenString The expressionTreeToken String for
     *                           setting the formula.
     * @param inOrder The proper order of the equation.
     */
    public void changeCellFormulaAndRecalculate(CellToken cellToken, String expTreeTokenString,
                                                String inOrder, ExpressionTreeNode expTreeNode) {
        mySpreadsheet[cellToken.getRow()][cellToken.getColumn()] = new Cell(cellToken.getRow(), cellToken.getColumn());
        mySpreadsheet[cellToken.getRow()][cellToken.getColumn()].setFormula(expTreeTokenString);
        mySpreadsheet[cellToken.getRow()][cellToken.getColumn()].setMyExpressionTree(expTreeNode);
        mySpreadsheet[cellToken.getRow()][cellToken.getColumn()].setFormulaInOrder(inOrder);

        this.evaluate();
    }

    /**
     * getCellToken
     * *
     * Assuming that the next chars in a String (at the given startIndex)
     * is a cell reference, set cellToken's column and row to the
     * cell's column and row.
     * If the cell reference is invalid, the row and column of the return CellToken
     * are both set to BadCell (which should be a final int that equals -1).
     * Also, return the index of the position in the string after processing
     * the cell reference.
     * (Possible improvement: instead of returning a CellToken with row and
     * column equal to BadCell, throw an exception that indicates a parsing error.)
     * *
     * A cell reference is defined to be a sequence of CAPITAL letters,
     * followed by a sequence of digits (0-9).  The letters refer to
     * columns as follows: A = 0, B = 1, C = 2, ..., Z = 25, AA = 26,
     * AB = 27, ..., AZ = 51, BA = 52, ..., ZA = 676, ..., ZZ = 701,
     * AAA = 702.  The digits represent the row number.
     *
     * @param inputString  the input string
     * @param startIndex  the index of the first char to process
     * @param cellToken  a cellToken (essentially a return value)
     * @return  index corresponding to the position in the string just after the cell
    reference
     */
    public int getCellToken(String inputString, int startIndex, CellToken cellToken) {
        char ch;
        int column = 0;
        int row = 0;
        int index = startIndex;
        // handle a bad startIndex
        if ((startIndex < 0) || (startIndex >= inputString.length() )) {
            cellToken.setColumn(BadCell);
            cellToken.setRow(BadCell);
            return index;
        }
        // get rid of leading whitespace characters
        while (index < inputString.length() ) {
            ch = inputString.charAt(index);
            if (!Character.isWhitespace(ch)) {
                break;
            }
            index++;
        }
        if (index == inputString.length()) {
            // reached the end of the string before finding a capital letter
            cellToken.setColumn(BadCell);
            cellToken.setRow(BadCell);
            return index;
        }
        // ASSERT: index now points to the first non-whitespace character
        ch = inputString.charAt(index);
        // process CAPITAL alphabetic characters to calculate the column
        if (!Character.isUpperCase(ch)) {
            cellToken.setColumn(BadCell);
            cellToken.setRow(BadCell);
            return index;
        } else {
            column = ch - 'A';
            index++;
        }
        while (index < inputString.length() ) {
            ch = inputString.charAt(index);
            if (Character.isUpperCase(ch)) {
                column = ((column + 1) * 26) + (ch - 'A');
                index++;
            } else {
                break;
            }
        }
        if (index == inputString.length() ) {
            // reached the end of the string before fully parsing the cell reference
            cellToken.setColumn(BadCell);
            cellToken.setRow(BadCell);
            return index;
        }
        // ASSERT: We have processed leading whitespace and the
        // capital letters of the cell reference
        // read numeric characters to calculate the row
        if (Character.isDigit(ch)) {
            row = ch - '0';
            index++;
        } else {
            cellToken.setColumn(BadCell);
            cellToken.setRow(BadCell);
            return index;
        }
        while (index < inputString.length() ) {
            ch = inputString.charAt(index);
            if (Character.isDigit(ch)) {
                row = (row * 10) + (ch - '0');
                index++;
            } else {
                break;
            }
        }
        // successfully parsed a cell reference
        cellToken.setColumn(column);
        cellToken.setRow(row);
        return index;
    }

    /**
     *  Given a CellToken, print it out as it appears on the
     *  spreadsheet (e.g., "A3")
     *  @param cellToken  a CellToken
     *  @return  the cellToken's coordinates
     */
    public String printCellToken (CellToken cellToken) {
        char ch;
        String returnString = "";
        int col;
        int largest = 26;  // minimum col number with number_of_digits digits
        int number_of_digits = 2;
        col = cellToken.getColumn();
        // compute the biggest power of 26 that is less than or equal to col
        // We don't check for overflow of largest here.
        while (largest <= col) {
            largest = largest * 26;
            number_of_digits++;
        }
        largest = largest / 26;
        number_of_digits--;
        // append the column label, one character at a time
        while (number_of_digits > 1) {
            ch = (char) ((char) ((col / largest) - 1) + 'A');
            returnString += ch;
            col = col % largest;
            largest = largest  / 26;
            number_of_digits--;
        }
        // handle last digit
        ch = (char) (col + 'A');
        returnString += ch;
        // append the row as an integer
        returnString += cellToken.getRow();
        return returnString;
    }

    /**
     * Gets the cell and returns it.
     * @param row The row of the cell.
     * @param col The column of the cell.
     * @return Returns a cell found from the spreadsheet.
     */
    public static Cell getCell(final int row, final int col) {
        try {
            return mySpreadsheet[row][col];
        } catch (Exception e) {
            return null;
        }
    }

    /** Evaluates the spreadsheet.
     *  Topologically sorts all the cells in the spreadsheet then evaluates them in that order.
     *  Also takes not of any cycles.
     */
    public void evaluate() {
        for (int row = 0; row < mySpreadsheet.length; row++) {
            for (int col = 0; col < mySpreadsheet[0].length; col++) {
                if (mySpreadsheet[row][col] != null) {
                    mySpreadsheet[row][col].clearLists();
                }
            }
        }

        for (int row = 0; row < mySpreadsheet.length; row++) {
            for (int col = 0; col < mySpreadsheet[0].length; col++) {
                if (mySpreadsheet[row][col] == null) {
                    mySpreadsheet[row][col] = new Cell(row, col);
                }

                if (mySpreadsheet[row][col].getFormula() == null) {
                    mySpreadsheet[row][col].createListOfPrerequisites("", this);
                } else {
                    mySpreadsheet[row][col].createListOfPrerequisites(mySpreadsheet[row][col].getFormula(), this);
                }
            }
        }

        for (int row = 0; row < mySpreadsheet.length; row++) {
            for (int col = 0; col < mySpreadsheet[0].length; col++) {
                evaluationOrder.add(mySpreadsheet[row][col]);
            }
        }

        boolean isCycle = false;
        while (!evaluationOrder.isEmpty()) {
            isCycle = false;
            evaluationOrder.sort(new CellPrerequisiteComparator());
            Cell currentCell = evaluationOrder.remove(0);
            if (currentCell.getCellsInMyFormula().size() > 0) {
                isCycle = true;
                myGUI.setErrorInCell(currentCell.getRow(), currentCell.getColumn(), true, "CYCLE");
                System.out.println("Cycle detected");
            } else {
                currentCell.removeSelfFromOtherCellsDependencyList(this);
                currentCell.evaluate(this);
                myGUI.setCellText(currentCell.getRow(), currentCell.getColumn(), Integer.toString(currentCell.getValue()));
                if (currentCell.getFormula() != null) {
                    //System.out.println("evaluating cell with formula " + currentCell.getFormula());
                }
            }
        }

        if (!isCycle) {
            for (int row = 0; row < mySpreadsheet.length; row++) {
                for (int col = 0; col < mySpreadsheet[0].length; col++) {
                    try {
                        // Puts values in text fields
                        if (mySpreadsheet[row][col].getFormula() == null) {
                            myGUI.setCellText(row + 1, col + 1,"");
                        }
                        if (!mySpreadsheet[row][col].getFormula().equals("0")) {
                            myGUI.setCellText(row + 1, col + 1,
                                    Integer.toString(mySpreadsheet[row][col].getValue()));
                        }
                    } catch (Exception e) {
                        //myGUI.setCellText(row, col, mySpreadsheet[row][col].toString());
                        //e.printStackTrace();
                    }
                }
            }
        }
    }

    /** Class to compare cells in a list, based off of how many cells contain that cell in the formula. */
    class CellPrerequisiteComparator implements Comparator<Cell> {
        @Override
        public int compare(Cell cell1, Cell cell2) {
            return Integer.compare(cell1.getCellsThatContainThisInFormula().size(),
                    cell2.getCellsThatContainThisInFormula().size()) * -1;
        }
    }
}
