package sample.market.domain.product;

import java.util.ArrayList;
import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ProductInfoMapper {

    @Mapping(target = "purchasePrice", ignore = true)
    ProductInfo of(Product product);

    List<ProductInfo> of(List<Product> products);

//    @Mapping(target = "price", ignore = true) // Product의 price 필드를 무시합니다.

    @Mapping(target = "purchasePrice", source = "purchasePrice")
    ProductInfo toProductInfo(Product product, Integer purchasePrice);

    @IterableMapping(elementTargetType = ProductInfo.class)
    default List<ProductInfo> toProductInfos(List<Product> products, List<Integer> prices) {
        List<ProductInfo> productInfos = new ArrayList<>();
        for (int i = 0; i < products.size(); i++) {
            productInfos.add(toProductInfo(products.get(i), prices.get(i)));
        }
        return productInfos;
    }

}
