package homework;

import java.util.Map;

public interface Atm {
    AtmResult acceptCash(Map<BanknoteDenomination, Integer> cashLoad);

    AtmResult withdraw(int sum);

    void showRemainingCash();

    enum AtmResult {
        SUCCESS,
        FAILURE
    }

}

