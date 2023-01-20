package homework;


import java.util.AbstractMap;
import java.util.Comparator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;

public class CustomerService {
    private final NavigableMap<Customer, String> customers;

    public CustomerService() {
        customers = new TreeMap<>(Comparator.comparingLong(Customer::getScores));
    }

    public Map.Entry<Customer, String> getSmallest() {
        Map.Entry<Customer, String> smallestEntry = customers.firstEntry();
        if (Objects.isNull(smallestEntry)) {
            return null;
        }
        return wrapValue(smallestEntry);
    }

    private static AbstractMap.SimpleEntry<Customer, String> wrapValue(Map.Entry<Customer, String> smallestEntry) {
        return new AbstractMap.SimpleEntry<>(new Customer(smallestEntry.getKey()), smallestEntry.getValue());
    }

    public Map.Entry<Customer, String> getNext(Customer customer) {
        Map.Entry<Customer, String> nextEntry = customers.higherEntry(customer);
        if (Objects.isNull(nextEntry)) {
            return null;
        }
        return wrapValue(nextEntry);
    }

    public void add(Customer customer, String data) {
        customers.put(customer, data);
    }
}
