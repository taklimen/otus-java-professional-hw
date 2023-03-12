package homework;

public enum BanknoteDenomination {
    FIFTY(50),
    HUNDRED(100),
    THOUSAND(1000),
    FIVE_THOUSAND(5000);

    private int value;

    public int getValue() {
        return value;
    }

    BanknoteDenomination(int value) {
        this.value = value;
    }
}
