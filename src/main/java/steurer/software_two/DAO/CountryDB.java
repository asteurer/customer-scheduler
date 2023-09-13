package steurer.software_two.DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import steurer.software_two.model.Country;
import steurer.software_two.model.Customer;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class simplifies interacting with the countries table in the database.
 */
public class CountryDB {
    /**
     * @return This returns all countries from the database.
     */
    public static ObservableList<Country> getAllCountries(){
        ObservableList<Country> countryList = FXCollections.observableArrayList();

        try {
            ResultSet rs = JDBC.sendQuery("SELECT * from countries");

            while(rs.next()) {
                int id = rs.getInt("Country_ID");
                String name = rs.getString("Country");

                countryList.add(new Country(id, name));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return countryList;
    }
}
