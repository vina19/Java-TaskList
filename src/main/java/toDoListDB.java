import org.sqlite.core.DB;

import javax.swing.plaf.nimbus.State;
import java.sql.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

public class toDoListDB {

    private static final String DB_CONNECTION_URL = "jdbc:sqlite:ToDoList.sqlite";

    private static final String ID_COL = "id";
    private static final String NAME_COL = "Class";
    private static final String DESC_COL = "Description";
    private static final String FILE_COL = "File";
    private static final String DATE_COL = "Created Date";
    private static final String DUEDATE_COL = "Due Date";

    private static final String CREATE_TO_DO_LIST_TABLE = "CREATE TABLE IF NOT EXISTS to_do_list_manager (id INTEGER PRIMARY KEY, " +
            "Class TEXT, Description TEXT, FileData TEXT, DateCreated DATE, DueDate DATE)";

    private static final String GET_ALL_LISTS = "SELECT * FROM to_do_list_manager";

    private static final String EDIT_LIST = "UPDATE to_do_list_manager SET Class = ?, " +
            "Description = ?, DueDate = ? WHERE Class = ?";

    private static final String DELETE_LIST = "DELETE FROM to_do_list_manager WHERE ID = ?";

    private static final String ADD_TO_DO_LIST = "INSERT INTO to_do_list_manager " +
            "(Class, Description, DateCreated, DueDate) VALUES (?, ?, ?, ?)";


    toDoListDB() {
        createTable();
    }

    private void createTable() {

        try(Connection conn = DriverManager.getConnection(DB_CONNECTION_URL);
            Statement statement = conn.createStatement()){

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
        colToDoList.add(FILE_COL);
        colToDoList.add(DATE_COL);
        colToDoList.add(DUEDATE_COL);

        return colToDoList;
    }

    Vector<Vector> getAllLists() {

        try (Connection connection = DriverManager.getConnection(DB_CONNECTION_URL);
             Statement statement = connection.createStatement()) {

            ResultSet rs = statement.executeQuery(GET_ALL_LISTS);

            Vector<Vector> vectors = new Vector<>();

            String Class, Description, FileData;
            int id;
            Date dateCreated, dueDate;

            while (rs.next()) {

                id = rs.getInt(ID_COL);
                Class = rs.getString(NAME_COL);
                Description = rs.getString(DESC_COL);
                FileData = rs.getString(FILE_COL);
                dateCreated = rs.getDate(DATE_COL);
                dueDate = rs.getDate(DUEDATE_COL);

                Vector v = new Vector();
                v.add(id);
                v.add(Class);
                v.add(Description);
                v.add(FileData);
                v.add(dateCreated);
                v.add(dueDate);

                vectors.add(v);
            }

            return vectors;

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void addNewList(String Class, String Description, String FileData, Date dateCreated, Date dueDate){

        try(Connection connection = DriverManager.getConnection(DB_CONNECTION_URL);
            PreparedStatement preparedStatement = connection.prepareStatement(ADD_TO_DO_LIST)){

            long today_Date = System.currentTimeMillis();
            java.sql.Date date = new java.sql.Date(today_Date);

            preparedStatement.setString(1, Class);
            preparedStatement.setString(2, Description);
            preparedStatement.setString(3, FileData);
            preparedStatement.setDate(4, date);
            preparedStatement.setDate(5, dueDate);

            preparedStatement.executeUpdate();

        }catch (SQLException e){
            throw new RuntimeException(e);
        }

    }


    public void deleteList(int listID){

        try(Connection connection = DriverManager.getConnection(DB_CONNECTION_URL);
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_LIST)){

            preparedStatement.setInt(1, listID);
            preparedStatement.executeUpdate();


        }catch (SQLException e){
            throw new RuntimeException(e);
        }

    }
}
