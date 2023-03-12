package homework;

import lombok.Getter;

@Getter
public class BanknoteCell implements Comparable<BanknoteCell> {
    private static final int MAX_BANKNOTES = 100;
    private final BanknoteDenomination denomination;
    private int quantity;

    public BanknoteCell(BanknoteDenomination denomination, int quantity) {
        this.denomination = denomination;
        if (quantity> MAX_BANKNOTES || quantity < 0) {
            this.quantity = 0;
            System.out.println("Quantity of denomination: " + denomination + " is unacceptable, creating empty cell");
            return;
        }
        this.quantity = quantity;
    }

    public Atm.AtmResult addCash(int addQuantity) {
        if (quantity + addQuantity > MAX_BANKNOTES) {
            return Atm.AtmResult.FAILURE;
        }
        quantity += addQuantity;
        return Atm.AtmResult.SUCCESS;
    }

    public Atm.AtmResult withdrawCash(int withdrawQuantity) {
        if (quantity - withdrawQuantity < 0) {
            return Atm.AtmResult.FAILURE;
        }
        quantity -= withdrawQuantity;
        return Atm.AtmResult.SUCCESS;
    }

    @Override
    public int compareTo(BanknoteCell other) {
        return Integer.compare(this.getDenomination().getValue(), other.getDenomination().getValue());
    }

    public boolean canAcceptCash(Integer value) {
        return value + quantity <= MAX_BANKNOTES;
    }
}
