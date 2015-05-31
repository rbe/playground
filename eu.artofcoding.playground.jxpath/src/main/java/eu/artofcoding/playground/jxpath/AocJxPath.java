package eu.artofcoding.playground.jxpath;

import org.apache.commons.jxpath.JXPathContext;

public class AocJxPath {

    public static void main(String[] args) {
        final Employee employee = new Employee("Ralf");
        final Address homeAddress = new Address("Homannstr.");
        employee.setHomeAddress(homeAddress);
        final JXPathContext context = JXPathContext.newContext(employee);
        Address address = (Address) context.getValue("homeAddress");
        String street = (String) context.getValue("/homeAddress/street");
        System.out.println(street);
    }

}
