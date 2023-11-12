package com.example.springproduct.controllers;


import com.example.springproduct.dto.ProductRecordDTO;
import com.example.springproduct.models.ProductModel;
import com.example.springproduct.repositories.ProductRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class ProductController {

    @Autowired
    private ProductRepository productRepository;


    @PostMapping("/products")
    public ResponseEntity<ProductModel> saveProduct(@RequestBody @Valid ProductRecordDTO product){
        var productModel = new ProductModel();
        BeanUtils.copyProperties(product,productModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(productRepository.save(productModel));
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductModel>> getAll(){
        List<ProductModel> productList = productRepository.findAll();
        if (!productList.isEmpty()){
            for (ProductModel productModel: productList){
                UUID id = productModel.getIdProduct();
                productModel.add(linkTo(methodOn(ProductController.class).getOneProduct(id)).withSelfRel());
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body();
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<Object> getOneProduct(@PathVariable UUID id){
        Optional<ProductModel> optionalProductModel = productRepository.findById(id);
        if (optionalProductModel.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not Found!");
        }
        return ResponseEntity.status(HttpStatus.OK).body(optionalProductModel.get());
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<Object> updateProduct(@PathVariable UUID id, @RequestBody @Valid ProductRecordDTO product){
        Optional<ProductModel> optionalProductModel = productRepository.findById(id);
        if (optionalProductModel.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found!");
        }
        var productModel = optionalProductModel.get();
        BeanUtils.copyProperties(product, productModel);
        return ResponseEntity.status(HttpStatus.OK).body(productRepository.save(productModel));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable(value = "id") UUID id){
        Optional<ProductModel> optionalProductModel = productRepository.findById(id);
        if (optionalProductModel.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found!");
        }
        productRepository.delete(optionalProductModel.get());
        return ResponseEntity.status(HttpStatus.OK).body("Product deleted sucessfully");
    }
}
