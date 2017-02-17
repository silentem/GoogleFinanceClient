package eu.toloka.tradre.time;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author Viktor Korovyanko <viktor.koro@gmail.com>
 */
public class TimingUtils {

    public static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    public static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd MMM HH:mm");
//    public static final SimpleDateFormat dateSimpleFormat = new SimpleDateFormat("dd MMM");
      //todo
    public static final long offsetInMillis = 25200000l;

    public static Date getDayBegin(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 30);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date getCurrentDate(){
        Calendar calendar = Calendar.getInstance();//todo
        calendar.add(Calendar.HOUR, - 5 - TimeZone.getDefault().getRawOffset()/(60*60*1000));
        return calendar.getTime();
    }
    
//    public static void main(String[] args){
//        Long time = System.currentTimeMillis();
//        time = time - time % (24 * Alert.MILLIS_HOUR);
//        System.out.println(new Date(time));
//        time -= Alert.OFFSET_HOURS * Alert.MILLIS_HOUR;
//        System.out.println(new Date(time));
//    }
}
