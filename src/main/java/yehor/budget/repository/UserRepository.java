package yehor.budget.repository;

import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;
import yehor.budget.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>  {

    User findByUsername(String username);
}
