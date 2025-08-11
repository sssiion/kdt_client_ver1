package org.example.kdt_bank_client2.UserBank.ServiceUser;

import lombok.RequiredArgsConstructor;
import org.example.kdt_bank_client2.Controller.UserController;
import org.example.kdt_bank_client2.ControllerUser.ProductController;
import org.example.kdt_bank_client2.DtoUser.ProductResponseDto;
import org.example.kdt_bank_client2.UserBank.SessionUser.ProductSession;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductService {

    private final ProductController productController;
    private final ProductSession productSession;
    public void getAllProducts(){
        try{
            List<ProductResponseDto> dtos = productController.getAllProducts().getData();
            productSession.setProductResponseDtos(dtos);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public void getProductsByCategory(String category)  {
        try{
            List<ProductResponseDto> dtos = productController.getProductsByCategory(category).getData();
            productSession.setProductResponseDtos(dtos);
        }catch(Exception e){
            e.printStackTrace();
        }

    }
}
