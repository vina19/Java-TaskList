import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

        addListeners();
        configureTable();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    }

    private void addListeners() {

        AddNewListButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

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
        });

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String className = classTextField.getText();
                String descToDoList = descriptionTextField.getText();
                Date dueDate = (Date)DueDateSpinner.getModel().getValue();
                String fileData = fileNameLabel.getText();

                db.editList(className, descToDoList, dueDate, fileData);

            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                DefaultTableModel tableModel = (DefaultTableModel)toDoListTable.getModel();

                int deleteConfirmation = JOptionPane.showConfirmDialog(null, "Are you sure to delete this task?",
                            null, JOptionPane.YES_NO_OPTION);

                if (deleteConfirmation == 0) {
                    int selectedRow = toDoListTable.getSelectedRow();

                    if(selectedRow == -1) {
                        JOptionPane.showMessageDialog(rootPanel, "Please select a task to delete");
                    }else{
                        tableModel.removeRow(selectedRow);
                        int id = (Integer) tableModel.getValueAt(selectedRow, 0);
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

                DefaultTableModel tableModel = (DefaultTableModel)toDoListTable.getModel();

                try {
                    int selectedRow = toDoListTable.getSelectedRow();

                    if (selectedRow == -1) {
                        JOptionPane.showMessageDialog(rootPanel, "Please select a completed task.");
                    } else {
                        tableModel.removeRow(selectedRow);
                        int id = (Integer) tableModel.getValueAt(selectedRow, 0);
                        db.deleteList(id);
                        configureTable();
                        JOptionPane.showMessageDialog(rootPanel, "Awesome! You did this task on time/before the due date.");
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
}
