package repository;


import model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    Optional<Item> findByNameAndUnit(String nome, String unidadeMedida);

    @Query(value = "select i.id from itens i where i.name = :name and i.unit = :unit",nativeQuery = true)
    Optional<Long> findIdByNameAndUnit(@Param("name") String name, @Param("unit") String unit);
}
