package homework;

import static java.util.Comparator.comparingLong;

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class CustomerService {

    private final NavigableMap<Customer, String> customerMap;

    public CustomerService() {
        this.customerMap = new TreeMap<>(comparingLong(Customer::getScores));
    }

    public Map.Entry<Customer, String> getSmallest() {
        Map.Entry<Customer, String> smallest = customerMap.firstEntry();
        return Map.entry(deepCopy(smallest.getKey()), smallest.getValue());
    }

    public Map.Entry<Customer, String> getNext(Customer customer) {
        Map.Entry<Customer, String> next = customerMap.higherEntry(customer);
        return next != null ? Map.entry(deepCopy(next.getKey()), next.getValue()) : null;
    }

    public void add(Customer customer, String data) {
        customerMap.put(customer, data);
    }

    private Customer deepCopy(Customer customer) {
        return new Customer(customer.getId(), customer.getName(), customer.getScores());
    }
}
