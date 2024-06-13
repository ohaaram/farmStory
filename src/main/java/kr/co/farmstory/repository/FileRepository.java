package kr.co.farmstory.repository;

import kr.co.farmstory.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<File, Integer> {

    void deleteFilesByAno(int ano);
}
