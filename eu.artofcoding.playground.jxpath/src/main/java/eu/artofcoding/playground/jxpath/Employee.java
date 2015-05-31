package eu.artofcoding.playground.jxpath;

public class Employee {

    private String firstName;

    private Address homeAddress;

    public Employee(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setHomeAddress(Address homeAddress) {
        this.homeAddress = homeAddress;
    }

    public Address getHomeAddress() {
        return homeAddress;
    }

}
