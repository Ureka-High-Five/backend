package org.highfive.backend.content.repository;

import java.util.List;

import org.highfive.backend.content.entity.metadata.MetaInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MetaInfoRepository extends JpaRepository<MetaInfo, Long> {

    @Query("SELECT m FROM MetaInfo m WHERE m.name = :genreName AND m.type = 'GENRE'")
    List<MetaInfo> findGenreMetaIdByName(String genreName);

    @Query("SELECT mi FROM MetaInfo mi WHERE mi.name = :countryName AND mi.type = 'COUNTRY'")
    MetaInfo findByCountryName(String countryName);

    @Query("SELECT mi FROM MetaInfo mi WHERE mi.name = :actorName AND mi.type = 'ACTOR'")
    MetaInfo findByActorName(String actorName);

    @Query("SELECT mi FROM MetaInfo mi WHERE mi.name = :directorName AND mi.type = 'DIRECTOR'")
    MetaInfo findByDirectorName(String directorName);
}
