package com.driver;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class OrderRepository {

    HashMap<String,Order> orderDB=new HashMap<>();
    HashMap<String,DeliveryPartner> partnerDB=new HashMap<>();

    HashMap<String,String> orderPartnerDB=new HashMap<>();

    HashMap<String, List<String>> partnerOrders=new HashMap<>();
    public void addOrder(Order order) {
        orderDB.put(order.getId(),order);
    }

    public void addPartner(String partnerId) {
        partnerDB.put(partnerId,new DeliveryPartner(partnerId));
    }

    public void addOrderPartnerPair(String orderId, String partnerId) {
        if(partnerDB.containsKey(partnerId) && orderDB.containsKey(orderId)) {
            orderPartnerDB.put(orderId,partnerId);
            List<String> orders=partnerOrders.getOrDefault(partnerId,new ArrayList<>());
            orders.add(orderId);
            partnerOrders.put(partnerId,orders);
            partnerDB.get(partnerId).setNumberOfOrders(orders.size());
        }
    }

    public Order getOrderById(String orderId) {
        return orderDB.get(orderId);
    }

    public DeliveryPartner getPartnerById(String partnerId) {
        return partnerDB.get(partnerId);
    }

    public Integer getOrderCountByPartnerId(String partnerId) {
        if (!partnerOrders.containsKey(partnerId)) return 0;
        return partnerOrders.get(partnerId).size();
    }

    public List<String> getOrdersByPartnerId(String partnerId) {
        return partnerOrders.get(partnerId);
    }

    public List<String> getAllOrders() {
        List<String> orders=new ArrayList<>();
        orders.addAll(orderDB.keySet());
        return orders;
    }

    public Integer getCountOfUnassignedOrders() {
        return orderDB.size()-orderPartnerDB.size();
    }

    public void deletePartnerById(String partnerId) {
        partnerDB.remove(partnerId);

        List<String> orders=partnerOrders.get(partnerId);
        partnerOrders.remove(partnerId);
        if (orders==null) return;
        for (String order:orders) {
            orderPartnerDB.remove(order);
        }


    }

    public void deleteOrderById(String orderId) {
        orderDB.remove(orderId);

        String partner = orderPartnerDB.get(orderId);
        orderPartnerDB.remove(orderId);

        if (partnerOrders.get(partner)!=null) {
            partnerOrders.get(partner).remove(orderId);
            partnerDB.get(partner).setNumberOfOrders(partnerOrders.get(partner).size());
        }
    }

    public int getLastDeliveryTimeByPartnerId(String partnerId) {
         List<String> orders=partnerOrders.get(partnerId);
        if (orders==null) return 0;
         int time=0;
         for (String order:orders) {
            int orderTime=orderDB.get(order).getDeliveryTime();
            time=Math.max(time,orderTime);
         }
         return time;

    }

    public Integer getOrdersLeftAfterGivenTimeByPartnerId(int time, String partnerId) {
        List<String> orders=partnerOrders.get(partnerId);
        if (orders==null) return 0;
        Integer cnt=0;
        for (String order:orders) {
            int orderTime=orderDB.get(order).getDeliveryTime();
            if(orderTime>time) cnt++;
        }
        return cnt;
    }
}
