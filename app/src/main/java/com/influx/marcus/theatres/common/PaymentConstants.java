package com.influx.marcus.theatres.common;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Mani on 20-06-2017.
 */

public class PaymentConstants {
    static String VISA = "VISA";
    static String LASER = "LASER";
    static String DISCOVER = "DISCOVER";
    static String MAES = "MAES";
    static String MAST = "MAST";
    static String AMEX = "AMEX";
    static String DINR = "DINR";
    static String JCB = "JCB";
    static String SMAE = "SMAE";
    static String RUPAY = "RUPAY";
    static String MASTERCARD = "MASTERCARD";

    public static String cbVersion;
    /**
     * Known Sate bank maestro bins
     * helps us to creditCardvalidate cvv expmon, expyr,
     * SMAE cards do not have CVV, Expiry month, Expiry year.
     */
    public static Set<String> SBI_MAES_BIN;

    //analytics key for device and event analytics
    private static String keyAnalyticsUtil;

    static {
        SBI_MAES_BIN = new HashSet<>();
        SBI_MAES_BIN.add("504435");
        SBI_MAES_BIN.add("504645");
        SBI_MAES_BIN.add("504775");
        SBI_MAES_BIN.add("504809");
        SBI_MAES_BIN.add("504993");
        SBI_MAES_BIN.add("600206");
        SBI_MAES_BIN.add("603845");
        SBI_MAES_BIN.add("622018");
        SBI_MAES_BIN.add("504774");
    }

    /**
     * includes length validation also
     *
     * @param cardNumber any card number
     * @return true if valid else false
     */
    public Boolean validateCardNumber(String cardNumber) {
        if (cardNumber.length() < 12) {
            return false;
        } else if (getIssuer(cardNumber).contentEquals(PaymentConstants.RUPAY) && cardNumber.length() == 16) {
            return luhn(cardNumber);
        } else if (getIssuer(cardNumber).contentEquals(PaymentConstants.VISA) && cardNumber.length() == 16) {
            return luhn(cardNumber);
        } else if (getIssuer(cardNumber).contentEquals(PaymentConstants.MAST) && cardNumber.length() == 16) {
            return luhn(cardNumber);
        } else if ((getIssuer(cardNumber).contentEquals(PaymentConstants.MAES) || getIssuer(cardNumber).contentEquals(PaymentConstants.SMAE)) && cardNumber.length() >= 12 && cardNumber.length() <= 19) {
            return luhn(cardNumber);
        } else if (getIssuer(cardNumber).contentEquals(PaymentConstants.DINR) && cardNumber.length() == 14) {
            return luhn(cardNumber);
        } else if (getIssuer(cardNumber).contentEquals(PaymentConstants.AMEX) && cardNumber.length() == 15) {
            return luhn(cardNumber);
        }
      /*  else if (getIssuer(cardNumber).contentEquals(PaymentConstants.JCB) && cardNumber.length() == 16) {
            return luhn(cardNumber);
        }*/
        return false;
    }

    /**
     * Universal luhn validation for Credit, Debit cards.
     *
     * @param cardNumber any card number
     * @return true if valid else false
     */
    public Boolean luhn(String cardNumber) {
        int sum = 0;
        boolean alternate = false;
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(cardNumber.substring(i, i + 1));
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n = (n % 10) + 1;
                }
            }
            sum += n;
            alternate = !alternate;
        }
        if (sum % 10 == 0) {
            return true;
        }
        return false;
    }

    public String getIssuer(String mCardNumber) {
        if (mCardNumber.startsWith("4")) {
            return PaymentConstants.VISA;
        } else if (mCardNumber.startsWith("5")) {
            return PaymentConstants.MAST;
        } else if (mCardNumber.startsWith("6")) {
            return PaymentConstants.DISCOVER;
        } else {
            return PaymentConstants.JCB;
        }
        /*else if (mCardNumber.matches("^508[5-9][0-9][0-9]|60698[5-9]|60699[0-9]|607[0-8][0-9][0-9]|6079[0-7][0-9]|60798[0-4]|(?!608000)608[0-4][0-9][0-9]|608500|6521[5-9][0-9]|652[2-9][0-9][0-9]|6530[0-9][0-9]|6531[0-4][0-9]")) {
            return PaymentConstants.RUPAY;
        } else if (mCardNumber.matches("^((6304)|(6706)|(6771)|(6709))[\\d]+")) {
            return PaymentConstants.LASER;
        } else if (mCardNumber.matches("6(?:011|5[0-9]{2})[0-9]{12}[\\d]+")) {
            return PaymentConstants.LASER;
        } else if (mCardNumber.matches("^6(?:011|5[0-9]{2})[0-9]{3,}$")) {
            return PaymentConstants.DISCOVER;
        } else if (mCardNumber.matches("(5[06-8]|6\\d)\\d{14}(\\d{2,3})?[\\d]+") || mCardNumber.matches("(5[06-8]|6\\d)[\\d]+") || mCardNumber.matches("((504([435|645|774|775|809|993]))|(60([0206]|[3845]))|(622[018])\\d)[\\d]+")) {
            if (mCardNumber.length() >= 6) { // wel we have 6 digit bin
                if (SBI_MAES_BIN.contains(mCardNumber.substring(0, 6))) {
                    return PaymentConstants.SMAE;
                }
            }
            return PaymentConstants.MAES;
        } else if (mCardNumber.matches("^5[1-5][\\d]+")) {
            return PaymentConstants.MAST;
        } else if (mCardNumber.matches("^3[47][\\d]+")) {
            return PaymentConstants.AMEX;
        } else if (mCardNumber.startsWith("36") || mCardNumber.matches("^30[0-5][\\d]+") || mCardNumber.matches("2(014|149)[\\d]+")) {
            return PaymentConstants.DINR;
        } else if (mCardNumber.matches("^35(2[89]|[3-8][0-9])[\\d]+")) {
            return PaymentConstants.JCB;
        }*/
    }

    public boolean validateCvv(String cardNumber, String cvv) {
        String issuer = getIssuer(cardNumber);
        if (issuer.contentEquals(PaymentConstants.SMAE)) {
            return true;
        } else if (issuer.contentEquals("")) {
            return false;
        } else if (issuer.contentEquals(PaymentConstants.AMEX) & cvv.length() == 4) {
            return true;
        } else if (!issuer.contentEquals(PaymentConstants.AMEX) && cvv.length() == 3) {
            return true;
        }
        return false;
    }


    private static String validatecardStr(String cardStr) {

        String cardtype = "";
        if ((cardStr.substring(0, 1) == "4") && (cardStr.length() >= 13 || cardStr.length() <= 16)) {
            //visa
            //   self.imgVwCard.image = UIImage(named: "visa")
            cardtype = "001";
        } else if (cardStr.length() >= 2 && (cardStr.substring(0, 2)) == "51" ||
                cardStr.substring(0, 2) == "52" ||
                cardStr.substring(0, 2) == "53" ||
                cardStr.substring(0, 2) == "54" && (cardStr.length() <= 16)) {
            //mastercard
            cardtype = "002";
            //  self.imgVwCard.image = UIImage(named: "mastercard")
        } else if (cardStr.length() > 2 && (cardStr.substring(0, 2)) == "34" || cardStr.substring(0, 2) == "37" && cardStr.length() <= 16) {
            //amex
            cardtype = "003";
            // self.imgVwCard.image = UIImage(named: "amex")
        } else if (cardStr.length() > 4 && (cardStr.substring(0, 4)) == "5018" ||
                cardStr.substring(0, 4) == "5020" ||
                cardStr.substring(0, 4) == "5038" ||
                cardStr.substring(0, 4) == "6220" ||
                cardStr.substring(0, 4) == "6304" ||
                cardStr.substring(0, 4) == "6759" ||
                cardStr.substring(0, 4) == "6761" &&
                        (cardStr.length() <= 16)) {
            //mastero international
            cardtype = "042";
            //   self.imgVwCard.image = UIImage(named: "mastero-international")
        } else if (cardStr.length() > 2 && (cardStr.substring(0, 2) == "35") && (cardStr.length() <= 16)) {
            //jcb
            cardtype = "007";
            //  self.imgVwCard.image = UIImage(named: "jcb")
        } else {
            cardtype = "000";
            //  self.imgVwCard.image = UIImage(named: "IconCard")
        }
        System.out.println("cardtype" + cardtype);
        return "";
    }
}
