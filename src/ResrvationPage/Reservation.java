package ResrvationPage;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.regex.Pattern;

import static ResrvationPage.error.*;

public class Reservation extends JFrame {
    private JTextField tfName;
    private JTextField tfAge;
    private JTextField tfGender;
    private JTextField tfMail;
    private JTextField tfTrainNo;
    private JTextField tfTrainName;
    private JTextField tfCoach;
    private JTextField tfFrom;
    private JTextField tfTo;
    private JTextField tfDateOfTravel;
    private JButton submitButton;
    private JTextField tfMobile;
    private JPanel mainPanel;

    public Reservation() {
        super();
        setContentPane(mainPanel);
        setLocation(250, 100);
        setMinimumSize(new Dimension(600, 600));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        submitButton.addActionListener(e -> get());
    }

    private void get() {
        String name = tfName.getText().toLowerCase();
        String age = tfAge.getText();
        String gender = tfGender.getText();
        String email = tfMail.getText();
        String trainNo = tfTrainNo.getText();
        String trainName = tfTrainName.getText();
        String coach = tfCoach.getText();
        String from = tfFrom.getText();
        String to = tfTo.getText();
        String dateOfJourney = tfDateOfTravel.getText();
        String mobile = tfMobile.getText();
        String out;
        if (name.isEmpty() || age.isEmpty() || gender.isEmpty() || email.isEmpty() || trainNo.isEmpty() || trainName.isEmpty() || coach.isEmpty() || from.isEmpty() || to.isEmpty() || dateOfJourney.isEmpty() || mobile.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "found empty fields",
                    "please try again later",
                    JOptionPane.ERROR_MESSAGE);
        } else {
            out = validation(name, age, gender, email, trainNo, dateOfJourney, mobile);
            if (out == null) {
                String Db_Server_Url = "jdbc:mysql://localhost:3306";
                String Db_url = "jdbc:mysql://localhost:3306/reservation";
                String user = "root";
                String password = "santhu";
                try {
                    Connection con = DriverManager.getConnection(Db_Server_Url, user, password);
                    Statement st = con.createStatement();
                    st.executeUpdate("CREATE DATABASE IF NOT EXISTS reservation");
                    st.close();
                    con.close();

                    con = DriverManager.getConnection(Db_url, user, password);
                    st = con.createStatement();
                    String sql = """
                            CREATE TABLE IF NOT EXISTS reservation (
                                                id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
                                                name VARCHAR(200) NOT NULL,
                                                age VARCHAR(200) NOT NULL,
                                                gender VARCHAR(200) NOT NULL,
                                                email VARCHAR(200) NOT NULL UNIQUE,
                                                trainno VARCHAR(200) NOT NULL,
                                                trainname VARCHAR(200) NOT NULL,
                                                coach VARCHAR(200) NOT NULL,
                                                fromloc VARCHAR(200) NOT NULL,
                                                toloc VARCHAR(200) NOT NULL,
                                                dateofjourney VARCHAR(200) NOT NULL,
                                                mobile VARCHAR(200) NOT NULL UNIQUE)""";
                    st.executeUpdate(sql);


                    st = con.createStatement();
                    sql = "INSERT INTO reservation(name, age, gender, email, trainno, trainname, coach, fromloc, toloc, dateofjourney, mobile)" + "VALUES(?,?,?,?,?,?,?,?,?,?,?)";
                    PreparedStatement preparedStatement = con.prepareStatement(sql);
                    preparedStatement.setString(1, name);
                    preparedStatement.setString(2, age);
                    preparedStatement.setString(3, gender);
                    preparedStatement.setString(4, email);
                    preparedStatement.setString(5, trainNo);
                    preparedStatement.setString(6, trainName);
                    preparedStatement.setString(7, coach);
                    preparedStatement.setString(8, from);
                    preparedStatement.setString(9, to);
                    preparedStatement.setString(10, dateOfJourney);
                    preparedStatement.setString(11, mobile);
                    int count = preparedStatement.executeUpdate();
                    if (count > 0) {
                        JOptionPane.showMessageDialog(this, "success ful", "reserved", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Database Error",
                                "please try again later",
                                JOptionPane.ERROR_MESSAGE);
                    }
                    st.close();
                    con.close();

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        out,
                        "please try again later",
                        JOptionPane.ERROR_MESSAGE);
            }

        }
    }

    private String validation(String name, String age, String gender, String email, String trainNo, String dateOfJourney, String mobile) {
        String out = "";
        // name check
        if (name.length() != name.replaceAll("[^\\sa-z]", "").length()) {
            out += NAME_SHOULD_CONTAIN_ONLY_LOWER_CASE_ALPHABETS + "\n";
        }

        // age check
        try {
            if (Integer.parseInt(age) < 18 || Integer.parseInt(age) > 100) {
                out += AGE_SHOULD_BE_GREATER_THAN_18_AND_LESS_THAN_100 + "\n";
            }
        } catch (NumberFormatException e) {
            out += AGE_SHOULD_ONLY_BE_DIGITS + "\n";
        }

        //gender check
        boolean genCheck = gender.equalsIgnoreCase("male") ||
                gender.equalsIgnoreCase("female") ||
                gender.equalsIgnoreCase("m") ||
                gender.equalsIgnoreCase("f") ||
                gender.equalsIgnoreCase("others");
        if (!genCheck) {
            out += GENDER_SHOULD_BE_MALE_FEMALE_OR_OTHERS + "\n";
        }

        //email check
        final Pattern VALID_EMAIL_ADDRESS_REGEX =
                Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        if (!VALID_EMAIL_ADDRESS_REGEX.matcher(email).matches()) {
            out += EMAIL_SHOULD_BE_OF_ + "abc@gmail.com format" + "\n";
        }

        // train no check
        if (trainNo.length() != 6) {
            out += TRAIN_NO_MUST_BE_6_DIGIT + "\n";
        }
        if(trainNo.replaceAll("[0-9]","").length() != 0){
            out += TRAIN_NO_MUST_CONTAIN_DIGITS + "\n";
        }
        // date of journey check
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate d1;
        LocalDate d2;
        try {
            d1 = LocalDate.parse(dateOfJourney, formatter);
            d2 = LocalDate.parse(formatter.format(LocalDate.now()), formatter);
            out += ChronoUnit.DAYS.between(Objects.requireNonNull(d2), d1) >= 1 ? "" : DATE_MUST_BE_GREATER_THAN_TODAY_S_DATE + "\n";
        } catch (Exception e) {
            out += INVALID_DATE_LEADING_TO_EXCEPTION_PLEASE_CHECK_FORMAT_DATE_MONTH_YEAR + "\n";
        }


        // mobile no check
            if(mobile.replaceAll("[^0-9]", "").length() == 0){
                out += MOBILE_NO_MUST_CONTAIN_ONLY_DIGITS + "\n";
            }
            if (mobile.length() != 10) {
                out += MOBILE_NO_SHOULD_CONTAIN_10_DIGITS_ONLY + "\n";
            }
            boolean numberStartCheck = mobile.charAt(0) == '9' ||
                    mobile.charAt(0) == '8' ||
                    mobile.charAt(0) == '7' ||
                    mobile.charAt(0) == '6';
            if(!numberStartCheck)
                out += MOBILE_NO_STARTS_WITH_9_OR_8_OR_7_OR_6 + "\n";


        return out.isEmpty() ? null : out;
    }


    public static void main(String[] args) {
        new Reservation();
    }
}

enum error {
    NAME_SHOULD_CONTAIN_ONLY_LOWER_CASE_ALPHABETS,
    AGE_SHOULD_BE_GREATER_THAN_18_AND_LESS_THAN_100,
    AGE_SHOULD_ONLY_BE_DIGITS,
    GENDER_SHOULD_BE_MALE_FEMALE_OR_OTHERS,
    EMAIL_SHOULD_BE_OF_,
    TRAIN_NO_MUST_BE_6_DIGIT,
    TRAIN_NO_MUST_CONTAIN_DIGITS,
    INVALID_DATE_LEADING_TO_EXCEPTION_PLEASE_CHECK_FORMAT_DATE_MONTH_YEAR,
    DATE_MUST_BE_GREATER_THAN_TODAY_S_DATE,
    MOBILE_NO_SHOULD_CONTAIN_10_DIGITS_ONLY,
    MOBILE_NO_MUST_CONTAIN_ONLY_DIGITS,
    MOBILE_NO_STARTS_WITH_9_OR_8_OR_7_OR_6
}
