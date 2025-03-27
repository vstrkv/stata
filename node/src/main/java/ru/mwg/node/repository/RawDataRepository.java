package ru.mwg.node.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mwg.node.entyty.RawData;

@Repository
public interface RawDataRepository extends JpaRepository<RawData, Long> {

}
