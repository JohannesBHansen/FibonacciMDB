package com.herokuapp.iandownard;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.annotation.Resource;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * A simple servlet 3 as client that sends several messages to a queue.
 * </p>
 *
 * <p>
 * The servlet is registered and mapped to /FibonacciMDBServletClient using the {@linkplain WebServlet
 * @HttpServlet}.
 * </p>
 *
 * @author Serge Pagop (spagop@redhat.com)
 *
 */
@WebServlet("/FibonacciMDBServletClient")
public class FibonacciMDBServletClient extends HttpServlet {

    private static int MSG_SIZE=30;  //This is also the Fibonacci argument
    private static int NUM_MSGS=10;
    private static final String[] RANDOM_WORDS = {"nonsequ ", "ismodol ", "oreetuer ", "iril ", "ette ", "dolore ", "facidunt ", "vullup ", "tat ", "lor ", "volore ", "consecte ", "dolesed ", "ar ", "se ", "veliter ", "tetiure ", "te ", "lortis ", "dunt ", "feugiam. ", "Commodit ", "dolorer ", "iure ", "te ", "adionse ", "quatet ", "blan ", "henim ", "exer ", "aute ", "enit ", "alit ", "veliqua ", "Eliquatuero ", "dip ", "etiros ", "numsan ", "vent ", "lam ", "conum ", "zzrit ", "la ", "iam ", "iure ", "nonsen ", "dre ", "exeril ", "ad ", "te ", "facip ", "eugait ", "lametue ", "consecte ", "dolesed ", "dolor ", "sevelit ", "ver ", "adionse ", "estrud ", "magnisc ", "tatummy ", "niam ", "dolorti ", "onulaore ", "teed ", "dolor ", "estrud ", "dunt ", "do ", "conulla ", "mconse ", "ming ", "exent ", "am ", "quat ", "velenit ", "exerci ", "tate ", "duipsusci ", "et ", "landrem ", "zzriuscinim ", "nullaorem ", "Uptat ", "prat ", "lut ", "lut ", "iriliquat ", "quis ", "alisl ", "irilitam ", "irillum ", "augue ", "zzrit ", "verosto ", "et ", "non ", "consequ ", "ismodit ", "iriuscin ", "el ", "dion ", "sequi ", "doleniam ", "zzriure ", "ex ", "eraesto ", "la ", "ait ", "consed ", "del ", "dolortie ", "augait ", "praesto ", "od ", "lor ", "tat ", "dunt ", "lum ", "ametue ", "Uptat ", "prat ", "lut ", "lut ", "iriliquat ", "quis ", "alisl ", "irilit ", "am ", "irillum ", "at ", "nia ", "zrit ", "verosto ", "consequ ", "ismodit ", "iriuscin ", "el ", "dolorero ", "dionsequi ", "tie ", "veliquin ", "exerit ", "inis ", "ea ", "feugue ", "feum ", "irilisit ", "lam ", "doleniam ", "zzriure ", "consed ", "del ", "ex ", "dolortie ", "dolortio ", "eraesto ", "odoluptat ", "at ", "augait ", "praesto ", "odmitat ", "dunt ", "lum ", "ametue ", "doluptat ", "Ut ", "praestrud ", "min ", "el ", "digniat ", "voluptat ", "lore ", "et ", "veniscilla ", "nim ", "ad ", "magna ", "commodo "};

    @Resource(mappedName = "java:/ConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(mappedName = "queue/FibonacciMDBQueue")
    private Queue queue;

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            MSG_SIZE = Integer.parseInt(req.getParameter("value"));
            NUM_MSGS = Integer.parseInt(req.getParameter("iterations"));
        } catch (NumberFormatException e) {
        }


        //prepare the database
        dbinit();

        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        Connection connection = null;
        //out.write("<h1>Example demonstrates the use of *JMS 1.1* and *EJB 3.1 Message-Driven Bean* in JBoss AS.</h1>");
        try {
            //// added by Ian Downard <idownard@opnet.com> ////
            InitialContext initialContext = new InitialContext();
            connectionFactory = (ConnectionFactory)initialContext.lookup("/ConnectionFactory");
            Queue queue = (Queue)initialContext.lookup("/queue/FibonacciMDBQueue");
            //////////////////////

            connection = connectionFactory.createConnection();
            Session session = connection.createSession(false,
                    Session.AUTO_ACKNOWLEDGE);
            MessageProducer messageProducer = session.createProducer(queue);
            connection.start();
            //out.write("<p>View JDBC records, <a href='ViewResults'>View Records</a>");
            //out.write("<h2>Sending "+NUM_MSGS+" messages to the queue.</h2>");
            //out.write("<h2>Following messages will be send to the queue:</h2>");
            TextMessage message = session.createTextMessage();
            String tmpmsg="";
            for (int j = 0; j < MSG_SIZE; j++)
            {
                tmpmsg = tmpmsg + RANDOM_WORDS[j % RANDOM_WORDS.length];
            }
            for (int i = 0; i < NUM_MSGS; i++) {
                message.setText(i+" "+tmpmsg);
                messageProducer.send(message);
                //out.write("Message ("+i+"): " + message.getText() +"</br>");
            }
            //out.write("<p><i>Go to your JBoss Application Server console or Server log to see the result of messages processing</i></p>");
            out.write("<meta http-equiv=\"Refresh\" content=\"0; URL=ViewResults\">");
        } catch (JMSException e) {
            e.printStackTrace();
            out.write("<h2>A problem occurred during the delivery of this message</h2>");
            out.write("</br>");
            out.write("<p><i>Go your the JBoss Application Server console or Server log to see the error stack trace</i></p>");
        } catch (NamingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
            if(out != null) {
                out.close();
            }
        }
    }

    public void dbinit() throws ServletException {
        java.sql.Connection con;

        try {
            Class.forName("org.hsqldb.jdbcDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace(System.out);
        }
        try {
            con= DriverManager.getConnection("jdbc:hsqldb:jmstestdb", "SA", "");
            con.createStatement().executeUpdate("drop table fibo_results");
        } catch (SQLException e) {
            //e.printStackTrace(System.out);
        }
        try {
            con= DriverManager.getConnection("jdbc:hsqldb:jmstestdb", "SA", "");
            //con.createStatement().executeUpdate("create table fibo_results (arg integer,result integer,proc_time integer)");
            con.createStatement().executeUpdate("create table fibo_results (msg_num integer, fibo_arg integer,fibo_result varchar(45),elapsed_time varchar(45), msg varchar(" + RANDOM_WORDS.length + 1 + "))");
            for(int i=0; i<NUM_MSGS; i++) {
                con.createStatement().executeUpdate("insert into fibo_results(msg_num) values("+i+")");
            }
            //  Statement stmt = con.createStatement();
//            rs = stmt.executeQuery("select * from fibo_results");
//            while(rs.next()){
//                out.write("<hr><br/>"+rs.getString(1));
//                out.write(", "+rs.getString(2));
//                out.write(", "+rs.getString(3));
//            }
        } catch (SQLException e) {
            //e.printStackTrace(System.out);
        }
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doGet(req, resp);
    }

}
