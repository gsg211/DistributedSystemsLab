import beans.StudentBean;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.time.Year;

public class ProcessStudentServlet extends HttpServlet {

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
        String prenume = request.getParameter("prenume");
        int varsta = Integer.parseInt(request.getParameter("varsta"));
        Double medie = Double.parseDouble(request.getParameter("medie"));

        /*
        procesarea datelor - calcularea anului nasterii
         */
        int anCurent = Year.now().getValue();
        int anNastere = anCurent - varsta;

        // initializare serializator Jackson
        XmlMapper mapper = new XmlMapper();

        // creare bean si populare cu date
        StudentBean bean = new StudentBean();
        bean.setNume(nume);
        bean.setPrenume(prenume);
        bean.setVarsta(varsta);
        bean.setMedie(medie);
        // serializare bean sub forma de string XML
        String webPath = "/content/student.xml";

        String absolutePath = getServletContext().getRealPath(webPath);
        mapper.writeValue(new File(absolutePath), bean);

        // se trimit datele primite si anul nasterii catre o alta pagina JSP pentru afisare
        request.setAttribute("nume", nume);
        request.setAttribute("prenume", prenume);
        request.setAttribute("varsta", varsta);
        request.setAttribute("anNastere", anNastere);
        request.setAttribute("medie", medie);
        request.getRequestDispatcher("./info-student.jsp").forward(request, response);

        //serializ
        DbConfig.saveStudent(bean);
    }
}