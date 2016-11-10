/*
 * Universidad Catolica - Seguridad - Obligatorio.
 */
package main;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Esta clase tiene varias funcionalidades utilies.
 * @author Masoller, Artegoytia, Galleto, Olivera.
 */
public class Utiles {
    public static String getHoraActual() {
        String hora = "";
        
        Calendar calendar = Calendar.getInstance();
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);
        hora = String.valueOf(hours) + String.valueOf(minutes) + String.valueOf(seconds);
        
        return hora;
    }
    
    public static String getFechaActual() {
        String fecha = "";
        
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        fecha = dateFormat.format(date); 
        
        return fecha;
    }
    
    public static boolean validarPass (String password) {
        boolean esValida = false;
        int cantidadNumeros = 0;
   
        String[] caracteres = new String[16];
        for (int i = 0; i < caracteres.length; i++) {
            caracteres[i] = String.valueOf(password.charAt(i));
        }
        
        // Checkeo que la pass tenga número/s
        for (int i = 0; i < caracteres.length; i++) {
            if(caracteres[i].matches(".*\\d.*")){
                cantidadNumeros ++;
            } 
        }
        
        if (cantidadNumeros > 0) {
            Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(password);
            esValida = m.find();
        } 
        return esValida;
    }
}
