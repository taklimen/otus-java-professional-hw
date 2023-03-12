package homework;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

public class AtmImpl implements Atm {
    private final List<BanknoteCell> banknoteCells;

    public AtmImpl() {
        banknoteCells = Arrays.stream(BanknoteDenomination.values())
                .map(denomination -> new BanknoteCell(denomination, 0))
                .collect(Collectors.toList());
    }

    public AtmImpl(Map<BanknoteDenomination, Integer> initialLoad) {
        banknoteCells = Arrays.stream(BanknoteDenomination.values())
                .map(denomination -> {
                    if (initialLoad.containsKey(denomination)) {
                        return new BanknoteCell(denomination, initialLoad.get(denomination));
                    }
                    return new BanknoteCell(denomination, 0);
                })
                .collect(Collectors.toList());
    }

    @Override
    public AtmResult acceptCash(Map<BanknoteDenomination, Integer> cashLoad) {
        Map<BanknoteCell, Integer> acceptMap = cashLoad.entrySet().stream()
                .filter(entry -> entry.getValue() >= 0)
                .map(entry -> new AbstractMap.SimpleEntry<>(findCell(entry.getKey()), entry.getValue()))
                .filter(entry -> Objects.nonNull(entry.getKey()))
                .filter(entry -> entry.getKey().canAcceptCash(entry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        if (acceptMap.size() != cashLoad.size()) {
            acceptMap.keySet().forEach(key -> cashLoad.remove(key.getDenomination()));
            System.out.println("Cell(s): " + cashLoad.keySet() + " cannot accept the required amount of cash");
            return AtmResult.FAILURE;
        }
        return cashLoad.entrySet().stream()
                .map(entry ->
                        Optional.ofNullable(findCell(entry.getKey()))
                                .map(cell -> cell.addCash(entry.getValue()))
                                .orElse(AtmResult.FAILURE))
                .reduce(AtmResult.SUCCESS, getOperationResult());

    }

    private static BinaryOperator<AtmResult> getOperationResult() {
        return (result1, result2) -> {
            if (result1 == AtmResult.FAILURE || result2 == AtmResult.FAILURE) {
                return AtmResult.FAILURE;
            }
            return AtmResult.SUCCESS;
        };
    }

    private BanknoteCell findCell(BanknoteDenomination denomination) {
        return banknoteCells.stream()
                .filter(cell -> cell.getDenomination() == denomination)
                .findFirst()
                .orElse(null);
    }

    @Override
    public AtmResult withdraw(int sum) {
        if (sum < 0) {
            System.out.println("Sum is less than 0");
            return AtmResult.FAILURE;
        }
        List<BanknoteCell> cellsReversed = banknoteCells.stream()
                .sorted(Comparator.reverseOrder())
                .toList();

        Map<BanknoteCell, Integer> withdrawMap = new HashMap<>();
        for (BanknoteCell cell : cellsReversed) {
            int currentDenomination = cell.getDenomination().getValue();
            int banknotesToWithdraw = sum / currentDenomination;
            int currentQuantity = cell.getQuantity();
            if (currentQuantity >= banknotesToWithdraw) {
                withdrawMap.put(cell, banknotesToWithdraw);
                sum -= banknotesToWithdraw * currentDenomination;
                continue;
            }
            if (currentQuantity > 0) {
                withdrawMap.put(cell, currentQuantity);
                sum -= currentQuantity * currentDenomination;
            }
        }
        if (sum > 0) {
            System.out.println("The required sum in not available");
            return AtmResult.FAILURE;
        }
        return withdrawMap.entrySet().stream()
                .map(entry -> entry.getKey().withdrawCash(entry.getValue()))
                .reduce(AtmResult.SUCCESS, getOperationResult());
    }

    @Override
    public void showRemainingCash() {
        banknoteCells.forEach(cell -> System.out.println("Denomination: " + cell.getDenomination().getValue() +
                " quantity: " + cell.getQuantity()));
    }
}
