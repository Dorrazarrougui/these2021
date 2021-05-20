package com.these.school.utils;

import android.text.InputFilter;
import android.widget.EditText;

import java.util.regex.Pattern;

/**
 * Created by IT on 15/07/2017.
 */

public class InputValidations {

    public static String phoneReg = "^0[0-9]{9}";
    public static String dateReg = "^.{1,2}-.{1,2}-.{4}$";
    public static String nameReg = "^(([a-zA-Z\u0600-\u06FF ])*)$";
    public static String nameDigitReg = "^(([a-zA-Z\u0600-\u06FF0-9 ])*)$";
    public static String timeReg = "^.{1,2}:.{2}$";
    public static String emailReg = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private static String requiredMsg = "Obligatoire";
    private static String DateMsg = "##/##/#### ou ##-##-####";
    private static String PhoneMsg = "Que des chiffres";
    private static String NameMsg = "Lettres et espaces";
    private static String NameDigitMsg = "Lettres, espaces et chiffres";
    private static String TimeMsg = "##:##";
    private static String EmailMsg = "Email non valide";


    public static boolean isPhoneNumber(EditText editText, boolean required){
        return isValid(editText, phoneReg, PhoneMsg, required);
    }

    public static boolean isBirthDate(EditText editText, boolean required){
        return isValid(editText, dateReg, DateMsg, required);
    }

    public static boolean isName(EditText editText, boolean required){
        return isValid(editText, nameReg, NameMsg, required);
    }

    public static boolean isNameDigit(EditText editText, boolean required){
        return isValid(editText, nameDigitReg, NameDigitMsg, required);
    }

    public static boolean isTime(EditText editText, boolean required){
        return isValid(editText, timeReg, TimeMsg, required);
    }

    public static boolean isEmail(EditText editText, boolean required){
        return isValid(editText, emailReg, EmailMsg, required);
    }

    public static boolean isValid(EditText editText, String reg, String msg, boolean required){
        String text = editText.getText().toString().trim();
        editText.setError(null);
        if(required && !hasText(editText)) {
            editText.setError(requiredMsg);
            return false;
        }
        if(required && !Pattern.matches(reg, text)){
            editText.setError(msg);
            return false;
        }
        return  true;
    }

    public static boolean hasText(EditText editText){
        String text = editText.getText().toString().trim();
        editText.setError(null);
        if(text.length()==0){
            editText.setError(requiredMsg);
            return false;
        }
        return  true;
    }

    public static boolean trueLength(EditText editText, int min){
        editText.setError(null);
        if(editText.getText().length()<min){
            editText.setError("Minimum "+String.valueOf(min)+" caractères.");
            return false;
        }else{
            return true;
        }
    }
    public static void setEditMaxLength(EditText editText, int max){
        InputFilter[] inFilter = new InputFilter[1];
        inFilter[0] = new InputFilter.LengthFilter(max);
        editText.setFilters(inFilter);
    }
}
