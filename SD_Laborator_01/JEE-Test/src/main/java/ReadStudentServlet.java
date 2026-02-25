import beans.StudentBean;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.time.Year;

public class ReadStudentServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        if (getServletContext().getRealPath("/WEB-INF/students.db") != null) {
            String path = getServletContext().getRealPath("/WEB-INF/students.db");
            DbConfig.setPath(path);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        StudentBean bean = DbConfig.getLastStudent();

        if (bean == null) {
            response.sendError(404, "Nu avem studenti :(!");
            return;
        }

        int anCurent = Year.now().getValue();
        int anNastere = anCurent - bean.getVarsta();

        // 3. Set attributes for JSP
        request.setAttribute("nume", bean.getNume());
        request.setAttribute("prenume", bean.getPrenume());
        request.setAttribute("varsta", bean.getVarsta());
        request.setAttribute("medie", bean.getMedie());
        request.setAttribute("anNastere", anNastere);


        request.getRequestDispatcher("./info-student.jsp").forward(request, response);
    }
}
