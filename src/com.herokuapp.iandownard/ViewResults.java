package com.herokuapp.iandownard;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ViewResults extends HttpServlet {
    Connection con;
    @Override
    public void init() throws ServletException {
        try {
            Class.forName("org.hsqldb.jdbcDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace(System.out);
        }
        try {
            con=DriverManager.getConnection("jdbc:hsqldb:jmstestdb","SA","");
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out=response.getWriter();

        out.println("<html>");
        out.println("<head> ");
        out.println(
                "<script type=\"text/javascript\" src=\"https://www.google.com/jsapi\"></script>" +
                        "<script type=\"text/javascript\">\n" +
                        "      google.load(\"visualization\", \"1\", {packages:[\"corechart\"]});\n" +
                        "      google.setOnLoadCallback(drawChart);\n" +
                        "      function drawChart() {\n" +
                        "        var data = new google.visualization.DataTable();\n" +
                        "        data.addColumn('number', 'Iteration');\n" +
                        "        data.addColumn('number', 'Time (ms)');\n" +
                        "        data.addRows([\n");

        String fibonacci_argument=" ";
        int completed=0;
        int uncompleted=0;
        int total_iterations=0;

        try {
            PreparedStatement pst= null;
            pst = con.prepareStatement("select * from fibo_results");
            pst.clearParameters();
            ResultSet rs=pst.executeQuery();
            while(rs.next()){
                if (rs.getString(2) != null) {
                    completed++;
                    fibonacci_argument = rs.getString(2);
                    //out.write("<tr><td>"+i+"</td>");
                    out.println("[" + rs.getString(1)+", "+rs.getString(4)+"],");
                } else {
                    uncompleted++;
                    out.println("[" + rs.getString(1)+", 0],");
                }
                total_iterations = completed + uncompleted;
            }
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


        String charttype = "ScatterChart";
        out.println("]);\n\n" +
                "        var options = {\n" +
                //"        width: 1000, height: 240," +
                "        title: 'Fibonacci(" + fibonacci_argument + ") Processing Time', legend: {position: 'none'}, vAxis: {title: 'Time (ms)'}, hAxis: {title: 'Iteration'}};\n" +
                "        \n" +
                "        var chart = new google.visualization."+charttype+"(document.getElementById('chart_div'));\n" +
                "        chart.draw(data, options);\n" +
                "      }\n" +
                "    </script>");
        out.println("<title>JMS Load Testing Utility</title> " +
                "<style type=\"text/css\"> " +
                "body { " +
                "font-family: verdana, tahoma, sans-serif; " +
                "text-align: center; " +
                "} " +
                "h1 { " +
                "margin-top: 50px; " +
                "color: #cc6666; " +
                "} " +
                ".message_text { " +
                "color: #996666; " +
                "} " +
                "</style> ");

        if (uncompleted > 0) {
            out.write("<meta http-equiv=\"Refresh\" content=\"2; URL=ViewResults\">");
        }
        out.write("</head>");


        //out.write("<meta http-equiv=\"refresh\" content=\"2\" > ");
        //out.write("<hr/><a href='FibonacciMDBServletClient'>This page will refresh every 2 seconds...</a> ");
        //out.write("<hr/><a href='index.jsp'>Rerun</a> ");

        out.println("<body>" +
                "<h1><A href=\"index.jsp\">JMS Load Testing Utility</a></h1> " +
                "<form method=post action=\"FibonacciMDBServletClient\">" +
                "<p class=\"message_text\">This utility will send the specified number of messages to a JMS" +
                "<br>service, which will concurrently process each message in the" +
                "<br>time it takes to calculate the specified Fibonacci sequence.</p>" +
                "<p>Fibonacci value to compute: <input type=text name=value value=\"" + fibonacci_argument +"\" size=5>\n" +
                "<p>Number of times to compute it (concurrent MessageDriven EJBs): <input type=text name=iterations value=\"" + total_iterations +"\" size=5> </p>\n" +
                "<input type=submit value=\"Submit\"></p>" +
                "</form>" +
                "<HR><H3>Results:</H3><P>");
        out.write("<form method=get action=\"ViewResults\">");
        out.write("<input type=submit value=\"Refresh\"></form>");

        try {
            PreparedStatement pst=con.prepareStatement("select * from fibo_results");
            pst.clearParameters();
            ResultSet rs=pst.executeQuery();
            out.println("<p>Server has processed " + completed + " out of " + total_iterations + " messages.</p>");
            out.println("<p><div id=\"chart_div\"></div></p>");
            out.write("<table border=\"1\"> <tr><th>Msg Number</th><th>Fib("+fibonacci_argument+")</th><th>Processing Time(ms)</th></tr>");
            while(rs.next()){
                //out.write("<tr><td>"+i+"</td>");
                out.write("<td>"+rs.getString(1)+"</td>");
                //out.write("<td>"+rs.getString(2)+"</td>");
                out.write("<td>"+rs.getString(3)+"</td>");
                out.write("<td>"+rs.getString(4)+"</td></tr>");
                //out.write(", "+rs.getString(5));
            }
            out.write("</tr></table>");
            out.write("</html>");


//            Statement stmt = con.createStatement();
//            rs = stmt.executeQuery("select * from fibo_results");
//            while(rs.next()){
//                out.write("<hr><br/>"+rs.getString(1));
//                out.write(", "+rs.getString(2));
//                out.write(", "+rs.getString(3));
//            }

        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
    }

}
