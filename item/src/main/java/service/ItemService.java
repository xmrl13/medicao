package service;

import dto.ItemRequestDTO;
import model.Item;
import org.springframework.beans.factory.annotation.Autowired;
import repository.ItemRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;


@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    @Transactional
    public void createItem(@Valid ItemRequestDTO itemRequestDTO) {

        itemRepository.findByNameAndUnit(itemRequestDTO.getName(), itemRequestDTO.getUnit()).ifPresent(item -> {
            throw new IllegalArgumentException("Já existe um item com o nome: " + itemRequestDTO.getName() + " e unidade de medida: " + itemRequestDTO.getUnit());
        });

        itemRepository.save(new Item(itemRequestDTO.getName(), itemRequestDTO.getUnit()));
        ResponseEntity.ok().build();
    }


    private void saveInBatches(List<Item> itens) {
        Stream.iterate(0, i -> i + 5)
                .limit((itens.size() + 5 - 1) / 5)
                .forEach(i -> {
                    List<Item> bloco = itens.subList(i, Math.min(i + 5, itens.size()));
                    itemRepository.saveAll(bloco);
                });
    }


/*
    @Transactional
    public ItemDTO updateItem(ItemDTO itemDTO) {

        Item existingItem = itemRepository.findByNameAndUnit(itemDTO.getName(), itemDTO.getUnit())
                .orElseThrow(() -> new IllegalArgumentException("Item não encontrado"));

        existingItem.setUnit(itemDTO.getUnit());
        existingItem.setName(itemDTO.getName());

        itemRepository.save(existingItem);

        return new ItemDTO(Long 1,existingItem.getName(), existingItem.getUnit());
    }
*/

    public boolean existsById(Long id) {

        return itemRepository.existsById(id);
    }

    public Optional<Long> getIdByNameAndUnit(String name, String unit) {
        return itemRepository.findIdByNameAndUnit(name, unit);
    }
}
