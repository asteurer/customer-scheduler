package steurer.software_two.DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import steurer.software_two.model.Customer;
import steurer.software_two.model.FirstLevelDivision;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class simplifies interacting with the first leve division table in the database.
 */
public class FirstLevelDivisionDB {
    /**
     * @return This returns all state/province data in the database.
     */
    public static ObservableList<FirstLevelDivision> getAllDivisions(){
        ObservableList<FirstLevelDivision> divisionList = FXCollections.observableArrayList();

        try {
            ResultSet rs = JDBC.sendQuery("select * from first_level_divisions");

            while(rs.next()) {
                String division = rs.getString("Division");
                int divisionID = rs.getInt("Division_ID");
                int countryID = rs.getInt("Country_ID");

                divisionList.add(new FirstLevelDivision(division, divisionID, countryID));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return divisionList;
    }
}
