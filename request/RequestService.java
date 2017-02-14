package request;

import chart.BarData;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Влад on 14.02.2017.
 */
public class RequestService implements Reguest {
    private final String request = "https://www.google.com/finance/getprices?";
    private String company;
    private Integer interval;
    private GregorianCalendar date = new GregorianCalendar();


    private String generateRequestString(){
        StringBuilder requestString = new StringBuilder();
        requestString.append(request).append("q="+ this.company).append("&i=" + this.interval);
        return requestString.toString();
    }
/*    public RequestService(String company, String interval){

    }*/

    public List<BarData> getBarData(){

        List<String> result = makeRequest(generateRequestString());
        List<BarData> barDatas = new ArrayList<BarData>(result.size());

        for (String cortage: result) {
            barDatas.add(parseBarFromString(cortage));
        }
        return barDatas;
    }

    public BarData parseBarFromString(String bar){
        GregorianCalendar tmpCalendar = new GregorianCalendar();
        String[] params = bar.split(",");
        if(params[0].startsWith("a")){
            String time = params[0].substring(1);
            Date d = new Date(Long.parseLong(time));
            DateFormat format = new SimpleDateFormat("hh:mm");
            date.setTime(d);
            tmpCalendar = (GregorianCalendar)date.clone();
        }else{
            tmpCalendar = (GregorianCalendar)date.clone();
            tmpCalendar = (GregorianCalendar) date.clone();
            tmpCalendar.add(Calendar.MINUTE, (interval/60)*Integer.parseInt(params[0]));
        }
        return new BarData((GregorianCalendar)tmpCalendar.clone(),
                Double.parseDouble(params[1]),
                Double.parseDouble(params[2]),
                Double.parseDouble(params[3]),
                Double.parseDouble(params[4]),1);
    }

    public List<String> makeRequest(String uri) {
        List<String> result = new ArrayList<String>(1000);

        BufferedReader reader = null;

        try{
            URL url = new URL(generateRequestString());
            reader = new BufferedReader(new InputStreamReader(url.openStream()));

            int coutRow = 1;

            while(coutRow++ < 8){
                reader.readLine();
            }

            String resultString;
            while ((resultString = reader.readLine())!= null){
                result.add(resultString);
            }
            return result;
        }catch (Exception ex){
            System.out.println("Make request " + ex.getMessage());
        }finally {
            if(reader!= null){
                try{
                    reader.close();
                }catch(IOException ex){
                    System.out.println(ex.getMessage());
                }
            }
        }


        return result;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }
}
