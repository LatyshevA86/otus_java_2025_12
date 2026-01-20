package homework;

import java.util.ArrayDeque;
import java.util.Deque;

public class CustomerReverseOrder {

    private final Deque<Customer> deque;

    public CustomerReverseOrder() {
        this.deque = new ArrayDeque<>();
    }

    public void add(Customer customer) {
        deque.addLast(customer);
    }

    public Customer take() {
        return deque.pollLast();
    }
}
