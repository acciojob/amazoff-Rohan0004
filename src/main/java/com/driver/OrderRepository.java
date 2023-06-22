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
        if(!partnerDB.containsKey(partnerId) || !orderDB.containsKey(orderId)) return;
        orderPartnerDB.put(orderId,partnerId);
        partnerDB.get(partnerId).setNumberOfOrders(partnerDB.get(partnerId).getNumberOfOrders()+1);
        partnerOrders.putIfAbsent(partnerId,new ArrayList<>());
        partnerOrders.get(partnerId).add(orderId);
    }

    public Order getOrderById(String orderId) {
        return orderDB.get(orderId);
    }

    public DeliveryPartner getPartnerById(String partnerId) {
        return partnerDB.get(partnerId);
    }

    public Integer getOrderCountByPartnerId(String partnerId) {
        if(!partnerOrders.containsKey(partnerId)) return 0;
        return partnerOrders.get(partnerId).size();
    }

    public List<String> getOrdersByPartnerId(String partnerId) {
        return partnerOrders.get(partnerId);
    }

    public List<String> getAllOrders() {
        List<String> orders=new ArrayList<>();
        for (String order:orderDB.keySet()) {
            orders.add(order);
        }
        return orders;
    }

    public Integer getCountOfUnassignedOrders() {
        return orderDB.size()-orderPartnerDB.size();
    }

    public void deletePartnerById(String partnerId) {
        if(partnerOrders.containsKey(partnerId)){
            for (String order:partnerOrders.get(partnerId)) {
                orderPartnerDB.remove(order);
            }
            partnerDB.remove(partnerId);
            partnerOrders.remove(partnerId);
        }

    }

    public void deleteOrderById(String orderId) {
        orderDB.remove(orderId);
        if (orderPartnerDB.containsKey(orderId)) {
            partnerOrders.get(orderPartnerDB.get(orderId)).remove(orderId);
            partnerDB.get(orderPartnerDB.get(orderId)).setNumberOfOrders(partnerDB.get(orderPartnerDB.get(orderId)).getNumberOfOrders()-1);
            orderPartnerDB.remove(orderId);
        }
    }

    public String getLastDeliveryTimeByPartnerId(String partnerId) {
         List<String> orders=partnerOrders.get(partnerId);
         if (orders==null) return null;
         int time=0;
         for (String order:orders) {
            int orderTime=orderDB.get(order).getDeliveryTime();
            time=Math.max(time,orderTime);
         }
         return time/60+":"+(time-(time/60)*60);

    }

    public Integer getOrdersLeftAfterGivenTimeByPartnerId(String time, String partnerId) {
        List<String> orders=partnerOrders.get(partnerId);
        if (orders==null) return null;
        Integer cnt=0,t=Integer.parseInt(time.substring(0,2),10)*60+Integer.parseInt(time.substring(3));
        for (String order:orders) {
            int orderTime=orderDB.get(order).getDeliveryTime();
            if(orderTime>t) cnt++;
        }
        return cnt;
    }
}
