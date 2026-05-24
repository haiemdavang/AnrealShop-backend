package com.haiemdavang.AnrealShop.service.serviceImp;

import com.haiemdavang.AnrealShop.dto.address.AddressDto;
import com.haiemdavang.AnrealShop.dto.address.AddressRequestDto;
import com.haiemdavang.AnrealShop.dto.address.IBaseAddressDto;
import com.haiemdavang.AnrealShop.dto.address.SingleAddressDto;
import com.haiemdavang.AnrealShop.exception.BadRequestException;
import com.haiemdavang.AnrealShop.mapper.AddressMapper;
import com.haiemdavang.AnrealShop.modal.entity.address.*;
import com.haiemdavang.AnrealShop.modal.entity.shop.Shop;
import com.haiemdavang.AnrealShop.modal.entity.user.User;
import com.haiemdavang.AnrealShop.repository.address.*;
import com.haiemdavang.AnrealShop.security.SecurityUtils;
import com.haiemdavang.AnrealShop.service.serviceInter.IAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressServiceImp implements IAddressService {
    private final UserAddressRepository userAddressRepository;
    private final ShopAddressRepository shopAddressRepository;
    private final ProvinceRepository provinceRepository;
    private final DistrictRepository districtRepository;
    private final WardRepository wardRepository;
    private final AddressMapper mapper;
    private final SecurityUtils securityUtils;



    @Override
    public AddressDto findAddressPrimary() {
        User currentUser = securityUtils.getCurrentUser();
        UserAddress userAddress = userAddressRepository.findByUserIdAndPrimaryAddressTrue(currentUser.getId())
                .orElseThrow(() -> new BadRequestException("PRIMARY_ADDRESS_NOT_FOUND"));
        return mapper.toAddressDto(userAddress);
    }

    @Override
    public List<AddressDto> findAll() {
        User currentUser = securityUtils.getCurrentUser();
        return userAddressRepository.findAllByUserIdOrderByPrimaryAddressDesc(currentUser.getId()).stream().map(mapper::toAddressDto).toList();
    }

    @Override
    public AddressDto findShopAddressPrimary() {
        Shop currentUserShop = securityUtils.getCurrentUserShop();
        ShopAddress shopAddress = shopAddressRepository.findByShopIdAndPrimaryAddressTrue(currentUserShop.getId())
                .orElseThrow(() -> new BadRequestException("PRIMARY_ADDRESS_NOT_FOUND"));
        return mapper.toAddressDto(shopAddress);
    }

    @Override
    public List<AddressDto> findShopAll() {
        Shop currentUserShop = securityUtils.getCurrentUserShop();
        return shopAddressRepository.findAllByShopIdOrderByPrimaryAddressDesc(currentUserShop.getId()).stream().map(mapper::toAddressDto).toList();
    }

    @Override
    public Set<SingleAddressDto> getProvinceList(String keyword) {
        List<Province> provinces;
        if (keyword != null && !keyword.trim().isEmpty()) {
            provinces = provinceRepository.findByNameContainingIgnoreCase(keyword.trim());
        } else {
            provinces = provinceRepository.findAll();
        }

        return provinces.stream()
                .map(province -> SingleAddressDto.builder()
                        .id(Integer.parseInt(province.getId()))
                        .nameDisplay(province.getName())
                        .build())
                .collect(Collectors.toSet());
    }


    @Override
    public Set<SingleAddressDto> getDistrictList(String provinceId, String keyword) {
        List<District> districts;
        if (keyword != null && !keyword.trim().isEmpty()) {
            districts = districtRepository.findByProvinceIdAndNameContainingIgnoreCase(String.valueOf(provinceId), keyword.trim());
        } else {
            districts = districtRepository.findByProvinceId(String.valueOf(provinceId));
        }

        return districts.stream()
                .map(district -> SingleAddressDto.builder()
                        .id(Integer.parseInt(district.getId()))
                        .nameDisplay(district.getName())
                        .build())
                .collect(Collectors.toSet());
    }

    @Override
    public Set<SingleAddressDto> getWardList(String districtId, String keyword) {
        List<Ward> wards;
        if (keyword != null && !keyword.trim().isEmpty()) {
            wards = wardRepository.findByDistrictIdAndNameContainingIgnoreCase(String.valueOf(districtId), keyword.trim());
        } else {
            wards = wardRepository.findByDistrictId(String.valueOf(districtId));
        }

        return wards.stream()
                .map(ward -> SingleAddressDto.builder()
                        .id(Integer.parseInt(ward.getId()))
                        .nameDisplay(ward.getName())
                        .build())
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    @CacheEvict(
            value = "userInfo",
            key = "T(com.haiemdavang.AnrealShop.tech.redis.config.RedisTemplate).PREFIX_USER.getValue() + #principal.name"
    )
    public AddressDto createUserAddress(AddressRequestDto addressDto, Principal principal) {
        User currentUser = securityUtils.getCurrentUser();

        if (addressDto.isPrimary()) {
            userAddressRepository.findByUserIdAndPrimaryAddressTrue(currentUser.getId())
                    .ifPresent(existingDefault -> {
                        existingDefault.setPrimaryAddress(false);
                        userAddressRepository.save(existingDefault);
                    });
        }

        IBaseAddressDto baseAddressDto = wardRepository.findProvinceAndDistrictAndWardByWardId(addressDto.getWardId())
                .orElseThrow(() -> new BadRequestException("ADDRESS_NOT_FOUND_BY_WARD_ID"));

        UserAddress userAddress = mapper.toUserAddress(addressDto);
        userAddress.setUser(currentUser);
        userAddress.setProvince(baseAddressDto.getProvince());
        userAddress.setDistrict(baseAddressDto.getDistrict());
        userAddress.setWard(baseAddressDto.getWard());

        UserAddress savedAddress = userAddressRepository.save(userAddress);
        return mapper.toAddressDto(savedAddress);
    }

    @Override
    @Transactional
    public AddressDto createShopAddress(AddressRequestDto addressDto) {
        Shop currentShop = securityUtils.getCurrentUserShop();

        if (addressDto.isPrimary()) {
            Optional<ShopAddress> existingDefault = shopAddressRepository.findByShopIdAndPrimaryAddressTrue(currentShop.getId());
            if (existingDefault.isPresent()) {
                existingDefault.get().setPrimaryAddress(false);
                shopAddressRepository.save(existingDefault.get());
            }
        }
        IBaseAddressDto baseAddressDto = wardRepository.findProvinceAndDistrictAndWardByWardId(addressDto.getWardId())
                .orElseThrow(() -> new BadRequestException("ADDRESS_NOT_FOUND_BY_WARD_ID"));
        ShopAddress shopAddress = mapper.toShopAddress(addressDto);
        shopAddress.setShop(currentShop);
        shopAddress.setProvince(baseAddressDto.getProvince());
        shopAddress.setDistrict(baseAddressDto.getDistrict());
        shopAddress.setWard(baseAddressDto.getWard());

        ShopAddress savedAddress = shopAddressRepository.save(shopAddress);
        return mapper.toAddressDto(savedAddress);
    }

    @Override
    @Transactional
    public AddressDto updateUserAddress(String id, AddressRequestDto addressDto) {
        User currentUser = securityUtils.getCurrentUser();
        UserAddress userAddress = userAddressRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new BadRequestException("ADDRESS_NOT_FOUND"));

        if (addressDto.isPrimary()) {
            userAddressRepository.findByUserIdAndPrimaryAddressTrue(currentUser.getId())
                    .ifPresent(existingDefault -> {
                        existingDefault.setPrimaryAddress(false);
                        userAddressRepository.save(existingDefault);
                    });
        }

        if (!Objects.equals(addressDto.getWardId(), userAddress.getWard().getId())) {
            IBaseAddressDto baseAddressDto = wardRepository.findProvinceAndDistrictAndWardByWardId(addressDto.getWardId())
                    .orElseThrow(() -> new BadRequestException("ADDRESS_NOT_FOUND_BY_WARD_ID"));
            userAddress.setProvince(baseAddressDto.getProvince());
            userAddress.setDistrict(baseAddressDto.getDistrict());
            userAddress.setWard(baseAddressDto.getWard());
        }
        mapper.updateUserAddress(userAddress, addressDto);
        userAddress.setUser(currentUser);
        UserAddress savedAddress = userAddressRepository.save(userAddress);
        return mapper.toAddressDto(savedAddress);
    }

    @Override
    @Transactional
    public AddressDto updateShopAddress(String id, AddressRequestDto addressDto) {
        Shop currentShop = securityUtils.getCurrentUserShop();
        ShopAddress shopAddress = shopAddressRepository.findByIdAndShopId(id, currentShop.getId())
                .orElseThrow(() -> new BadRequestException("ADDRESS_NOT_FOUND"));

        if (addressDto.isPrimary()) {
            Optional<ShopAddress> existingDefault = shopAddressRepository.findByShopIdAndPrimaryAddressTrue(currentShop.getId());
            if (existingDefault.isPresent()) {
                existingDefault.get().setPrimaryAddress(false);
                shopAddressRepository.save(existingDefault.get());
            }
        }

        if (!Objects.equals(addressDto.getWardId(), shopAddress.getWard().getId())) {
            IBaseAddressDto baseAddressDto = wardRepository.findProvinceAndDistrictAndWardByWardId(addressDto.getWardId())
                    .orElseThrow(() -> new BadRequestException("ADDRESS_NOT_FOUND_BY_WARD_ID"));
            shopAddress.setProvince(baseAddressDto.getProvince());
            shopAddress.setDistrict(baseAddressDto.getDistrict());
            shopAddress.setWard(baseAddressDto.getWard());
        }

        mapper.updateShopAddress(shopAddress, addressDto);
        shopAddress.setShop(currentShop);
        ShopAddress savedAddress = shopAddressRepository.save(shopAddress);
        return mapper.toAddressDto(savedAddress);
    }

    @Override
    @Transactional
    @CacheEvict(
            value = "userInfo",
            key = "T(com.haiemdavang.AnrealShop.tech.redis.config.RedisTemplate).PREFIX_USER.getValue() + #principal.name"
    )
    public void deleteUserAddress(String id, Principal principal) {
        User currentUser = securityUtils.getCurrentUser();
        UserAddress userAddress = userAddressRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new BadRequestException("ADDRESS_NOT_FOUND"));

        if (userAddress.isPrimaryAddress()) {
            List<UserAddress> userAddressList = userAddressRepository.findAllByUserIdAndIdNot(currentUser.getId(), id);
            if (!userAddressList.isEmpty()) {
                UserAddress updatePrimary = userAddressList.get(0);
                updatePrimary.setPrimaryAddress(true);
                userAddressRepository.save(updatePrimary);
            }
        }

        userAddressRepository.delete(userAddress);
    }

    @Override
    @Transactional
    public void deleteShopAddress(String id) {
        Shop currentShop = securityUtils.getCurrentUserShop();
        ShopAddress shopAddress = shopAddressRepository.findByIdAndShopId(id, currentShop.getId())
                .orElseThrow(() -> new BadRequestException("ADDRESS_NOT_FOUND"));

        if (shopAddress.isPrimaryAddress()) {
            List<ShopAddress> userAddressList = shopAddressRepository.findAllByShopIdAndIdNot(currentShop.getId(), id);
            if (!userAddressList.isEmpty()) {
                ShopAddress updatePrimary = userAddressList.get(0);
                updatePrimary.setPrimaryAddress(true);
                shopAddressRepository.save(updatePrimary);
            }
        }

        shopAddressRepository.delete(shopAddress);
    }

    @Override
    public Map<String, AddressDto> getShopAddressByIdIn(Set<String> shopIds) {
        Set<ShopAddress> shopAddressSet = shopAddressRepository.findByShopIdInAndPrimaryAddressTrue(shopIds);
        return shopAddressSet.stream().collect(Collectors.toMap(it -> it.getShop().getId(), mapper::toAddressDto));
    }

    @Override
    public UserAddress getCurrentUserAddressById(String addressId) {
        User currentUser = securityUtils.getCurrentUser();
        return userAddressRepository.findByIdAndUserId(addressId, currentUser.getId())
                .orElseThrow(() -> new BadRequestException("ADDRESS_NOT_FOUND"));
    }

    @Override
    public ShopAddress getShopAddressByIdShop(String idShop) {
        return shopAddressRepository.findByShopIdAndPrimaryAddressTrue(idShop)
                .orElseThrow(() -> new BadRequestException("ADDRESS_NOT_FOUND"));
    }

    @Override
    public ShopAddress getShopAddressById(String shopId, String addressId) {
        return shopAddressRepository.findByIdAndShopId(addressId, shopId)
                .orElseThrow(() -> new BadRequestException("ADDRESS_NOT_FOUND"));
    }

    @Override
    public AddressDto findAddressPrimaryOrNull() {
        User currentUser = securityUtils.getCurrentUser();
        UserAddress userAddress = userAddressRepository.findByUserIdAndPrimaryAddressTrue(currentUser.getId())
                .orElse(null);
        return mapper.toAddressDto(userAddress);
    }

}
