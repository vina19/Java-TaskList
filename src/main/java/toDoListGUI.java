import javax.swing.*;
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
    private JButton previewToDoListsButton;
    private JButton editButton;
    private JButton doneButton;
    private JButton deleteButton;
    private JTable toDoListTable;
    private JButton AddNewListButton;
    private JSpinner TodaysDateSpinner;
    private JSpinner DueDateSpinner;
    private JButton selectAFileButton;
    private JLabel fileNameLabel;
    private JButton getTodaysDateButton;
    private JButton getDueDateButton;

    private toDoListDB database;


    toDoListGUI() {

        database = new toDoListDB();

        pack();
        setVisible(true);

        setContentPane(rootPanel);
        setPreferredSize(new Dimension(500,500));

        TodaysDateSpinner.setModel(new SpinnerDateModel());
        DueDateSpinner.setModel(new SpinnerDateModel());

        addListeners();

        setTitle("To Do List Manager");

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 1);
        cal.getTime();

        getRootPane().setDefaultButton(previewToDoListsButton);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    }

    private void addListeners() {

        getTodaysDateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                Date d = (Date)TodaysDateSpinner.getModel().getValue();
            }
        });

        getDueDateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                Date d = (Date)DueDateSpinner.getModel().getValue();
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

        AddNewListButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String className = classTextField.getText();
                String descToDoList = descriptionTextField.getText();
                Date todayDate = (Date)TodaysDateSpinner.getModel().getValue();
                Date dueDate = (Date)DueDateSpinner.getModel().getValue();


                if(className == null && descToDoList == null){
                    JOptionPane.showMessageDialog(rootPanel, "Please enter a class and description of the task");
                }
                
                database.addNewList(className, descToDoList, todayDate, dueDate);
            }
        });

        previewToDoListsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                Vector columnToDoList = database.getColumnToDoLists();
                Vector data = database.getAllLists();

                DefaultTableModel tableModel = new DefaultTableModel(data, columnToDoList);
                toDoListTable.setModel(tableModel);

            }
        });

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {



            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int selectedRow = toDoListTable.getSelectedRow();




            }
        });

        doneButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

    }


}
