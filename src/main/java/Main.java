public class Main {

    public static void main(String[] args) {

        toDoListDB db = new toDoListDB();
        toDoListGUI gui = new toDoListGUI(db);
    }

}
