package it.unisa.model.user;

import it.unisa.model.DriverManagerConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class UserDAO {

    //Aggiunge un utente con privilegi standard
    public static boolean addStandardUser(String codice_fiscale, String email, String password) throws SQLException{
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        String statement = "insert into Utente(email,password,codice_fiscale,is_admin) VALUES (?,?,?,?)";
        try {
            connection = DriverManagerConnectionPool.getConnection();
            preparedStatement = connection.prepareStatement(statement);
            preparedStatement.setString(1,email);
            preparedStatement.setString(2,password);
            preparedStatement.setString(3,codice_fiscale);
            preparedStatement.setBoolean(4,false);

            preparedStatement.executeUpdate();
            connection.commit();

            return true;
        }finally {
            try{
                if(preparedStatement != null) preparedStatement.close();
            } finally {
                DriverManagerConnectionPool.releaseConnection(connection);
            }
        }
    }

    // Controlla se il cliente con il parametro specificato è registrato
    public static boolean isUser(String codice_fiscale) throws SQLException {
        ArrayList<UserBean> usersList = (ArrayList<UserBean>) UserDAO.allUsers();
        for(UserBean userBean: usersList){
            if(userBean.getCodicefiscale().equals(codice_fiscale)) return true;
        }
        return false;
    }


    //Ritorna la lista di tutti gli utenti
    public static Collection<UserBean> allUsers() throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ArrayList<UserBean> usersList = new ArrayList<UserBean>();

        String statement = "SELECT * from Utente ;";
        try {
            connection = DriverManagerConnectionPool.getConnection();
            preparedStatement = connection.prepareStatement(statement);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                UserBean user = new UserBean();
                user.setCodicefiscale(resultSet.getString("codice_fiscale"));
                user.setEmail(resultSet.getString("email"));
                user.setPassword(resultSet.getString("password"));
                user.setAdmin(resultSet.getBoolean("is_admin"));

                usersList.add(user);
            }

            return  usersList;

        }finally {
            try{
                if(preparedStatement != null) preparedStatement.close();
            } finally {
                DriverManagerConnectionPool.releaseConnection(connection);
            }
        }
    }
}