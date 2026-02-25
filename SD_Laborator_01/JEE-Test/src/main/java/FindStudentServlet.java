import beans.StudentBean;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.time.Year;

public class FindStudentServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        String path = getServletContext().getRealPath("/WEB-INF/students.db");
        DbConfig.setPath(path);
        DbConfig.initializeDatabase();
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String nume = request.getParameter("nume");
        StudentBean student = DbConfig.getStudentByName(nume);


        String prenume = student.getPrenume();
        int varsta = student.getVarsta();
        Double medie = student.getMedie();

        int anCurent = Year.now().getValue();
        int anNastere = anCurent - varsta;


        // se trimit datele primite si anul nasterii catre o alta pagina JSP pentru afisare
        request.setAttribute("nume", nume);
        request.setAttribute("prenume", prenume);
        request.setAttribute("varsta", varsta);
        request.setAttribute("anNastere", anNastere);
        request.setAttribute("medie", medie);
        request.getRequestDispatcher("./info-student.jsp").forward(request, response);

    }
}
