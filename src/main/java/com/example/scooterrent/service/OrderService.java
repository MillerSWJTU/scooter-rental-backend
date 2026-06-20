package com.example.scooterrent.service;

import com.example.scooterrent.dto.OrderDTO;
import com.example.scooterrent.entity.Order;
import com.example.scooterrent.entity.Scooter;
import com.example.scooterrent.entity.User;
import com.example.scooterrent.enums.OrderStatus;
import com.example.scooterrent.enums.ScooterStatus;
import com.example.scooterrent.repository.OrderRepository;
import com.example.scooterrent.repository.ScooterRepository;
import com.example.scooterrent.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ScooterRepository scooterRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Transactional
    public OrderDTO createOrder(OrderDTO orderDTO) {
        // 获取用户和滑板车
        User user = userRepository.findByUsername(orderDTO.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found: " + orderDTO.getUsername()));
        Scooter scooter = scooterRepository.findById(orderDTO.getScooterId())
                .orElseThrow(() -> new RuntimeException("Scooter not found: " + orderDTO.getScooterId()));

        // 检查滑板车是否可用
        if (scooter.getStatus() != ScooterStatus.AVAILABLE) {
            throw new RuntimeException("Scooter is not available");
        }

        // 创建订单
        Order order = new Order();
        order.setUser(user);
        order.setScooter(scooter);
        order.setStartTime(orderDTO.getStartTime());
        order.setEndTime(orderDTO.getEndTime());
        order.setStatus(OrderStatus.PENDING);
        order.setTotalHours((int) ChronoUnit.HOURS.between(orderDTO.getStartTime(), orderDTO.getEndTime()));
        order.setTotalAmount(calculateTotalAmount(scooter, orderDTO.getStartTime(), orderDTO.getEndTime()));

        // 更新滑板车状态
        scooter.setStatus(ScooterStatus.RENTED);
        scooterRepository.save(scooter);

        // 保存订单
        Order savedOrder = orderRepository.save(order);
        return modelMapper.map(savedOrder, OrderDTO.class);
    }

    public OrderDTO getOrderById(Long id) {
        Optional<Order> order = orderRepository.findById(id);
        return order.map(o -> modelMapper.map(o, OrderDTO.class))
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }

    public List<OrderDTO> getOrdersByUsername(String username) {
        List<Order> orders = orderRepository.findByUsername(username);
        return orders.stream()
                .map(order -> modelMapper.map(order, OrderDTO.class))
                .collect(Collectors.toList());
    }

    public List<OrderDTO> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(order -> modelMapper.map(order, OrderDTO.class))
                .collect(Collectors.toList());
    }

    public List<OrderDTO> getOrdersByScooterId(Long scooterId) {
        List<Order> orders = orderRepository.findByScooterId(scooterId);
        return orders.stream()
                .map(order -> modelMapper.map(order, OrderDTO.class))
                .collect(Collectors.toList());
    }

    public List<OrderDTO> getOrdersByStatus(OrderStatus status) {
        List<Order> orders = orderRepository.findByStatus(status);
        return orders.stream()
                .map(order -> modelMapper.map(order, OrderDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderDTO updateOrderStatus(Long id, OrderStatus status) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            order.setStatus(status);
            Order updatedOrder = orderRepository.save(order);
            return modelMapper.map(updatedOrder, OrderDTO.class);
        }
        throw new RuntimeException("Order not found with id: " + id);
    }

    @Transactional
    public OrderDTO extendOrder(Long id, LocalDateTime newEndTime) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            order.setEndTime(newEndTime);
            order.setTotalHours((int) ChronoUnit.HOURS.between(order.getStartTime(), newEndTime));
            order.setTotalAmount(calculateTotalAmount(order.getScooter(), order.getStartTime(), newEndTime));
            Order updatedOrder = orderRepository.save(order);
            return modelMapper.map(updatedOrder, OrderDTO.class);
        }
        throw new RuntimeException("Order not found with id: " + id);
    }

    @Transactional
    public void cancelOrder(Long id) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            Scooter scooter = order.getScooter();
            scooter.setStatus(ScooterStatus.AVAILABLE);
            scooterRepository.save(scooter);
            
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
        } else {
            throw new RuntimeException("Order not found with id: " + id);
        }
    }

    public List<OrderDTO> getOrdersByDateRange(LocalDateTime start, LocalDateTime end) {
        List<Order> orders = orderRepository.findByStartTimeBetween(start, end);
        return orders.stream()
                .map(order -> modelMapper.map(order, OrderDTO.class))
                .collect(Collectors.toList());
    }

    public List<OrderDTO> getUserOrdersByDateRange(String username, LocalDateTime start, LocalDateTime end) {
        List<Order> orders = orderRepository.findByUsernameAndStartTimeBetween(username, start, end);
        return orders.stream()
                .map(order -> modelMapper.map(order, OrderDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
                
        if (order.getStatus() == OrderStatus.PENDING || order.getStatus() == OrderStatus.PAID) {
            // 如果订单状态是待处理或已支付，可以在这里添加额外的处理逻辑
            // 例如：更新滑板车状态等
        }
        
        orderRepository.delete(order);
    }

    private double calculateTotalAmount(Scooter scooter, LocalDateTime startTime, LocalDateTime endTime) {
        long hours = ChronoUnit.HOURS.between(startTime, endTime);
        if (hours < 1) {
            hours = 1;
        }
        return hours * scooter.getPricePerHour();
    }
} 