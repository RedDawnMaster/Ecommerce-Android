package org.sop.ecommerceapp.repositories;

import org.sop.ecommerceapp.models.Statistic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface StatisticDao extends JpaRepository<Statistic, Long> {

}
