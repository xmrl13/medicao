package place.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import place.model.Place;

import java.util.Optional;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {

    Optional<Place> findByNameAndProjectContract(String name, String projectContract);

    boolean existsByNameAndProjectContract(String name, String projectContract);

    @Query(value = "select p.id from places p where p.name = :name and p.project_contract = :projectContract", nativeQuery = true)
    Optional<Long> findPlaceIdByNameAndProjectContract(@Param("name") String name, @Param("projectContract") String projectContract);

}
