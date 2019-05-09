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
    private DefaultTableModel tableModel;
    private Vector columnData;


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

                int selectedRow = toDoListTable.getSelectedRow();

                db.deleteList(selectedRow);


            }
        });

        doneButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int selectedRow = toDoListTable.getSelectedRow();

                db.deleteList(selectedRow);

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
