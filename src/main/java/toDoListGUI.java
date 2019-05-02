import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

public class toDoListGUI extends JFrame {
    private JPanel rootPanel;
    private JTextField classTextField;
    private JTextField descriptionTextField;
    private JComboBox<Calendar> TodayDateComboBox;
    private JComboBox<Calendar> DueDateComboBox;
    private JButton previewToDoListsButton;
    private JButton editButton;
    private JButton doneButton;
    private JButton deleteButton;
    private JTable toDoListTable;
    private JButton AddNewListButton;

    private toDoListDB db;


    toDoListGUI(toDoListDB db) {

        this.db = db;

        setContentPane(rootPanel);
        setPreferredSize(new Dimension(500,500));

        addListeners();

        setTitle("To Do List Manager");

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 1);
        cal.getTime();

        TodayDateComboBox.addItem(cal);
        DueDateComboBox.addItem(cal);

        getRootPane().setDefaultButton(previewToDoListsButton);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    }

    private void addListeners() {

        AddNewListButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String className = classTextField.getText();
                String descToDoList = descriptionTextField.getText();

                if(className == null && descToDoList == null){
                    JOptionPane.showMessageDialog(rootPanel, "Please enter a class and description of the task");
                    return;
                }

                TodayDateComboBox.getSelectedItem();
                DueDateComboBox.getSelectedItem();

            }
        });

        previewToDoListsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                Vector columnToDoList = db.getColumnToDoLists();
                Vector data = db.getAllLists();

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
