package org.svip.sbom.model.metadata;

/**
 * File: Contact.java
 *
 * Represents contact information
 *
 * @author Derek Garcia
 */
public class Contact {
    private final String name;
    private final String email;
    private final String phone;


    /**
     * Create new Contact
     *
     * @param name Name of contact
     * @param email Email of contact
     * @param phone Phone number of contact
     */
    public Contact(String name, String email, String phone){
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    ///
    /// Getters
    ///

    /**
     * @return Name
     */
    public String getName() {
        return name;
    }

    /**
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @return phone
     */
    public String getPhone() {
        return phone;
    }
}
