import org.sqlite.core.DB;

import javax.swing.plaf.nimbus.State;
import java.sql.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

public class toDoListDB {

    private static final String DB_CONNECTION_URL = "jdbc:sqlite:ToDoList.sqlite";

    private static final String TABLE_NAME = "to_do_list_manager";
    private static final String ID_COL = "id";
    private static final String NAME_COL = "Class";
    private static final String DESC_COL = "Description";
    private static final String DATE_COL = "DateCreated";
    private static final String DUE_DATE_COL = "DueDate";
    private static final String FILE_COL = "FileData";

    private static final String CREATE_TO_DO_LIST_TABLE = "CREATE TABLE IF NOT EXISTS TABLE_NAME (id INTEGER PRIMARY KEY, " +
            "Class TEXT, Description TEXT, DateCreated DATE, DueDate DATE, FileData TEXT)";

    private static final String GET_ALL_LISTS = "SELECT * FROM TABLE_NAME ";

    private static final String EDIT_LIST = "UPDATE TABLE_NAME SET Class = ?, Description = ?, " +
            "DateCreated = ?, DueDate = ?, FileData = ? WHERE id = ?";

    private static final String DELETE_LIST = "DELETE FROM TABLE_NAME WHERE id = ?";

    private static final String ADD_TO_DO_LIST = "INSERT INTO TABLE_NAME " +
            "(Class, Description, DateCreated, DueDate, FileData) VALUES (?, ?, ?, ?, ?)";




    toDoListDB() {
        createTable();
    }

    private void createTable() {

        try(Connection connection = DriverManager.getConnection(DB_CONNECTION_URL);
            Statement statement = connection.createStatement()){

            statement.executeUpdate(CREATE_TO_DO_LIST_TABLE);

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    Vector getColumnToDoLists() {

        Vector colToDoList = new Vector();
        colToDoList.add(ID_COL);
        colToDoList.add(NAME_COL);
        colToDoList.add(DESC_COL);
        colToDoList.add(DATE_COL);
        colToDoList.add(DUE_DATE_COL);
        colToDoList.add(FILE_COL);

        return colToDoList;
    }

    Vector<Vector> getAllLists() {

        try (Connection connection = DriverManager.getConnection(DB_CONNECTION_URL);
             Statement statement = connection.createStatement()) {

            ResultSet rs = statement.executeQuery(GET_ALL_LISTS);

            Vector<Vector> vectors = new Vector<>();

            String Class, Description, FileData;
            int id;
            Date DateCreated, DueDate;

            while (rs.next()) {

                id = rs.getInt(ID_COL);
                Class = rs.getString(NAME_COL);
                Description = rs.getString(DESC_COL);
                DateCreated = rs.getDate(DATE_COL);
                DueDate = rs.getDate(DUE_DATE_COL);
                FileData = rs.getString(FILE_COL);

                Vector v = new Vector();
                v.add(id);
                v.add(Class);
                v.add(Description);
                v.add(DateCreated);
                v.add(DueDate);
                v.add(FileData);

                vectors.add(v);
            }

            return vectors;

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void addNewList(String Class, String Description, Date DateCreated, Date dueDateTask, String FileData){

        try(Connection connection = DriverManager.getConnection(DB_CONNECTION_URL);
            PreparedStatement preparedStatement = connection.prepareStatement(ADD_TO_DO_LIST)){

            long today_Date = DateCreated.getTime();
            java.sql.Date date = new java.sql.Date(today_Date);

            long due_Date = dueDateTask.getTime();
            java.sql.Date dueDate = new java.sql.Date(due_Date);

            preparedStatement.setString(1, Class);
            preparedStatement.setString(2, Description);
            preparedStatement.setDate(3, date);
            preparedStatement.setDate(4, dueDate);
            preparedStatement.setString(5, FileData);


            preparedStatement.execute();

        }catch (SQLException e){
            throw new RuntimeException(e);
        }

    }

    public void editList(String Class, String Desc, Date todayDate, Date dueDateTask, String File_Data, int taskID){

        try(Connection connection = DriverManager.getConnection(DB_CONNECTION_URL);
            PreparedStatement preparedStatement = connection.prepareStatement(EDIT_LIST)) {

            long today_Date = todayDate.getTime();
            java.sql.Date date = new java.sql.Date(today_Date);

            long due_Date = dueDateTask.getTime();
            java.sql.Date dueDate = new java.sql.Date(due_Date);

            preparedStatement.setString(1, Class);
            preparedStatement.setString(2, Desc);
            preparedStatement.setDate(3, date);
            preparedStatement.setDate(4, dueDate);
            preparedStatement.setString(5, File_Data);
            preparedStatement.setInt(6, taskID);

            preparedStatement.execute();


        }catch (SQLException e){
            throw new RuntimeException(e);
        }

    }

    public void deleteList(int taskID){

        try(Connection connection = DriverManager.getConnection(DB_CONNECTION_URL);
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_LIST)){

            preparedStatement.setInt(1, taskID);
            preparedStatement.execute();


        }catch (SQLException e){
            throw new RuntimeException(e);
        }

    }

}
