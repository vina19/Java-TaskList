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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
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
        //Clear the TextField.
        emptyTextFields();
        //Sorting data.
        sort();
    }

    private void addListeners(){

        AddNewListButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                //Grabbing the text and date from text fields and spinners.
                String class_name = classTextField.getText();
                String task_desc = descriptionTextField.getText();
                Date date_created = (Date)TodaysDateSpinner.getModel().getValue();
                Date due_date = (Date)DueDateSpinner.getModel().getValue();
                String file_data = fileNameLabel.getText();

                //Empty the text fields after adding a new file.
                emptyTextFields();
                //Set the due date spinner to today's date.
                DueDateSpinner.setValue(date_created);

                //If className and descToDoList text fields empty showMessageDialog.
                if(class_name == null && task_desc == null){
                    JOptionPane.showMessageDialog(rootPanel, "Please filled the task information.");
                }

                //Take the method from toDoListDb aka db addNewList and add the parameter from the user input.
                db.addNewList(class_name, task_desc, date_created, due_date, file_data);

                //create empty fields in the JTable.
                configureTable();
            }
        });

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    //Get selected row.
                    int selectedRowIndex = toDoListTable.getSelectedRow();

                    //Getting selected row data from table to textFields.
                    //Found this from http://1bestcsharp.blogspot.com/2015/05/java-jtable-add-delete-update-row.html
                    classTextField.setText(toDoListTable.getModel().getValueAt(selectedRowIndex, 1).toString());
                    descriptionTextField.setText(toDoListTable.getModel().getValueAt(selectedRowIndex, 2).toString());
                    TodaysDateSpinner.setValue(toDoListTable.getModel().getValueAt(selectedRowIndex, 3));
                    DueDateSpinner.setValue(toDoListTable.getModel().getValueAt(selectedRowIndex, 4));
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
                    //Get selected row.
                    int selectedRowIndex = toDoListTable.getSelectedRow();

                    //If there is row selected, add data into editList method in db file.
                    if(selectedRowIndex >= 0){
                        String class_name = classTextField.getText();
                        String desc_task = descriptionTextField.getText();
                        Date today_date = (Date)TodaysDateSpinner.getValue();
                        Date due_date = (Date)DueDateSpinner.getValue();
                        String file_name = fileNameLabel.getText();
                        int task_id = Integer.parseInt(toDoListTable.getModel().getValueAt(selectedRowIndex, 0).toString());

                        db.editList(class_name, desc_task, today_date, due_date, file_name, task_id);
                        configureTable();

                        //empty the text fields after saving the task.
                        emptyTextFields();
                        //set the due date spinner to today's date.
                        DueDateSpinner.setValue(today_date);
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
                //Create delete confirmation from the user by showing showConfirmDialog.
                int deleteConfirmation = JOptionPane.showConfirmDialog(null, "Are you sure to delete this task?",
                        null, JOptionPane.YES_NO_OPTION);

                //if yes then get the selected row.
                if (deleteConfirmation == 0) {
                    int selectedRowIndex = toDoListTable.getSelectedRow();

                    //if there is no row selected than showMessageDialog or else
                    //grab the deleteList method from db and delete the data by the selected row id.
                    if(selectedRowIndex == -1) {
                        JOptionPane.showMessageDialog(rootPanel, "Please select a task to delete");
                    }else{
                        int id = (Integer) toDoListTable.getModel().getValueAt(selectedRowIndex, 0);
                        db.deleteList(id);
                        configureTable();
                        emptyTextFields();
                    }
                } else {
                    JOptionPane.showMessageDialog(rootPanel, "Stop task deletion");
                }
            }
        });

        doneButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //try-catch exception
                try {
                    //get selected row
                    int selected_row = toDoListTable.getSelectedRow();

                    //if there is no row selected showMessageDialog.
                    //Else grab the deleteList from db and delete the data by the selected row id which indicate
                    //that the user done with the task by showing showMessageDialog.
                    if (selected_row == -1) {
                        JOptionPane.showMessageDialog(rootPanel, "Please select a completed task.");
                    } else {
                        int id = (Integer) toDoListTable.getModel().getValueAt(selected_row, 0);
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
                //Create file chooser dialog
                JFileChooser fileChooser = new JFileChooser();

                int returnVal = fileChooser.showOpenDialog(toDoListGUI.this);

                //if the user choose the file by a approved option by clicking open
                //then get a file object from selected file.
                if(returnVal == JFileChooser.APPROVE_OPTION){
                    File fileSelected = fileChooser.getSelectedFile();
                    //set fileNameLable with the file name.
                    fileNameLabel.setText(fileSelected.toString());
                }
            }
        });
    }

    private void configureTable(){

        //Create Vector for columnData which hold column names and data which hold all data.
        Vector columnData = db.getColumnToDoLists();
        Vector<Vector> data = db.getAllLists();

        //Set table model
        DefaultTableModel tableModel = new DefaultTableModel(data, columnData);
        toDoListTable.setModel(tableModel);

    }

    //Clear all the fields.
    private void emptyTextFields(){
        classTextField.setText("");
        descriptionTextField.setText("");
        fileNameLabel.setText("");
    }

    //Sort the data by the column header.
    private void sort(){

        TableRowSorter<DefaultTableModel> sorter =
                new TableRowSorter<DefaultTableModel>((DefaultTableModel)toDoListTable.getModel());

        toDoListTable.setRowSorter(sorter);

    }

}
