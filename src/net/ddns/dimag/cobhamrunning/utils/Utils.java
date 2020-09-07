package net.ddns.dimag.cobhamrunning.utils;

import net.ddns.dimag.cobhamrunning.models.Measurements;
import net.ddns.dimag.cobhamrunning.models.Tests;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public final static long ONE_MILLISECOND = 1;
    public final static long MILLISECONDS_IN_A_SECOND = 1000;

    public final static long ONE_SECOND = 1000;
    public final static long SECONDS_IN_A_MINUTE = 60;

    public final static long ONE_MINUTE = ONE_SECOND * 60;
    public final static long MINUTES_IN_AN_HOUR = 60;

    public final static long ONE_HOUR = ONE_MINUTE * 60;
    public final static long HOURS_IN_A_DAY = 24;
    public final static long ONE_DAY = ONE_HOUR * 24;
    public final static long DAYS_IN_A_YEAR = 365;

    public static final Comparator<Measurements> COMPARE_BY_MEASDATE = new Comparator<Measurements>() {
        @Override
        public int compare(Measurements meas1, Measurements meas2) {
            return meas2.getMeasDate().compareTo(meas1.getMeasDate());
        }
    };

    public static final Comparator<Tests> COMPARE_BY_TESTDATE = new Comparator<Tests>() {
        @Override
        public int compare(Tests test1, Tests test2) {
            return test2.getDateTest().compareTo(test1.getDateTest());
        }
    };

    public static final Comparator<Measurements> COMPARE_BY_MEASNUM = new Comparator<Measurements>() {
        @Override
        public int compare(Measurements meas1, Measurements meas2) {
            return meas1.getMeasureNumber() - meas2.getMeasureNumber();
        }
    };

    public static <T> Set<T> asSortedSet(Set<T> c, Comparator<? super T> comparator){
        return listToSet(asSortedList(setToList(c), comparator));
    }

    public static <T> List<T> asSortedList(Collection<T> c, Comparator<? super T> comparator){
        List<T> list = new ArrayList<T>(c);
        Collections.sort(list, comparator);
        return list;
    }

    public static <T> List<T> setToList(Set<T> currSet){
         return new ArrayList<T>(currSet);
    }

    public static <T> Set<T> listToSet(List<T> currList){
        return new HashSet<T>(currList);
    }

    public static JSONObject jsonToObject(String val) throws ParseException {
        try {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(val);
            return json;
        } catch (ParseException e) {
            throw e;
        }
    }

    public static String formatHMSM(Number n) {
        String res = "";
        if (n != null) {
            long duration = n.longValue() * 1000;

            duration /= ONE_MILLISECOND;
            int milliseconds = (int) (duration % MILLISECONDS_IN_A_SECOND);
            duration /= ONE_SECOND;
            int seconds = (int) (duration % SECONDS_IN_A_MINUTE);
            duration /= SECONDS_IN_A_MINUTE;
            int minutes = (int) (duration % MINUTES_IN_AN_HOUR);
            duration /= MINUTES_IN_AN_HOUR;
            int hours = (int) (duration % HOURS_IN_A_DAY);
            duration /= HOURS_IN_A_DAY;
            int days = (int) (duration % DAYS_IN_A_YEAR);
            duration /= DAYS_IN_A_YEAR;
            int years = (int) (duration);

            if (days == 0) {
                res = String.format("%02d:%02d:%02d", hours, minutes, seconds);
            } else {
                res = String.format("%d days %02d:%02d:%02d", days, hours, minutes, seconds);
            }
        }
        return res;
    }

    public static String getFormattedDate(Date date){
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dt.format(date);
    }

    public static Set<Measurements> getMeasListByTest(RmvUtils rmvUtils, Tests test) throws CobhamRunningException {
        Set <Measurements> measList = new HashSet<>();
        for(HashMap<String, Object> measObj: rmvUtils.getMeasuresById(test.getHeaderID())){
            Measurements  meas = new Measurements();
            meas.setTest(test);
            meas.setMeasName((String) measObj.get("Description"));
            meas.setMeasureNumber((Integer) measObj.get("MeasureNumber"));
            meas.setMeasMax((String) measObj.get("MaxLim"));
            meas.setMeasMin((String) measObj.get("MinLim"));
            meas.setMeasVal(((String) measObj.get("Result")));
            meas.setMeasDate((Date) measObj.get("MeasureDate"));
            meas.setMeasStatus((Integer) measObj.get("TestStatus"));
            measList.add(meas);
        }
        return measList;
    }

    public static HashMap<Long, Set <Measurements>> getMeasListByHeaderList(RmvUtils rmvUtils, List<Long> headerList)
            throws CobhamRunningException {
        HashMap<Long, Set <Measurements>> measMap = new HashMap<>();
        Set <Measurements> measList = new HashSet<>();
        Long oldHeaderId = null;
        Long currHeaderId = null;
        for(HashMap<String, Object> measObj: rmvUtils.getMeasuresByHeaderList(headerList)){
            currHeaderId = (Long) measObj.get("HeaderID");
            if (oldHeaderId == null){
                oldHeaderId = currHeaderId;
            } else if (!oldHeaderId.equals(currHeaderId)){
                measMap.put(oldHeaderId, measList);
                measList = new HashSet<>();
                oldHeaderId = currHeaderId;
            }
            Measurements  meas = new Measurements();
            meas.setMeasName((String) measObj.get("Description"));
            meas.setMeasureNumber((Integer) measObj.get("MeasureNumber"));
            meas.setMeasMax((String) measObj.get("MaxLim"));
            meas.setMeasMin((String) measObj.get("MinLim"));
            meas.setMeasVal(((String) measObj.get("Result")));
            meas.setMeasDate((Date) measObj.get("MeasureDate"));
            meas.setMeasStatus((Integer) measObj.get("TestStatus"));
            measList.add(meas);
        }
        measMap.put(oldHeaderId, measList);
        return measMap;
    }

    public static String getComputerName() {
        Map<String, String> env = System.getenv();
        if (env.containsKey("COMPUTERNAME"))
            return env.get("COMPUTERNAME");
        else if (env.containsKey("HOSTNAME"))
            return env.get("HOSTNAME");
        else
            return "Unknown Computer";
    }

    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
    }

    public static boolean isItAsis(String s){
        Pattern pattern = Pattern.compile("^[A-Z0-9]{4}$");
        Matcher matcher = pattern.matcher(s.toUpperCase());
        return matcher.matches();
    }

    public static void getPrinterStatus(String printerName){
        ProcessBuilder builder = new ProcessBuilder("powershell.exe", "get-wmiobject -class win32_printer | Select-Object Name, PrinterState, PrinterStatus | where {$_.Name -eq '"+printerName+"'}");

        String fullStatus = null;
        Process reg;
        builder.redirectErrorStream(true);
        try {
            reg = builder.start();
            fullStatus = getStringFromInputStream(reg.getInputStream());
            reg.destroy();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        System.out.print(fullStatus);
    }

    private static String getStringFromInputStream(InputStream inputStream) {
        String foReturn = null;
        try {
            StringWriter writer = new StringWriter();
            IOUtils.copy(inputStream, writer, StandardCharsets.UTF_8);
            foReturn = writer.toString();
        } catch (IOException e){
            e.printStackTrace();
        }
        return foReturn;
    }

}
