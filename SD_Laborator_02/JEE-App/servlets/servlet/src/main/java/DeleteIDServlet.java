import ejb.StudentEntity;

import javax.persistence.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class DeleteIDServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));


        // pregatire EntityManager
        EntityManagerFactory factory =   Persistence.createEntityManagerFactory("bazaDeDateSQLite");
        EntityManager em = factory.createEntityManager();


        StudentEntity student = em.find(StudentEntity.class,id);
        if(student == null)
        {
            response.setContentType("text/html");
            response.getWriter().println("Nu l-am gasit." +
                    "<br /><br /><a href='./'>Inapoi la meniul principal</a>");
            return;
        }

        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        em.remove(student);
        transaction.commit();

        // inchidere EntityManager
        em.close();
        factory.close();

        // trimitere raspuns inapoi la client
        response.setContentType("text/html");
        response.getWriter().println("L-am extmatriculat din baza de date." +
                "<br /><br /><a href='./'>Inapoi la meniul principal</a>");
    }
}
