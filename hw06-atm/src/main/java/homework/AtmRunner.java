package homework;

import java.util.HashMap;
import java.util.Map;

public class AtmRunner {
    public static void main(String[] args) {
        Atm atm = new AtmImpl();
        atm.showRemainingCash();

        Map<BanknoteDenomination, Integer> cash = new HashMap<>();
        cash.put(BanknoteDenomination.FIFTY, 20);
        cash.put(BanknoteDenomination.HUNDRED, 20);
        cash.put(BanknoteDenomination.THOUSAND, 5);
        cash.put(BanknoteDenomination.FIVE_THOUSAND, 3);
        System.out.println(atm.acceptCash(cash));
        atm.showRemainingCash();

        System.out.println(atm.withdraw(2000));
        atm.showRemainingCash();

        System.out.println(atm.withdraw(4000));
        atm.showRemainingCash();

        System.out.println(atm.withdraw(1001));
        atm.showRemainingCash();

        System.out.println(atm.withdraw(6150));
        atm.showRemainingCash();

        Atm atm1 = new AtmImpl(cash);
        atm1.showRemainingCash();

        System.out.println(atm1.withdraw(20000));
        atm1.showRemainingCash();

        Map<BanknoteDenomination, Integer> cash1 = new HashMap<>();
        cash1.put(BanknoteDenomination.FIFTY, 1000);
        System.out.println(atm1.acceptCash(cash1));
        atm1.showRemainingCash();

        Map<BanknoteDenomination, Integer> cash2 = new HashMap<>();
        cash2.put(BanknoteDenomination.FIFTY, 1000);
        cash2.put(BanknoteDenomination.HUNDRED, 20);
        cash2.put(BanknoteDenomination.THOUSAND, 5);
        cash2.put(BanknoteDenomination.FIVE_THOUSAND, 3);
        Atm atm2 = new AtmImpl(cash2);
        atm2.showRemainingCash();

        Map<BanknoteDenomination, Integer> cash3 = new HashMap<>();
        cash3.put(BanknoteDenomination.FIFTY, 0);
        cash3.put(BanknoteDenomination.HUNDRED, 20);
        cash3.put(BanknoteDenomination.THOUSAND, 5);
        cash3.put(BanknoteDenomination.FIVE_THOUSAND, 3);
        Atm atm3 = new AtmImpl(cash3);
        atm3.showRemainingCash();
    }
}
