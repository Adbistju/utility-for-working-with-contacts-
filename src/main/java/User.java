public class User {
    private String number;
    private String name;
    private String nameData;

    public String getNameData() {
        return nameData;
    }

    public void setNameData(String nameData) {
        this.nameData = nameData;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User(String number, String name) {
        this.number = number;
        this.name = name;
    }

    public User(String number, String name, String nameData) {
        this.number = number;
        this.name = name;
        this.nameData = nameData;
    }

    public User() {
    }

    @Override
    public String toString() {
        System.out.println("-------------------------------------");
        return name + " : " + number;
    }
}
