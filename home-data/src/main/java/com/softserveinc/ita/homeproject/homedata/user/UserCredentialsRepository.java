package com.softserveinc.ita.homeproject.homedata.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Repository
@Component
public interface UserCredentialsRepository extends JpaRepository<UserCredentials, Long> {
}
