package com.webthuongmai.repository;
import com.webthuongmai.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUser_UserID(Long userId);
}