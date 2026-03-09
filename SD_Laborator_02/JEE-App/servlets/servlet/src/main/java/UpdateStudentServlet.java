import ejb.StudentEntity;

import javax.persistence.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class UpdateStudentServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String nume = request.getParameter("nume");
        String prenume = request.getParameter("prenume");
        int varsta = Integer.parseInt(request.getParameter("varsta"));

        // pregatire EntityManager
        EntityManagerFactory factory =   Persistence.createEntityManagerFactory("bazaDeDateSQLite");
        EntityManager em = factory.createEntityManager();


        TypedQuery<StudentEntity> query = em.createQuery("SELECT s FROM StudentEntity s WHERE s.nume = :NAME", StudentEntity.class);
        query.setParameter("NAME",nume);

        List<StudentEntity> studenti = query.getResultList();

        if(studenti.isEmpty())
        {
            response.setContentType("text/html");
            response.getWriter().println("Nu l-am gasit." +
                    "<br /><br /><a href='./'>Inapoi la meniul principal</a>");
            return;
        }

        StudentEntity student = studenti.get(0);
        student.setNume(nume);
        student.setPrenume(prenume);
        student.setVarsta(varsta);


        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        em.merge(student);
        transaction.commit();

        // inchidere EntityManager
        em.close();
        factory.close();

        // trimitere raspuns inapoi la client
        response.setContentType("text/html");
        response.getWriter().println("L-am schimbat in baza de date." +
                "<br /><br /><a href='./'>Inapoi la meniul principal</a>");
    }
}
