package me.kbh.jpa.repository;

import me.kbh.jpa.entity.Item;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ItemRepositoryTest {

    @Autowired ItemRepository itemRepository;

    //org.springframework.data.jpa.repository.support.SimpleJpaRepository -> save() 에 디버깅하여 체크하라
    @Test
    void save() throws Exception {
        Item item = new Item("1");
        itemRepository.save(item);
    }
}