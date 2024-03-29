package com.spiet.payments.resources;

import com.spiet.payments.VOs.SaleVO;
import com.spiet.payments.entities.Sale;
import com.spiet.payments.services.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;


@RestController
@RequestMapping(value = "sale")
public class SaleController {

    SaleService service;
    PagedResourcesAssembler<SaleVO> assembler;

    @Autowired
    public SaleController(SaleService service, PagedResourcesAssembler<SaleVO> assembler) {
        this.service = service;
        this.assembler = assembler;
    }

    @GetMapping(value = "/{id}", produces = {"application/json", "application/xml", "application/x-yaml"})
    public SaleVO findById(@PathVariable("id") Long id) {
        SaleVO sale = service.findById(id);
        sale.add(linkTo(methodOn(SaleController.class).findById(sale.getId())).withSelfRel());
        return sale;
    }

    @PostMapping
    public SaleVO create(@RequestBody SaleVO saleVO) {
        SaleVO sale = service.create(saleVO);
        sale.add(linkTo(methodOn(SaleController.class).findById(sale.getId())).withSelfRel());
        return sale;
    }

    @GetMapping
    public ResponseEntity<?> findAll(@RequestParam(value = "page", defaultValue = "0") int page,
                                     @RequestParam(value = "limit", defaultValue = "12") int limit,
                                     @RequestParam(value = "direction", defaultValue = "asc") String direction) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, limit, Sort.by(sortDirection, "date"));

        Page<SaleVO> sales = service.findAll(pageable);
        sales.stream().forEach(p -> p.add(linkTo(methodOn(SaleController.class).findById(p.getId())).withSelfRel()));

        PagedModel<EntityModel<SaleVO>> pagedModel = assembler.toModel(sales);

        return new ResponseEntity<>(pagedModel, HttpStatus.OK);
    }

}
