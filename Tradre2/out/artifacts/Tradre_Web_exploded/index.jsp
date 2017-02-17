<%@ page import="eu.ScriptRunner" %>
<%@ page import="eu.CalcRunner" %>
<%
    String symbol = request.getParameter("symbol");


    if(symbol == null){
        java.util.List<String> list = new java.util.ArrayList<String>();
        new Thread(new CalcRunner(list, "mspaint.exe")).start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace(); 
        }

        if(list.size() > 0){
            out.print(list.get(0));
        }
    }else{
        new Thread(new ScriptRunner(symbol, "C:\\Perl\\bin\\perl.exe", "C:\\Projects\\Tradre\\perl\\insert.pl")).start();
    }
%>
