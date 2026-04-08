package com.yunji.backend.repository;

import com.yunji.backend.entity.BucketItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BucketItemRepository extends JpaRepository<BucketItem, Long> {
}