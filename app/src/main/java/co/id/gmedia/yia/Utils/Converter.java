package co.id.gmedia.yia.Utils;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Converter {

    private static SimpleDateFormat d_format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private static SimpleDateFormat d_format_string = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());

    public static String doubleToRupiah(double value){
        NumberFormat rupiahFormat = NumberFormat.getInstance(Locale.GERMANY);
        return "Rp " + rupiahFormat.format(Double.parseDouble(String.valueOf(value)));
    }

    public static String getDateString(Date date){
        return d_format_string.format(date);
    }

    public static Date stringDToDate(String date){
        try {
            return d_format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    public static String DToString(Date date){
        return d_format.format(date);
    }
}
