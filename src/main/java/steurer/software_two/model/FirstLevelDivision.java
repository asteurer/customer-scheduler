package steurer.software_two.model;

public class FirstLevelDivision {
    private String division;
    private int divisionID;
    private int countryID;

    public FirstLevelDivision(String division, int divisionID, int countryID) {
        this.division = division;
        this.divisionID = divisionID;
        this.countryID = countryID;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public int getDivisionID() {
        return divisionID;
    }

    public void setDivisionID(int divisionID) {
        this.divisionID = divisionID;
    }

    public int getCountryID() {
        return countryID;
    }

    public void setCountryID(int countryID) {
        this.countryID = countryID;
    }
}
