import beans.StudentBean;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DbConfig {
    private static String databasePath = null;

    public static void setPath(String path) {
        databasePath = "jdbc:sqlite:" + path;
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");

            if (databasePath == null) {
                String domainRoot = System.getProperty("com.sun.aas.instanceRoot");

                String fullPath = domainRoot + File.separator + "config" + File.separator + "students.db";
                databasePath = "jdbc:sqlite:" + fullPath;

                System.out.println("Database initialized at: " + fullPath);
            }
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQLite Driver not found in GlassFish lib folder", e);
        }

        return DriverManager.getConnection(databasePath);
    }

    public static void initializeDatabase() {
        String sql = "CREATE TABLE IF NOT EXISTS students (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nume TEXT," +
                "prenume TEXT," +
                "medie REAL," +
                "varsta INTEGER" +
                ");";
        runQuery(sql);
    }

    public static void runQuery(String sql) {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void saveStudent(StudentBean student) {
        String sql = "INSERT INTO students(nume, prenume, medie, varsta) VALUES(?,?,?,?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, student.getNume());
            pstmt.setString(2, student.getPrenume());
            // Handle null medie if not set
            pstmt.setDouble(3, student.getMedie() != null ? student.getMedie() : 0.0);
            pstmt.setInt(4, student.getVarsta());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static StudentBean getLastStudent() {
        StudentBean student = null;
        // Get the most recently added student
        String sql = "SELECT * FROM students ORDER BY id DESC LIMIT 1";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                student = new StudentBean();
                student.setNume(rs.getString("nume"));
                student.setPrenume(rs.getString("prenume"));
                student.setMedie(rs.getDouble("medie"));
                student.setVarsta(rs.getInt("varsta"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return student;
    }

    public static StudentBean getStudentByName(String name) {
        StudentBean student = null;
        String sql = "SELECT * FROM students WHERE nume = ? ORDER BY id DESC LIMIT 1";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    student = new StudentBean();
                    student.setNume(rs.getString("nume"));
                    student.setPrenume(rs.getString("prenume"));
                    student.setMedie(rs.getDouble("medie"));
                    student.setVarsta(rs.getInt("varsta"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return student;
    }

    public static List<StudentBean> getAllStudents() {
        List<StudentBean> lista = new ArrayList<>();
        String sql = "SELECT * FROM students";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                StudentBean s = new StudentBean();
                s.setNume(rs.getString("nume"));
                s.setPrenume(rs.getString("prenume"));
                s.setMedie(rs.getDouble("medie"));
                s.setVarsta(rs.getInt("varsta"));
                lista.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public static String exportToJson() {
        try {
            List<StudentBean> lista = getAllStudents();

            // ObjectMapper este echivalentul JSON al lui XmlMapper
            ObjectMapper mapper = new ObjectMapper();

            return mapper.writeValueAsString(lista);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}