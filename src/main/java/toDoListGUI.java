import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
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
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //Create a title.
        setTitle("To Do List Manager");

        //Create a model for JSpinners
        TodaysDateSpinner.setModel(new SpinnerDateModel());
        DueDateSpinner.setModel(new SpinnerDateModel());

        //Method to make a toDoListTable non-editable.
        toDoListTable.setDefaultEditor(Object.class, null);

        //A user can only select one row.
        toDoListTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        //a generic sorter
        toDoListTable.setAutoCreateRowSorter(true);

        //A user can only select row and not column.
        toDoListTable.setRowSelectionAllowed(true);
        toDoListTable.setColumnSelectionAllowed(false);

        //Method to configure the table model.
        configureTable();
        //Adding listeners.
        addListeners();
        //Sorting data.
        sort();
    }

    private void addListeners(){

        AddNewListButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String className = classTextField.getText();
                String descToDoList = descriptionTextField.getText();
                Date todayDate = (Date)TodaysDateSpinner.getModel().getValue();
                Date dueDate = (Date)DueDateSpinner.getModel().getValue();
                String fileData = fileNameLabel.getText();

                classTextField.setText("");
                descriptionTextField.setText("");
                DueDateSpinner.setValue(todayDate);
                fileNameLabel.setText("");

                if(className == null && descToDoList == null){
                    JOptionPane.showMessageDialog(rootPanel, "Please filled the task information.");
                }

                db.addNewList(className, descToDoList, todayDate, dueDate, fileData);
                configureTable();
            }
        });

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int selectedRowIndex = toDoListTable.getSelectedRow();

                try{
                    //Getting selected row data from table to textFields.
                    //Found this from http://1bestcsharp.blogspot.com/2015/05/java-jtable-add-delete-update-row.html
                    classTextField.setText(toDoListTable.getModel().getValueAt(selectedRowIndex, 1).toString());
                    descriptionTextField.setText(toDoListTable.getModel().getValueAt(selectedRowIndex, 2).toString());
                    TodaysDateSpinner.setValue(toDoListTable.getModel().getValueAt(selectedRowIndex, 3).toString());
                    DueDateSpinner.setValue(toDoListTable.getModel().getValueAt(selectedRowIndex, 4).toString());
                    fileNameLabel.setText(toDoListTable.getModel().getValueAt(selectedRowIndex, 5).toString());

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
                        String class_name = classTextField.getText();
                        String desc_task = descriptionTextField.getText();
                        Date today_date = (Date)TodaysDateSpinner.getValue();
                        Date due_date = (Date)DueDateSpinner.getValue();
                        String file_name = fileNameLabel.getText();
                        int task_id = Integer.parseInt(toDoListTable.getModel().getValueAt(selectedRowIndex, 0).toString());

                        db.editList(class_name, desc_task, today_date, due_date, file_name, task_id);
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

    private void sort(){

        TableRowSorter<DefaultTableModel> sorter =
                new TableRowSorter<DefaultTableModel>((DefaultTableModel)toDoListTable.getModel());

        toDoListTable.setRowSorter(sorter);
        
    }

}
