package wms.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import wms.entity.PurchaseOrderItem;
import wms.entity.SaleOrderItem;

import java.util.List;

public interface SaleOrderItemRepo extends JpaRepository<SaleOrderItem, Long> {
    @Query(value = "select * from where order_code = :orderCode", nativeQuery = true)
    List<SaleOrderItem> search(String orderCode);
    @Query(value = "select * from scm_sale_order_item soi left join product p on p.code = soi.product_code\n" +
            " where order_code = :orderCode or :orderCode = ''", nativeQuery = true)
    Page<SaleOrderItem> search(Pageable pageable, String orderCode);

    @Query(value = "select * from scm_sale_order_item where order_code = :orderCode and product_code = :productCode", nativeQuery = true)
    SaleOrderItem getItemByProductCode(String orderCode, String productCode);
}
