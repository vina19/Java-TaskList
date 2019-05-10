import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

public class toDoListGUI extends JFrame {
    private JPanel rootPanel;
    private JTextField classTextField;
    private JTextField descriptionTextField;
    private JButton editButton;
    private JButton doneButton;
    private JButton deleteButton;
    private JTable toDoListTable;
    private JButton AddNewListButton;
    private JSpinner TodaysDateSpinner;
    private JSpinner DueDateSpinner;
    private JButton selectAFileButton;
    private JLabel fileNameLabel;
    private JButton saveButton;

    private toDoListDB db;

    toDoListGUI(toDoListDB db) {

        this.db = db;

        setContentPane(rootPanel);
        pack();
        setVisible(true);
        setTitle("To Do List Manager");

        TodaysDateSpinner.setModel(new SpinnerDateModel());
        DueDateSpinner.setModel(new SpinnerDateModel());

        toDoListTable.setDefaultEditor(Object.class, null);
        toDoListTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        toDoListTable.setRowSelectionAllowed(true);
        toDoListTable.setColumnSelectionAllowed(false);

        configureTable();
        addList();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        AddNewListButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                addList();

                int lastRow = toDoListTable.getRowCount() - 1;
                toDoListTable.setRowSelectionInterval(lastRow, lastRow);

                //Getting selected row data from table to textFields.
                //Found this from http://1bestcsharp.blogspot.com/2015/05/java-jtable-add-delete-update-row.html

//              String className = (toDoListTable.getModel().getValueAt(selectedRowIndex, 1).toString());
//              String descTask = (toDoListTable.getModel().getValueAt(selectedRowIndex, 2).toString());
//              String todayDate = (toDoListTable.getModel().getValueAt(selectedRowIndex, 3).toString());
//              String dueDate = (toDoListTable.getModel().getValueAt(selectedRowIndex, 4).toString());
//              String fileData = (toDoListTable.getModel().getValueAt(selectedRowIndex, 5).toString());
            }
        });

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                DefaultTableModel tableModel = (DefaultTableModel)toDoListTable.getModel();

                try{
                    toDoListTable.setModel(tableModel);
                    int selectedRowIndex = toDoListTable.getSelectedRow();

                    //Getting selected row data from table to textFields.
                    //Found this from http://1bestcsharp.blogspot.com/2015/05/java-jtable-add-delete-update-row.html
                    classTextField.setText(tableModel.getValueAt(selectedRowIndex, 1).toString());
                    descriptionTextField.setText(tableModel.getValueAt(selectedRowIndex, 2).toString());
                    TodaysDateSpinner.setValue(tableModel.getValueAt(selectedRowIndex, 3).toString());
                    DueDateSpinner.setValue(tableModel.getValueAt(selectedRowIndex, 4).toString());
                    fileNameLabel.setText(tableModel.getValueAt(selectedRowIndex, 5).toString());

                }catch (IndexOutOfBoundsException i){
                    System.out.println("Please select a row.");
                }

            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {

                    int selectedRowIndex = toDoListTable.getSelectedRow();

                    if(selectedRowIndex >= 0){
                        String className = classTextField.getText();
                        String descToDoList = descriptionTextField.getText();
                        Date todayDate = (Date)TodaysDateSpinner.getModel().getValue();
                        Date dueDate = (Date)DueDateSpinner.getModel().getValue();
                        String fileData = fileNameLabel.getText();

                        db.editList(className, descToDoList, todayDate, dueDate, fileData);
                        addList();
                        configureTable();

                    }else{
                        JOptionPane.showMessageDialog(rootPanel, "Update Error.");
                    }

                }catch (IndexOutOfBoundsException i){
                    System.out.println("Please select a row.");
                }

            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int deleteConfirmation = JOptionPane.showConfirmDialog(null, "Are you sure to delete this task?",
                        null, JOptionPane.YES_NO_OPTION);

                if (deleteConfirmation == 0) {

                    int selectedRowIndex = toDoListTable.getSelectedRow();

                    if(selectedRowIndex == -1) {
                        JOptionPane.showMessageDialog(rootPanel, "Please select a task to delete");
                    }else{
                        int id = (Integer) toDoListTable.getModel().getValueAt(selectedRowIndex, 0);
                        db.deleteList(id);
                        configureTable();
                        classTextField.setText("");
                        descriptionTextField.setText("");
                        fileNameLabel.setText("");
                    }

                    JOptionPane.showMessageDialog(rootPanel, "Task deleted successfully.");

                } else {
                    JOptionPane.showMessageDialog(rootPanel, "Stop task deletion");
                }

            }
        });

        doneButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    int selectedRowIndex = toDoListTable.getSelectedRow();

                    if (selectedRowIndex == -1) {
                        JOptionPane.showMessageDialog(rootPanel, "Please select a completed task.");
                    } else {
                        int id = (Integer) toDoListTable.getModel().getValueAt(selectedRowIndex, 0);
                        db.deleteList(id);
                        configureTable();
                        JOptionPane.showMessageDialog(rootPanel, "Awesome! You did this task.");
                    }
                }catch (Exception ex){
                    JOptionPane.showMessageDialog(null, ex);
                }
            }
        });

        selectAFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JFileChooser fileChooser = new JFileChooser();

                int returnVal = fileChooser.showOpenDialog(toDoListGUI.this);

                if(returnVal == JFileChooser.APPROVE_OPTION){
                    File fileSelected = fileChooser.getSelectedFile();
                    fileNameLabel.setText(fileSelected.toString());
                }
            }
        });

    }

    private void configureTable(){

        Vector columnData = db.getColumnToDoLists();
        Vector<Vector> data = db.getAllLists();

        DefaultTableModel tableModel = new DefaultTableModel(data, columnData);
        toDoListTable.setModel(tableModel);

    }

    private void addList(){

        String className = classTextField.getText();
        String descToDoList = descriptionTextField.getText();
        Date todayDate = (Date)TodaysDateSpinner.getModel().getValue();
        Date dueDate = (Date)DueDateSpinner.getModel().getValue();
        String fileData = fileNameLabel.getText();

        if(className == null && descToDoList == null){
            JOptionPane.showMessageDialog(rootPanel, "Please enter a class and description of the task");
        }

        db.addNewList(className, descToDoList, todayDate, dueDate, fileData);
        configureTable();

    }

}
