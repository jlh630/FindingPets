package com.easyarch.FindingPetsSys.mapper;

import com.easyarch.FindingPetsSys.entity.Order;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OrderMapper {
    int insertOrder(@Param("order") Order order);

    Order queryOrderByOrderId(@Param("orderId") Long orderId);

    Order queryOrderByNoteId(@Param("noteId") Long noteId);

    List<Order> queryOrdersByUserId(@Param("userId") Long userId);

    int updateOrderStatus(@Param("order") Order order);

    int updateOrderStatusByNoteId(@Param("noteId") Long noteId, @Param("status") byte status);

    List<Order> selectOrdersByStatus(@Param("status") byte status);
}
