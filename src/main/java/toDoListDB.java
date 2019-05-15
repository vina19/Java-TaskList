import org.sqlite.core.DB;

import javax.swing.plaf.nimbus.State;
import java.sql.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

public class toDoListDB {

    //Database connection path
    private static final String DB_CONNECTION_URL = "jdbc:sqlite:ToDoList.sqlite";

    private static final String TABLE_NAME = "to_do_list_manager";
    private static final String ID_COL = "id";
    private static final String NAME_COL = "class_name";
    private static final String DESC_COL = "task_desc";
    private static final String DATE_COL = "date_created";
    private static final String DUE_DATE_COL = "due_date";
    private static final String FILE_COL = "file_data";

    //Create table in table to_do_list_manager.
    private static final String CREATE_TO_DO_LIST_TABLE = "CREATE TABLE IF NOT EXISTS TABLE_NAME (" +
            "id INTEGER PRIMARY KEY, class_name TEXT, task_desc TEXT, date_created DATE, due_date DATE, file_data TEXT)";

    //Get everything from the table method.
    private static final String GET_ALL_LISTS = "SELECT * FROM TABLE_NAME ";

    //Update table by id.
    private static final String EDIT_LIST = "UPDATE TABLE_NAME SET class_name = ?, task_desc = ?, " +
            "date_created = ?, due_date = ?, file_data = ? WHERE id = ?";

    //Delete data by id.
    private static final String DELETE_LIST = "DELETE FROM TABLE_NAME WHERE id = ?";

    //Insert data to table.
    private static final String ADD_TO_DO_LIST = "INSERT INTO TABLE_NAME " +
            "(class_name, task_desc, date_created, due_date, file_data) VALUES (?, ?, ?, ?, ?)";

    //Use createTable method in toDoListDB.
    toDoListDB() {
        createTable();
    }

    //Create table method.
    private void createTable() {

        try(Connection connection = DriverManager.getConnection(DB_CONNECTION_URL);
            Statement statement = connection.createStatement()){

            statement.executeUpdate(CREATE_TO_DO_LIST_TABLE);

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    Vector getColumnToDoLists() {
        //Set the column names
        Vector col_Names = new Vector();
        col_Names.add(ID_COL);
        col_Names.add(NAME_COL);
        col_Names.add(DESC_COL);
        col_Names.add(DATE_COL);
        col_Names.add(DUE_DATE_COL);
        col_Names.add(FILE_COL);

        return col_Names;
    }

    Vector<Vector> getAllLists() {
        try (Connection connection = DriverManager.getConnection(DB_CONNECTION_URL);
             Statement statement = connection.createStatement()) {

            //Create ResultSet and put all the table data in there.
            ResultSet rs = statement.executeQuery(GET_ALL_LISTS);

            //Create Vector list to add in all the data.
            Vector<Vector> vectors = new Vector<>();

            //Create variables for each data
            String class_name, task_desc, file_data;
            int task_id;
            Date date_created, due_date;

            while (rs.next()) {
                //get each column data and add it to the new Vector v.
                task_id = rs.getInt(ID_COL);
                class_name = rs.getString(NAME_COL);
                task_desc = rs.getString(DESC_COL);
                date_created = rs.getDate(DATE_COL);
                due_date = rs.getDate(DUE_DATE_COL);
                file_data = rs.getString(FILE_COL);

                Vector v = new Vector();
                v.add(task_id);
                v.add(class_name);
                v.add(task_desc);
                v.add(date_created);
                v.add(due_date);
                v.add(file_data);

                //Add Vector v to the vectors list.
                vectors.add(v);
            }
            return vectors;
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    //Add new data to the database method.
    public void addNewList(String class_name, String task_desc, Date date_created, Date due_date, String file_data){

        try(Connection connection = DriverManager.getConnection(DB_CONNECTION_URL);
            PreparedStatement preparedStatement = connection.prepareStatement(ADD_TO_DO_LIST)){

            //Set the today_Date to the SQL value.
            long today_Date = date_created.getTime();
            java.sql.Date date = new java.sql.Date(today_Date);

            //Set the due_Date to the SQL value.
            long due_date_task = due_date.getTime();
            java.sql.Date dueDate = new java.sql.Date(due_date_task);

            //Set the data to be added to the preparedStatement
            preparedStatement.setString(1, class_name);
            preparedStatement.setString(2, task_desc);
            preparedStatement.setDate(3, date);
            preparedStatement.setDate(4, dueDate);
            preparedStatement.setString(5, file_data);

            preparedStatement.execute();

        }catch (SQLException e){
            throw new RuntimeException(e);
        }

    }

    //Edit database method.
    public void editList(String class_name, String task_desc, Date date_created, Date due_date, String file_data, int task_id){

        try(Connection connection = DriverManager.getConnection(DB_CONNECTION_URL);
            PreparedStatement preparedStatement = connection.prepareStatement(EDIT_LIST)) {

            long today_Date = date_created.getTime();
            java.sql.Date date = new java.sql.Date(today_Date);

            long due_date_task = due_date.getTime();
            java.sql.Date dueDate = new java.sql.Date(due_date_task);

            //Set the data that edited.
            preparedStatement.setString(1, class_name);
            preparedStatement.setString(2, task_desc);
            preparedStatement.setDate(3, date);
            preparedStatement.setDate(4, dueDate);
            preparedStatement.setString(5, file_data);
            preparedStatement.setInt(6, task_id);

            preparedStatement.execute();

        }catch (SQLException e){
            throw new RuntimeException(e);
        }

    }

    //Delete database method.
    public void deleteList(int taskID){

        try(Connection connection = DriverManager.getConnection(DB_CONNECTION_URL);
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_LIST)){

            //Delete by id
            preparedStatement.setInt(1, taskID);
            preparedStatement.execute();

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
}
