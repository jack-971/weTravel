package uk.ac.qub.jmccambridge06.wetravel.ui;

public class UserCard {

    private String image;
    private String name;
    private String username;
    private String home;

    public UserCard(String imageString, String name, String username, String home) {
        this.image = imageString;
        this.name = name;
        this.username = username;
        this.home = home;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }
}
