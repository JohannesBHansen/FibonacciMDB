package com.herokuapp.iandownard;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Logger;


/**
 * <p>
 * A simple Message Driven Bean that asynchronously receives and processes
 * messages that are sent to the "FibonacciMDBQueue".
 * </p>
 *
 * @author Ian Downard (idownard@opnet.com), Serge Pagop (spagop@redhat.com)
 *
 */
@MessageDriven(name = "FibonacciMDB", activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/FibonacciMDBQueue"),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge")})
public class FibonacciMDB implements MessageListener {
    private Connection con;

    private final static Logger LOGGER = Logger.getLogger(FibonacciMDB.class
            .toString());

    /**
     * see MessageListener#onMessage(Message)
     */
    public void onMessage(javax.jms.Message rcvMessage) {
        String msg = null;
        String[] msg_string;
        Integer message_i;

        try {
            if (rcvMessage instanceof TextMessage) {
                msg = ((TextMessage) rcvMessage).getText();
                msg_string = msg.split("\\s");
                message_i = Integer.parseInt(msg_string[0]);
                //LOGGER.info("Received Message: " + msg_string[0]);


                Integer fib_arg = new Integer(msg_string.length)-1;

                dbinit();  // I want some long running connections here, for testing purposes.
                FibonacciTest(message_i, fib_arg, msg);
                dbclose();

            } else {
                LOGGER.warning("Message of wrong type: "
                        + rcvMessage.getClass().getName());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void dbinit() {
        try {
            Class.forName("org.hsqldb.jdbcDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace(System.out);
        }
        try {
            con= DriverManager.getConnection("jdbc:hsqldb:jmstestdb", "SA", "");
        } catch (SQLException e) {
            //e.printStackTrace(System.out);
        }
    }

    private void dbclose() {
        try {
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public long fibonacci(int n) {
        long ret = n;
        if (n > 1)
            ret = fibonacci(n-1) + fibonacci(n-2);
        return ret;
    }

    public void FibonacciTest(Integer message_i, int fib_arg, String msg) {
        long initialTime = System.currentTimeMillis();
        long result = fibonacci(fib_arg);
        long elapsedTime = System.currentTimeMillis() - initialTime;
        System.out.println("Processing message " + message_i + ":\tFib("+fib_arg+")=" + result + " computed after " + elapsedTime/1000F + "s");
        DatabaseInsert(message_i, fib_arg, result, elapsedTime, msg);
    }

    public void DatabaseInsert(Integer message_i, Integer fibo_arg, Long fibo_result, Long elapsed_time, String msg) {

        try {
            //PreparedStatement pst=con.prepareStatement("insert into fibo_results(fibo_arg,fibo_result,elapsed_time,msg) values(?,?,?,?) where msg_num=" + message_i);
            //System.out.println("update fibo_results set fibo_arg="+fibo_arg+",fibo_result="+fibo_result+",elapsed_time="+elapsed_time+" where msg_num=" + message_i);
            PreparedStatement pst=con.prepareStatement("update fibo_results set fibo_arg=?,fibo_result=?,elapsed_time=? where msg_num=" + message_i);
//            PreparedStatement pst=con.prepareStatement("udpate fibo_results(fibo_arg,fibo_result,elapsed_time,msg) values(?,?,?,?) where msg_num=" + message_i);
//            pst.clearParameters();
            pst.setInt(1, fibo_arg);
            pst.setString(2, fibo_result.toString());
            pst.setString(3, elapsed_time.toString());
//            pst.setString(4, msg);
            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
    }

}
