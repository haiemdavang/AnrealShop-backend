package com.haiemdavang.AnrealShop.controller;

import com.haiemdavang.AnrealShop.dto.address.AddressDto;
import com.haiemdavang.AnrealShop.dto.address.AddressRequestDto;
import com.haiemdavang.AnrealShop.dto.address.SingleAddressDto;
import com.haiemdavang.AnrealShop.service.serviceInter.IAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/address")
public class AddressController {
    private final IAddressService addressService;

    @GetMapping("/get-province-list")
    public ResponseEntity<Set<SingleAddressDto>> getProvinceList(@RequestParam(required = false) String keyword) {
        Set<SingleAddressDto> provinces = addressService.getProvinceList(keyword);
        return ResponseEntity.ok(provinces);
    }

    @GetMapping("/get-district-list")
    public ResponseEntity<Set<SingleAddressDto>> getDistrictList(@RequestParam String provinceId,
                                                                 @RequestParam(required = false) String keyword) {
        Set<SingleAddressDto> districts = addressService.getDistrictList(provinceId, keyword);
        return ResponseEntity.ok(districts);
    }

    @GetMapping("/get-ward-list")
    public ResponseEntity<Set<SingleAddressDto>> getWardList(@RequestParam String districtId,
                                                              @RequestParam(required = false) String keyword) {
        Set<SingleAddressDto> wards = addressService.getWardList(districtId, keyword);
        return ResponseEntity.ok(wards);
    }

    @GetMapping("/get-address-primary")
    public ResponseEntity<AddressDto> getAddressPrimary() {
        AddressDto addressPrimary = addressService.findAddressPrimary();
        return ResponseEntity.ok(addressPrimary);
    }

    @GetMapping("/get-address-all")
    public ResponseEntity<List<AddressDto>> getAddressAll() {
        List<AddressDto> addressAll = addressService.findAll();
        return ResponseEntity.ok(addressAll);
    }

    @GetMapping("/get-shop-address-primary")
    public ResponseEntity<AddressDto> getShopAddressPrimary() {
        AddressDto shopAddressPrimary = addressService.findShopAddressPrimary();
        return ResponseEntity.ok(shopAddressPrimary);
    }

    @GetMapping("/get-shop-address-all")
    public ResponseEntity<List<AddressDto>> getShopAddressAll() {
        List<AddressDto> shopAddressAll = addressService.findShopAll();
        return ResponseEntity.ok(shopAddressAll);
    }

    @PostMapping("/user-address")
    public ResponseEntity<AddressDto> createUserAddress(@RequestBody AddressRequestDto addressDto) {
        AddressDto createdAddress = addressService.createUserAddress(addressDto);
        return ResponseEntity.ok(createdAddress);
    }

    @PostMapping("/shop-address")
    public ResponseEntity<AddressDto> createShopAddress(@RequestBody AddressRequestDto addressDto) {
        AddressDto createdAddress = addressService.createShopAddress(addressDto);
        return ResponseEntity.ok(createdAddress);
    }

    @PutMapping("/user-address/{id}")
    public ResponseEntity<AddressDto> updateUserAddress(@PathVariable String id, @RequestBody AddressRequestDto addressDto) {
        AddressDto updatedAddress = addressService.updateUserAddress(id, addressDto);
        return ResponseEntity.ok(updatedAddress);
    }

    @PutMapping("/shop-address/{id}")
    public ResponseEntity<AddressDto> updateShopAddress(@PathVariable String id, @RequestBody AddressRequestDto addressDto) {
        AddressDto updatedAddress = addressService.updateShopAddress(id, addressDto);
        return ResponseEntity.ok(updatedAddress);
    }

    @DeleteMapping("/user-address/{id}")
    public ResponseEntity<?> deleteUserAddress(@PathVariable String id) {
        addressService.deleteUserAddress(id);
        return ResponseEntity.ok(Map.of("message", "Delete success"));
    }

    @DeleteMapping("/shop-address/{id}")
    public ResponseEntity<?> deleteShopAddress(@PathVariable String id) {
        addressService.deleteShopAddress(id);
        return ResponseEntity.ok(Map.of("message", "Delete success"));
    }
}
