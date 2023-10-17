package me.kbh.jpa.repository;

import me.kbh.jpa.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
