import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ExportServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        String path = getServletContext().getRealPath("/WEB-INF/students.db");
        DbConfig.setPath(path);
        DbConfig.initializeDatabase();
    }

    @Override
    public void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
            throws IOException {
        httpServletResponse.getWriter().print(DbConfig.exportToJson());
    }
}
